"""Extract, transform and load EDAM concepts"""

from os import getenv

# Get config from the environment variables
oc_db_url = getenv("OC_DB_URL")
print(f"OC DB URL: {oc_db_url}")

# TODO Download EDAM concepts from GitHub or S3 bucket (CSV file)
print("[TODO] Download EDAM concepts from GitHub or S3 bucket (CSV file)")

# TODO Process the EDAM concepts
print("[TODO] Process the EDAM concepts")

# TODO Push the EDAM concept to the OC challenge service DB
print("[TODO] Push the EDAM concept to the OC challenge service DB")
