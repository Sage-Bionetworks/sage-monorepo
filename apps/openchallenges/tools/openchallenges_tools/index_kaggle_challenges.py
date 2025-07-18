import os
import kaggle


def collect_unique_competitions(search_terms):
    """
    Collect unique competitions from Kaggle API using the provided search terms.
    
    Args:
        search_terms (list): List of search terms to query competitions
        
    Returns:
        dict: Dictionary of unique competitions with competition_id as key
    """
    api = kaggle.api
    all_competitions = {}  # Use dict to store unique competitions by ID
    
    print("Collecting competitions...")
    print(f"Search terms: {', '.join(search_terms)}")
    print("-" * 80)
    
    for search_term in search_terms:
        print(f"Searching for competitions with term: '{search_term}'")
        try:
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
                        
        except Exception as e:
            print(f"  Error searching for '{search_term}': {e}")
    
    print(f"\nTotal unique competitions collected: {len(all_competitions)}")
    return all_competitions


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

    try:
        # First, collect all unique competitions
        unique_competitions = collect_unique_competitions(search_terms)
        
        # Then, print all competitions
        print_competitions(unique_competitions)
        
    except Exception as e:
        print(f"Error occurred: {e}")


if __name__ == "__main__":
    main()
