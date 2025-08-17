"""
Model configuration loading
From fastchat/serve/gradio_web_server.py
"""

import json
from fastchat.model.model_registry import model_info

# Global variable for API-based models
api_endpoint_info = {}


def get_model_list(register_api_endpoint_file, multimodal):
    global api_endpoint_info

    models = []
    # Add models from the API providers
    if register_api_endpoint_file:
        api_endpoint_info = json.load(open(register_api_endpoint_file))
        for mdl, mdl_dict in api_endpoint_info.items():
            mdl_multimodal = mdl_dict.get("multimodal", False)
            if multimodal and mdl_multimodal:
                models += [mdl]
            elif not multimodal and not mdl_multimodal:
                models += [mdl]

    # Remove anonymous models
    models = list(set(models))
    visible_models = models.copy()
    for mdl in visible_models:
        if mdl not in api_endpoint_info:
            continue
        mdl_dict = api_endpoint_info[mdl]
        if mdl_dict["anony_only"]:
            visible_models.remove(mdl)

    # Sort models and add descriptions
    priority = {k: f"___{i:03d}" for i, k in enumerate(model_info)}
    models.sort(key=lambda x: priority.get(x, str(x)))
    visible_models.sort(key=lambda x: priority.get(x, str(x)))

    return visible_models, models
