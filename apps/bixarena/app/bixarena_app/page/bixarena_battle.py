"""
BixArena Battle Page

Adapted from FastChat's gradio_web_server_multi.py and
simplified to a single function for a single-page LLM comparison arena.
"""

import logging
from uuid import UUID

import gradio as gr
from bixarena_api_client import (
    BattleApi,
    BattleCreateRequest,
    BattleEvaluationCreateRequest,
    BattleEvaluationOutcome,
    BattleRoundCreateRequest,
    BattleUpdateRequest,
    MessageCreate,
    MessageRole,
)

from bixarena_app.api.api_client_helper import create_authenticated_api_client
from bixarena_app.auth.request_auth import get_session_cookie
from bixarena_app.config.constants import (
    BATTLE_ROUND_LIMIT,
    PROMPT_LEN_LIMIT,
    PROMPT_USE_LIMIT,
)
from bixarena_app.config.system_message import create_system_message_html
from bixarena_app.config.utils import _ga4_event_js
from bixarena_app.model.battle_state import BattleSession, State
from bixarena_app.config.system_message import get_battle_round_limit_message
from bixarena_app.model.model_response import bot_response_multi
from bixarena_app.page.battle_page_css import (
    CHATBOT_BATTLE_CSS,
    DISCLAIMER_CSS,
    EXAMPLE_PROMPTS_CSS,
    INPUT_PROMPT_CSS,
    NEXT_BATTLE_BUTTON_CSS,
)
from bixarena_app.page.example_prompt_ui import (
    PROMPT_CARD_CLICK_JS,
    ExamplePromptUI,
)

logger = logging.getLogger(__name__)

# JavaScript to inject a submit arrow button inside the prompt textbox
SUBMIT_BUTTON_INJECT_JS = """
() => {
    const wrapper = document.querySelector('#input_box');
    if (!wrapper || wrapper.querySelector('.submit-arrow-injected')) return;
    const textarea = wrapper.querySelector('textarea');
    if (!textarea) return;

    const container = textarea.parentElement;
    container.style.position = 'relative';

    const btn = document.createElement('button');
    btn.className = 'submit-arrow-injected';
    btn.innerHTML = '\\u2192';
    btn.type = 'button';
    Object.assign(btn.style, {
        position: 'absolute',
        right: '8px',
        top: '50%',
        transform: 'translateY(-50%)',
        width: '32px',
        height: '32px',
        borderRadius: '6px',
        background: 'var(--border-color-primary)',
        color: 'var(--body-text-color)',
        border: 'none',
        fontSize: '18px',
        cursor: 'pointer',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        opacity: '0.7',
        transition: 'opacity 0.2s',
        zIndex: '10',
    });

    btn.onmouseenter = () => { btn.style.opacity = '0.9'; };
    btn.onmouseleave = () => { btn.style.opacity = '0.7'; };

    btn.onclick = () => {
        if (textarea.value.trim()) {
            // Click the hidden Gradio button to trigger the submit chain
            const hiddenBtn = document.querySelector('#arrow-submit-btn');
            if (hiddenBtn) hiddenBtn.click();
        }
    };

    container.appendChild(btn);
}
"""

# JavaScript to show/hide disclaimer based on textbox focus and content
DISCLAIMER_TOGGLE_JS = """
() => {
    const textarea = document.querySelector('#input_box textarea');
    const disclaimer = document.getElementById('disclaimer');
    if (!textarea || !disclaimer || disclaimer._disclaimerSetup) return;
    disclaimer._disclaimerSetup = true;

    const update = () => {
        const hasFocus = document.activeElement === textarea;
        const hasContent = textarea.value.length > 0;
        if (hasFocus || hasContent) {
            disclaimer.classList.add('show');
        } else {
            disclaimer.classList.remove('show');
        }
    };

    textarea.addEventListener('focus', update);
    textarea.addEventListener('blur', () => setTimeout(update, 150));
    textarea.addEventListener('input', update);
    update();
}
"""

num_sides = 2
anony_names = ["", ""]


