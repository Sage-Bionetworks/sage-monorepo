"""Service implementing list challenge platforms use case."""

from __future__ import annotations

from collections.abc import Iterable

from ..config.loader import ClientConfig
from ..core.metrics import MetricsCollector
from ..domain.models import PlatformSummary
from ..gateways.platform_gateway import PlatformGateway


class ListPlatformsService:
    def __init__(self, gateway: PlatformGateway, config: ClientConfig) -> None:
        self._gw = gateway
        self._cfg = config

    def execute(
        self,
        *,
        limit: int | None,
        search: str | None = None,
        metrics: MetricsCollector | None = None,
    ) -> Iterable[PlatformSummary]:
        effective_limit = limit or self._cfg.default_limit
        platforms = self._gw.list_platforms(
            effective_limit, search_terms=search, metrics=metrics
        )
        for p in platforms:
            yield PlatformSummary(
                id=p.id,
                slug=p.slug,
                name=p.name,
                avatar_key=getattr(p, "avatar_key", None),
                website_url=getattr(p, "website_url", None),
            )
