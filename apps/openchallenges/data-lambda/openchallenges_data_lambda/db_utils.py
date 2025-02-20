import os
import sys
import logging

import pymysql
import pymysql.cursors
import pandas as pd


def connect_to_db(db: str = "challenge_service") -> pymysql.Connection:
    """Establishes connection to the MariaDB database."""
    credentials = {
        "host": os.getenv("MARIADB_HOST"),
        "port": int(os.getenv("MARIADB_PORT", 3306)),
        "user": os.getenv("MARIADB_USER"),
        "password": os.getenv("MARIADB_PASSWORD"),
        "database": db,
        "cursorclass": pymysql.cursors.DictCursor,
    }
    try:
        conn = pymysql.connect(**credentials)
        logging.info(f"Connected to `{db}` database")
        return conn
    except pymysql.Error as err:
        logging.error(f"Error connecting to the database: {err}")
        sys.exit(1)


def get_table(conn: pymysql.Connection, table_name: str) -> pd.DataFrame:
    """Returns all records from the specified table."""
    query = f"SELECT * FROM {table_name}"
    try:
        with conn.cursor() as cursor:
            cursor.execute(query)
            records = cursor.fetchall()
            colnames = [val[0] for val in cursor.description]
        return pd.DataFrame(records, columns=colnames)
    except pymysql.Error as err:
        logging.error(f"Error executing query: {err}")
        return pd.DataFrame()


def truncate_table(conn: pymysql.Connection, table_name: str):
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
    except pymysql.Error as err:
        logging.error(f"Error truncating: {err}")
        conn.rollback()  # Revert any changes made to data.


def insert_data(conn: pymysql.Connection, table_name: str, data_df: pd.DataFrame):
    """Adds data to the specified table, one row at a time.

    This iterative approach allows for logging invalid rows for later review.
    """
    logging.info(f"Adding data to table `{table_name}`")
    with conn.cursor() as cursor:
        for _, row in data_df.iterrows():
            colnames = ", ".join(row.index)
            placeholders = ", ".join(["%s"] * len(row))
            query = f"INSERT INTO {table_name} ({colnames}) VALUES ({placeholders})"
            try:
                cursor.execute(query, tuple(row))
                conn.commit()
            except (pymysql.IntegrityError, pymysql.DataError) as err:
                id_colname = "id" if row.get("id") else "challenge_id"
                id_value = row.get("id", row.get("challenge_id"))
                logging.error(
                    f"Invalid row to table `{table_name}`\n"
                    + f"   → {id_colname} in Google Sheet: {id_value}\n"
                    + f"   → Error: {err}"
                )
                conn.rollback()
            except pymysql.Error as err:
                logging.error(f"Error adding row to table `{table_name}`: {err}")
                conn.rollback()


def update_table(conn: pymysql.Connection, table_name: str, data: pd.DataFrame):
    """Updates the specified table."""
    truncate_table(conn, table_name)
    insert_data(conn, table_name, data)
