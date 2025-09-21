"""Challenge gateway with per-item validation (no sanitization).

We fetch raw JSON pages, attempt to build each `Challenge` model individually,
and drop only the items that fail validation. For every dropped challenge we
emit a concise warning containing its ID (if present) and Pydantic error details.
This avoids losing an entire page due to a single malformed record while also
not mutating ("sanitizing") the incoming data.
"""

from __future__ import annotations

import json
import sys
from collections.abc import Iterator

import openchallenges_api_client
from openchallenges_api_client.api.challenge_api import ChallengeApi
from openchallenges_api_client.models.challenge_status import ChallengeStatus
from openchallenges_api_client.rest import ApiException
from pydantic import ValidationError

from ..config.loader import ClientConfig
from ..core.errors import AuthError, OpenChallengesError, map_status
from ..core.metrics import MetricsCollector
from ._shared_paging import PageSpec, iter_paginated


class ChallengeGateway:
    def __init__(self, config: ClientConfig) -> None:
        self._cfg = config

    def list_challenges(
        self,
        limit: int,
        status: list[str] | None = None,
        *,
        metrics: MetricsCollector | None = None,
    ) -> Iterator:
        """Yield up to ``limit`` challenges.

        Any challenge that fails individual validation is skipped with a warning.
        No sanitization/mutation of malformed nested fields is performed.
        """

        if limit <= 0:
            return iter(())

        def fetch_page(spec: PageSpec):  # returns raw JSON dict
            with openchallenges_api_client.ApiClient(
                openchallenges_api_client.Configuration(host=self._cfg.api_url)
            ) as api_client:
                api = ChallengeApi(api_client)

                converted_status = None
                if status:
                    converted_status = []
                    for s in status:
                        try:
                            converted_status.append(ChallengeStatus(s))
                        except Exception:
                            continue
                    if not converted_status:
                        converted_status = None

                query_params: list[tuple[str, str]] = [
                    ("pageNumber", str(spec.page_number)),
                    ("pageSize", str(spec.page_size)),
                ]
                if converted_status:
                    for st in converted_status:
                        query_params.append(("status", st.value))

                param = api.api_client.param_serialize(
                    method="GET",
                    resource_path="/challenges",
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
            from openchallenges_api_client.models.challenge import (  # type: ignore
                Challenge,
            )

            if not isinstance(raw, dict):
                return None
            raw_items = raw.get("challenges")
            if not isinstance(raw_items, list):
                return None
            out: list[openchallenges_api_client.Challenge] = []
            for ch in raw_items:
                if not isinstance(ch, dict):
                    continue
                cid = ch.get("id")
                try:
                    model = Challenge.from_dict(ch)
                except ValidationError as e:
                    ident = cid if isinstance(cid, int) else "?"
                    print(
                        f"[warn] Challenge {ident} validation failed:\n{e}\n",
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
                        "Provide an API key via --api-key flag, OC_API_KEY env var, or "
                        ".openchallenges.toml"
                    ),
                ) from e
            raise err_cls(str(e)) from e
        except Exception as e:  # pragma: no cover
            raise OpenChallengesError(str(e)) from e

    # No fallback helper; per-item validation suffices.
