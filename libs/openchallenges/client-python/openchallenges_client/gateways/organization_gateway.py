"""Gateway wrapping SDK OrganizationApi (with simple pagination)."""
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


class OrganizationAnomaly:
    def __init__(self, org_id: int, field: str, issue: str, original_value: str):
        self.org_id = org_id
        self.field = field
        self.issue = issue
        self.original_value = original_value

    def to_dict(self) -> dict[str, str | int]:
        return {
            "org_id": self.org_id,
            "field": self.field,
            "issue": self.issue,
        }


class OrganizationGateway:
    def __init__(self, config: ClientConfig) -> None:
        self._cfg = config
        self._anomalies: list[OrganizationAnomaly] = []
        self._skipped_invalid_total: int = 0

    @property
    def anomalies(self) -> list[OrganizationAnomaly]:
        """Return anomalies detected during the last listing call."""
        return list(self._anomalies)

    @property
    def skipped_invalid_total(self) -> int:
        """Total invalid organization items skipped in the last call (tolerant mode)."""
        return self._skipped_invalid_total

    def list_organizations(
        self, limit: int, search_terms: str | None = None, *, strict: bool = False
    ) -> Iterator[openchallenges_api_client_python.Organization]:
        if limit <= 0:
            return iter(())
        # Reset per-call counters
        self._skipped_invalid_total = 0
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
                            # Tolerant by default; strict mode disables skipping
                            skip_invalid_items=not strict,
                        )
                        # Accumulate skipped count if present (tolerant mode only)
                        if page and not strict:
                            skipped = getattr(page, "_skipped_invalid_organizations", 0)
                            if skipped:
                                self._skipped_invalid_total += int(skipped)
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
                    # Yield only up to remaining
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
