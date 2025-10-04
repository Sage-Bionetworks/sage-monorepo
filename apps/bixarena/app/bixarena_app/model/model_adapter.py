"""Minimal model adapter for conversation templates."""

import os

from bixarena_app.config.conversation import Conversation, get_conv_template


class BaseModelAdapter:
    """Base model adapter class."""

    def match(self, model_path: str) -> bool:
        """Check if the model path matches this adapter."""
        return False

    def get_default_conv_template(self, model_path: str) -> Conversation:
        """Get the default conversation template."""
        return get_conv_template("one_shot")


class OpenAIAdapter(BaseModelAdapter):
    """OpenAI models adapter."""

    def match(self, model_path: str) -> bool:
        return "gpt-" in model_path.lower() or model_path.lower().startswith("openai/")

    def get_default_conv_template(self, model_path: str) -> Conversation:
        return get_conv_template("chatgpt")


class AnthropicAdapter(BaseModelAdapter):
    """Anthropic models adapter."""

    def match(self, model_path: str) -> bool:
        return "claude" in model_path.lower() or model_path.lower().startswith(
            "anthropic/"
        )

    def get_default_conv_template(self, model_path: str) -> Conversation:
        return get_conv_template("claude")


class GeminiAdapter(BaseModelAdapter):
    """Google Gemini models adapter."""

    def match(self, model_path: str) -> bool:
        return "gemini" in model_path.lower() or model_path.lower().startswith(
            "google/"
        )

    def get_default_conv_template(self, model_path: str) -> Conversation:
        return get_conv_template("gemini")


# Register model adapters
model_adapters = [
    OpenAIAdapter(),
    AnthropicAdapter(),
    GeminiAdapter(),
    BaseModelAdapter(),  # Default fallback
]


def get_model_adapter(model_path: str) -> BaseModelAdapter:
    """Get a model adapter for a model_path."""
    model_path_basename = os.path.basename(os.path.normpath(model_path))

    # Try the basename of model_path at first
    for adapter in model_adapters:
        if adapter.match(model_path_basename) and type(adapter) != BaseModelAdapter:
            return adapter

    # Then try the full path
    for adapter in model_adapters:
        if adapter.match(model_path):
            return adapter

    # Return default adapter if no match
    return model_adapters[-1]


def get_conversation_template(model_path: str) -> Conversation:
    """Get the default conversation template."""
    adapter = get_model_adapter(model_path)
    return adapter.get_default_conv_template(model_path)
