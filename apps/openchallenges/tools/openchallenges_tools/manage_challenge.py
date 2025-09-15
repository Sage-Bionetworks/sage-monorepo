"""OpenChallenges challenge CRUD demo.

Run:
    uv run python openchallenges_tools/manage_challenge.py

Auth: Set OPENCHALLENGES_API_KEY (Bearer token). Falls back to a demo key.
Config vars:
    OPENCHALLENGES_API_URL  (default http://localhost:8000/api/v1)
    OPENCHALLENGES_API_KEY

Update requires some fields that are optional on create, so we populate simple
placeholder values (e.g. operation=1). Adjust as needed for your backend data.
"""

from __future__ import annotations

import os
import time

import openchallenges_api_client_python
from openchallenges_api_client_python.api.challenge_platform_api import (
    ChallengePlatformApi,
)
from openchallenges_api_client_python.models.challenge import Challenge
from openchallenges_api_client_python.models.challenge_category import (
    ChallengeCategory,
)
from openchallenges_api_client_python.models.challenge_create_request import (
    ChallengeCreateRequest,
)
from openchallenges_api_client_python.models.challenge_incentive import (
    ChallengeIncentive,
)
from openchallenges_api_client_python.models.challenge_status import ChallengeStatus
from openchallenges_api_client_python.models.challenge_submission_type import (
    ChallengeSubmissionType,
)
from openchallenges_api_client_python.models.challenge_update_request import (
    ChallengeUpdateRequest,
)
from openchallenges_api_client_python.rest import ApiException

OC_API_URL = os.getenv("OPENCHALLENGES_API_URL", "http://localhost:8000/api/v1")
OC_API_KEY = os.getenv(
    "OPENCHALLENGES_API_KEY", "oc_dev_dev1.dev_secret_9876543210fedcba"
)


def build_configuration() -> openchallenges_api_client_python.Configuration:
    """Create a configured client using the apiKey security scheme.

    Prefer apiKey over jwtBearer when an API key is provided; caller can still
    set OPENCHALLENGES_JWT if we later add support for direct JWT usage.
    """
    cfg = openchallenges_api_client_python.Configuration(host=OC_API_URL)
    if OC_API_KEY:
        # The Python client expects configuration.api_key['apiKey']
        cfg.api_key["apiKey"] = OC_API_KEY
    return cfg


def pretty(title: str, obj) -> None:
    print(f"\n=== {title} ===")
    if isinstance(obj, Challenge):
        print(
            f"ID: {obj.id}\n"
            f"Slug: {obj.slug}\n"
            f"Name: {obj.name}\n"
            f"Status: {obj.status}\n"
            f"Platform: {getattr(obj.platform, 'id', None)}\n"
            f"Incentives: {obj.incentives}\n"
            f"Submission types: {obj.submission_types}\n"
            f"Categories: {obj.categories}\n"
            f"Created: {obj.created_at} Updated: {obj.updated_at}"
        )
    else:
        print(obj)


def get_first_platform_id(api_client) -> int | None:
    """Fetch the first available challenge platform ID (if any)."""
    try:
        page = ChallengePlatformApi(api_client).list_challenge_platforms()
        if page.challenge_platforms:
            return page.challenge_platforms[0].id
    except ApiException as e:
        print(f"Warning: could not list challenge platforms: {e}")
    return None


def create_challenge(api_client) -> Challenge:
    challenge_api = openchallenges_api_client_python.ChallengeApi(api_client)
    platform_id = get_first_platform_id(api_client)
    unique_suffix = int(time.time())
    slug = f"demo-challenge-{unique_suffix}"
    req = ChallengeCreateRequest(
        slug=slug,
        name=f"Demo Challenge {unique_suffix}",
        description="Demo challenge created by manage_challenge.py.",
        websiteUrl="https://example.org/challenge",  # required (alias websiteUrl)
        status=ChallengeStatus.UPCOMING,
        platformId=platform_id,
        incentives=[ChallengeIncentive.MONETARY],
        submissionTypes=[ChallengeSubmissionType.CONTAINER_IMAGE],
        categories=[ChallengeCategory.BENCHMARK],
        inputDataTypes=[],  # keep empty; update will still satisfy schema
        operation=1,
    )
    try:
        challenge = challenge_api.create_challenge(req)
    except ApiException as e:
        raise SystemExit(f"Failed to create challenge: {e}") from e
    pretty("Created challenge", challenge)
    return challenge


