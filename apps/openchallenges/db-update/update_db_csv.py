import os
import re
import csv
import gspread
import numpy as np
import pandas as pd

GOOGLE_SHEET_TITLE = "OpenChallenges Data"
CHALLENGE_FOLDER = "apps/openchallenges/challenge-service/src/main/resources/db"
ORGANIZATION_FOLDER = "apps/openchallenges/organization-service/src/main/resources/db"
PLOT_FILE = (
    "apps/openchallenges/challenge-service/src/main/java/org/sagebionetworks/"
    "openchallenges/challenge/service/service/ChallengeAnalyticsService.java"
)
UPDATE_MARKER = r"(\/\* AUTO-UPDATE MARKER \*\/\s+)"  # capture as a group to preserve


def output_csv(df, output_filename, output_folder="", print_row=False):
    """Output a dataframe into CSV file.

    CSV file should not include index numbers and all values should
    be surrounded with double-quotes.
    """
    df.to_csv(
        os.path.join(output_folder, output_filename),
        index=print_row,
        quoting=csv.QUOTE_ALL,
    )


def get_challenge_data(wks, sheet_name="challenges"):
    """Get challenges data and clean up as needed.

    Output:
        - challenges
        - challenge incentives
        - challenge submission types
    """
    df = pd.DataFrame(wks.worksheet(sheet_name).get_all_records()).fillna("")
    df.loc[df._platform == "Other", "platform"] = "\\N"

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
            "platform",
            "doi",
            "start_date",
            "end_date",
            "operation_id",
            "created_at",
            "updated_at",
        ]
    ]
    challenges = (
        challenges.replace({r"\s+$": "", r"^\s+": ""}, regex=True)
        .replace(r"\n", " ", regex=True)
        .replace("'", "''")
        .replace(u"\u2019", "''")  # replace curly right-quote 
        .replace(u"\u202f", " ")  # replace narrow no-break space
        .replace(u"\u2060", "")  # remove word joiner
    )
    challenges["headline"] = (
        challenges["headline"]
        .astype(str)
        .apply(lambda x: x[:76] + "..." if len(x) > 80 else x)
    )
    challenges["description"] = (
        challenges["description"]
        .astype(str)
        .apply(lambda x: x[:995] + "..." if len(x) > 1000 else x)
    )
    challenges.loc[challenges.start_date == "", "start_date"] = "\\N"
    challenges.loc[challenges.end_date == "", "end_date"] = "\\N"
    challenges.loc[challenges.operation_id == "", "operation_id"] = "\\N"

    incentives = pd.concat(
        [
            df[df.monetary_incentive == "TRUE"][["id", "created_at"]].assign(
                incentives="monetary"
            ),
            df[df.publication_incentive == "TRUE"][["id", "created_at"]].assign(
                incentives="publication"
            ),
            df[df.speaking_incentive == "TRUE"][["id", "created_at"]].assign(
                incentives="speaking_engagement"
            ),
            df[df.other_incentive == "TRUE"][["id", "created_at"]].assign(
                incentives="other"
            ),
        ]
    ).rename(columns={"id": "challenge_id"})
    incentives["incentives"] = pd.Categorical(
        incentives["incentives"],
        categories=["monetary", "publication", "speaking_engagement", "other"],
    )
    incentives = incentives.sort_values(["challenge_id", "incentives"])
    incentives.index = np.arange(1, len(incentives) + 1)

    sub_types = pd.concat(
        [
            df[df.file_submission == "TRUE"][["id", "created_at"]].assign(
                submission_types="prediction_file"
            ),
            df[df.container_submission == "TRUE"][["id", "created_at"]].assign(
                submission_types="container_image"
            ),
            df[df.notebook_submission == "TRUE"][["id", "created_at"]].assign(
                submission_types="notebook"
            ),
            df[df.mlcube_submission == "TRUE"][["id", "created_at"]].assign(
                submission_types="mlcube"
            ),
            df[df.other_submission == "TRUE"][["id", "created_at"]].assign(
                submission_types="other"
            ),
        ]
    ).rename(columns={"id": "challenge_id"})
    sub_types["submission_types"] = pd.Categorical(
        sub_types["submission_types"],
        categories=["prediction_file", "container_image", "notebook", "mlcube", "other"],
    )
    sub_types = sub_types.sort_values(["challenge_id", "submission_types"])
    sub_types.index = np.arange(1, len(sub_types) + 1)

    return (
        challenges,
        incentives[["incentives", "challenge_id", "created_at"]],
        sub_types[["submission_types", "challenge_id", "created_at"]],
    )