def create_battle(
    title: str | None = None,
    cookies: dict[str, str] | None = None,
):
    """Create a new battle record in the database.

    The backend randomly selects two models and returns the battle with full model information.

    Returns:
        The Battle object if successful, None otherwise.
    """

    try:
        battle_request = BattleCreateRequest(title=title)

        with create_authenticated_api_client(cookies) as api_client:
            battle_api = BattleApi(api_client)
            battle = battle_api.create_battle(battle_request)
            if battle and battle.id and battle.model1 and battle.model2:
                logger.info(
                    f"✅ Battle created: {battle.id} - '{title}' - "
                    f"{battle.model1.name} vs {battle.model2.name}"
                )
                return battle
            logger.warning("❌ Failed to create battle.")
    except Exception as e:
        logger.warning(f"❌ Failed to create battle: {e}")
    return None


def end_battle(battle_id: UUID, cookies: dict[str, str] | None = None) -> None:
    """Update battle endedAt timestamp when voting completes."""
    if not battle_id:
        logger.warning("⚠️ No battle_id to end")
        return

    try:
        with create_authenticated_api_client(cookies) as api_client:
            battle_api = BattleApi(api_client)
            battle_api.update_battle(battle_id, BattleUpdateRequest())
            logger.info(f"✅ Battle ended: {battle_id}")
    except Exception as e:
        logger.warning(f"❌ Failed to end battle {battle_id}: {e}")


def create_battle_round(
    battle_id: UUID,
    prompt: str,
    cookies: dict[str, str] | None = None,
) -> UUID | None:
    """Create a battle round with the submitted prompt."""
    if not battle_id:
        logger.warning("⚠️ Cannot create round without battle_id")
        return None

    try:
        prompt_message = BattleRoundCreateRequest(
            prompt_message=MessageCreate(role=MessageRole.USER, content=prompt)
        )

        with create_authenticated_api_client(cookies) as api_client:
            battle_api = BattleApi(api_client)
            battle_round = battle_api.create_battle_round(battle_id, prompt_message)
            if battle_round and battle_round.id:
                logger.info(f"✅ Battle round created: {battle_round.id}")
                return battle_round.id
            logger.warning(f"❌ Failed to create battle round for battle {battle_id}.")
    except Exception as e:
        logger.warning(f"❌ Failed to create battle round for battle {battle_id}: {e}")
    return None


def create_battle_evaluation(
    battle_id: UUID,
    outcome: BattleEvaluationOutcome,
    cookies: dict[str, str] | None = None,
) -> UUID | None:
    """Create a battleevaluation record for the battle.

    Args:
        battle_id: The battle ID to evaluate
        outcome: BattleEvaluationOutcome enum (MODEL1, MODEL2, or TIE)

    Returns:
        Battle Evaluation ID if created successfully, None otherwise
    """
    if not battle_id:
        logger.warning("⚠️ No battle_id to evaluate")
        return None

    evaluation_request = BattleEvaluationCreateRequest(
        outcome=outcome,
    )

    try:
        with create_authenticated_api_client(cookies) as api_client:
            battle_api = BattleApi(api_client)
            evaluation = battle_api.create_battle_evaluation(
                battle_id, evaluation_request
            )
            if evaluation and evaluation.id:
                logger.info(
                    f"✅ Evaluation created: {evaluation.id} for battle {battle_id}"
                )
                return evaluation.id
            logger.warning(f"❌ Failed to create evaluation for battle {battle_id}.")
    except Exception as e:
        logger.warning(f"❌ Failed to create evaluation for battle {battle_id}: {e}")
    return None


def load_demo_side_by_side_anony():
    """Initialize the battle demo with empty states.

    Returns:
        Tuple of initial states and selector updates for Gradio UI
    """
    states = (None,) * num_sides
    selector_updates = (
        gr.Markdown(visible=True),
        gr.Markdown(visible=True),
    )

    return states + selector_updates


