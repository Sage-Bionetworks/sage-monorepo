import logging
import os
from concurrent.futures import ThreadPoolExecutor, as_completed
from datetime import datetime
from itertools import chain
from typing import Any, Optional

import kaggle
from opensearchpy import OpenSearch

# Configure logging
LOG_LEVEL = os.getenv("LOG_LEVEL", "INFO").upper()
logging.basicConfig(
    level=getattr(logging, LOG_LEVEL, logging.INFO),
    format="%(asctime)s - %(name)s - %(levelname)s - %(message)s",
)
logger = logging.getLogger(__name__)


def fetch_competitions_for_term(search_term: str) -> tuple[str, list[Any]]:
    """
    Fetch competitions for a single search term.

    Args:
        search_term: The search term to query

    Returns:
        Tuple containing the search term and list of competitions

    Raises:
        Exception: If the API call fails
    """
    api = kaggle.api
    try:
        competitions = api.competitions_list(search=search_term)
        count = len(competitions) if competitions else 0
        logger.info(f"Found {count} competitions for '{search_term}'")
        return search_term, competitions or []
    except Exception as e:
        logger.error(f"Error searching for '{search_term}': {e}")
        return search_term, []


def get_competition_id(competition: Any) -> Optional[str]:  # noqa: UP045
    """
    Extract a unique identifier from a competition object.

    Args:
        competition: Kaggle competition object

    Returns:
        Competition ID if found, None otherwise
    """
    return (
        getattr(competition, "ref", None)
        or getattr(competition, "id", None)
        or getattr(competition, "url", None)
    )


def get_opensearch_client() -> OpenSearch:
    """
    Get OpenSearch client configured with environment variables.

    Returns:
        OpenSearch client instance

    Raises:
        ValueError: If required environment variables are not set
    """
    host = os.getenv("OC_OPENSEARCH_HOST")
    if not host:
        raise ValueError("OC_OPENSEARCH_HOST environment variable is required")

    # Parse host and port
    if ":" in host:
        host_name, port = host.split(":", 1)
        port = int(port)
    else:
        host_name = host
        port = 9200

    client = OpenSearch(
        hosts=[{"host": host_name, "port": port}],
        use_ssl=False,
        verify_certs=False,
        connection_class=None,
    )

    return client


def get_index_name() -> str:
    """
    Get the OpenSearch index name using environment variables and current date.

    Returns:
        Index name in format: {prefix}YYYYMMDD

    Raises:
        ValueError: If OC_OPENSEARCH_INDEX_PREFIX is not set
    """
    prefix = os.getenv("OC_OPENSEARCH_INDEX_PREFIX")
    if not prefix:
        raise ValueError("OC_OPENSEARCH_INDEX_PREFIX environment variable is required")

    current_date = datetime.now().strftime("%Y%m%d")
    return f"{prefix}{current_date}"


