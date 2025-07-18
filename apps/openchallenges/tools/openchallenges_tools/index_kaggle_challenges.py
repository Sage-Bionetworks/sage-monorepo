import os
import kaggle


def main():
    """Print the Kaggle username and key from environment variables."""
    kaggle_username = os.environ.get("KAGGLE_USERNAME")
    kaggle_key = os.environ.get("KAGGLE_KEY")

    api = kaggle.api

    username_from_api = api.get_config_value("username")
    print(f"Username from Kaggle API: {username_from_api}")


if __name__ == "__main__":
    main()
