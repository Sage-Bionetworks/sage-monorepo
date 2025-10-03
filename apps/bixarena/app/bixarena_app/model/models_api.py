"""
Simple FastAPI endpoint to serve model data
Single endpoint: GET /models
Includes database access functions - no external dependencies
"""

import subprocess
from fastapi import FastAPI, HTTPException
from typing import Any
import uvicorn

app = FastAPI(title="BixArena Models API", version="1.0.0")


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


def get_all_models(visible_only=True):
    """Get all models from database

    Args:
        visible_only (bool): If True, only return visible models. If False, return all models.
    """
    if visible_only:
        query = """
        SELECT id, slug, name, organization, license, description,
               api_model_name, api_base, api_type, external_link, visible
        FROM model 
        WHERE visible = true
        ORDER BY name;
        """
    else:
        query = """
        SELECT id, slug, name, organization, license, description,
               api_model_name, api_base, api_type, external_link, visible
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
            if len(parts) >= 11:
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
                        "visible": parts[10].lower() == "t",
                    }
                )

    return models


def is_database_available():
    """Check if database connection is available"""
    result = _run_query("SELECT 1;")
    return result is not None


@app.get("/models", response_model=list[dict[str, Any]])
async def get_models(visible_only: bool = True):
    """Get models from database

    Args:
        visible_only: If True, only return visible models. If False, return all models.
    """
    try:
        if not is_database_available():
            raise HTTPException(status_code=503, detail="Database not available")

        models = get_all_models(visible_only=visible_only)
        return models

    except Exception as e:
        raise HTTPException(
            status_code=500, detail=f"Error fetching models: {str(e)}"
        ) from None


if __name__ == "__main__":
    print("🚀 Starting BixArena Models API...")
    print("📍 Available at: http://localhost:8000")
    print("📋 Models endpoint: http://localhost:8000/models")
    uvicorn.run(app, host="0.0.0.0", port=8000)