def get_challenge(api_client, challenge_id: int) -> Challenge:
    challenge_api = openchallenges_api_client_python.ChallengeApi(api_client)
    try:
        challenge = challenge_api.get_challenge(challenge_id)
    except ApiException as e:
        raise SystemExit(f"Failed to get challenge {challenge_id}: {e}") from e
    pretty("Fetched challenge", challenge)
    return challenge


def update_challenge(api_client, challenge: Challenge) -> Challenge:
    challenge_api = openchallenges_api_client_python.ChallengeApi(api_client)

    # Build a full update request (all required fields of ChallengeUpdateRequest).
    platform_id = getattr(challenge.platform, "id", None)
    if platform_id is None:
        raise SystemExit(
            "Cannot update challenge: platform ID missing (set platformId on create)."
        )
    # Fallbacks for required fields
    operation_id = (
        getattr(getattr(challenge.operation, "id", None), "__int__", lambda: None)()
        or 1
    )
    input_data_type_ids: list[int] = [
        getattr(c, "id", None)  # type: ignore[arg-type]
        for c in (challenge.input_data_types or [])
        if getattr(c, "id", None) is not None
    ]

    update_req = ChallengeUpdateRequest(
        slug=challenge.slug,
        name=challenge.name + " (Updated)",
        headline=(challenge.headline or "Updated headline"),
        description=challenge.description or "Updated description",
        doi=challenge.doi,
        status=ChallengeStatus.ACTIVE,
        platformId=platform_id,
        websiteUrl=challenge.website_url,
        avatarUrl=challenge.avatar_url,
        incentives=challenge.incentives or [ChallengeIncentive.OTHER],
        submissionTypes=challenge.submission_types or [ChallengeSubmissionType.OTHER],
        categories=challenge.categories or [ChallengeCategory.BENCHMARK],
        inputDataTypes=input_data_type_ids,
        operation=operation_id,
        startDate=challenge.start_date,
        endDate=challenge.end_date,
    )

    try:
        updated = challenge_api.update_challenge(challenge.id, update_req)
    except ApiException as e:
        # Gracefully handle common validation failure when placeholder
        # fields (e.g. operation) are invalid in the demo environment.
        if getattr(e, "status", None) == 400:
            print(
                "Warning: update failed with 400 Bad Request (likely placeholder "
                "field such as 'operation'). Skipping update step. Payload was:"
            )
            try:
                print(update_req.to_json())
            except Exception:  # noqa: BLE001
                print(repr(update_req))
            return challenge
        raise SystemExit(f"Failed to update challenge {challenge.id}: {e}") from e
    pretty("Updated challenge", updated)
    return updated


def delete_challenge(api_client, challenge_id: int) -> None:
    challenge_api = openchallenges_api_client_python.ChallengeApi(api_client)
    try:
        challenge_api.delete_challenge(challenge_id)
    except ApiException as e:
        if getattr(e, "status", None) == 401:
            print(
                f"Warning: unauthorized to delete challenge {challenge_id} "
                "with current API key. Skipping delete."
            )
            return
        raise SystemExit(f"Failed to delete challenge {challenge_id}: {e}") from e
    pretty("Deleted challenge", f"Challenge {challenge_id} deleted.")


def demo_crud_flow():
    cfg = build_configuration()
    with openchallenges_api_client_python.ApiClient(cfg) as api_client:
        created = create_challenge(api_client)
        retrieved = get_challenge(api_client, created.id)
        updated = update_challenge(api_client, retrieved)
        # Final retrieval to confirm persistence
        get_challenge(api_client, updated.id)
        delete_challenge(api_client, updated.id)
        print("\nCRUD demo complete.")


if __name__ == "__main__":
    demo_crud_flow()
