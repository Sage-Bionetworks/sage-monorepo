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
import random
import sys
import time
from collections.abc import Iterator

import openchallenges_api_client_python
from openchallenges_api_client_python.api.organization_api import OrganizationApi
from openchallenges_api_client_python.rest import ApiException

from ..config.loader import ClientConfig
from ..core.errors import AuthError, OpenChallengesError, map_status


def _emit_validation_warning(kind: str, ident: object, error: Exception) -> None:
    """Emit a multi-line validation warning to stderr, consistent with challenges."""
    label = ident if isinstance(ident, int) else "?"
    print(f"[warn] {kind} {label} validation failed:\n{error}\n", file=sys.stderr)


class OrganizationGateway:
    def __init__(self, config: ClientConfig) -> None:
        self._cfg = config

    def list_organizations(
        self, limit: int, search_terms: str | None = None
    ) -> Iterator[openchallenges_api_client_python.Organization]:
        if limit <= 0:
            return iter(())
        remaining = limit
        page_number = 0
        MAX_PAGE_SIZE = 100
        page_size = min(limit, MAX_PAGE_SIZE)
        try:
            with openchallenges_api_client_python.ApiClient(
                openchallenges_api_client_python.Configuration(host=self._cfg.api_url)
            ) as api_client:
                api = OrganizationApi(api_client)
                attempt = 0
                while remaining > 0:
                    requested = min(page_size, remaining)
                    # Build flat query params
                    query_params: list[tuple[str, str]] = [
                        ("pageNumber", str(page_number)),
                        ("pageSize", str(requested)),
                    ]
                    if search_terms:
                        query_params.append(("searchTerms", search_terms))
                    try:
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
                        raw_dict = json.loads(resp.data.decode("utf-8"))  # type: ignore[attr-defined]
                    except ApiException as e:
                        if (
                            getattr(e, "status", None) in {429, 500, 502, 503, 504}
                            and attempt < self._cfg.retries
                        ):
                            sleep_for = (2**attempt) + random.random()
                            time.sleep(min(sleep_for, 30))
                            attempt += 1
                            continue
                        raise
                    except Exception as e:  # pragma: no cover
                        raise OpenChallengesError(f"Fetch failure: {e}") from e

                    # Expect key 'organizations'
                    raw_items = (
                        raw_dict.get("organizations")
                        if isinstance(raw_dict, dict)
                        else None
                    )
                    if not isinstance(raw_items, list) or not raw_items:
                        break

                    from openchallenges_api_client_python.models.organization import (  # type: ignore
                        Organization,
                    )

                    emitted_this_page = 0
                    for org in raw_items:
                        if not isinstance(org, dict):  # defensive
                            continue
                        try:
                            model = Organization.from_dict(org)
                        except Exception as e:  # ValidationError not explicitly exposed
                            # Warn & skip (mirrors challenge style)
                            _emit_validation_warning(
                                "Organization",
                                org.get("id", "?"),
                                e,
                            )
                            continue
                        if remaining > 0 and model is not None:  # defensive
                            yield model  # type: ignore[misc]
                            emitted_this_page += 1
                            remaining -= 1
                    if emitted_this_page == 0:
                        break
                    attempt = 0
                    page_number += 1
                    if remaining <= 0:
                        break
                    if emitted_this_page < requested:
                        break
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
