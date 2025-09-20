"""Challenge gateway with tolerant per-item sanitation.

Purpose: list challenges while reporting and sanitizing malformed platform objects
whose fields (id, slug, name) are all null. We avoid page-level model aborts by
fetching raw JSON via the generated client's private serialize method, scanning
each challenge dict, and only then constructing `Challenge` models individually.
We log two categories (currently merged into a single warning line): IDs whose
platform was sanitized (set to None) or which were completely dropped because
other validation errors persisted.
"""

from __future__ import annotations

from collections.abc import Iterator
import json
import random
import sys
import time

from pydantic import ValidationError

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

    def list_challenges(self, limit: int, status: list[str] | None = None) -> Iterator:
        """Iterate up to ``limit`` challenges with optional status filtering.

        Strategy:
        1. Fetch raw JSON for each page using the generated client's private
           serialize method.
        2. Sanitize any challenge whose platform object has all critical
           fields null.
        3. Validate each item individually; drop those still invalid.
        4. Emit a single warning (stderr) per page listing sanitized & dropped IDs.
        """

        if limit <= 0:
            return iter(())

        remaining = limit
        page_number = 0
        page_size = min(limit, 100)

        try:
            with openchallenges_api_client_python.ApiClient(
                openchallenges_api_client_python.Configuration(host=self._cfg.api_url)
            ) as api_client:
                api = ChallengeApi(api_client)
                attempt = 0
                while remaining > 0:
                    requested = min(page_size, remaining)

                    # Convert status strings to enums if provided.
                    converted_status = None
                    if status:
                        converted_status = []
                        for s in status:
                            try:
                                converted_status.append(ChallengeStatus(s))
                            except Exception:
                                continue
                        if not converted_status:
                            converted_status = None

                    search = ChallengeSearchQuery(
                        pageNumber=page_number,
                        pageSize=requested,
                        status=converted_status,
                    )

                    # Low-level serialized API call to capture raw JSON.
                    try:
                        param = api._list_challenges_serialize(  # type: ignore[attr-defined]
                            challenge_search_query=search,
                            _request_auth=None,
                            _content_type=None,
                            _headers=None,
                            _host_index=0,
                        )
                        resp = api.api_client.call_api(*param)
                        resp.read()
                        raw_dict = json.loads(
                            resp.data.decode("utf-8")  # type: ignore[attr-defined]
                        )
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
                    except Exception as e:  # pragma: no cover
                        raise OpenChallengesError(f"Fetch failure: {e}") from e

                    raw_items = (
                        raw_dict.get("challenges")
                        if isinstance(raw_dict, dict)
                        else None
                    )
                    if not isinstance(raw_items, list) or not raw_items:
                        break

                    from openchallenges_api_client_python.models.challenge import (
                        Challenge,  # type: ignore
                    )

                    sanitized: list[Challenge] = []
                    platform_sanitized: set[int] = set()
                    dropped: set[int] = set()
                    for ch in raw_items:
                        if not isinstance(ch, dict):
                            continue
                        cid = ch.get("id")
                        plat = ch.get("platform")
                        if (
                            isinstance(plat, dict)
                            and plat.get("id") is None
                            and plat.get("slug") is None
                            and plat.get("name") is None
                        ):
                            if isinstance(cid, int):
                                platform_sanitized.add(cid)
                            ch["platform"] = None
                        try:
                            sanitized.append(Challenge.from_dict(ch))
                        except ValidationError:
                            if isinstance(cid, int):
                                dropped.add(cid)
                            continue

                    if platform_sanitized or dropped:
                        parts: list[str] = []
                        if platform_sanitized:
                            parts.append(
                                "sanitized:"
                                + ",".join(str(i) for i in sorted(platform_sanitized))
                            )
                        if dropped:
                            parts.append(
                                "dropped:" + ",".join(str(i) for i in sorted(dropped))
                            )
                        print(
                            "[warn] Challenge anomalies (" + " | ".join(parts) + ")",
                            file=sys.stderr,
                        )

                    if not sanitized:
                        break

                    attempt = 0  # reset attempts after a successful page
                    page_number += 1
                    to_emit = sanitized[:remaining]
                    yield from to_emit
                    remaining -= len(to_emit)
                    # If fewer emitted than requested items, we are done.
                    if len(to_emit) < requested:
                        break
        except ApiException as e:  # pragma: no cover (network path)
            http_status = getattr(e, "status", None)
            err_cls = map_status(http_status)
            if err_cls is AuthError and not self._cfg.api_key:
                raise AuthError(
                    str(e),
                    hint=(
                        "Provide an API key via --api-key flag, OC_API_KEY env var, or "
                        ".openchallenges.toml"
                    ),
                ) from e
            raise err_cls(str(e)) from e
        except Exception as e:  # pragma: no cover
            raise OpenChallengesError(str(e)) from e

    # Inline tolerant processing; no separate fallback helper.
