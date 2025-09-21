"""Challenge Platform gateway mirroring challenge & organization gateway patterns.

Features:
  - Flat query params (pageNumber/pageSize/searchTerms)
  - Transient error retry via shared iter_paginated utility
  - Per-item validation (using generated SDK model) with skip + metrics update

This keeps consistency for future resource expansion.
"""

from __future__ import annotations

import json
import sys
from collections.abc import Iterator

import openchallenges_api_client
from openchallenges_api_client.api.challenge_platform_api import ChallengePlatformApi
from openchallenges_api_client.rest import ApiException
from pydantic import ValidationError

from ..config.loader import ClientConfig
from ..core.errors import AuthError, OpenChallengesError, map_status
from ..core.metrics import MetricsCollector
from ._shared_paging import PageSpec, iter_paginated


class PlatformGateway:
    def __init__(self, config: ClientConfig) -> None:
        self._cfg = config

    def list_platforms(
        self,
        limit: int,
        search_terms: str | None = None,
        *,
        metrics: MetricsCollector | None = None,
    ) -> Iterator:
        if limit <= 0:
            return iter(())

        def fetch_page(spec: PageSpec):
            with openchallenges_api_client.ApiClient(
                openchallenges_api_client.Configuration(host=self._cfg.api_url)
            ) as api_client:
                api = ChallengePlatformApi(api_client)
                query_params: list[tuple[str, str]] = [
                    ("pageNumber", str(spec.page_number)),
                    ("pageSize", str(spec.page_size)),
                ]
                if search_terms:
                    query_params.append(("searchTerms", search_terms))
                param = api.api_client.param_serialize(
                    method="GET",
                    resource_path="/challenge-platforms",
                    path_params={},
                    query_params=query_params,
                    header_params={"Accept": "application/json"},
                    body=None,
                    post_params=[],
                    files={},
                    auth_settings=["apiKey", "jwtBearer"],
                    collection_formats={},
                    _host=None,
                    _request_auth=None,
                )
                resp = api.api_client.call_api(*param)
                resp.read()
                return json.loads(resp.data.decode("utf-8"))  # type: ignore[attr-defined]

        def extract_items(raw: object):
            from openchallenges_api_client.models.challenge_platform import (  # type: ignore
                ChallengePlatform,
            )

            if not isinstance(raw, dict):
                return None
            raw_items = raw.get("challengePlatforms")
            if not isinstance(raw_items, list):
                return None
            out: list[openchallenges_api_client.ChallengePlatform] = []
            for p in raw_items:
                if not isinstance(p, dict):
                    continue
                pid = p.get("id")
                try:
                    model = ChallengePlatform.from_dict(p)
                except ValidationError as e:
                    ident = pid if isinstance(pid, int) else "?"
                    print(
                        f"[warn] Platform {ident} validation failed:\n{e}\n",
                        file=sys.stderr,
                    )
                    if metrics is not None:
                        metrics.inc_skipped()
                    continue
                if model is not None:
                    out.append(model)
            return out

        try:
            yield from iter_paginated(
                config=self._cfg,
                limit=limit,
                fetch_page=fetch_page,
                extract_items=extract_items,
                metrics=metrics,
            )
        except ApiException as e:  # pragma: no cover
            http_status = getattr(e, "status", None)
            err_cls = map_status(http_status)
            if err_cls is AuthError and not self._cfg.api_key:
                raise AuthError(
                    str(e),
                    hint=(
                        "Provide an API key via --api-key flag, OC_API_KEY env var, "
                        ".openchallenges.toml"
                    ),
                ) from e
            raise err_cls(str(e)) from e
        except Exception as e:  # pragma: no cover
            raise OpenChallengesError(str(e)) from e