def vote_last_response(
    states, battle_session: BattleSession, outcome, model_selectors, request: gr.Request
):
    # Create evaluation record and end the battle
    cookies = get_session_cookie(request)

    if battle_session.battle_id:
        create_battle_evaluation(
            battle_session.battle_id,
            outcome,
            cookies,
        )
        end_battle(battle_session.battle_id, cookies)
        battle_session.reset()

    # State 4: User voted
    left_vote_variant = (
        "primary" if outcome == BattleEvaluationOutcome.MODEL1 else "secondary"
    )
    tie_vote_variant = (
        "primary" if outcome == BattleEvaluationOutcome.TIE else "secondary"
    )
    right_vote_variant = (
        "primary" if outcome == BattleEvaluationOutcome.MODEL2 else "secondary"
    )

    left_winner = outcome in (
        BattleEvaluationOutcome.MODEL1,
        BattleEvaluationOutcome.TIE,
    )
    right_winner = outcome in (
        BattleEvaluationOutcome.MODEL2,
        BattleEvaluationOutcome.TIE,
    )
    left_cls = "model-name-footer winner" if left_winner else "model-name-footer"
    right_cls = "model-name-footer winner" if right_winner else "model-name-footer"
    names = (
        f'<div class="{left_cls}">{states[0].model_name}</div>',
        f'<div class="{right_cls}">{states[1].model_name}</div>',
    )
    # Decrement prompt use counter after a successful vote
    battle_session.prompt_use_remaining = max(
        0, battle_session.prompt_use_remaining - 1
    )

    # Determine "New Battle Same Prompt" button state
    can_reuse = (
        battle_session.last_prompt is not None
        and battle_session.prompt_use_remaining > 0
    )
    new_battle_same_prompt_upd = gr.Button(
        value=f"New Battle\nSame Prompt ({battle_session.prompt_use_remaining} left)",
        variant="primary",
        visible=can_reuse,
        interactive=can_reuse,
    )

    yield (
        names  # model_selector0, model_selector1: reveal model names
        + (
            gr.update(interactive=False, placeholder="Ready for the next battle?"),
        )  # textbox: disable
        + (
            gr.Button(variant=left_vote_variant, interactive=False),
            gr.Button(variant=tie_vote_variant, interactive=False),
            gr.Button(variant=right_vote_variant, interactive=False),
        )  # buttons: show which was clicked, disable all
        + (gr.Row(visible=True),)  # voting_row: keep visible
        + (gr.Row(visible=True),)  # next_battle_row: show
        + (gr.HTML(visible=False),)  # page_header: hide
        + (gr.Row(visible=False),)  # textbox_row: hide
        + (gr.HTML(visible=False),)  # disclaimer: hide
        + (
            new_battle_same_prompt_upd,
        )  # new_battle_same_prompt_btn: show/hide based on reuse count
        + (
            gr.Button(variant="secondary" if can_reuse else "primary"),
        )  # new_battle_btn
    )


def left_vote_last_response(
    state0,
    state1,
    battle_session: BattleSession,
    model_selector0,
    model_selector1,
    request: gr.Request,
):
    for x in vote_last_response(
        [state0, state1],
        battle_session,
        BattleEvaluationOutcome.MODEL1,
        [model_selector0, model_selector1],
        request,
    ):
        yield x


def right_vote_last_response(
    state0,
    state1,
    battle_session: BattleSession,
    model_selector0,
    model_selector1,
    request: gr.Request,
):
    for x in vote_last_response(
        [state0, state1],
        battle_session,
        BattleEvaluationOutcome.MODEL2,
        [model_selector0, model_selector1],
        request,
    ):
        yield x


def tie_vote_last_response(
    state0,
    state1,
    battle_session: BattleSession,
    model_selector0,
    model_selector1,
    request: gr.Request,
):
    for x in vote_last_response(
        [state0, state1],
        battle_session,
        BattleEvaluationOutcome.TIE,
        [model_selector0, model_selector1],
        request,
    ):
        yield x


