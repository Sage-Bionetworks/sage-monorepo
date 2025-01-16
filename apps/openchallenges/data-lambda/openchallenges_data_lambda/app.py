import os
import json

import gspread

import oc_data_sheet


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

    write_credentials_file(GOOGLE_SHEET_CREDENTIALS_FILE)

    try:
        google_client = gspread.service_account(filename=GOOGLE_SHEET_CREDENTIALS_FILE)
    except Exception as err:
        message = "Private key not found in the credentials file. Please try again."
        status_code = 401
    else:
        try:
            wks = google_client.open(GOOGLE_SHEET_TITLE)

            platforms = oc_data_sheet.get_platform_data(wks)
            print(platforms.head())

            roles = oc_data_sheet.get_roles(wks)
            print(roles.head())

            categories = oc_data_sheet.get_challenge_categories(wks)
            print(categories.head())

            organizations = oc_data_sheet.get_organization_data(wks)
            print(organizations.head())

            edam_data_annotations = oc_data_sheet.get_edam_annotations(wks)
            print(edam_data_annotations.head())

            challenges, incentives, sub_types = oc_data_sheet.get_challenge_data(wks)
            print(challenges.head())
            print(incentives.head())
            print(sub_types.head())

            status_code = 200
            message = "Data successfully pulled from OC Data google sheet."

        except Exception as err:
            status_code = 400
            message = f"Something went wrong with pulling the data: {err}."

    data = {"message": message}
    return {
        "statusCode": status_code,
        "body": json.dumps(data),
    }


def write_credentials_file(output_json):
    """Write credentials JSON file for Google Sheets API authentication."""
    with open(output_json, "w") as out:
        credentials = {
            "type": os.getenv("TYPE"),
            "project_id": os.getenv("PROJECT_ID"),
            "private_key_id": os.getenv("PRIVATE_KEY_ID"),
            "private_key": os.getenv("PRIVATE_KEY", "")
            .encode()
            .decode("unicode_escape"),
            "client_email": os.getenv("CLIENT_EMAIL"),
            "client_id": os.getenv("CLIENT_ID"),
            "auth_uri": os.getenv("AUTH_URI"),
            "token_uri": os.getenv("TOKEN_URI"),
            "auth_provider_x509_cert_url": os.getenv("AUTH_PROVIDER_X509_CERT_URL"),
            "client_x509_cert_url": os.getenv("CLIENT_X509_CERT_URL"),
            "universe_domain": os.getenv("UNIVERSE_DOMAIN"),
        }
        out.write(json.dumps(credentials))


if __name__ == "__main__":
    lambda_handler({}, "")