def prepare_competition_document(
    competition: Any, competition_id: str
) -> dict[str, Any]:
    """
    Prepare a Kaggle competition for indexing in OpenSearch.

    Args:
        competition: Kaggle competition object
        competition_id: Competition identifier

    Returns:
        Dictionary ready for OpenSearch indexing
    """

    # Helper function to safely extract and serialize complex objects
    def safe_extract(obj, attr: str, default=None):
        value = getattr(obj, attr, default)
        if value is None:
            return default

        # Special handling for tags - extract just the names
        if attr == "tags" and isinstance(value, list):
            tag_names = []
            for item in value:
                if hasattr(item, "name"):
                    tag_names.append(str(getattr(item, "name", "")))
                else:
                    tag_names.append(str(item))
            return tag_names

        # Handle other lists of objects
        if isinstance(value, list):
            serialized_list = []
            for item in value:
                if hasattr(item, "__dict__"):  # Complex object
                    # Convert complex objects to string
                    serialized_list.append(str(item))
                else:
                    serialized_list.append(item)
            return serialized_list

        # Handle single complex objects - convert to string
        if hasattr(value, "__dict__"):
            return str(value)

        return value

    # Extract common fields from competition object
    doc = {
        "competition_id": competition_id,
        "title": safe_extract(competition, "title", ""),
        "description": safe_extract(competition, "description", ""),
        "url": safe_extract(competition, "url", ""),
        "ref": safe_extract(competition, "ref", ""),
        "tags": safe_extract(competition, "tags", []),
        "category": safe_extract(competition, "category", ""),
        "organizationName": safe_extract(competition, "organizationName", ""),
        "organizationRef": safe_extract(competition, "organizationRef", ""),
        "enabledDate": safe_extract(competition, "enabledDate", ""),
        "deadlineDate": safe_extract(competition, "deadlineDate", ""),
        "rewardType": safe_extract(competition, "rewardType", ""),
        "totalTeams": safe_extract(competition, "totalTeams", 0),
        "userHasEntered": safe_extract(competition, "userHasEntered", False),
        "userRank": safe_extract(competition, "userRank", None),
        "mergerDeadline": safe_extract(competition, "mergerDeadline", ""),
        "newEntrantDeadline": safe_extract(competition, "newEntrantDeadline", ""),
        "submissionDeadline": safe_extract(competition, "submissionDeadline", ""),
        "teamMergerDeadline": safe_extract(competition, "teamMergerDeadline", ""),
        "indexed_at": datetime.now().isoformat(),
    }

    # Debug logging for tags
    if doc["tags"]:
        logger.debug(f"Tags for {competition_id}: {doc['tags']}")
        logger.debug(f"Tags type: {type(doc['tags'])}")
        if isinstance(doc["tags"], list) and len(doc["tags"]) > 0:
            logger.debug(f"First tag type: {type(doc['tags'][0])}")

    # Remove None values and empty strings for cleaner indexing
    return {k: v for k, v in doc.items() if v is not None and v != ""}


def index_competitions_to_opensearch(competitions: dict[str, Any]) -> None:
    """
    Index competitions to OpenSearch.

    Args:
        competitions: Dictionary of competitions with competition_id as key

    Raises:
        Exception: If indexing fails
    """
    if not competitions:
        logger.warning("No competitions to index")
        return

    try:
        client = get_opensearch_client()
        index_name = get_index_name()

        logger.info(f"Indexing {len(competitions)} competitions to OpenSearch")
        logger.info(f"Index name: {index_name}")

        # Create index if it doesn't exist
        if not client.indices.exists(index=index_name):
            logger.info(f"Creating index: {index_name}")
            client.indices.create(
                index=index_name,
                body={
                    "mappings": {
                        "properties": {
                            "competition_id": {"type": "keyword"},
                            "title": {"type": "text"},
                            "description": {"type": "text"},
                            "url": {"type": "keyword"},
                            "ref": {"type": "keyword"},
                            "tags": {"type": "keyword"},
                            "category": {"type": "keyword"},
                            "organizationName": {"type": "text"},
                            "organizationRef": {"type": "keyword"},
                            "enabledDate": {"type": "date"},
                            "deadlineDate": {"type": "date"},
                            "rewardType": {"type": "keyword"},
                            "totalTeams": {"type": "integer"},
                            "userHasEntered": {"type": "boolean"},
                            "userRank": {"type": "integer"},
                            "mergerDeadline": {"type": "date"},
                            "newEntrantDeadline": {"type": "date"},
                            "submissionDeadline": {"type": "date"},
                            "teamMergerDeadline": {"type": "date"},
                            "indexed_at": {"type": "date"},
                        }
                    }
                },
            )

        # Index competitions
        indexed_count = 0
        for competition_id, competition in competitions.items():
            try:
                doc = prepare_competition_document(competition, competition_id)

                response = client.index(index=index_name, id=competition_id, body=doc)

                if response.get("result") in ["created", "updated"]:
                    indexed_count += 1
                    logger.debug(f"Indexed competition: {competition_id}")
                else:
                    logger.warning(
                        f"Unexpected response for {competition_id}: {response}"
                    )

            except Exception as e:
                logger.error(f"Failed to index competition {competition_id}: {e}")

        logger.info(f"Successfully indexed {indexed_count} competitions")

    except Exception as e:
        logger.error(f"Failed to index competitions to OpenSearch: {e}")
        raise


