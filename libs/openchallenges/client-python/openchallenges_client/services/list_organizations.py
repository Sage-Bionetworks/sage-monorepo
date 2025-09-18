"""Service implementing list organizations use case."""

from __future__ import annotations

from collections.abc import Iterable

from ..config.loader import ClientConfig
from ..domain.models import OrganizationSummary
from ..gateways.organization_gateway import OrganizationGateway


class ListOrganizationsService:
    def __init__(self, gateway: OrganizationGateway, config: ClientConfig) -> None:
        self._gw = gateway
        self._cfg = config

    def execute(
        self, *, limit: int | None, search: str | None, strict: bool = False
    ) -> Iterable[OrganizationSummary]:  # search unused MVP
        effective_limit = limit or self._cfg.default_limit
        orgs = self._gw.list_organizations(
            effective_limit, search_terms=search, strict=strict
        )
        for org in orgs:
            yield OrganizationSummary(
                id=org.id,
                name=org.name,
                acronym=getattr(org, "acronym", None),
                website_url=getattr(org, "website_url", None),
            )
