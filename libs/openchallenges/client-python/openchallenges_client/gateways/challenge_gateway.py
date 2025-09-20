"""Challenge gateway with per-item validation (no sanitization).

We fetch raw JSON pages, attempt to build each `Challenge` model individually,
and drop only the items that fail validation. For every dropped challenge we
emit a concise warning containing its ID (if present) and Pydantic error details.
This avoids losing an entire page due to a single malformed record while also
not mutating ("sanitizing") the incoming data.
"""

from __future__ import annotations

import json
import random
import sys
import time
from collections.abc import Iterator

import openchallenges_api_client_python
from openchallenges_api_client_python.api.challenge_api import ChallengeApi
from openchallenges_api_client_python.models.challenge_status import ChallengeStatus
from openchallenges_api_client_python.rest import ApiException
from pydantic import ValidationError

from ..config.loader import ClientConfig
from ..core.errors import AuthError, OpenChallengesError, map_status


class ChallengeGateway:
    def __init__(self, config: ClientConfig) -> None:
        self._cfg = config

    def list_challenges(self, limit: int, status: list[str] | None = None) -> Iterator:
        """Yield up to ``limit`` challenges.

        Any challenge that fails individual validation is skipped with a warning.
        No sanitization/mutation of malformed nested fields is performed.
        """

        if limit <= 0:
            return iter(())

        remaining = limit
        page_number = 0
        MAX_PAGE_SIZE = 100  # server page size cap (adjust if backend changes)
        page_size = min(limit, MAX_PAGE_SIZE)

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
                                continue
                        if not converted_status:
                            converted_status = None

                    # Build flat query params instead of sending the full
                    # ChallengeSearchQuery object (server expects discrete params).
                    query_params: list[tuple[str, str]] = [
                        ("pageNumber", str(page_number)),
                        ("pageSize", str(requested)),
                    ]
                    if converted_status:
                        for st in converted_status:
                            query_params.append(("status", st.value))

                    try:
                        param = api.api_client.param_serialize(
                            method="GET",
                            resource_path="/challenges",
                            path_params={},
                            query_params=query_params,
                            header_params={"Accept": "application/json"},
                            body=None,
                            post_params=[],
                            files={},
                            auth_settings=["apiKey", "jwtBearer"],
                            collection_formats={},
                            _host=None,
                            _request_auth=None,
                        )
                        resp = api.api_client.call_api(*param)
                        resp.read()
                        raw_dict = json.loads(resp.data.decode("utf-8"))  # type: ignore[attr-defined]
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

                    from openchallenges_api_client_python.models.challenge import (  # type: ignore
                        Challenge,
                    )

                    emitted_this_page = 0
                    # Even if we reach the user-requested limit, we still
                    # traverse the rest of the page to surface validation
                    # warnings for items we won't yield.
                    for ch in raw_items:
                        if not isinstance(ch, dict):
                            continue
                        cid = ch.get("id")
                        try:
                            model = Challenge.from_dict(ch)
                        except ValidationError as e:
                            ident = cid if isinstance(cid, int) else "?"
                            # Print full Pydantic multi-line error block directly.
                            print(
                                f"[warn] Challenge {ident} validation failed:\n{e}\n",
                                file=sys.stderr,
                            )
                            continue
                        if remaining > 0:
                            yield model
                            emitted_this_page += 1
                            remaining -= 1

                    if emitted_this_page == 0:
                        break

                    attempt = 0
                    page_number += 1
                    if remaining <= 0:
                        break
                    if emitted_this_page < requested:
                        # Fewer valid items than requested -> likely end.
                        break
        except ApiException as e:  # pragma: no cover
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

    # No fallback helper; per-item validation suffices.
