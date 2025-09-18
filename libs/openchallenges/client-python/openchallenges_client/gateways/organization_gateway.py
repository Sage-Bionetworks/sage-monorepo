"""Gateway wrapping SDK OrganizationApi (with simple pagination)."""

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

    @property
    def anomalies(self) -> list[OrganizationAnomaly]:
        """Return anomalies detected during the last listing call."""
        return list(self._anomalies)

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
                        page = api.list_organizations(organization_search_query=search)
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
                    for raw in items[:remaining]:
                        # Defensive validation: ensure login within spec; if not, sanitize.
                        try:
                            login_val = getattr(raw, "login", None)
                            if isinstance(login_val, str) and len(login_val) > 64:
                                # Record anomaly and truncate login in-place for downstream consumers.
                                self._anomalies.append(
                                    OrganizationAnomaly(
                                        org_id=getattr(raw, "id", -1),
                                        field="login",
                                        issue="over_length_truncated",
                                        original_value=login_val,
                                    )
                                )
                                # Truncate without suffix to stay within limit.
                                raw.login = login_val[:64]  # type: ignore[attr-defined]
                        except Exception:  # pragma: no cover - defensive guard
                            pass
                        yield raw
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
