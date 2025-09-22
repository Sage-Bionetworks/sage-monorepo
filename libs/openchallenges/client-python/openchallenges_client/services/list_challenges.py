"""Service implementing list challenges use case."""

from __future__ import annotations

from collections.abc import Iterable

from ..config.loader import ClientConfig
from ..core.metrics import MetricsCollector
from ..domain.models import ChallengeSummary
from ..gateways.challenge_gateway import ChallengeGateway


class ListChallengesService:
    def __init__(self, gateway: ChallengeGateway, config: ClientConfig) -> None:
        self._gw = gateway
        self._cfg = config

    def execute(
        self,
        *,
        limit: int | None,
        status: list[str] | None,
        search: str | None = None,
        metrics: MetricsCollector | None = None,
    ) -> Iterable[ChallengeSummary]:
        effective_limit = limit or self._cfg.default_limit
        challenges = self._gw.list_challenges(
            effective_limit,
            status=status,
            search_terms=search,
            metrics=metrics,
        )
        for ch in challenges:
            start_date = getattr(ch, "start_date", None)
            end_date = getattr(ch, "end_date", None)
            status_val = getattr(ch, "status", None)
            duration_days = None
            if start_date and end_date:
                try:
                    duration_days = (end_date - start_date).days
                    if duration_days < 0:
                        duration_days = None
                except Exception:  # pragma: no cover
                    duration_days = None
            yield ChallengeSummary(
                id=ch.id,
                slug=ch.slug,
                name=ch.name,
                status=status_val,
                platform_id=getattr(getattr(ch, "platform", None), "id", None),
                platform_name=getattr(getattr(ch, "platform", None), "name", None),
                start_date=start_date,
                end_date=end_date,
                duration_days=duration_days,
            )
