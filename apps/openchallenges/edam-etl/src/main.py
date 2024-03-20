import pandas as pd
import requests
from os import getenv
from typing import Optional

# Get config from the environment variables

OC_DB_URL = getenv("OC_DB_URL")
VERSION = getenv("VERSION")
print(f"EDAM Version: {VERSION}")
print(f"OC DB URL: {OC_DB_URL}")


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


def print_info_statistics(df: pd.DataFrame) -> None:
    """Gather data about the EDAM ontology"""
    if df is not None:
        print(f"Number of Concepts Transformed: {len(df)}")
        print(f"Column names: {df.columns.tolist()}")
        print("Statistics:")
        # Set the display options to show only 2 decimal places
        pd.set_option("display.float_format", "{:.0f}".format)
        print(df.describe())
    else:
        print("No data available.")


def main() -> None:
    """Main function to execute preceding functions"""
    url: str = (
        f"https://raw.githubusercontent.com/Sage-Bionetworks/edamontology/main/releases/EDAM_{VERSION}.csv"
    )
    if download_edam_csv(url, VERSION):
        df: pd.DataFrame = transform_to_dataframe(VERSION)
        print_info_statistics(df)


if __name__ == "__main__":
    main()
