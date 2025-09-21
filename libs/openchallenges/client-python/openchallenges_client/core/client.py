"""High-level facade exposed to library users."""

from __future__ import annotations

from collections.abc import Iterable, Iterator

from ..config.loader import ClientConfig, load_config
from ..core.metrics import MetricsCollector
from ..domain.models import (
    ChallengeSummary,
    OrganizationSummary,
    PlatformSummary,
)
from ..gateways.challenge_gateway import ChallengeGateway
from ..gateways.organization_gateway import OrganizationGateway
from ..gateways.platform_gateway import PlatformGateway
from ..services.list_challenges import ListChallengesService
from ..services.list_organizations import ListOrganizationsService
from ..services.list_platforms import ListPlatformsService


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
        self._platform_gateway = PlatformGateway(self._cfg)

    # Public API ---------------------------------------------------------
    def list_challenges(
        self,
        *,
        limit: int | None = None,
        status: list[str] | None = None,
        search: str | None = None,
        metrics: MetricsCollector | None = None,
    ) -> Iterable[ChallengeSummary]:
        svc = ListChallengesService(self._challenge_gateway, self._cfg)
        return svc.execute(limit=limit, status=status, search=search, metrics=metrics)

    def iter_all_challenges(
        self,
        *,
        status: list[str] | None = None,
        metrics: MetricsCollector | None = None,
        search: str | None = None,
    ) -> Iterator[ChallengeSummary]:
        """Stream all challenges lazily (no implicit limit).

        Still pages under the hood; caller controls termination.
        """
        svc = ListChallengesService(self._challenge_gateway, self._cfg)

        def _gen():
            yield from svc.execute(
                limit=2**31 - 1, status=status, search=search, metrics=metrics
            )

        return _gen()

    def list_organizations(
        self,
        *,
        limit: int | None = None,
        search: str | None = None,
        metrics: MetricsCollector | None = None,
    ) -> Iterable[OrganizationSummary]:
        svc = ListOrganizationsService(self._org_gateway, self._cfg)
        return svc.execute(limit=limit, search=search, metrics=metrics)

    def iter_all_organizations(
        self,
        *,
        search: str | None = None,
        metrics: MetricsCollector | None = None,
    ) -> Iterator[OrganizationSummary]:
        """Stream all organizations lazily (no implicit limit)."""
        svc = ListOrganizationsService(self._org_gateway, self._cfg)

        def _gen():
            yield from svc.execute(limit=2**31 - 1, search=search, metrics=metrics)

        return _gen()

    # Platforms -----------------------------------------------------------
    def list_platforms(
        self,
        *,
        limit: int | None = None,
        search: str | None = None,
        metrics: MetricsCollector | None = None,
    ) -> Iterable[PlatformSummary]:
        svc = ListPlatformsService(self._platform_gateway, self._cfg)
        return svc.execute(limit=limit, search=search, metrics=metrics)

    def iter_all_platforms(
        self,
        *,
        search: str | None = None,
        metrics: MetricsCollector | None = None,
    ) -> Iterator[PlatformSummary]:
        svc = ListPlatformsService(self._platform_gateway, self._cfg)

        def _gen():
            yield from svc.execute(limit=2**31 - 1, search=search, metrics=metrics)

        return _gen()

    def create_platform(
        self, *, slug: str, name: str, avatar_key: str, website_url: str
    ):
        """Create a challenge platform and return the underlying SDK model.

        Write operations intentionally bypass the service abstraction for now
        (no additional orchestration required). If we later add validation or
        conflict handling, we can introduce a dedicated service.
        """
        return self._platform_gateway.create_platform(
            slug=slug, name=name, avatar_key=avatar_key, website_url=website_url
        )

    def delete_platform(self, *, platform_id: int) -> None:
        """Delete a challenge platform by id.

        Returns None on success (204 from server). Raises mapped domain
        errors on failure.
        """
        self._platform_gateway.delete_platform(platform_id=platform_id)
