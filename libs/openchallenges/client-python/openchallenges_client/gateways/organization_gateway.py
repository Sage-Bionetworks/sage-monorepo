"""Organization gateway aligned with challenge gateway semantics.

Features:
    - Flat query params (pageNumber/pageSize/searchTerms).
    - Transient error retry with exponential backoff + jitter.
    - Strict per-item validation with multi-line warning output (mirrors
      challenge gateway) – invalid organizations are skipped, full Pydantic /
      exception message is emitted to stderr.

This keeps behavior uniform between challenge and organization listing and
reduces surprise for users relying on validation transparency.
"""

from __future__ import annotations

import json
import sys
from collections.abc import Iterator

import openchallenges_api_client
from openchallenges_api_client.api.organization_api import OrganizationApi
from openchallenges_api_client.rest import ApiException

from ..config.loader import ClientConfig
from ..core.errors import AuthError, OpenChallengesError, map_status
from ..core.metrics import MetricsCollector
from ._base import BaseGateway
from ._shared_paging import PageSpec, iter_paginated


def _emit_validation_warning(kind: str, ident: object, error: Exception) -> None:
    """Emit a multi-line validation warning to stderr, consistent with challenges."""
    label = ident if isinstance(ident, int) else "?"
    print(f"[warn] {kind} {label} validation failed:\n{error}\n", file=sys.stderr)


class OrganizationGateway(BaseGateway):
    def __init__(self, config: ClientConfig) -> None:  # pragma: no cover - init
        super().__init__(config)

    def list_organizations(
        self,
        limit: int,
        search_terms: str | None = None,
        *,
        metrics: MetricsCollector | None = None,
    ) -> Iterator[openchallenges_api_client.Organization]:
        if limit <= 0:
            return iter(())

        def fetch_page(spec: PageSpec):
            with self._api_client() as api_client:
                api = OrganizationApi(api_client)
                query_params: list[tuple[str, str]] = [
                    ("pageNumber", str(spec.page_number)),
                    ("pageSize", str(spec.page_size)),
                ]
                if search_terms:
                    query_params.append(("searchTerms", search_terms))
                param = api.api_client.param_serialize(
                    method="GET",
                    resource_path="/organizations",
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
            from openchallenges_api_client.models.organization import (  # type: ignore
                Organization,
            )

            if not isinstance(raw, dict):
                return None
            raw_items = raw.get("organizations")
            if not isinstance(raw_items, list):
                return None
            out: list[openchallenges_api_client.Organization] = []
            for org in raw_items:
                if not isinstance(org, dict):
                    continue
                try:
                    model = Organization.from_dict(org)
                except Exception as e:  # TODO: narrow once SDK exposes ValidationError
                    _emit_validation_warning(
                        "Organization",
                        org.get("id", "?"),
                        e,
                    )
                    if metrics is not None:
                        metrics.inc_skipped()
                    continue
                if model is not None:
                    out.append(model)
            return out

        try:
            yield from iter_paginated(  # type: ignore[misc]
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
                        "or .openchallenges.toml"
                    ),
                ) from e
            raise err_cls(str(e)) from e
        except Exception as e:  # pragma: no cover
            raise OpenChallengesError(str(e)) from e
