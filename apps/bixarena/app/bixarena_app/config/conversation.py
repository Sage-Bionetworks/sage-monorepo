"""Conversation state management for Gradio chatbot UI."""

import dataclasses
from dataclasses import field

from bixarena_app.config.system_message import (
    CONTINUATION_PROMPT,
    create_system_message_html,
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
