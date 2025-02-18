import os
import sys
import json
import logging

import gspread
import mariadb
import pandas as pd

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


def connect_to_db(db: str = "challenge_service") -> mariadb.Connection:
    """Establishes connection to the MariaDB database."""
    credentials = {
        "host": os.getenv("MARIADB_HOST"),
        "port": int(os.getenv("MARIADB_PORT", 3306)),
        "user": os.getenv("MARIADB_USER"),
        "password": os.getenv("MARIADB_PASSWORD"),
        "database": db,
    }
    try:
        conn = mariadb.connect(**credentials)
        logging.info(f"Connected to `{db}` database")
        return conn
    except mariadb.Error as err:
        logging.error(f"Error connecting to the database: {err}")
        sys.exit(1)


def get_table(conn: mariadb.Connection, table_name: str) -> pd.DataFrame:
    """Returns all records from the specified table."""
    query = f"SELECT * FROM {table_name}"
    try:
        with conn.cursor() as cursor:
            cursor.execute(query)
            records = cursor.fetchall()
            colnames = [val[0] for val in cursor.description]
        return pd.DataFrame(records, columns=colnames)
    except mariadb.Error as err:
        logging.error(f"Error executing query: {err}")
        return pd.DataFrame()


def truncate_table(conn: mariadb.Connection, table_name: str):
    """Deletes all rows from the specified table.

    Temporarily disables foreign key checks for this operation.
    """
    logging.info(f"Truncating table `{table_name}`")
    try:
        with conn.cursor() as cursor:
            cursor.execute("SET FOREIGN_KEY_CHECKS = 0")
            cursor.execute(f"TRUNCATE TABLE {table_name}")
            cursor.execute("SET FOREIGN_KEY_CHECKS = 1")
            conn.commit()  # Save changes made to table.
    except mariadb.Error as err:
        logging.error(f"Error truncating: {err}")
        conn.rollback()  # Revert any changes made to data.


def insert_data(conn: mariadb.Connection, table_name: str, data_df: pd.DataFrame):
    """Adds data to the specified table, one row at a time.

    This iterative approach allows for logging invalid rows for later review.
    """
    logging.info(f"Adding data to table `{table_name}`")
    with conn.cursor() as cursor:
        for _, row in data_df.iterrows():
            colnames = ", ".join(row.index)
            placeholders = ", ".join(["?"] * len(row))
            query = f"INSERT INTO {table_name} ({colnames}) VALUES ({placeholders})"
            try:
                cursor.execute(query, tuple(row))
                conn.commit()
            except (mariadb.IntegrityError, mariadb.DataError) as err:
                id_colname = "id" if row.get("id") else "challenge_id"
                id_value = row.get("id", row.get("challenge_id"))
                logging.error(
                    f"Invalid row to table `{table_name}`\n"
                    + f"   → {id_colname} in Google Sheet: {id_value}\n"
                    + f"   → Error: {err}"
                )
                conn.rollback()
            except mariadb.Error as err:
                logging.error(f"Error adding row to table `{table_name}`: {err}")
                conn.rollback()


def update_table(conn: mariadb.Connection, table_name: str, data: pd.DataFrame):
    """Updates the specified table."""
    truncate_table(conn, table_name)
    insert_data(conn, table_name, data)


def lambda_handler(event, context) -> dict:
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
            # organizations = oc_data_sheet.get_organization_data(wks)
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
    conn = connect_to_db()
    update_table(conn, table_name="challenge_platform", data=platforms)
    update_table(conn, table_name="challenge", data=challenges)
    update_table(conn, table_name="challenge_contribution", data=roles)
    update_table(conn, table_name="challenge_incentive", data=incentives)
    update_table(conn, table_name="challenge_submission_type", data=sub_types)
    update_table(
        conn, table_name="challenge_input_data_type", data=edam_data_annotations
    )
    update_table(conn, table_name="challenge_category", data=categories)
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
