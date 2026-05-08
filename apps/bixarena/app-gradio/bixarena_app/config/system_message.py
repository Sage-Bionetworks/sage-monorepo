"""System messages displayed to users in the chat UI (not LLM chat content)."""

from bixarena_app.config.constants import BATTLE_ROUND_LIMIT

CONTINUATION_PROMPT = (
    "The response reached the maximum token limit and was truncated.<br>"
    "Would you like the model to resume streaming its response?"
)


def get_battle_round_limit_message() -> str:
    return (
        f"You've reached the round limit ({BATTLE_ROUND_LIMIT}) for this battle.<br>"
        "Please submit your evaluation of the model."
    )


def create_system_message_html(text: str) -> str:
    """Wrap a system message in styled HTML for Gradio chatbot display."""
    return (
        '<div class="system-message">'
        '<div class="system-message-content">'
        '<span class="system-icon">ⓘ</span>'
        f'<div class="system-text">{text}</div>'
        "</div></div>"
    )
