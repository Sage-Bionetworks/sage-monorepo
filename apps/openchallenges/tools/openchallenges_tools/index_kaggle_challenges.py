import logging
from concurrent.futures import ThreadPoolExecutor, as_completed
from itertools import chain
from typing import Any, Optional

import kaggle

# Configure logging
logging.basicConfig(
    level=logging.INFO, format="%(asctime)s - %(name)s - %(levelname)s - %(message)s"
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


def get_competition_id(competition: Any) -> Optional[str]:
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


def main() -> None:
    """Search for Kaggle competitions with biomedical search terms."""
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

        # Then, print all competitions
        print_competitions(unique_competitions)

    except Exception as e:
        logger.error(f"Error occurred: {e}")
        raise


if __name__ == "__main__":
    main()
