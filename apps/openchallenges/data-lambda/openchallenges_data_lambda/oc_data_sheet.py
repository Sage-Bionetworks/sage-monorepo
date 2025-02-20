import numpy as np
import pandas as pd


def _reformat_df_values(df: pd.DataFrame) -> pd.DataFrame:
    df = (
        df.replace({r"\s+$": "", r"^\s+": ""}, regex=True)
        .replace(r"\n", " ", regex=True)
        .replace("'", "''")
        .replace("\u2019", "''", regex=True)  # replace curly right-quote
        .replace("\u202f", " ", regex=True)  # replace narrow no-break space
        .replace("\u2060", "", regex=True)  # remove word joiner
    )
    return df


def get_challenge_data(wks, sheet_name: str = "challenges") -> tuple:
    """Get challenges data and clean up as needed.

    Output:
        - challenges
        - challenge incentives
        - challenge submission types
    """
    df = pd.DataFrame(wks.worksheet(sheet_name).get_all_records()).fillna("")

    # Challenges
    challenges = df[
        [
            "id",
            "slug",
            "name",
            "headline",
            "description",
            "avatar_url",
            "website_url",
            "status",
            "_platform",
            "platform",
            "doi",
            "start_date",
            "end_date",
            "operation_id",
            "created_at",
            "updated_at",
        ]
    ]
    challenges = _reformat_df_values(challenges)
    challenges["headline"] = (
        challenges["headline"]
        .astype(str)
        .apply(lambda x: x[:75] + "..." if len(x) > 80 else x)
    )
    challenges["description"] = (
        challenges["description"]
        .astype(str)
        .apply(lambda x: x[:995] + "..." if len(x) > 1000 else x)
    )
    challenges.loc[challenges._platform == "Other", "platform"] = None
    challenges.loc[challenges.start_date == "", "start_date"] = None
    challenges.loc[challenges.end_date == "", "end_date"] = None
    challenges.loc[challenges.operation_id == "", "operation_id"] = None

    # Challenge incentive(s)
    incentives = pd.concat(
        [
            df[df.monetary_incentive == "TRUE"][["id", "created_at"]].assign(
                name="monetary"
            ),
            df[df.publication_incentive == "TRUE"][["id", "created_at"]].assign(
                name="publication"
            ),
            df[df.speaking_incentive == "TRUE"][["id", "created_at"]].assign(
                name="speaking_engagement"
            ),
            df[df.other_incentive == "TRUE"][["id", "created_at"]].assign(name="other"),
        ]
    ).rename(columns={"id": "challenge_id"})
    incentives["name"] = pd.Categorical(
        incentives["name"],
        categories=["monetary", "publication", "speaking_engagement", "other"],
    )
    incentives = incentives.sort_values(["challenge_id", "name"])
    incentives.index = np.arange(1, len(incentives) + 1)

    # Challenge submission type(s)
    sub_types = pd.concat(
        [
            df[df.file_submission == "TRUE"][["id", "created_at"]].assign(
                name="prediction_file"
            ),
            df[df.container_submission == "TRUE"][["id", "created_at"]].assign(
                name="container_image"
            ),
            df[df.notebook_submission == "TRUE"][["id", "created_at"]].assign(
                name="notebook"
            ),
            df[df.mlcube_submission == "TRUE"][["id", "created_at"]].assign(
                name="mlcube"
            ),
            df[df.other_submission == "TRUE"][["id", "created_at"]].assign(
                name="other"
            ),
        ]
    ).rename(columns={"id": "challenge_id"})
    sub_types["name"] = pd.Categorical(
        sub_types["name"],
        categories=[
            "prediction_file",
            "container_image",
            "notebook",
            "mlcube",
            "other",
        ],
    )
    sub_types = sub_types.sort_values(["challenge_id", "name"])
    sub_types.index = np.arange(1, len(sub_types) + 1)

    return (
        challenges.rename(columns={"platform": "platform_id"}).drop(
            columns=["_platform"]
        ),
        incentives[["name", "challenge_id", "created_at"]],
        sub_types[["name", "challenge_id", "created_at"]],
    )


def get_challenge_categories(
    wks, sheet_name: str = "challenge_category"
) -> pd.DataFrame:
    """Get challenge categories."""
    return (
        pd.DataFrame(wks.worksheet(sheet_name).get_all_records())
        .fillna("")
        .rename(columns={"category": "name"})[["id", "challenge_id", "name"]]
    )


def get_platform_data(wks, sheet_name: str = "platforms") -> pd.DataFrame:
    """Get platform data and clean up as needed."""
    platforms = (
        pd.DataFrame(wks.worksheet(sheet_name).get_all_records())
        .fillna("")
        .rename(columns={"avatar_url": "avatar_key"})
    )
    return platforms[platforms._public == "TRUE"][
        ["id", "slug", "name", "avatar_key", "website_url", "created_at", "updated_at"]
    ]


def get_organization_data(wks, sheet_name: str = "organizations") -> pd.DataFrame:
    """Get organization data and clean up as needed."""
    organizations = pd.DataFrame(wks.worksheet(sheet_name).get_all_records()).fillna("")
    organizations = organizations[organizations._public == "TRUE"][
        [
            "id",
            "name",
            "login",
            "avatar_url",
            "website_url",
            "description",
            "challenge_count",
            "created_at",
            "updated_at",
            "acronym",
        ]
    ]
    organizations = _reformat_df_values(organizations)
    organizations["description"] = (
        organizations["description"]
        .astype(str)
        .apply(lambda x: x[:995] + "..." if len(x) > 1000 else x)
    )
    return organizations.rename(columns={"avatar_url": "avatar_key"})


def get_roles(wks, sheet_name: str = "contribution_role") -> pd.DataFrame:
    """Get data on organization's role(s) in challenges."""
    return (
        pd.DataFrame(wks.worksheet(sheet_name).get_all_records())
        .fillna("")
        .drop(["_challenge", "_organization"], axis=1)
    )


def get_edam_annotations(wks, sheet_name: str = "challenge_data") -> pd.DataFrame:
    """Get data on challenge's EDAM annotations."""
    return (
        pd.DataFrame(wks.worksheet(sheet_name).get_all_records())
        .fillna("")
        .drop(["_challenge", "_edam_name"], axis=1)
        .rename(columns={"edam_id": "edam_concept_id"})
    )
