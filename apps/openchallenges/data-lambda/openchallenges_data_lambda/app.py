import os
import json
import logging

import gspread

import db_utils
import oc_data_sheet


GOOGLE_SHEET_CREDENTIALS_FILE = "service_account.json"
GOOGLE_SHEET_TITLE = "OpenChallenges Data"


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


def lambda_handler(event, context) -> dict:
    """Main function.

    Pulls data from the OC Data Google sheet (https://shorturl.at/pf3Mr) and "syncs" the data
    to the OpenChallenges database.

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
        status_code = 401
        message = "Private key not found in the credentials file. Please try again."
    else:
        try:
            wks = google_client.open(GOOGLE_SHEET_TITLE)

            platforms = oc_data_sheet.get_platform_data(wks)
            platforms["avatar_url"] = (
                ""  # FIXME: table has this column for some reason?
            )

            roles = oc_data_sheet.get_roles(wks)
            categories = oc_data_sheet.get_challenge_categories(wks)
            organizations = oc_data_sheet.get_organization_data(wks)
            edam_data_annotations = oc_data_sheet.get_edam_annotations(wks)
            challenges, incentives, sub_types = oc_data_sheet.get_challenge_data(wks)
        except Exception as err:
            status_code = 400
            message = f"Something went wrong with pulling the data: {err}."

    # output logs to stdout and logfile
    logging.basicConfig(
        level=logging.DEBUG,
        format="%(levelname)s | %(asctime)s | %(message)s",
        handlers=[
            logging.FileHandler("oc_database_update.log"),
            logging.StreamHandler(),
        ],
    )

    # Update challenge_service
    conn = db_utils.connect_to_db()
    db_utils.update_table(conn, table_name="challenge_platform", data=platforms)
    db_utils.update_table(conn, table_name="challenge", data=challenges)
    db_utils.update_table(conn, table_name="challenge_contribution", data=roles)
    db_utils.update_table(conn, table_name="challenge_incentive", data=incentives)
    db_utils.update_table(conn, table_name="challenge_submission_type", data=sub_types)
    db_utils.update_table(
        conn, table_name="challenge_input_data_type", data=edam_data_annotations
    )
    db_utils.update_table(conn, table_name="challenge_category", data=categories)
    conn.close()

    # Update organization_service
    conn = db_utils.connect_to_db("organization_service")
    db_utils.update_table(conn, table_name="organization", data=organizations)
    db_utils.update_table(conn, table_name="challenge_contribution", data=roles)
    conn.close()

    logging.info("FIN. ✅")
    status_code = 200
    message = "Data from the OC Data Sheet successfully added to the database."

    data = {"message": message}
    return {
        "statusCode": status_code,
        "body": json.dumps(data),
    }


if __name__ == "__main__":
    lambda_handler({}, "")
