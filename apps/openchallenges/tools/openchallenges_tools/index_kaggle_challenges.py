import os
import kaggle


def main():
    """Search for Kaggle competitions in research category with biomedical search terms."""
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

    api = kaggle.api

    print("Searching for research competitions with biomedical search terms...")
    print(f"Search terms: {', '.join(search_terms)}")
    print("-" * 80)

    try:
        # Get competitions for each search term and collect unique ones
        all_competitions = {}  # Use dict to store unique competitions by ID

        for search_term in search_terms:
            print(f"Searching for competitions with term: '{search_term}'")
            competitions = api.competitions_list(search=search_term)

            if competitions:
                for competition in competitions:
                    # Use competition ref as unique identifier
                    competition_id = getattr(competition, "ref", None) or getattr(
                        competition, "id", None
                    )
                    if competition_id and competition_id not in all_competitions:
                        all_competitions[competition_id] = competition
                        print(f"  Found: {getattr(competition, 'title', 'No title')}")

        print(f"\nTotal unique competitions found: {len(all_competitions)}")
        print("-" * 80)

        # Print all unique competitions
        for competition_id, competition in all_competitions.items():
            title = getattr(competition, "title", "") or ""
            print(f"Competition: {title}")
            print(f"Competition object: {competition}")
            print("-" * 80)

    except Exception as e:
        print(f"Error occurred while fetching competitions: {e}")


if __name__ == "__main__":
    main()
