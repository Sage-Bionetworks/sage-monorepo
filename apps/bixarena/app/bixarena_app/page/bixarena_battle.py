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
)
from bixarena_app.model import model_response
from bixarena_app.model.error_handler import get_battle_round_limit_message
from bixarena_app.model.model_response import (
    BattleSession,
    State,
    bot_response_multi,
)
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

num_sides = 2
anony_names = ["", ""]


def create_battle(
    title: str | None = None,
    cookies: dict[str, str] | None = None,
) -> tuple[UUID | None, str | None, str | None]:
    """Create a new battle record in the database.

    The backend randomly selects two models and returns the battle with full model information.

    Args:
        title: Optional battle title (first prompt snippet)
        cookies: Session cookies for authentication

    Returns:
        Tuple of (battle_id, model1_name, model2_name) if successful, (None, None, None) otherwise
    """

    try:
        battle_request = BattleCreateRequest(title=title)

        with create_authenticated_api_client(cookies) as api_client:
            battle_api = BattleApi(api_client)
            battle = battle_api.create_battle(battle_request)
            if battle and battle.id and battle.model1 and battle.model2:
                model1_name = battle.model1.name
                model2_name = battle.model2.name

                # Update api_endpoint_info with the selected models
                model_response.api_endpoint_info[model1_name] = {
                    "model_id": battle.model1.id,
                    "api_type": "openai",
                    "api_base": battle.model1.api_base,
                    "model_name": battle.model1.api_model_name,
                }
                model_response.api_endpoint_info[model2_name] = {
                    "model_id": battle.model2.id,
                    "api_type": "openai",
                    "api_base": battle.model2.api_base,
                    "model_name": battle.model2.api_model_name,
                }

                logger.info(
                    f"✅ Battle created: {battle.id} - '{title}' - {model1_name} vs {model2_name}"
                )
                return battle.id, model1_name, model2_name
            logger.warning("❌ Failed to create battle.")
    except Exception as e:
        logger.warning(f"❌ Failed to create battle: {e}")
    return None, None, None


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

    names = (
        f'<div class="model-name-footer">{states[0].model_name}</div>',
        f'<div class="model-name-footer">{states[1].model_name}</div>',
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
        conv = states[0].conv
        if (len(conv.messages) - conv.offset) // 2 >= BATTLE_ROUND_LIMIT:
            logger.info(
                f"🛑 Battle round limit reached: battle_id={battle_session.battle_id}"
            )
            round_limit_msg = get_battle_round_limit_message()
            for i in range(num_sides):
                if states[i]:
                    states[i].conv.append_message("user", text)
                    states[i].conv.append_message("assistant", round_limit_msg)
                    states[i].skip_next = True

    text = text[:PROMPT_LEN_LIMIT]  # Hard cut-off

    # Create battle with first prompt as title (only for first message)
    # Backend will randomly select two models
    if battle_session.battle_id is None:
        # Use first 50 characters of prompt as battle title
        battle_title = text[:50] + "..." if len(text) > 50 else text
        battle_id, model1, model2 = create_battle(battle_title, cookies)
        if battle_id and model1 and model2:
            battle_session.battle_id = battle_id
            # Initialize states with the models selected by the backend
            states = [
                State(model1),
                State(model2),
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

        for i in range(num_sides):
            # In continuation mode, skip models that aren't truncated
            states[i].skip_next = any_truncated and not states[i].is_truncated

            if not states[i].skip_next:
                states[i].conv.append_message(states[i].conv.roles[0], text)
                states[i].conv.append_message(states[i].conv.roles[1], None)  # type: ignore
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
        <p style="font-size: var(--text-xl); color: var(--body-text-color-subdued); margin: 0;">
            Benchmarking AI Models for Biomedical Breakthroughs
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
    with gr.Column():
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
                            height=550,
                            show_copy_button=True,
                            type="messages",
                            group_consecutive_messages=False,
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

        # Next Round button
        with gr.Row(visible=False, elem_id="next-battle-row") as next_battle_row:
            clear_btn = gr.Button(
                value="Next Battle",
                variant="primary",
                elem_id="next-battle-btn",
            )

        # Disclaimer
        disclaimer = gr.HTML(
            """
            <div id="disclaimer">
                <div id="disclaimer-content">
                    <h3 id="disclaimer-title">Data Processing & Privacy:</h3>
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
            visible=True,
        )

    # Register listeners
    left_vote_btn.click(
        left_vote_last_response,
        states + [battle_session] + model_selectors,
        model_selectors
        + [textbox]
        + [left_vote_btn, tie_btn, right_vote_btn]
        + [voting_row, next_battle_row, page_header, textbox_row, disclaimer],
    )
    right_vote_btn.click(
        right_vote_last_response,
        states + [battle_session] + model_selectors,
        model_selectors
        + [textbox]
        + [left_vote_btn, tie_btn, right_vote_btn]
        + [voting_row, next_battle_row, page_header, textbox_row, disclaimer],
    )
    tie_btn.click(
        tie_vote_last_response,
        states + [battle_session] + model_selectors,
        model_selectors
        + [textbox]
        + [left_vote_btn, tie_btn, right_vote_btn]
        + [voting_row, next_battle_row, page_header, textbox_row, disclaimer],
    )
    clear_btn.click(
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
    )

    # Direct JavaScript functions for enter key control
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

    textbox.submit(
        add_text,
        states + [battle_session] + model_selectors + [textbox],
        states
        + [battle_session]
        + chatbots
        + [textbox]
        + [battle_interface, voting_row, next_battle_row, example_prompts_group]
        + [page_header, textbox_row, disclaimer],
    ).then(
        lambda: None,  # Disable enter key
        [],
        [],
        js=disable_enter_js,
    ).then(
        bot_response_multi,
        states + [battle_session],
        states
        + [battle_session]
        + chatbots
        + [voting_row, next_battle_row, page_header, textbox_row],
    ).then(
        lambda: None,  # Enable enter key
        [],
        [],
        js=enable_enter_js,
    )

    # Re-wire prompt buttons now that the real textbox exists (created above)
    # Replace the temporary hidden textbox inside example prompt UI with actual textbox
    # We simply add new handlers that append to existing click chain.
    for card in prompt_cards:
        card.click(lambda v: v, inputs=[card], outputs=[textbox]).then(
            add_text,
            states + [battle_session] + model_selectors + [textbox],
            states
            + [battle_session]
            + chatbots
            + [textbox]
            + [battle_interface, voting_row, next_battle_row, example_prompts_group]
            + [page_header, textbox_row, disclaimer],
        ).then(
            lambda: None,
            [],
            [],
            js=disable_enter_js,
        ).then(
            bot_response_multi,
            states + [battle_session],
            states
            + [battle_session]
            + chatbots
            + [voting_row, next_battle_row, page_header, textbox_row],
        ).then(
            lambda: None,
            [],
            [],
            js=enable_enter_js,
        )

    return (
        states + model_selectors,
        example_prompt_ui,
        [example_prompts_group, prev_btn, next_btn] + prompt_cards,
        prevent_empty_prompt_js,
        disclaimer,
    )


def build_battle_page():
    """Build the battle page

    Returns:
        tuple: (battle_page, example_prompt_ui, prompt_outputs) for hooking up navigation refresh
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

    return battle_page, example_prompt_ui, prompt_outputs
