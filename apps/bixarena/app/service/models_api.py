"""
Simple FastAPI endpoint to serve model data
Uses existing dependencies: fastapi, uvicorn
"""

from fastapi import FastAPI, HTTPException
from typing import List, Dict, Any
from model_data import ModelDataAccess

app = FastAPI(title="BixArena Models API", version="1.0.0")


@app.get("/")
async def root():
    """API info"""
    return {
        "name": "BixArena Models API",
        "version": "1.0.0",
        "endpoints": {"models": "/models", "model": "/models/{slug}"},
    }


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


@app.get("/models/{slug}", response_model=Dict[str, Any])
async def get_model(slug: str):
    """Get specific model by slug"""
    try:
        if not ModelDataAccess.is_available():
            raise HTTPException(status_code=503, detail="Database not available")

        model = ModelDataAccess.get_model_by_slug(slug)
        if not model:
            raise HTTPException(status_code=404, detail=f"Model '{slug}' not found")

        return model

    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Error fetching model: {str(e)}")


@app.get("/health")
async def health():
    """Health check"""
    db_available = ModelDataAccess.is_available()
    return {
        "status": "healthy" if db_available else "degraded",
        "database": "connected" if db_available else "disconnected",
    }


if __name__ == "__main__":
    import uvicorn

    print("🚀 Starting BixArena Models API...")
    print("📍 Available at: http://localhost:8000")
    print("📋 Models endpoint: http://localhost:8000/models")
    print("📖 API docs: http://localhost:8000/docs")
    uvicorn.run(app, host="0.0.0.0", port=8000)
