"""High-level facade exposed to library users."""

from __future__ import annotations

from collections.abc import Iterable, Iterator

from ..config.loader import ClientConfig, load_config
from ..domain.models import ChallengeSummary, OrganizationSummary
from ..gateways.challenge_gateway import ChallengeGateway
from ..gateways.organization_gateway import OrganizationGateway
from ..services.list_challenges import ListChallengesService
from ..services.list_organizations import ListOrganizationsService


class OpenChallengesClient:
    def __init__(
        self,
        *,
        api_key: str | None = None,
        api_url: str | None = None,
        default_limit: int = 5,
    ) -> None:
        self._cfg: ClientConfig = load_config(
            override_api_key=api_key,
            override_api_url=api_url,
            default_limit=default_limit,
        )
        self._challenge_gateway = ChallengeGateway(self._cfg)
        self._org_gateway = OrganizationGateway(self._cfg)

    # Public API ---------------------------------------------------------
    def list_challenges(
        self, *, limit: int | None = None, status: list[str] | None = None
    ) -> Iterable[ChallengeSummary]:
        svc = ListChallengesService(self._challenge_gateway, self._cfg)
        return svc.execute(limit=limit, status=status)

    def iter_all_challenges(
        self, *, status: list[str] | None = None
    ) -> Iterator[ChallengeSummary]:
        """Stream all challenges lazily (no implicit limit).

        Still pages under the hood; caller controls termination.
        """
        svc = ListChallengesService(self._challenge_gateway, self._cfg)

        def _gen():
            yield from svc.execute(limit=2**31 - 1, status=status)

        return _gen()

    def list_organizations(
        self,
        *,
        limit: int | None = None,
        search: str | None = None,
    ) -> Iterable[OrganizationSummary]:
        svc = ListOrganizationsService(self._org_gateway, self._cfg)
        return svc.execute(limit=limit, search=search)

    def iter_all_organizations(
        self, *, search: str | None = None
    ) -> Iterator[OrganizationSummary]:
        """Stream all organizations lazily (no implicit limit)."""
        svc = ListOrganizationsService(self._org_gateway, self._cfg)

        def _gen():
            yield from svc.execute(limit=2**31 - 1, search=search)

        return _gen()
