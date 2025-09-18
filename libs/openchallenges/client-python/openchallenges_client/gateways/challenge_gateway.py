"""Gateway wrapping SDK ChallengeApi (with simple pagination)."""

from __future__ import annotations

from collections.abc import Iterator
import random
import time

import openchallenges_api_client_python
from openchallenges_api_client_python.api.challenge_api import ChallengeApi
from openchallenges_api_client_python.models.challenge_search_query import (
    ChallengeSearchQuery,
)
from openchallenges_api_client_python.models.challenge_status import ChallengeStatus
from openchallenges_api_client_python.rest import ApiException

from ..config.loader import ClientConfig
from ..core.errors import map_status, OpenChallengesError, AuthError


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
        # Request at most page_size per page (capped at 100) but not beyond remaining.
        try:
            with openchallenges_api_client_python.ApiClient(
                openchallenges_api_client_python.Configuration(host=self._cfg.api_url)
            ) as api_client:
                api = ChallengeApi(api_client)
                attempt = 0
                while remaining > 0:
                    requested = min(page_size, remaining)
                    converted_status = None
                    if status:
                        converted_status = []
                        for s in status:
                            try:
                                converted_status.append(ChallengeStatus(s))
                            except Exception:
                                # Ignore unknown values (could log later)
                                continue
                        if not converted_status:
                            converted_status = None
                    search = ChallengeSearchQuery(
                        pageNumber=page_number,
                        pageSize=requested,
                        status=converted_status,
                    )
                    try:
                        page = api.list_challenges(challenge_search_query=search)
                    except ApiException as e:
                        # Retry transient server / rate limit responses
                        if (
                            getattr(e, "status", None) in {429, 500, 502, 503, 504}
                            and attempt < self._cfg.retries
                        ):
                            sleep_for = (2**attempt) + random.random()
                            time.sleep(min(sleep_for, 30))  # cap backoff
                            attempt += 1
                            continue
                        raise
                    attempt = 0  # reset after a successful page
                    items = list(page.challenges or [])
                    if not items:
                        break
                    yield from items[:remaining]
                    remaining -= len(items)
                    # If the API returned fewer than requested, we've exhausted results.
                    if len(items) < requested:
                        break
                    page_number += 1
        except ApiException as e:  # pragma: no cover (network path)
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