def clear_history(
    battle_session: BattleSession, request: gr.Request = None, example_prompt_ui=None
):
    """Clear battle history and end the active battle."""
    logger.info("clear_history (anony).")

    cookies = get_session_cookie(request)

    # End the active battle if one exists
    if battle_session.battle_id:
        end_battle(battle_session.battle_id, cookies)

    battle_session.reset()

    # State 0: Reset to initial (Next Battle clicked or page load)
    base_outputs = (
        [None] * num_sides  # state0, state1: reset
        + [battle_session]  # battle_session: reset
        + [None] * num_sides  # chatbot0, chatbot1: clear
        + anony_names  # model_selector0, model_selector1: clear names
        + [
            gr.update(
                value="", interactive=True, placeholder="Ask anything biomedical..."
            )
        ]  # textbox: enable
        + [
            gr.Button(variant="secondary", interactive=True),
            gr.Button(variant="secondary", interactive=True),
            gr.Button(variant="secondary", interactive=True),
        ]  # voting buttons: reset to default state
        + [gr.Group(visible=False)]  # battle_interface: hide
        + [gr.Row(visible=False)]  # voting_row: hide
        + [gr.Row(visible=False)]  # next_battle_row: hide
        + [gr.HTML(visible=True)]  # page_header: show
        + [gr.Row(visible=True)]  # textbox_row: show
        + [gr.HTML(visible=True)]  # disclaimer: show
    )

    # If example_prompt_ui is provided, also refresh the prompts
    if example_prompt_ui:
        prompt_updates = example_prompt_ui.refresh_prompts()
        return base_outputs + prompt_updates

    return base_outputs


def new_battle_same_prompt(battle_session: BattleSession, request: gr.Request = None):
    """Reset battle state and prepare to resubmit the last prompt.

    Similar to clear_history() but preserves and reuses the last prompt.
    The use counter is not decremented here — it is decremented after voting.
    """
    logger.info("new_battle_same_prompt: reusing last prompt.")

    cookies = get_session_cookie(request)

    # End the active battle if one exists
    if battle_session.battle_id:
        end_battle(battle_session.battle_id, cookies)

    battle_session.reset()

    last_prompt = battle_session.last_prompt or ""

    # Update button text for the next round
    can_reuse = battle_session.prompt_use_remaining > 0
    new_battle_same_prompt_upd = gr.Button(
        value=f"New Battle\nSame Prompt ({battle_session.prompt_use_remaining} left)",
        variant="primary",
        visible=can_reuse,
        interactive=can_reuse,
    )

    return (
        [None] * num_sides  # state0, state1: reset
        + [battle_session]  # battle_session: reset (last_prompt preserved)
        + [None] * num_sides  # chatbot0, chatbot1: clear
        + anony_names  # model_selector0, model_selector1: clear names
        + [
            gr.update(
                value=last_prompt,
                interactive=True,
                placeholder="Ask anything biomedical...",
            )
        ]  # textbox: prefill with last prompt
        + [
            gr.Button(variant="secondary", interactive=True),
            gr.Button(variant="secondary", interactive=True),
            gr.Button(variant="secondary", interactive=True),
        ]  # voting buttons: reset to default state
        + [gr.Group(visible=False)]  # battle_interface: hide
        + [gr.Row(visible=False)]  # voting_row: hide
        + [gr.Row(visible=False)]  # next_battle_row: hide
        + [gr.HTML(visible=False)]  # page_header: hide (going straight to battle)
        + [gr.Row(visible=True)]  # textbox_row: show temporarily
        + [gr.HTML(visible=False)]  # disclaimer: hide
        + [gr.Column(visible=False)]  # example_prompts_group: hide
        + [new_battle_same_prompt_upd]  # new_battle_same_prompt_btn: update counter
        + [gr.Button(variant="secondary" if can_reuse else "primary")]  # new_battle_btn
    )


