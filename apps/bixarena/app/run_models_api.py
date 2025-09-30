#!/usr/bin/env python3
"""
Simple script to start the BixArena Models API
"""

import os
import sys

# Add the src/model directory to the path
model_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "src", "model")
sys.path.insert(0, model_dir)

# Import and run the API
from models_api import app
import uvicorn

if __name__ == "__main__":
    print("🚀 Starting BixArena Models API...")
    print("📍 Available at: http://localhost:8000")
    print("📋 Models endpoint: http://localhost:8000/models")
    print("❌ Stop with Ctrl+C")

    uvicorn.run(app, host="0.0.0.0", port=8000)
