"""
Simple Model Data Access Utility for BixArena
Access model data from PostgreSQL - no extra dependencies
"""

import subprocess


def _run_query(query):
    """Run PostgreSQL query"""
    cmd = [
        "psql",
        "-h",
        "bixarena-postgres",
        "-p",
        "21000",
        "-U",
        "postgres",
        "-d",
        "bixarena",
        "-t",
        "-A",
        "-F",
        "|",
        "-c",
        query,
    ]

    try:
        result = subprocess.run(
            cmd, capture_output=True, text=True, env={"PGPASSWORD": "changeme"}
        )
        return result.stdout.strip() if result.returncode == 0 else None
    except Exception:
        return None


class ModelDataAccess:
    """Simple class for accessing model data from PostgreSQL"""

    @staticmethod
    def get_all_models():
        """
        Get all models from database
        Returns: List of model dictionaries
        """
        query = """
        SELECT id, slug, name, organization, license, description,
               api_model_name, api_base, api_type, external_link
        FROM model 
        ORDER BY name;
        """

        result = _run_query(query)
        if not result:
            return []

        models = []
        for line in result.split("\n"):
            if line.strip():
                parts = line.split("|")
                if len(parts) >= 10:
                    models.append(
                        {
                            "id": parts[0],
                            "slug": parts[1],
                            "name": parts[2],
                            "organization": parts[3] or "Unknown",
                            "license": parts[4] or "Unknown",
                            "description": parts[5] or "",
                            "api_model_name": parts[6],
                            "api_base": parts[7],
                            "api_type": parts[8],
                            "external_link": parts[9] or "",
                        }
                    )

        return models

    @staticmethod
    def get_model_by_slug(slug):
        """
        Get a specific model by slug
        Args: slug (str) - The model slug
        Returns: Model dictionary or None
        """
        query = f"""
        SELECT id, slug, name, organization, license, description,
               api_model_name, api_base, api_type, external_link
        FROM model 
        WHERE slug = '{slug}';
        """

        result = _run_query(query)
        if not result:
            return None

        parts = result.split("|")
        if len(parts) >= 10:
            return {
                "id": parts[0],
                "slug": parts[1],
                "name": parts[2],
                "organization": parts[3] or "Unknown",
                "license": parts[4] or "Unknown",
                "description": parts[5] or "",
                "api_model_name": parts[6],
                "api_base": parts[7],
                "api_type": parts[8],
                "external_link": parts[9] or "",
            }

        return None

    @staticmethod
    def get_model_slugs():
        """
        Get all model slugs
        Returns: List of slug strings
        """
        query = "SELECT slug FROM model ORDER BY name;"
        result = _run_query(query)

        if not result:
            return []

        return [line.strip() for line in result.split("\n") if line.strip()]

    @staticmethod
    def get_organizations():
        """
        Get all unique organizations
        Returns: List of organization names
        """
        query = "SELECT DISTINCT organization FROM model WHERE organization IS NOT NULL ORDER BY organization;"
        result = _run_query(query)

        if not result:
            return []

        return [line.strip() for line in result.split("\n") if line.strip()]

    @staticmethod
    def get_models_by_organization(organization):
        """
        Get models filtered by organization
        Args: organization (str) - Organization name
        Returns: List of model dictionaries
        """
        query = f"""
        SELECT id, slug, name, organization, license, description,
               api_model_name, api_base, api_type, external_link
        FROM model 
        WHERE LOWER(organization) = LOWER('{organization}')
        ORDER BY name;
        """

        result = _run_query(query)
        if not result:
            return []

        models = []
        for line in result.split("\n"):
            if line.strip():
                parts = line.split("|")
                if len(parts) >= 10:
                    models.append(
                        {
                            "id": parts[0],
                            "slug": parts[1],
                            "name": parts[2],
                            "organization": parts[3] or "Unknown",
                            "license": parts[4] or "Unknown",
                            "description": parts[5] or "",
                            "api_model_name": parts[6],
                            "api_base": parts[7],
                            "api_type": parts[8],
                            "external_link": parts[9] or "",
                        }
                    )

        return models

    @staticmethod
    def is_available():
        """
        Check if database connection is available
        Returns: Boolean
        """
        result = _run_query("SELECT 1;")
        return result is not None


# Convenience functions for direct usage
def get_all_models():
    """Get all models - convenience function"""
    return ModelDataAccess.get_all_models()


def get_model(slug):
    """Get model by slug - convenience function"""
    return ModelDataAccess.get_model_by_slug(slug)


def list_model_slugs():
    """List all model slugs - convenience function"""
    return ModelDataAccess.get_model_slugs()


def list_organizations():
    """List all organizations - convenience function"""
    return ModelDataAccess.get_organizations()


# Test the utility
if __name__ == "__main__":
    print("🔍 Testing Model Data Access...")

    if not ModelDataAccess.is_available():
        print("❌ Database not available")
        exit(1)

    print("✅ Database connected")

    models = get_all_models()
    print(f"📋 Found {len(models)} models:")
    for model in models:
        print(f"  • {model['name']} ({model['organization']})")

    if models:
        test_slug = models[0]["slug"]
        model = get_model(test_slug)
        if model:
            print(f"\n🔍 Model '{test_slug}':")
            print(f"  API: {model['api_model_name']}")
            print(f"  Base: {model['api_base']}")

    orgs = list_organizations()
    print(f"\n🏢 Organizations: {', '.join(orgs)}")
    print("\n✅ All tests passed!")