def add_text(
    state0,
    state1,
    battle_session: BattleSession,
    model_selector0,
    model_selector1,
    text,
    request: gr.Request,
):
    logger.info(f"add_text (anony). len: {len(text)}")
    states = [state0, state1]

    cookies = get_session_cookie(request)

    # Init states if necessary (will be initialized when battle is created)
    if states[0] is None:
        assert states[1] is None
        battle_session.reset()

    # State: Edge case - battle round limit reached
    if states[0] is not None:
        if battle_session.round_count >= BATTLE_ROUND_LIMIT:
            logger.info(
                f"🛑 Battle round limit reached: battle_id={battle_session.battle_id}"
            )
            round_limit_msg = get_battle_round_limit_message()
            round_limit_content = create_system_message_html(round_limit_msg)
            for i in range(num_sides):
                if states[i]:
                    states[i].append_message("user", text)
                    states[i].append_message("assistant", round_limit_content)
                    states[i].skip_next = True

    text = text[:PROMPT_LEN_LIMIT]  # Hard cut-off

    # Create battle with first prompt as title (only for first message)
    # Backend will randomly select two models
    if battle_session.battle_id is None:
        # Use first 50 characters of prompt as battle title
        battle_title = text[:50] + "..." if len(text) > 50 else text
        battle = create_battle(battle_title, cookies)
        if battle:
            battle_session.battle_id = battle.id
            # Track prompt for "New Battle Same Prompt" reuse
            if text != battle_session.last_prompt:
                battle_session.prompt_use_remaining = PROMPT_USE_LIMIT
            battle_session.last_prompt = text
            # Initialize states with the models selected by the backend
            states = [
                State(battle.model1.name, battle.model1.id),
                State(battle.model2.name, battle.model2.id),
            ]
        else:
            # State: Edge case - failed to create battle
            logger.error("Failed to create battle - cannot proceed")
            return (
                [state0, state1]  # state0, state1: unchanged
                + [battle_session]  # battle_session: unchanged
                + [
                    x.to_gradio_chatbot() if x else [] for x in [state0, state1]
                ]  # chatbot0, chatbot1: unchanged
                + [
                    gr.update(
                        value="",
                        interactive=True,
                        placeholder="Error creating battle. Please try again.",
                    )
                ]  # textbox: clear with error message
                + [gr.Group(visible=False)]  # battle_interface: hide
                + [gr.Row(visible=False)]  # voting_row: hide
                + [gr.Row(visible=False)]  # next_battle_row: hide
                + [gr.Column(visible=True)]  # example_prompts_group: show
                + [gr.HTML(visible=True)]  # page_header: show
                + [gr.Row(visible=True)]  # textbox_row: show
                + [gr.HTML(visible=True)]  # disclaimer: show
            )
    battle_id = battle_session.battle_id

    if not states[0].skip_next:
        any_truncated = any(s.is_truncated for s in states if s)

        round_id = None
        if battle_id:
            round_id = create_battle_round(battle_id, text, cookies)
            if round_id:
                battle_session.round_count += 1

        for i in range(num_sides):
            # In continuation mode, skip models that aren't truncated
            states[i].skip_next = any_truncated and not states[i].is_truncated

            if not states[i].skip_next:
                states[i].append_message("user", text)
                states[i].append_message("assistant", None)  # type: ignore
                states[i].is_truncated = False

        battle_session.round_id = round_id

    # State 1: User submits prompt (battle started)
    return (
        states  # state0, state1: updated with prompt
        + [battle_session]  # battle_session: updated with battle_id, round_id
        + [x.to_gradio_chatbot() for x in states]  # chatbot0, chatbot1: show prompt
        + [
            gr.update(value="", placeholder="Interact with models or ask follow-ups...")
        ]  # textbox: clear
        + [gr.Group(visible=True)]  # battle_interface: show
        + [gr.Row(visible=False)]  # voting_row: hide
        + [gr.Row(visible=False)]  # next_battle_row: hide
        + [gr.Column(visible=False)]  # example_prompts_group: hide
        + [gr.HTML(visible=False)]  # page_header: hide
        + [gr.Row(visible=True)]  # textbox_row: show
        + [gr.HTML(visible=True)]  # disclaimer: show
    )


