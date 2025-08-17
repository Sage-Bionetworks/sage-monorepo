"""
Call API providers - Only handles "openai" type
From fastchat/serve/api_provider.py
"""

import os
from fastchat.utils import build_logger

logger = build_logger("gradio_web_server", "gradio_web_server.log")


def get_api_provider_stream_iter(
    conv,
    model_name,
    model_api_dict,
    temperature,
    top_p,
    max_new_tokens,
):
    if model_api_dict["api_type"] == "openai":
        prompt = conv.to_openai_api_messages()
        stream_iter = openai_api_stream_iter(
            model_api_dict["model_name"],
            prompt,
            temperature,
            top_p,
            max_new_tokens,
            api_base=model_api_dict["api_base"],
            api_key=model_api_dict["api_key"],
        )
    else:
        raise NotImplementedError()

    return stream_iter


def openai_api_stream_iter(
    model_name,
    messages,
    temperature,
    top_p,
    max_new_tokens,
    api_base=None,
    api_key=None,
):
    import openai

    api_key = api_key or os.environ["OPENAI_API_KEY"]
    client = openai.OpenAI(
        base_url=api_base or "https://api.openai.com/v1", api_key=api_key
    )

    if model_name == "gpt-4-turbo":
        model_name = "gpt-4-1106-preview"

    gen_params = {
        "model": model_name,
        "prompt": messages,
        "temperature": temperature,
        "top_p": top_p,
        "max_new_tokens": max_new_tokens,
    }
    logger.info(f"==== request ====\n{gen_params}")

    res = client.chat.completions.create(
        model=model_name,
        messages=messages,
        temperature=temperature,
        max_tokens=max_new_tokens,
        stream=True,
    )
    text = ""
    for chunk in res:
        if len(chunk.choices) > 0:
            text += chunk.choices[0].delta.content or ""
            data = {
                "text": text,
                "error_code": 0,
            }
            yield data
