"""Pull information about Grand Challenge public challenges.

Example response object:
    {
        "api_url": "https://grand-challenge.org/api/v1/challenges/HECKTOR/",
        "url": "https://hecktor.grand-challenge.org/",
        "slug": "HECKTOR",
        "title": "MICCAI HECKTOR 2022",
        "description": "Automatic Head and Neck Tumor Segmentation and Outcome Prediction in PET/CT Images",
        "public": true,
        "status": "OPEN",  # other enum values: CLOSED, COMPLETED, OPENING_SOON
        "logo": "https://rumc-gcorg-p-public.s3.amazonaws.com/logos/challenge/691/grandchallenge_logo_d8JNqKz.png",
        "submission_types": [
            "Predictions"
        ],
        "start_date": "2022-08-26T10:00:00Z",  # can also be null for no start date
        "end_date": "2024-05-01T18:00:00Z",  # can also be null for no end date
        "publications": [
            "https://doi.org/10.1007/978-3-030-98253-9"
        ],
        "created": "2022-03-15T15:45:37.236895+01:00",
        "modified": "2023-07-14T13:41:40.773151Z",
        "incentives": [
            "Monetary",
            "Publication",
            "Speaking Engagement"
        ]
    }
"""

import requests

import pandas as pd
import numpy as np

GC_CHALLENGES_API = "https://grand-challenge.org/api/v1/challenges"


def process_data(df: pd.DataFrame) -> pd.DataFrame:
    """Process and clean the data so that it fits with the OC schema."""

    # Set default values for select columns.
    df = df.assign(
        headline="",
        platform="Grand Challenge",
        other_incentive="FALSE",
        notebook_submission="FALSE",
        mlcube_submission="FALSE",
        other_submission="FALSE",
    )

    # Default challenge name to the slug if title is not provided.
    df["title"] = np.where(
        df["title"] == "",
        df["slug"],
        df["title"],
    )

    # TODO: OC currently supports only one DOI per challenge.  Once multiple
    # DOIs is supported, update this so that the entire list is captured.
    df["doi"] = df["publications"].str[0]

    # Map GC status values to respective OC status values.
    df["status"] = df["status"].replace(
        {
            "COMPLETED": "completed",
            "OPEN": "active",
            "OPEN_SOON": "upcoming",
            "CLOSED": "completed",
        }
    )

    # Only capture the YYYY-MM-DD of the start/end datetimes.
    df["start_date"] = df["start_date"].str.slice(0, 10)
    df["end_date"] = df["end_date"].str.slice(0, 10)

    # Convert list values to string.
    df["incentives"] = df.incentives.apply(lambda x: ", ".join([str(i) for i in x]))
    df["submission_types"] = df.submission_types.apply(
        lambda x: ", ".join([str(i) for i in x])
    )

    # Assign boolean values for incentive and submission types.
    df["monetary_incentive"] = df["incentives"].str.contains("Monetary")
    df["publication_incentive"] = df["incentives"].str.contains("Publication")
    df["speaking_incentive"] = df["incentives"].str.contains("Speaking")
    df["file_submission"] = df["submission_types"].str.contains("Prediction")
    df["container_submission"] = df["submission_types"].str.contains("Algorithm")
    return df


def main():
    """Main function."""

    # Results are paginated, so keep calling API until all challenges are
    # returned.
    grand_challenges = pd.DataFrame()
    offset = 0
    with requests.Session() as session:
        res = session.get(
            GC_CHALLENGES_API, params={"offset": offset, "limit": 100}
        ).json()
        while res.get("results"):
            grand_challenges = pd.concat(
                [grand_challenges, pd.json_normalize(res["results"])]
            )
            offset += 100
            res = session.get(
                GC_CHALLENGES_API, params={"limit": 100, "offset": offset}
            ).json()
    cleaned_challenges = process_data(grand_challenges)

    # Output results to CSV in same column order as OC Data sheet.
    oc_data_cols = [
        "title",
        "slug",
        "headline",
        "logo",
        "description",
        "url",
        "doi",
        "platform",
        "start_date",
        "end_date",
        "status",
        "monetary_incentive",
        "publication_incentive",
        "speaking_incentive",
        "other_incentive",
        "file_submission",
        "container_submission",
        "notebook_submission",
        "mlcube_submission",
        "other_submission",
    ]
    cleaned_challenges[oc_data_cols].to_csv("grand-challenges.csv", index=False)


if __name__ == "__main__":
    main()