def get_challenge_categories(wks, sheet_name="challenge_category"):
    """Get challenge categories."""
    return pd.DataFrame(wks.worksheet(sheet_name).get_all_records()).fillna("")[
        ["id", "challenge_id", "category"]
    ]


def get_platform_data(wks, sheet_name="platforms"):
    """Get platform data and clean up as needed."""
    platforms = pd.DataFrame(wks.worksheet(sheet_name).get_all_records()).fillna("")
    return platforms[platforms._public == "TRUE"][
        ["id", "slug", "name", "avatar_url", "website_url", "created_at", "updated_at"]
    ]


def get_organization_data(wks, sheet_name="organizations"):
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
    organizations = (
        organizations.replace({r"\s+$": "", r"^\s+": ""}, regex=True)
        .replace(r"\n", " ", regex=True)
        .replace("'", "''")
        .replace(u"\u2019", "''")  # replace curly right-quote 
        .replace(u"\u202f", " ")  # replace narrow no-break space
        .replace(u"\u2060", "")  # remove word joiner
    )
    organizations["description"] = (
        organizations["description"]
        .astype(str)
        .apply(lambda x: x[:995] + "..." if len(x) > 1000 else x)
    )
    return organizations


def get_roles(wks, sheet_name="contribution_role"):
    """Get data on organization's role(s) in challenges."""
    return (
        pd.DataFrame(wks.worksheet(sheet_name).get_all_records())
        .fillna("")
        .drop(["_challenge", "_organization"], axis=1)
    )


def get_edam_annotations(wks, sheet_name="challenge_data"):
    """Get data on challenge's EDAM annotations."""
    return (
        pd.DataFrame(wks.worksheet(sheet_name).get_all_records())
        .fillna("")
        .drop(["_challenge", "_edam_name"], axis=1)
    )


def main(gc):
    """Main function."""
    wks = gc.open(GOOGLE_SHEET_TITLE)

    platforms = get_platform_data(wks)
    output_csv(platforms, "platforms.csv", output_folder=CHALLENGE_FOLDER)

    roles = get_roles(wks)
    output_csv(roles, "contribution_roles.csv", output_folder=CHALLENGE_FOLDER)
    output_csv(roles, "contribution_roles.csv", output_folder=ORGANIZATION_FOLDER)

    categories = get_challenge_categories(wks)
    output_csv(categories, "categories.csv", output_folder=CHALLENGE_FOLDER)

    edam_annotations = get_edam_annotations(wks)
    output_csv(edam_annotations, "challenge_data_edam.csv", output_folder=CHALLENGE_FOLDER)

    organizations = get_organization_data(wks)
    output_csv(organizations, "organizations.csv", output_folder=ORGANIZATION_FOLDER)

    challenges, incentives, sub_types = get_challenge_data(wks)
    output_csv(challenges, "challenges.csv", output_folder=CHALLENGE_FOLDER)
    output_csv(incentives, "incentives.csv", 
               output_folder=CHALLENGE_FOLDER, print_row=True)
    output_csv(sub_types, "submission_types.csv",
               output_folder=CHALLENGE_FOLDER, print_row=True)

    # Update static plot numbers (displayed on homepage).
    updated_plot_numbers = wks.worksheet("(plot) challenges_by_year").acell("D3").value
    with open(PLOT_FILE, "r", encoding="utf-8") as file:
        curr_content = file.read()
    updated_content = re.sub(
        UPDATE_MARKER + r"Arrays.asList\([\d, ]+\);",
        r"\1" + updated_plot_numbers,  # replace but keep reserved group (\1)
        curr_content,
    )
    with open(PLOT_FILE, "w", encoding="utf-8") as file:
        file.write(updated_content)


if __name__ == "__main__":
    google_client = gspread.service_account(filename="service_account.json")
    main(google_client)
