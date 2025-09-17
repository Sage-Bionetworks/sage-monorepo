"""Gateway wrapping SDK OrganizationApi (with simple pagination)."""

from __future__ import annotations

from collections.abc import Iterator
import openchallenges_api_client_python
from openchallenges_api_client_python.api.organization_api import OrganizationApi
from openchallenges_api_client_python.models.organization_search_query import (
    OrganizationSearchQuery,
)
from openchallenges_api_client_python.rest import ApiException

from ..config.loader import ClientConfig
from ..core.errors import map_status, OpenChallengesError


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
        page_size = min(limit, 100)
        try:
            with openchallenges_api_client_python.ApiClient(
                openchallenges_api_client_python.Configuration(host=self._cfg.api_url)
            ) as api_client:
                api = OrganizationApi(api_client)
                while remaining > 0:
                    search = OrganizationSearchQuery(
                        pageNumber=page_number,
                        pageSize=min(page_size, remaining),
                        searchTerms=search_terms if search_terms else None,
                    )
                    page = api.list_organizations(organization_search_query=search)
                    items = list(page.organizations or [])
                    if not items:
                        break
                    for item in items[:remaining]:
                        yield item
                    remaining -= len(items)
                    if len(items) < page_size:
                        break
                    page_number += 1
        except ApiException as e:  # pragma: no cover
            err_cls = map_status(getattr(e, "status", None))
            raise err_cls(str(e)) from e
        except Exception as e:  # pragma: no cover
            raise OpenChallengesError(str(e)) from e