def collect_unique_competitions(search_terms: list[str]) -> dict[str, Any]:
    """
    Collect unique competitions from Kaggle API using the provided search terms.
    Uses concurrent processing for better performance.

    Args:
        search_terms: List of search terms to query competitions

    Returns:
        Dictionary of unique competitions with competition_id as key

    Raises:
        Exception: If there's an error during collection
    """
    logger.info("Collecting competitions...")
    logger.info(f"Search terms: {', '.join(search_terms)}")
    logger.info("-" * 80)

    # Use ThreadPoolExecutor for concurrent API calls
    all_competitions_lists: list[list[Any]] = []

    with ThreadPoolExecutor(max_workers=3) as executor:
        # Submit all search tasks concurrently
        future_to_term = {
            executor.submit(fetch_competitions_for_term, term): term
            for term in search_terms
        }

        # Collect results as they complete
        for future in as_completed(future_to_term):
            term, competitions = future.result()
            all_competitions_lists.append(competitions)

    # Flatten all competition lists into a single iterable
    all_competitions_flat = chain.from_iterable(all_competitions_lists)

    # Create dictionary of unique competitions using dictionary comprehension
    unique_competitions: dict[str, Any] = {}

    for comp in all_competitions_flat:
        comp_id = get_competition_id(comp)
        if comp_id and comp_id not in unique_competitions:
            unique_competitions[comp_id] = comp

    logger.info(f"Total unique competitions collected: {len(unique_competitions)}")
    return unique_competitions


def print_competitions(competitions: dict[str, Any]) -> None:
    """
    Print all competitions to stdout.

    Args:
        competitions: Dictionary of competitions with competition_id as key
    """
    logger.info("\n" + "=" * 80)
    logger.info("PRINTING ALL UNIQUE COMPETITIONS")
    logger.info("=" * 80)

    for competition_id, competition in competitions.items():
        title = getattr(competition, "title", "") or "No title"
        logger.info(f"Competition: {title}")
        logger.debug(f"Competition ID: {competition_id}")
        logger.debug(f"Competition object: {competition}")
        logger.info("-" * 80)


def check_kaggle_authentication() -> None:
    """
    Check if the user is authenticated with Kaggle. Raise an error if not.
    """
    api = kaggle.api
    try:
        user = api.get_config_value("username")
        key = api.get_config_value("key")
        if not user or not key:
            logger.error(
                "Kaggle authentication failed: Missing Kaggle username or key."
            )
            exit(1)
        # Optionally, test API access
        api.authenticate()
    except Exception:
        logger.error("Kaggle authentication failed. Please check your credentials.")
        exit(1)


def main() -> None:
    """
    Search for Kaggle competitions with biomedical search terms and index to OpenSearch.
    """
    # Check Kaggle authentication first
    check_kaggle_authentication()

    # Define search terms for biomedical/life sciences competitions
    search_terms: list[str] = [
        "medicine",
        "cancer",
        "cell",
        "biology",
        "neuro",
        "drug",
        "life sciences",
        "health",
        "research",
    ]

    try:
        # First, collect all unique competitions
        unique_competitions = collect_unique_competitions(search_terms)

        # Then, index competitions to OpenSearch
        index_competitions_to_opensearch(unique_competitions)

        logger.info("Successfully completed indexing process")

    except Exception as e:
        logger.error(f"Error occurred: {e}")
        raise


if __name__ == "__main__":
    main()
