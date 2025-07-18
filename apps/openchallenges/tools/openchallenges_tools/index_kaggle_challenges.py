from concurrent.futures import ThreadPoolExecutor, as_completed
from itertools import chain

import kaggle


def fetch_competitions_for_term(search_term):
    """
    Fetch competitions for a single search term.

    Args:
        search_term (str): The search term to query

    Returns:
        tuple: (search_term, competitions_list)
    """
    api = kaggle.api
    try:
        competitions = api.competitions_list(search=search_term)
        count = len(competitions) if competitions else 0
        print(f"Found {count} competitions for '{search_term}'")
        return search_term, competitions or []
    except Exception as e:
        print(f"Error searching for '{search_term}': {e}")
        return search_term, []


def collect_unique_competitions(search_terms):
    """
    Collect unique competitions from Kaggle API using the provided search terms.
    Uses concurrent processing for better performance.

    Args:
        search_terms (list): List of search terms to query competitions

    Returns:
        dict: Dictionary of unique competitions with competition_id as key
    """
    print("Collecting competitions...")
    print(f"Search terms: {', '.join(search_terms)}")
    print("-" * 80)

    # Use ThreadPoolExecutor for concurrent API calls
    all_competitions_lists = []

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
    unique_competitions = {
        (getattr(comp, "ref", None) or getattr(comp, "id", None) or str(comp)): comp
        for comp in all_competitions_flat
        if getattr(comp, "ref", None) or getattr(comp, "id", None)
    }

    print(f"\nTotal unique competitions collected: {len(unique_competitions)}")
    return unique_competitions


def print_competitions(competitions):
    """
    Print all competitions to stdout.

    Args:
        competitions (dict): Dictionary of competitions with competition_id as key
    """
    print("\n" + "=" * 80)
    print("PRINTING ALL UNIQUE COMPETITIONS")
    print("=" * 80)

    for competition_id, competition in competitions.items():
        title = getattr(competition, "title", "") or ""
        print(f"Competition: {title}")
        print(f"Competition ID: {competition_id}")
        print(f"Competition object: {competition}")
        print("-" * 80)


def main():
    """Search for Kaggle competitions with biomedical search terms."""
    # Define search terms for biomedical/life sciences competitions
    search_terms = [
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
        print(f"Error occurred: {e}")


if __name__ == "__main__":
    main()
