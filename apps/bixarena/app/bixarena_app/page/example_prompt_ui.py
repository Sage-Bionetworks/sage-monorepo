"""Example Prompt UI Module

Fetching random visible example prompts from the BixArena API,
manages in-session navigation history, and renders a horizontal trio of
prompt cards with left/right arrow navigation.
"""

from __future__ import annotations

import logging
from html import escape

import gradio as gr
from bixarena_api_client import ApiClient, Configuration, ExamplePromptApi
from bixarena_api_client.models.example_prompt_search_query import (
    ExamplePromptSearchQuery,
)
from bixarena_api_client.models.example_prompt_sort import ExamplePromptSort

from bixarena_app.config.utils import _get_api_base_url

logger = logging.getLogger(__name__)

# JavaScript for handling prompt card clicks via event delegation
PROMPT_CARD_CLICK_JS = """
() => {
    document.addEventListener('click', (e) => {
        const card = e.target.closest('button.prompt-card');
        if (!card) return;

        const promptText = card.getAttribute('data-prompt');
        if (!promptText) return;

        const textbox = document.querySelector('#input_box textarea');
        if (textbox) {
            textbox.value = promptText;
            textbox.dispatchEvent(new Event('input', { bubbles: true }));
        }
    }, true);
}
"""


class ExamplePromptUI:
    """Stateful manager for example prompt navigation and rendering."""

    def __init__(self):
        self.api_host = _get_api_base_url()
        self.history: list[list[str]] = []
        self.index: int = -1
        self.prev_btn: gr.Button | None = None
        self.next_btn: gr.Button | None = None
        self.prompt_cards: list[gr.Button] = []
        self.group: gr.Row | None = None

    # ----------------------------- Data Layer ----------------------------- #
    def _fetch_random_prompts(self, num_prompts: int = 3) -> list[str]:
        try:
            configuration = Configuration(host=self.api_host)
            with ApiClient(configuration) as api_client:
                api_instance = ExamplePromptApi(api_client)
                search_query = ExamplePromptSearchQuery(
                    page_size=num_prompts,  # use alias defined by generator
                    sort=ExamplePromptSort.RANDOM,
                )
                resp = api_instance.list_example_prompts(
                    example_prompt_search_query=search_query
                )
                prompts = [p.question for p in resp.example_prompts]
                logger.debug(f"Fetched {len(prompts)} example prompts")
                return prompts
        except Exception as e:  # noqa: BLE001
            logger.error(f"Example prompt fetch failed: {e}")
            return [
                "What are the main symptoms of Type 2 diabetes?",
                "How does chemotherapy affect cancer cells?",
                "What is the role of genetics in heart disease?",
            ]

    # ------------------------- Helper Methods ---------------------------- #
    @staticmethod
    def _generate_prompt_button_html(index: int, prompt: str) -> str:
        """Generate HTML for a prompt card button with data attribute."""
        escaped_prompt = escape(prompt)
        return f"""<button class="prompt-card" data-prompt="{escaped_prompt}">
            <div class="prompt-text">{escaped_prompt}</div>
        </button>"""

    # --------------------------- Navigation Logic ------------------------- #
    def _nav_state_updates(
        self, prompts: list[str], show_group: bool = True
    ) -> list[object]:
        has_history = self.index > 0
        # Build class list explicitly; previous inline conditional
        # inadvertently replaced the base classes when no history.
        prev_upd = gr.update(
            interactive=has_history, elem_classes=["nav-button", "left"]
        )
        next_upd = gr.update(interactive=True, elem_classes=["nav-button", "right"])
        # Update prompt card HTML buttons
        prompt_card_upds = [
            gr.update(value=self._generate_prompt_button_html(i, p))
            for i, p in enumerate(prompts)
        ]
        group_upd = gr.update(visible=show_group)
        return [group_upd, prev_upd, next_upd, *prompt_card_upds]

    def _go_prev(self):  # bound as click handler
        if self.index > 0:
            self.index -= 1
        prompts = self.history[self.index] if self.history else ["", "", ""]
        return self._nav_state_updates(prompts)

    def _go_next(self):  # bound as click handler
        if self.index < len(self.history) - 1:
            self.index += 1
        else:
            prompts = self._fetch_random_prompts(3)
            # Truncate forward history (if any) then append
            self.history = self.history[: self.index + 1]
            self.history.append(prompts)
            self.index = len(self.history) - 1
        prompts = self.history[self.index]
        return self._nav_state_updates(prompts)

    def refresh_prompts(self):  # bound as handler for page refresh/reset
        """Fetch new random prompts and reset navigation state."""
        prompts = self._fetch_random_prompts(3)
        self.history = [prompts]
        self.index = 0
        return self._nav_state_updates(prompts)

    # ----------------------------- Build Method --------------------------- #
    def build(
        self,
        textbox: gr.Textbox | None = None,
        num_prompts: int = 3,
    ) -> tuple[gr.Row, list[gr.Button], gr.Button, gr.Button]:
        """Create the example prompt section UI.

        Args:
            textbox: Optional textbox to populate when a prompt is clicked
            num_prompts: Number of prompts to display

        Returns:
            tuple: (row_container, prompt_cards, prev_button, next_button)
        """
        # Start with empty prompts - will be loaded when page is navigated to
        initial = [""] * num_prompts
        self.history = [initial]
        self.index = 0

        with gr.Row(elem_id="prompt-card-section", visible=False) as group, gr.Row():
            self.prev_btn = gr.Button(
                value="←",
                elem_classes=["nav-button", "left", "hidden"],
                interactive=False,
            )
            with gr.Row():
                self.prompt_cards = []
                for i, p in enumerate(initial):
                    # Create HTML button with JavaScript to set textbox value
                    html_btn = gr.HTML(
                        self._generate_prompt_button_html(i, p),
                        elem_classes=["prompt-card-wrapper"],
                    )
                    self.prompt_cards.append(html_btn)
            self.next_btn = gr.Button(
                value="→", elem_classes=["nav-button", "right"], interactive=True
            )

        # Store group reference for visibility updates
        self.group = group

        # Arrow handlers produce updates for group, prev, next, and prompt card buttons
        self.prev_btn.click(
            self._go_prev,
            outputs=[self.group, self.prev_btn, self.next_btn, *self.prompt_cards],
            show_progress=False,
        )
        self.next_btn.click(
            self._go_next,
            outputs=[self.group, self.prev_btn, self.next_btn, *self.prompt_cards],
            show_progress=False,
        )

        return group, self.prompt_cards, self.prev_btn, self.next_btn


def example_prompt_cards(
    textbox: gr.Textbox | None = None,
    num_prompts: int = 3,
) -> tuple[gr.Row, list[gr.Button], gr.Button, gr.Button]:
    """Functional wrapper used to call in other modules."""
    ui = ExamplePromptUI()
    return ui.build(textbox=textbox, num_prompts=num_prompts)
