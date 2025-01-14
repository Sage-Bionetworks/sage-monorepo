import os
import json

import gspread
import numpy as np
import pandas as pd


GOOGLE_SHEET_CREDENTIALS_FILE = "service_account.json"
GOOGLE_SHEET_TITLE = "OpenChallenges Data"


def lambda_handler(event, context):
    """Sample pure Lambda function

    Parameters
    ----------
    event: dict, required
        API Gateway Lambda Proxy Input Format

        Event doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html#api-gateway-simple-proxy-for-lambda-input-format

    context: object, required
        Lambda Context runtime methods and attributes

        Context doc: https://docs.aws.amazon.com/lambda/latest/dg/python-context-object.html

    Returns
    ------
    API Gateway Lambda Proxy Output Format: dict

        Return doc: https://docs.aws.amazon.com/apigateway/latest/developerguide/set-up-lambda-proxy-integrations.html
    """

    write_credentials_file(event, GOOGLE_SHEET_CREDENTIALS_FILE)

    try:
        google_client = gspread.service_account(filename=GOOGLE_SHEET_CREDENTIALS_FILE)
    except Exception as err:
        message = "Private key not found in the credentials file. Please try again."
    else:
        try:
            wks = google_client.open(GOOGLE_SHEET_TITLE)

            platforms = get_platform_data(wks)
            print(platforms.head())

            roles = get_roles(wks)
            print(roles.head())

            categories = get_challenge_categories(wks)
            print(categories.head())

            organizations = get_organization_data(wks)
            print(organizations.head())

            edam_data_annotations = get_edam_annotations(wks)
            print(edam_data_annotations.head())

            challenges, incentives, sub_types = get_challenge_data(wks)
            print(challenges.head())
            print(incentives.head())
            print(sub_types.head())

            message = "Data successfully pulled from OC Data google sheet."

        except Exception as err:
            message = f"Something went wrong with pulling the data: {err}."

    data = {"message": message}
    return {
        "statusCode": 200,
        "body": json.dumps(data),
    }


def write_credentials_file(template, output_json):
    """Write credentials JSON file for Google Sheets API authentication."""
    with open(output_json, "w") as out:
        credentials = {
            **template.get("google_sheets_api_auth"),
            # "project_id": os.getenv("PROJECT_ID"),
            # "private_key_id": os.getenv("PRIVATE_KEY_ID"),
            # "private_key": os.getenv("PRIVATE_KEY"),
            # "client_email": os.getenv("client_email"),
            # "client_id": os.getenv("CLIENT_ID"),
            # "client_x509_cert_url": os.getenv("CLIENT_X509_CERT_URL")
        }
        out.write(json.dumps(credentials))


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
        .replace("\u2019", "''", regex=True)  # replace curly right-quote
        .replace("\u202f", " ", regex=True)  # replace narrow no-break space
        .replace("\u2060", "", regex=True)  # remove word joiner
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
        categories=[
            "prediction_file",
            "container_image",
            "notebook",
            "mlcube",
            "other",
        ],
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
        .replace("\u2019", "''", regex=True)  # replace curly right-quote
        .replace("\u202f", " ", regex=True)  # replace narrow no-break space
        .replace("\u2060", "", regex=True)  # remove word joiner
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
