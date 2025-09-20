"""Service implementing list challenges use case."""

from __future__ import annotations

from collections.abc import Iterable

from ..config.loader import ClientConfig
from ..domain.models import ChallengeSummary
from ..gateways.challenge_gateway import ChallengeGateway


class ListChallengesService:
    def __init__(self, gateway: ChallengeGateway, config: ClientConfig) -> None:
        self._gw = gateway
        self._cfg = config

    def execute(
        self, *, limit: int | None, status: list[str] | None
    ) -> Iterable[ChallengeSummary]:  # status unused MVP
        effective_limit = limit or self._cfg.default_limit
        challenges = self._gw.list_challenges(effective_limit, status=status)
        for ch in challenges:
            yield ChallengeSummary(
                id=ch.id,
                slug=ch.slug,
                name=ch.name,
                status=getattr(ch, "status", None),
                platform_id=getattr(getattr(ch, "platform", None), "id", None),
                platform_name=getattr(getattr(ch, "platform", None), "name", None),
                start_date=getattr(ch, "start_date", None),
                end_date=getattr(ch, "end_date", None),
            )
