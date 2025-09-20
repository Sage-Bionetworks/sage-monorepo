"""Gateway wrapping SDK OrganizationApi (with simple pagination, strict-only)."""
# ruff: noqa: I001

from __future__ import annotations

from collections.abc import Iterator
import random
import time

import openchallenges_api_client_python
from openchallenges_api_client_python.api.organization_api import OrganizationApi
from openchallenges_api_client_python.models.organization_search_query import (
    OrganizationSearchQuery,
)
from openchallenges_api_client_python.rest import ApiException

from ..config.loader import ClientConfig
from ..core.errors import map_status, OpenChallengesError, AuthError


class OrganizationGateway:
    """Thin pagination layer over generated OrganizationApi.

    All responses are validated strictly by the generated models. Previous tolerant
    skip-invalid behaviour has been removed (data is expected to be contract-valid).
    """

    def __init__(self, config: ClientConfig) -> None:
        self._cfg = config

    def list_organizations(
        self, limit: int, search_terms: str | None = None
    ) -> Iterator[openchallenges_api_client_python.Organization]:
        if limit <= 0:
            return iter(())
        remaining = limit
        page_number = 0
        page_size = min(limit, 100)
        try:
            with openchallenges_api_client_python.ApiClient(
                openchallenges_api_client_python.Configuration(host=self._cfg.api_url)
            ) as api_client:
                api = OrganizationApi(api_client)
                attempt = 0
                while remaining > 0:
                    search = OrganizationSearchQuery(
                        pageNumber=page_number,
                        pageSize=min(page_size, remaining),
                        searchTerms=search_terms if search_terms else None,
                    )
                    try:
                        page = api.list_organizations(
                            organization_search_query=search,
                        )
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
                    attempt = 0
                    items = list(page.organizations or [])
                    if not items:
                        break
                    yield from items[:remaining]
                    remaining -= len(items)
                    if len(items) < page_size:
                        break
                    page_number += 1
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
