"""
Simple FastAPI endpoint to serve model data
Single endpoint: GET /models
"""

from fastapi import FastAPI, HTTPException
from typing import List, Dict, Any
import uvicorn
from model_data import ModelDataAccess

app = FastAPI(title="BixArena Models API", version="1.0.0")


@app.get("/models", response_model=List[Dict[str, Any]])
async def get_models():
    """Get all models from database"""
    try:
        if not ModelDataAccess.is_available():
            raise HTTPException(status_code=503, detail="Database not available")

        models = ModelDataAccess.get_all_models()
        return models

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error fetching models: {str(e)}")


if __name__ == "__main__":
    print("🚀 Starting BixArena Models API...")
    print("📍 Available at: http://localhost:8000")
    print("📋 Models endpoint: http://localhost:8000/models")
    uvicorn.run(app, host="0.0.0.0", port=8000)