def build_side_by_side_ui_anony():
    # Page header with title and custom styles
    page_header_html = f"""
    <div style="text-align: center; padding: 0px;">
        <h1 style="font-size: var(--text-hero-title); margin-bottom: 0.5rem; color: var(--body-text-color);">BioArena</h1>
        <p style="font-size: var(--text-xl); color: var(--body-text-color); margin: 0 0 0.25rem 0;">
            Discover how different AIs answer biomedical questions. Find out which ones you trust.
        </p>
        <p style="font-size: var(--text-xl); color: var(--body-text-color-subdued); margin: 0;">
            No expertise needed. Just vote for the response you find most useful.
        </p>
    </div>
    <style>
    {CHATBOT_BATTLE_CSS}
    {EXAMPLE_PROMPTS_CSS}
    {INPUT_PROMPT_CSS}
    {DISCLAIMER_CSS}
    {NEXT_BATTLE_BUTTON_CSS}
    </style>
    """

    states = [gr.State() for _ in range(num_sides)]
    battle_session = gr.State(BattleSession())
    model_selectors = []
    chatbots = []

    # Create ExamplePromptUI instance to manage prompts across battle rounds
    example_prompt_ui = ExamplePromptUI()

    # Page content
    with gr.Column(elem_id="battle-page-content"):
        page_header = gr.HTML(page_header_html, visible=True)
        # Example prompts (cards + arrows) now provided by helper (textbox bound later)
        # Start with empty prompts - will be loaded when page is navigated to
        (
            example_prompts_group,
            prompt_cards,
            prev_btn,
            next_btn,
        ) = example_prompt_ui.build(textbox=None)

        # Battle interface - will appear once a prompt is submitted
        with gr.Group(elem_id="chatbot-container", visible=False) as battle_interface:
            with gr.Row(equal_height=True):
                for i in range(num_sides):
                    label = "Model 1" if i == 0 else "Model 2"
                    with gr.Column():
                        chatbot = gr.Chatbot(
                            label=label,
                            elem_id="chatbot",
                            show_copy_button=True,
                            type="messages",
                            group_consecutive_messages=False,
                            latex_delimiters=[
                                {"left": "$$", "right": "$$", "display": True},
                                {"left": "\\[", "right": "\\]", "display": True},
                                {"left": "$", "right": "$", "display": False},
                                {"left": "\\(", "right": "\\)", "display": False},
                            ],
                        )
                        chatbots.append(chatbot)

                        # Model name footer attached to each chatbot
                        model_selector = gr.HTML(anony_names[i])
                        model_selectors.append(model_selector)

        # Voting buttons
        with gr.Row(visible=False) as voting_row:
            left_vote_btn = gr.Button(value="Left is Better 👈")
            tie_btn = gr.Button(value="🤝 Tie")
            right_vote_btn = gr.Button(value="👉 Right is Better")

        # Prompt input - always visible, centered with 80% width via CSS
        with gr.Row(visible=True) as textbox_row:
            textbox = gr.Textbox(
                show_label=False,
                placeholder="Ask anything biomedical...",
                elem_id="input_box",
                elem_classes=["prompt_input"],
            )
            # Hidden button for arrow submit — JS clicks this to trigger Gradio submit
            arrow_submit_btn = gr.Button(visible=False, elem_id="arrow-submit-btn")

        # Disclaimer
        disclaimer = gr.HTML(
            """
            <div id="disclaimer">
                <div id="disclaimer-content">
                    <h3 id="disclaimer-title">Data Processing & Privacy</h3>
                    <p id="disclaimer-text">
                        We process your prompts to ensure they are relevant to
                        biomedical research. Your prompts are also sent to
                        third-party LLM proxies and AI model providers who may
                        store and use them for training and service improvement.
                        <strong>Do not include private, sensitive, confidential,
                        or personally identifiable information in your prompts.</strong>
                        AI responses may contain errors. Verify all AI responses
                        independently.
                    </p>
                </div>
            </div>
            """,
        )

        # New Battle / Same Prompt buttons
        with gr.Row(visible=False, elem_id="next-battle-row") as next_battle_row:
            new_battle_same_prompt_btn = gr.Button(
                value=f"New Battle\nSame Prompt ({PROMPT_USE_LIMIT} left)",
                variant="primary",
                elem_id="battle-again-btn",
                visible=False,
            )
            new_battle_btn = gr.Button(
                value="New Battle",
                variant="primary",
                elem_id="next-battle-btn",
            )

    # Register listeners
    vote_outputs = (
        model_selectors
        + [textbox]
        + [left_vote_btn, tie_btn, right_vote_btn]
        + [voting_row, next_battle_row, page_header, textbox_row, disclaimer]
        + [new_battle_same_prompt_btn]
        + [new_battle_btn]
    )
    left_vote_btn.click(
        left_vote_last_response,
        states + [battle_session] + model_selectors,
        vote_outputs,
        js=_ga4_event_js("vote_clicked", {"vote_choice": "model_1"}),
    )
    right_vote_btn.click(
        right_vote_last_response,
        states + [battle_session] + model_selectors,
        vote_outputs,
        js=_ga4_event_js("vote_clicked", {"vote_choice": "model_2"}),
    )
    tie_btn.click(
        tie_vote_last_response,
        states + [battle_session] + model_selectors,
        vote_outputs,
        js=_ga4_event_js("vote_clicked", {"vote_choice": "tie"}),
    )
    streaming_events: list = []

    new_battle_btn.click(
        lambda battle_session: clear_history(battle_session, None, example_prompt_ui),
        [battle_session],
        states
        + [battle_session]
        + chatbots
        + model_selectors
        + [textbox]
        + [left_vote_btn, tie_btn, right_vote_btn]
        + [battle_interface, voting_row, next_battle_row]
        + [page_header, textbox_row]
        + [disclaimer]
        + [example_prompts_group, prev_btn, next_btn]
        + prompt_cards,
        js=_ga4_event_js("new_battle_clicked", {"trigger": "new_battle"}),
    )

    # Direct JavaScript functions for enter key control
    # (defined early so new_battle_same_prompt_btn handler can reference them)
    disable_enter_js = """
    () => {
        const textbox = document.querySelector('#input_box textarea');
        if (textbox) {
            textbox._enterDisabled = true;

            if (!textbox._enterHandler) {
                textbox._enterHandler = function(event) {
                    if (
                        event.key === 'Enter' &&
                        !event.shiftKey &&
                        textbox._enterDisabled
                    ) {
                        event.preventDefault();
                        event.stopPropagation();
                        event.stopImmediatePropagation();
                        return false;
                    }
                };
                textbox.addEventListener('keydown', textbox._enterHandler, true);
            }
        }
        return [];
    }
    """

    enable_enter_js = """
    () => {
        const textbox = document.querySelector('#input_box textarea');
        if (textbox) {
            textbox._enterDisabled = false;
        }
        return [];
    }
    """

    # Setup empty prompt validation - prevent submission at keydown level
    prevent_empty_prompt_js = """
    () => {
        const textbox = document.querySelector('#input_box textarea');
        if (textbox && !textbox._emptyValidationInstalled) {
            textbox._emptyValidationInstalled = true;

            // Prevent Enter key submission when input is empty
            textbox.addEventListener('keydown', function(event) {
                if (event.key === 'Enter' && !event.shiftKey) {
                    const value = textbox.value.trim();
                    if (value === '') {
                        event.preventDefault();
                        event.stopPropagation();
                        event.stopImmediatePropagation();
                        return false;
                    }
                }
            }, true);
        }
        return [];
    }
    """

    # Shared inputs/outputs for all submit chains
    add_text_inputs = states + [battle_session] + model_selectors + [textbox]
    add_text_outputs = (
        states
        + [battle_session]
        + chatbots
        + [textbox]
        + [battle_interface, voting_row, next_battle_row, example_prompts_group]
        + [page_header, textbox_row, disclaimer]
    )
    bot_response_outputs = (
        states
        + [battle_session]
        + chatbots
        + [voting_row, next_battle_row, page_header, textbox_row]
        + [new_battle_same_prompt_btn, new_battle_btn]
    )

    def _wire_streaming(pre_event):
        """Wire bot_response_multi after a pre-event and track for cancellation."""
        evt = pre_event.then(
            bot_response_multi, states + [battle_session], bot_response_outputs
        )
        streaming_events.append(evt)
        evt.then(lambda: None, [], [], js=enable_enter_js)

    # "New Battle Same Prompt" → add_text → stream
    _wire_streaming(
        new_battle_same_prompt_btn.click(
            new_battle_same_prompt,
            [battle_session],
            states
            + [battle_session]
            + chatbots
            + model_selectors
            + [textbox]
            + [left_vote_btn, tie_btn, right_vote_btn]
            + [battle_interface, voting_row, next_battle_row]
            + [page_header, textbox_row]
            + [disclaimer]
            + [example_prompts_group]
            + [new_battle_same_prompt_btn]
            + [new_battle_btn],
            js=_ga4_event_js(
                "new_battle_clicked", {"trigger": "new_battle_same_prompt"}
            ),
        )
        .then(add_text, add_text_inputs, add_text_outputs)
        .then(lambda: None, [], [], js=disable_enter_js)
    )

    # textbox.submit → stream
    _wire_streaming(
        textbox.submit(add_text, add_text_inputs, add_text_outputs).then(
            lambda: None, [], [], js=disable_enter_js
        )
    )

    # arrow_submit_btn → stream
    _wire_streaming(
        arrow_submit_btn.click(add_text, add_text_inputs, add_text_outputs).then(
            lambda: None, [], [], js=disable_enter_js
        )
    )

    # prompt card clicks → stream
    for i, card in enumerate(prompt_cards):
        _wire_streaming(
            card.click(
                lambda idx=i: example_prompt_ui.prompt_messages[idx],
                inputs=[],
                outputs=[textbox],
            )
            .then(add_text, add_text_inputs, add_text_outputs)
            .then(lambda: None, [], [], js=disable_enter_js)
        )

    # Components needed to reset the battle from outside (e.g. nav button)
    reset_outputs = (
        states
        + [battle_session]
        + chatbots
        + model_selectors
        + [textbox]
        + [left_vote_btn, tie_btn, right_vote_btn]
        + [battle_interface, voting_row, next_battle_row]
        + [page_header, textbox_row]
        + [disclaimer]
    )

    return (
        states + model_selectors,
        example_prompt_ui,
        [example_prompts_group, prev_btn, next_btn] + prompt_cards,
        prevent_empty_prompt_js,
        disclaimer,
        battle_session,
        reset_outputs,
        streaming_events,
    )


