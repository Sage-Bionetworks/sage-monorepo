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
    # deprecated
    "zephyr-7b-alpha": 1.5,
    "codellama-13b-instruct": 1.0,
    "mpt-30b-chat": 1.5,
    "guanaco-33b": 1.0,
    "fastchat-t5-3b": 0.5,
    "alpaca-13b": 0.5,
    "mpt-7b-chat": 0.1,
    "oasst-pythia-12b": 0.1,
    "RWKV-4-Raven-14B": 0.1,
    "gpt4all-13b-snoozy": 0.1,
    "koala-13b": 0.1,
    "stablelm-tuned-alpha-7b": 0.1,
    "dolly-v2-12b": 0.1,
    "llama-13b": 0.1,
    "chatglm-6b": 0.5,
    "deluxe-chat-v1": 4,
    "palm-2": 1.5,
}

# target model sampling weights will be boosted.
BATTLE_TARGETS = {
    "gpt-4": {"gpt-4-0314", "claude-2.1", "gpt-4-1106-preview"},
    "gpt-4-0613": {"gpt-4-0314", "claude-2.1", "gpt-4-1106-preview"},
    "gpt-4-0314": {
        "gpt-4-1106-preview",
        "gpt-4-0613",
        "claude-2.1",
        "gpt-3.5-turbo-0613",
    },
    "gpt-4-1106-preview": {
        "gpt-4-0613",
        "gpt-3.5-turbo-0613",
        "gpt-3.5-turbo-1106",
        "claude-2.1",
        "bard-feb-2024",
    },
    "gpt-4-0125-preview": {
        "gpt-4-1106-preview",
        "gpt-4-0613",
        "gpt-3.5-turbo-0613",
        "claude-2.1",
        "mistral-medium",
        "bard-feb-2024",
    },
    "gpt-3.5-turbo-0613": {"claude-instant-1", "gpt-4-0613", "claude-2.1"},
    "gpt-3.5-turbo-1106": {"gpt-4-0613", "claude-instant-1", "gpt-3.5-turbo-0613"},
    "gpt-3.5-turbo-0125": {
        "gpt-4-0613",
        "gpt-4-1106-preview",
        "gpt-3.5-turbo-0613",
        "gpt-3.5-turbo-1106",
        "mixtral-8x7b-instruct-v0.1",
    },
    "qwen1.5-72b-chat": {
        "gpt-3.5-turbo-0125",
        "gpt-4-0613",
        "gpt-4-1106-preview",
        "llama-2-70b-chat",
        "mixtral-8x7b-instruct-v0.1",
        "mistral-medium",
        "yi-34b-chat",
    },
    "qwen1.5-7b-chat": {
        "gpt-3.5-turbo-0125",
        "starling-lm-7b-alpha",
        "llama-2-70b-chat",
        "openchat-3.5",
        "mixtral-8x7b-instruct-v0.1",
    },
    "qwen1.5-4b-chat": {
        "llama-2-70b-chat",
        "llama-2-13b-chat",
        "llama-2-7b-chat",
        "openchat-3.5",
    },
    "openchat-3.5-0106": {
        "gpt-3.5-turbo-0125",
        "gpt-3.5-turbo-0613",
        "llama-2-70b-chat",
        "openchat-3.5",
        "mixtral-8x7b-instruct-v0.1",
    },
    "nous-hermes-2-mixtral-8x7b-dpo": {
        "gpt-4-1106-preview",
        "claude-2.1",
        "mistral-medium",
        "gpt-3.5-turbo-0613",
        "mixtral-8x7b-instruct-v0.1",
    },
    "mistral-7b-instruct-v0.2": {
        "llama-2-70b-chat",
        "mixtral-8x7b-instruct-v0.1",
        "starling-lm-7b-alpha",
        "openhermes-2.5-mistral-7b",
    },
    "solar-10.7b-instruct-v1.0": {
        "mixtral-8x7b-instruct-v0.1",
        "gpt-3.5-turbo-0613",
        "llama-2-70b-chat",
    },
    "mistral-medium": {
        "gpt-3.5-turbo-0125",
        "gpt-3.5-turbo-0613",
        "gpt-4-1106-preview",
        "mixtral-8x7b-instruct-v0.1",
        "bard-feb-2024",
    },
    "mixtral-8x7b-instruct-v0.1": {
        "gpt-3.5-turbo-0125",
        "gpt-3.5-turbo-0613",
        "gpt-4-1106-preview",
        "llama-2-70b-chat",
    },
    "claude-2.1": {"gpt-4-1106-preview", "gpt-4-0613", "claude-1"},
    "claude-2.0": {"gpt-4-1106-preview", "gpt-4-0613", "claude-1"},
    "claude-1": {"claude-2.1", "gpt-4-0613", "gpt-3.5-turbo-0613"},
    "claude-instant-1": {"gpt-3.5-turbo-0125", "claude-2.1"},
    "gemini-pro": {"gpt-4-1106-preview", "gpt-4-0613", "gpt-3.5-turbo-0613"},
    "gemini-pro-dev-api": {
        "gpt-4-1106-preview",
        "gpt-4-0613",
        "gpt-3.5-turbo-0613",
        "bard-feb-2024",
    },
    "bard-jan-24-gemini-pro": {
        "gpt-4-1106-preview",
        "gpt-4-0613",
        "gpt-3.5-turbo-0613",
        "gemini-pro-dev-api",
    },
    "bard-feb-2024": {
        "gpt-4-1106-preview",
        "gpt-4-0613",
        "gpt-3.5-turbo-0613",
        "bard-jan-24-gemini-pro",
    },
    "deepseek-llm-67b-chat": {
        "gpt-4-1106-preview",
        "gpt-4-turbo",
        "gpt-3.5-turbo-0613",
    },
    "llama2-70b-steerlm-chat": {
        "llama-2-70b-chat",
        "tulu-2-dpo-70b",
        "yi-34b-chat",
    },
    "stripedhyena-nous-7b": {
        "starling-lm-7b-alpha",
        "openhermes-2.5-mistral-7b",
        "mistral-7b-instruct",
        "llama-2-7b-chat",
    },
    "deluxe-chat-v1.1": {"gpt-4-0613", "gpt-4-1106-preview"},
    "deluxe-chat-v1.2": {"gpt-4-0613", "gpt-4-1106-preview"},
    "pplx-7b-online": {"gpt-3.5-turbo-0125", "llama-2-70b-chat"},
    "pplx-70b-online": {"gpt-3.5-turbo-0125", "llama-2-70b-chat"},
    "openhermes-2.5-mistral-7b": {
        "gpt-3.5-turbo-0613",
        "openchat-3.5",
        "zephyr-7b-beta",
    },
    "dolphin-2.2.1-mistral-7b": {
        "gpt-3.5-turbo-0613",
        "vicuna-33b",
        "starling-lm-7b-alpha",
        "openhermes-2.5-mistral-7b",
    },
    "starling-lm-7b-alpha": {"gpt-3.5-turbo-0613", "openchat-3.5", "zephyr-7b-beta"},
    "tulu-2-dpo-70b": {"gpt-3.5-turbo-0613", "vicuna-33b", "claude-instant-1"},
    "yi-34b-chat": {"gpt-3.5-turbo-0613", "vicuna-33b", "claude-instant-1"},
    "openchat-3.5": {"gpt-3.5-turbo-0613", "llama-2-70b-chat", "zephyr-7b-beta"},
    "chatglm3-6b": {"yi-34b-chat", "qwen-14b-chat"},
    "qwen-14b-chat": {"vicuna-13b", "llama-2-13b-chat", "llama-2-70b-chat"},
    "zephyr-7b-alpha": {"mistral-7b-instruct", "llama-2-13b-chat"},
    "zephyr-7b-beta": {
        "mistral-7b-instruct",
        "llama-2-13b-chat",
        "llama-2-7b-chat",
        "wizardlm-13b",
    },
    "llama-2-70b-chat": {"gpt-3.5-turbo-0125", "claude-instant-1"},
    "llama-2-13b-chat": {"mistral-7b-instruct", "vicuna-13b", "llama-2-70b-chat"},
    "llama-2-7b-chat": {"mistral-7b-instruct", "vicuna-7b", "llama-2-13b-chat"},
    "mistral-7b-instruct": {
        "llama-2-7b-chat",
        "llama-2-13b-chat",
        "llama-2-70b-chat",
    },
    "vicuna-33b": {"llama-2-70b-chat", "gpt-3.5-turbo-0613", "claude-instant-1"},
    "vicuna-13b": {"llama-2-13b-chat", "llama-2-70b-chat"},
    "vicuna-7b": {"llama-2-7b-chat", "mistral-7b-instruct", "llama-2-13b-chat"},
    "wizardlm-70b": {"gpt-3.5-turbo-0613", "vicuna-33b", "claude-instant-1"},
}

SAMPLING_BOOST_MODELS = [
    # "claude-2.1",
    # "gpt-4-0613",
    # "gpt-4-0314",
    # "gpt-4-1106-preview",
    # "gpt-4-0125-preview",
    "gpt-3.5-turbo-0125",
    # "mistral-medium",
    "nous-hermes-2-mixtral-8x7b-dpo",
    "openchat-3.5-0106",
    "qwen1.5-72b-chat",
    "qwen1.5-7b-chat",
    "qwen1.5-4b-chat",
    # "mistral-7b-instruct-v0.2",
]

# outage models won't be sampled.
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
    # for p, w in zip(models, model_weights):
    #     print(p, w)

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
    # for p, w in zip(rival_models, rival_weights):
    #     print(p, w)
    rival_weights = rival_weights / np.sum(rival_weights)
    rival_idx = np.random.choice(len(rival_models), p=rival_weights)
    rival_model = rival_models[rival_idx]

    swap = np.random.randint(2)
    if swap == 0:
        return chosen_model, rival_model
    else:
        return rival_model, chosen_model
