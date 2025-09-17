"""Gateway wrapping SDK ChallengeApi (with simple pagination)."""

from __future__ import annotations

from collections.abc import Iterator
import openchallenges_api_client_python
from openchallenges_api_client_python.api.challenge_api import ChallengeApi
from openchallenges_api_client_python.models.challenge_search_query import (
    ChallengeSearchQuery,
)
from openchallenges_api_client_python.rest import ApiException

from ..config.loader import ClientConfig
from ..core.errors import map_status, OpenChallengesError


class ChallengeGateway:
    def __init__(self, config: ClientConfig) -> None:
        self._cfg = config

    def list_challenges(
        self, limit: int, status: list[str] | None = None
    ) -> Iterator[openchallenges_api_client_python.Challenge]:
        """Iterate up to `limit` challenges across pages.

        Applies optional status filter (list of status enums as strings).
        """
        if limit <= 0:
            return iter(())
        remaining = limit
        page_number = 0
        page_size = min(limit, 100)
        # We'll request at most `page_size` per page (capped at 100) but never more than `remaining`.
        try:
            with openchallenges_api_client_python.ApiClient(
                openchallenges_api_client_python.Configuration(host=self._cfg.api_url)
            ) as api_client:
                api = ChallengeApi(api_client)
                while remaining > 0:
                    requested = min(page_size, remaining)
                    search = ChallengeSearchQuery(
                        pageNumber=page_number,
                        pageSize=requested,
                        status=status if status else None,
                    )
                    page = api.list_challenges(challenge_search_query=search)
                    items = list(page.challenges or [])
                    if not items:
                        break
                    for item in items[:remaining]:
                        yield item
                    remaining -= len(items)
                    # If the API returned fewer than requested, we've exhausted results.
                    if len(items) < requested:
                        break
                    page_number += 1
        except ApiException as e:  # pragma: no cover (network path)
            err_cls = map_status(getattr(e, "status", None))
            raise err_cls(str(e)) from e
        except Exception as e:  # pragma: no cover
            raise OpenChallengesError(str(e)) from e