def build_battle_page():
    """Build the battle page.

    Returns:
        tuple: (battle_page, example_prompt_ui, prompt_outputs,
                battle_session, reset_outputs) for navigation and reset
    """
    # Initialize the demo with empty states
    load_demo_side_by_side_anony()

    with gr.Blocks(
        title="BioArena - Benchmarking AI Models for Biomedical Breakthroughs",
    ) as battle_page:
        (
            _,
            example_prompt_ui,
            prompt_outputs,
            empty_prompt_js,
            _,  # disclaimer (not needed)
            battle_session,
            reset_outputs,
            streaming_events,
        ) = build_side_by_side_ui_anony()

        # Refresh example prompts when page loads to ensure each user sees different prompts
        battle_page.load(
            example_prompt_ui.refresh_prompts,
            outputs=prompt_outputs,
        ).then(
            lambda: None,  # Install empty input validation
            [],
            [],
            js=empty_prompt_js,
        )

        # Load JavaScript for prompt card click handlers
        battle_page.load(lambda: None, None, None, js=PROMPT_CARD_CLICK_JS)

        # Inject submit arrow button into the textbox
        battle_page.load(lambda: None, None, None, js=SUBMIT_BUTTON_INJECT_JS)

        # Setup disclaimer show/hide based on textbox focus
        battle_page.load(lambda: None, None, None, js=DISCLAIMER_TOGGLE_JS)

    return (
        battle_page,
        example_prompt_ui,
        prompt_outputs,
        battle_session,
        reset_outputs,
        streaming_events,
    )
