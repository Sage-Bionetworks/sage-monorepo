"""
Conversation prompt templates extracted from FastChat.
"""

import dataclasses
from dataclasses import field

# Centralized system prompts
BIXARENA_SYSTEM_PROMPT = (
    "You are a helpful AI assistant specializing in biomedical topics. "
    "Do not reveal your identity, model name, organization or training details."
)

CONTINUATION_PROMPT = (
    "The response reached the maximum token limit and was truncated.<br>"
    "Would you like the model to resume streaming its response?"
)


def create_system_message_html(text: str) -> str:
    return (
        '<div class="system-message">'
        '<div class="system-message-content">'
        '<span class="system-icon">ⓘ</span>'
        f'<div class="system-text">{text}</div>'
        "</div></div>"
    )


@dataclasses.dataclass
class Conversation:
    """A class that manages prompt templates and keeps all conversation history."""

    # The names of two roles
    roles: tuple[str, ...] = ("user", "assistant")
    # All messages. Each item is (role, message).
    # Each message is either a string or a tuple of (string, List[image_url]).
    messages: list[list[str]] = field(default_factory=list)
    # The number of few shot examples
    offset: int = 0
    # Note: prompt-building helpers and image/file support are intentionally omitted

    def append_message(self, role: str, message: str):
        """Append a new message."""
        self.messages.append([role, message])

    def update_last_message(self, message: str):
        """Update the last output.

        The last message is typically set to be None when constructing the prompt,
        so we need to update it in-place after getting the response from a model.
        """
        self.messages[-1][1] = message

    def to_gradio_chatbot(self):
        """Convert the conversation to gradio chatbot format."""
        ret = []
        for role, msg in self.messages[self.offset :]:
            if msg is not None:
                content = msg
                # Handle continuation prompt for display
                if msg == CONTINUATION_PROMPT:
                    content = create_system_message_html(msg)
                ret.append({"role": role, "content": content})
        return ret

    def to_openai_api_messages(self):
        """Convert the conversation to OpenAI chat completion format."""
        # BixArena: Override system message for all models to prevent identity leaking
        ret = [{"role": "system", "content": BIXARENA_SYSTEM_PROMPT}]

        role_index = 0
        for _, msg in self.messages[self.offset :]:
            # Handle continuation prompt for OpenAI API
            if msg == CONTINUATION_PROMPT:
                ret.append({"role": "system", "content": msg})
                continue
            if role_index % 2 == 0:
                ret.append({"role": "user", "content": msg})
            else:
                if msg is not None:
                    ret.append({"role": "assistant", "content": msg})
            role_index += 1
        return ret

    def extract_text_from_messages(self):
        return [
            (role, message[0]) if type(message) is tuple else (role, message)
            for role, message in self.messages
        ]

    def dict(self):
        return {
            "roles": self.roles,
            "messages": self.extract_text_from_messages(),
            "offset": self.offset,
        }
