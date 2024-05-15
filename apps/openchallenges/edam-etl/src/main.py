import numpy as np
import pandas as pd
import requests
from os import getenv
from typing import Optional
import mariadb 
import sys
import mysql.connector
from sqlalchemy import create_engine

# Get config from the environment variables

OC_DB_URL = getenv("OC_DB_URL")
VERSION = getenv("VERSION")
print(f"EDAM Version: {VERSION}")
print(f"OC DB URL: {OC_DB_URL}")

# Intialize required connection variables from environment variables

USERNAME = getenv("USERNAME")
PASSWORD = getenv("PASSWORD")
PORT = getenv("PORT")
DB = getenv("DB")
HOST = getenv("HOST")

def connect_to_mariadb(username: str, password: str, port: str, host: str, database: str, df: pd.DataFrame) -> None:
    """Connect to the MariaDB database"""
    try:
        conn = mariadb.connect(
            user = username,
            password = password,
            host = host,
            port = int(port),
            database = database
        )
        print("Establishing a connection to the MariaDB Platform.")
        
        # Get the cursor
        cur = conn.cursor()
        print("Connection has been established to MariaDB Platform!")

        # Commit the transaction
        conn.commit()
        print("The table edam_etl has been added to the edam database!")

        # Create a SQLAlchemy engine from the MySQL Connector connection
        engine = create_engine(f'mysql+mysqlconnector://{username}:{password}@{host}/{database}')

        # Drop the table if it exists
        cur.execute("DROP TABLE IF EXISTS edam_etl")

        # Create the table with columns
        cur.execute("""
            CREATE TABLE edam_etl (
                id INT PRIMARY KEY,
                class_id VARCHAR(255),
                preferred_label VARCHAR(255)
            )
        """)

        #Load the concepts
        df.to_sql(
            name = "edam_etl",
            con = engine,
            if_exists = "append",
            index = False
        )

        print("The table edam_etl has been populated with the EDAM concepts!")

        # Close the connection
        conn.close()
    
    except mariadb.Error as e:
        print(f"Error connecting to MariaDB Platform: {e}")
        sys.exit(1)
    
def download_edam_csv(url: str, version: str) -> Optional[bool]:
    """Download EDAM concepts from GitHub or S3 bucket (CSV file)"""
    print("Downloading the EDAM concepts from GitHub (CSV file)...")
    try:
        response = requests.get(url)
        response.raise_for_status()  # Raise an exception for bad response
        with open(f"EDAM_{version}.csv", "wb") as f:
            f.write(response.content)
        print("EDAM concepts downloaded successfully.")
        return True
    except requests.RequestException as e:
        print(f"Error downloading EDAM concepts: {e}")
        return None


def transform_to_dataframe(version: str) -> pd.DataFrame | None:
    """Transform the CSV to a DataFrame with indices starting from 1"""
    print("Processing the EDAM concepts...")
    try:
        df = (
            pd.read_csv(f"EDAM_{version}.csv", usecols=["Class ID", "Preferred Label"])
            .rename(
                columns={"Class ID": "class_id", "Preferred Label": "preferred_label"}
            )
            .reset_index(drop=True)
            .reset_index(drop=False)
            .rename(columns={"index": "id"})
        )
        print("EDAM concepts processed successfully.")
        return df
    except FileNotFoundError as e:
        print(f"Error processing EDAM concepts: {e}")
        return None


def count_occurrences(identifier_pattern: str, column: pd.Series) -> np.int64:
    """Count the number of pattern occurrences"""
    return column.str.contains(
        identifier_pattern, case=False, na=False, regex=True
    ).sum()


def print_info_statistics(df: pd.DataFrame) -> None:
    """Gather data about the EDAM ontology"""
    if df is not None:
        print(f"Number of Concepts Transformed: {len(df)}")
        print(f"Column names: {df.columns.tolist()}")

        # Create regex patterns for each concept
        data_pattern = r"/data_\d+$"
        operation_pattern = r"/operation_\d+$"
        format_pattern = r"/format_\d+$"
        topic_pattern = r"/topic_\d+$"
        identifier_pattern = r"/identifier_\d+$"
        concept_column = df["class_id"]

        # Use pandas' vectorized string operations to count occurrences
        data_count = count_occurrences(data_pattern, concept_column)
        operation_count = count_occurrences(operation_pattern, concept_column)
        format_count = count_occurrences(format_pattern, concept_column)
        topic_count = count_occurrences(topic_pattern, concept_column)
        identifier_count = count_occurrences(identifier_pattern, concept_column)

        # Calculate 'other' count by subtracting the specific counts from the total
        other_count = len(df) - (
            data_count + operation_count + format_count + identifier_count + topic_count
        )

        # Print counts of each concept
        print("Concept Counts:")
        print(f"Data: {data_count}")
        print(f"Operation: {operation_count}")
        print(f"Format: {format_count}")
        print(f"Topic: {format_count}")
        print(f"Identifier: {format_count}")
        print(f"Other: {other_count}")

    else:
        print("No data available.")

# def load_edam_dataframe(df: pd.DataFrame) -> None:


def main() -> None:
    """Main function to execute preceding functions"""
    url: str = (
        f"https://raw.githubusercontent.com/Sage-Bionetworks/edamontology/main/releases/EDAM_{VERSION}.csv"
    )
    if download_edam_csv(url, VERSION):
        df: pd.DataFrame = transform_to_dataframe(VERSION)
        print_info_statistics(df)
    connect_to_mariadb(USERNAME, PASSWORD, PORT, HOST, DB, df)


if __name__ == "__main__":
    main()
