"""OpenChallenges challenge CRUD demo.

Run:
    uv run python openchallenges_tools/manage_challenge.py

Auth: Set OC_API_KEY (Bearer token). Falls back to a demo key.
Config vars:
    OC_API_URL  (default http://localhost:8000/api/v1)
    OC_API_KEY

Update requires some fields that are optional on create, so we populate simple
placeholder values (e.g. operation=1). Adjust as needed for your backend data.
"""

from __future__ import annotations

import os
import time
from datetime import date, timedelta

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

OC_API_URL = os.getenv("OC_API_URL", "http://localhost:8082/api/v1")
OC_API_KEY = os.getenv("OC_API_KEY", "oc_dev_admin1.admin_secret_abcd1234efgh5678")


def build_configuration() -> openchallenges_api_client_python.Configuration:
    """Create a configured client using the apiKey security scheme.

    Prefer apiKey over jwtBearer when an API key is provided; caller can still
    set OC_JWT if we later add support for direct JWT usage.
    """
    cfg = openchallenges_api_client_python.Configuration(host=OC_API_URL)
    if OC_API_KEY:
        # The Python client expects configuration.api_key['apiKey']
        cfg.api_key["apiKey"] = OC_API_KEY
    return cfg


def pretty(title: str, obj) -> None:
    print(f"\n=== {title} ===")
    if isinstance(obj, Challenge):
        operation_id = getattr(getattr(obj, "operation", None), "id", None)
        input_data_type_ids = [
            getattr(dt, "id", None) for dt in (obj.input_data_types or [])
        ]
        print(f"ID: {obj.id}")
        print(f"Slug: {obj.slug}")
        print(f"Name: {obj.name}")
        print(f"Headline: {obj.headline}")
        print(f"Description: {obj.description}")
        print(f"Status: {obj.status}")
        print(f"Platform: {getattr(obj.platform, 'id', None)}")
        print(f"Website URL: {obj.website_url}")
        print(f"Avatar URL: {obj.avatar_url}")
        print(f"DOI: {obj.doi}")
        print(f"Operation ID: {operation_id}")
        print(f"Start Date: {obj.start_date}  End Date: {obj.end_date}")
        print(f"Incentives: {obj.incentives}")
        print(f"Submission types: {obj.submission_types}")
        print(f"Categories: {obj.categories}")
        print(f"Input Data Type IDs: {input_data_type_ids}")
        print(f"Created: {obj.created_at}")
        print(f"Updated: {obj.updated_at}")
    else:
        print(obj)


def get_first_platform_id(api_client) -> int | None:
    """Fetch the first available challenge platform ID (if any)."""
    try:
        page = ChallengePlatformApi(api_client).list_challenge_platforms()
        if page.challenge_platforms:
            return page.challenge_platforms[0].id
    except ApiException as e:
        raise SystemExit(f"Failed to list challenge platforms: {e}") from e
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

    # Supply required fields if they were omitted on create.
    placeholder_start = challenge.start_date or date.today()
    placeholder_end = challenge.end_date or (placeholder_start + timedelta(days=30))
    placeholder_doi = challenge.doi or f"10.1234/demo-challenge-{challenge.id}"
    placeholder_avatar = (
        challenge.avatar_url or "https://example.org/static/demo-avatar.png"
    )

    new_status = ChallengeStatus.ACTIVE
    new_headline = challenge.headline or "Updated headline"
    new_description = challenge.description or "Updated description"
    # Name intentionally unchanged (user requested no synthetic name suffix)
    new_name = challenge.name

    # Build a diff summary before performing the update
    diffs: list[tuple[str, object, object]] = []

    def record(label: str, old, new):
        if old != new:
            diffs.append((label, old, new))

    record("status", challenge.status, new_status)
    record("headline", challenge.headline, new_headline)
    record("description", challenge.description, new_description)
    record("doi", challenge.doi, placeholder_doi)
    record("avatarUrl", challenge.avatar_url, placeholder_avatar)
    record("startDate", challenge.start_date, placeholder_start)
    record("endDate", challenge.end_date, placeholder_end)

    if diffs:
        print("\nPlanned update changes:")
        for label, old, new in diffs:
            print(f"  - {label}: {old!r} -> {new!r}")
    else:
        print(
            "\nNo field changes detected (sending update to populate newly required "
            "fields)."
        )

    update_req = ChallengeUpdateRequest(
        slug=challenge.slug,
        name=new_name,
        headline=new_headline,
        description=new_description,
        doi=placeholder_doi,
        status=new_status,
        platformId=platform_id,
        websiteUrl=challenge.website_url,
        avatarUrl=placeholder_avatar,
        incentives=challenge.incentives or [ChallengeIncentive.OTHER],
        submissionTypes=challenge.submission_types or [ChallengeSubmissionType.OTHER],
        categories=challenge.categories or [ChallengeCategory.BENCHMARK],
        inputDataTypes=input_data_type_ids,
        operation=operation_id,
        startDate=placeholder_start,
        endDate=placeholder_end,
    )

    try:
        updated = challenge_api.update_challenge(challenge.id, update_req)
    except ApiException as e:
        # Print rich error context then terminate execution immediately
        print("\n--- Update Error Details ---")
        print(f"Status: {getattr(e, 'status', None)}")
        print(f"Reason: {getattr(e, 'reason', None)}")
        if getattr(e, "headers", None):
            print(f"Headers: {e.headers}")
        if getattr(e, "body", None):
            print(f"Body: {e.body}")
        if getattr(e, "data", None):
            print(f"Data: {e.data}")
        try:
            print("Update payload:")
            print(update_req.to_json())
        except Exception:  # noqa: BLE001
            print(repr(update_req))
        print("--- End Update Error Details ---\n")
        raise SystemExit(f"Failed to update challenge {challenge.id}: {e}") from e
    pretty("Updated challenge", updated)
    return updated


def delete_challenge(api_client, challenge_id: int) -> None:
    challenge_api = openchallenges_api_client_python.ChallengeApi(api_client)
    try:
        challenge_api.delete_challenge(challenge_id)
    except ApiException as e:
        raise SystemExit(f"\nFailed to delete challenge {challenge_id}: {e}") from e
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
