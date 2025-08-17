"""
Model configuration loading
From fastchat/serve/gradio_web_server_multi.py
"""

import json


def load_models_from_config(register_api_endpoint_file):
    if not register_api_endpoint_file:
        return []
    try:
        with open(register_api_endpoint_file) as f:
            config = json.load(f)
        return list(config.keys())
    except (FileNotFoundError, json.JSONDecodeError):
        return []
