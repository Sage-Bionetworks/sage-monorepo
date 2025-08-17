"""
Model selection and pairing logic
From fastchat/serve/gradio_block_arena_anony.py
"""

import numpy as np

SAMPLING_WEIGHTS = {
    # tier 0
    "gpt-4": 4,
    "gpt-4-0314": 4,
    "gpt-4-0613": 4,
    "gpt-4-turbo": 4,
    "gpt-4-1106-preview": 4,
    "gpt-4-0125-preview": 4,
    "gpt-3.5-turbo-0613": 2,
    "gpt-3.5-turbo-1106": 2,
    "gpt-3.5-turbo-0125": 4,
    "claude-2.1": 4,
    "claude-2.0": 2,
    "claude-1": 2,
    "claude-instant-1": 2,
    "gemini-pro": 4,
    "gemini-pro-dev-api": 4,
    "bard-jan-24-gemini-pro": 4,
    "bard-feb-2024": 4,
    "mixtral-8x7b-instruct-v0.1": 4,
    "mistral-medium": 4,
    "qwen1.5-72b-chat": 4,
    "qwen1.5-7b-chat": 2,
    "qwen1.5-4b-chat": 2,
    "nous-hermes-2-mixtral-8x7b-dpo": 2,
    "deepseek-llm-67b-chat": 2,
    "stripedhyena-nous-7b": 2,
    "openchat-3.5-0106": 2,
    "mistral-7b-instruct-v0.2": 2,
    "solar-10.7b-instruct-v1.0": 2,
    "dolphin-2.2.1-mistral-7b": 2,
    "starling-lm-7b-alpha": 2,
    "tulu-2-dpo-70b": 2,
    "yi-34b-chat": 2,
    "zephyr-7b-beta": 2,
    # tier 1
    "deluxe-chat-v1.2": 4,
    "llama-2-70b-chat": 4,
    "llama-2-13b-chat": 2,
    "llama-2-7b-chat": 2,
    "mistral-7b-instruct": 2,
    "codellama-34b-instruct": 1.5,
    "vicuna-33b": 2,
    "vicuna-13b": 1.5,
    "wizardlm-13b": 1.5,
    "qwen-14b-chat": 1.5,
    # tier 2
    "pplx-7b-online": 1,
    "pplx-70b-online": 1,
    "openhermes-2.5-mistral-7b": 1.0,
    "llama2-70b-steerlm-chat": 1.0,
    "chatglm3-6b": 1.0,
    "openchat-3.5": 1.0,
    "wizardlm-70b": 1.0,
    "vicuna-7b": 1.0,
    "chatglm2-6b": 1.0,
}

BATTLE_TARGETS = {
    "gpt-4": {"gpt-4-0314", "claude-2.1", "gpt-4-1106-preview"},
    "gpt-4-0613": {"gpt-4-0314", "claude-2.1", "gpt-4-1106-preview"},
    "gpt-4-0314": {
        "gpt-4-1106-preview",
        "gpt-4-0613",
        "claude-2.1",
        "gpt-3.5-turbo-0613",
    },
    "claude-2.1": {"gpt-4-1106-preview", "gpt-4-0613", "claude-1"},
    "claude-2.0": {"gpt-4-1106-preview", "gpt-4-0613", "claude-1"},
    "claude-1": {"claude-2.1", "gpt-4-0613", "gpt-3.5-turbo-0613"},
    "claude-instant-1": {"gpt-3.5-turbo-0125", "claude-2.1"},
    "gemini-pro": {"gpt-4-1106-preview", "gpt-4-0613", "gpt-3.5-turbo-0613"},
    "mixtral-8x7b-instruct-v0.1": {
        "gpt-3.5-turbo-0125",
        "gpt-3.5-turbo-0613",
        "gpt-4-1106-preview",
        "llama-2-70b-chat",
    },
    "llama-2-70b-chat": {"gpt-3.5-turbo-0125", "claude-instant-1"},
}

SAMPLING_BOOST_MODELS = [
    "gpt-3.5-turbo-0125",
    "nous-hermes-2-mixtral-8x7b-dpo",
    "openchat-3.5-0106",
    "qwen1.5-72b-chat",
    "qwen1.5-7b-chat",
    "qwen1.5-4b-chat",
]

OUTAGE_MODELS = []


def get_sample_weight(model):
    if model in OUTAGE_MODELS:
        return 0
    weight = SAMPLING_WEIGHTS.get(model, 1.0)
    if model in SAMPLING_BOOST_MODELS:
        weight *= 5
    return weight


def get_battle_pair(models):
    if len(models) == 1:
        return models[0], models[0]

    model_weights = []
    for model in models:
        weight = get_sample_weight(model)
        model_weights.append(weight)
    total_weight = np.sum(model_weights)
    model_weights = model_weights / total_weight
    chosen_idx = np.random.choice(len(models), p=model_weights)
    chosen_model = models[chosen_idx]

    rival_models = []
    rival_weights = []
    for model in models:
        if model == chosen_model:
            continue
        weight = get_sample_weight(model)
        if (
            weight != 0
            and chosen_model in BATTLE_TARGETS
            and model in BATTLE_TARGETS[chosen_model]
        ):
            # boost to 50% chance
            weight = total_weight / len(BATTLE_TARGETS[chosen_model])
        rival_models.append(model)
        rival_weights.append(weight)
    rival_weights = rival_weights / np.sum(rival_weights)
    rival_idx = np.random.choice(len(rival_models), p=rival_weights)
    rival_model = rival_models[rival_idx]

    swap = np.random.randint(2)
    if swap == 0:
        return chosen_model, rival_model
    else:
        return rival_model, chosen_model
