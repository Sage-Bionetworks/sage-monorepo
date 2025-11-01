"""
BixArena Battle Page

Adapted from FastChat's gradio_web_server_multi.py and
simplified to a single function for a single-page LLM comparison arena.
"""

import logging
import time
import warnings
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
    SLOW_MODEL_MSG,
)
from bixarena_app.model import model_response
from bixarena_app.model.error_handler import get_battle_round_limit_message
from bixarena_app.model.model_response import (
    BattleSession,
    State,
    bot_response_multi,
    disable_btn,
    enable_btn,
    invisible_btn,
    no_change_btn,
)
from bixarena_app.page.battle_page_css import (
    DISCLAIMER_CSS,
    EXAMPLE_PROMPTS_CSS,
    INPUT_PROMPT_CSS,
)
from bixarena_app.page.example_prompt_ui import (
    ExamplePromptUI,
    PROMPT_CARD_CLICK_JS,
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

    if ":" not in model_selectors[0]:
        for _ in range(5):
            names = (
                "### Model 1: " + states[0].model_name,
                "### Model 2: " + states[1].model_name,
            )
            yield (
                names
                + (
                    gr.update(
                        interactive=False, placeholder="Ready for the next battle?"
                    ),
                )
                + (disable_btn,) * 3
                + (enable_btn,)
            )
            time.sleep(0.1)
    else:
        names = (
            "### Model 1: " + states[0].model_name,
            "### Model 2: " + states[1].model_name,
        )
        yield (
            names
            + (gr.update(interactive=False, placeholder="Ready for the next battle?"),)
            + (disable_btn,) * 3
            + (enable_btn,)
        )


def leftvote_last_response(
    state0,
    state1,
    battle_session: BattleSession,
    model_selector0,
    model_selector1,
    request: gr.Request,
):
    logger.info("leftvote (anony).")
    for x in vote_last_response(
        [state0, state1],
        battle_session,
        BattleEvaluationOutcome.MODEL1,
        [model_selector0, model_selector1],
        request,
    ):
        yield x


def rightvote_last_response(
    state0,
    state1,
    battle_session: BattleSession,
    model_selector0,
    model_selector1,
    request: gr.Request,
):
    logger.info("rightvote (anony).")
    for x in vote_last_response(
        [state0, state1],
        battle_session,
        BattleEvaluationOutcome.MODEL2,
        [model_selector0, model_selector1],
        request,
    ):
        yield x


def tievote_last_response(
    state0,
    state1,
    battle_session: BattleSession,
    model_selector0,
    model_selector1,
    request: gr.Request,
):
    logger.info("tievote (anony).")
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

    base_outputs = (
        [None] * num_sides  # states
        + [battle_session]
        + [None] * num_sides  # chatbots
        + anony_names  # model_selectors
        + [
            gr.update(
                value="", interactive=True, placeholder="Ask anything biomedical..."
            )
        ]  # re-enable textbox
        + [invisible_btn] * 3  # voting buttons (leftvote, rightvote, tie)
        + [disable_btn] * 1  # clear button
        + [""]  # slow_warning
        + [gr.Group(visible=False)]  # hide battle_interface
        + [gr.Row(visible=False)]  # hide voting_row
        + [gr.Row(visible=False)]  # hide next_battle_row
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

    if len(text) <= 0:
        if states[0] is not None:
            for i in range(num_sides):
                states[i].skip_next = True
        return (
            states
            + [battle_session]
            + [x.to_gradio_chatbot() if x else [] for x in states]
            + [""]
            + [
                no_change_btn,
            ]
            * 4
            + [""]  # slow_warning
            + [gr.Group(visible=False)]  # keep battle_interface hidden
            + [gr.Row(visible=False)]  # keep voting_row hidden
            + [gr.Row(visible=False)]  # keep next_battle_row hidden
            + [gr.Column(visible=True)]  # keep suggested_prompts_group visible
        )

    # Check battle round limit only if states are initialized
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
            return (
                states
                + [battle_session]
                + [x.to_gradio_chatbot() for x in states]
                + [""]
                + [
                    no_change_btn,
                ]
                * 4
                + [""]  # slow_warning
                + [
                    gr.Group(visible=True)
                ]  # show battle_interface (conversation exists)
                + [gr.Row(visible=True)]  # show voting_row
                + [gr.Row(visible=True)]  # show next_battle_row
                + [gr.Column(visible=False)]  # hide suggested_prompts_group
            )

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
            # Failed to create battle, return error state
            logger.error("Failed to create battle - cannot proceed")
            return (
                [state0, state1]
                + [battle_session]
                + [x.to_gradio_chatbot() if x else [] for x in [state0, state1]]
                + [
                    gr.update(
                        value="",
                        interactive=True,
                        placeholder="Error creating battle. Please try again.",
                    )
                ]
                + [no_change_btn] * 4
                + [""]  # slow_warning
                + [gr.Group(visible=False)]  # keep battle_interface hidden
                + [gr.Row(visible=False)]  # keep voting_row hidden
                + [gr.Row(visible=False)]  # keep next_battle_row hidden
                + [gr.Column(visible=True)]  # keep suggested_prompts_group visible
            )
    battle_id = battle_session.battle_id

    round_id = None
    if battle_id:
        round_id = create_battle_round(battle_id, text, cookies)

    for i in range(num_sides):
        states[i].conv.append_message(states[i].conv.roles[0], text)
        states[i].conv.append_message(states[i].conv.roles[1], None)  # type: ignore
        states[i].skip_next = False

    battle_session.round_id = round_id

    hint_msg = ""
    for i in range(num_sides):
        if "deluxe" in states[i].model_name:
            hint_msg = SLOW_MODEL_MSG
    return (
        states
        + [battle_session]
        + [x.to_gradio_chatbot() for x in states]
        + [gr.update(value="", placeholder="Ask followups...")]
        + [
            disable_btn,
        ]
        * 4
        + [hint_msg]
        + [gr.Group(visible=True)]  # show battle_interface
        + [gr.Row(visible=True)]  # show voting_row
        + [gr.Row(visible=True)]  # show next_battle_row
        + [gr.Column(visible=False)]  # hide example_prompts_group
    )


def build_side_by_side_ui_anony():
    # Page header with title and custom styles
    page_header_html = f"""
    <div style="text-align: center; padding: 0px;">
        <h1 style="font-size: 3rem; margin-bottom: 0.5rem;">BixArena</h1>
        <p style="font-size: 1.2rem; color: #666; margin: 0;">
            Benchmarking LLMs for Biomedical Breakthroughs
        </p>
    </div>
    <style>
    {EXAMPLE_PROMPTS_CSS}
    {INPUT_PROMPT_CSS}
    {DISCLAIMER_CSS}
    </style>
    """

    states = [gr.State() for _ in range(num_sides)]
    battle_session = gr.State(BattleSession())
    model_selectors = []
    chatbots = []

    # Create ExamplePromptUI instance to manage prompts across battle rounds
    example_prompt_ui = ExamplePromptUI()

    # Page content
    with gr.Column(elem_classes=["content-wrapper"]):
        gr.HTML(page_header_html)
        # Example prompts (cards + arrows) now provided by helper (textbox bound later)
        # Start with empty prompts - will be loaded when page is navigated to
        (
            example_prompts_group,
            prompt_cards,
            prev_btn,
            next_btn,
        ) = example_prompt_ui.build(textbox=None)

        # Battle interface - will appear once a prompt is submitted
        with gr.Group(elem_id="share-region-anony", visible=False) as battle_interface:
            with gr.Row():
                for i in range(num_sides):
                    label = "Model 1" if i == 0 else "Model 2"
                    with gr.Column():
                        # Suppress tuples deprecation warning until we migrate to messages format
                        with warnings.catch_warnings():
                            warnings.filterwarnings(
                                "ignore",
                                message=".*tuples.*format.*chatbot.*deprecated.*",
                                category=UserWarning,
                            )
                            chatbot = gr.Chatbot(
                                label=label,
                                elem_id="chatbot",
                                height=550,
                                show_copy_button=True,
                                type="tuples",
                            )
                        chatbots.append(chatbot)

            with gr.Row():
                for i in range(num_sides):
                    with gr.Column():
                        model_selector = gr.Markdown(
                            anony_names[i], elem_id="model_selector_md"
                        )
                        model_selectors.append(model_selector)
            with gr.Row():
                slow_warning = gr.Markdown("", elem_id="notice_markdown")

        # Voting buttons
        with gr.Row(visible=False) as voting_row:
            leftvote_btn = gr.Button(
                value="A is better 👈 ", visible=False, interactive=False
            )
            tie_btn = gr.Button(value="🤝 Tie", visible=False, interactive=False)

            rightvote_btn = gr.Button(
                value="👉 B is better", visible=False, interactive=False
            )

        # Prompt input - always visible, centered with 80% width via CSS
        with gr.Row():
            textbox = gr.Textbox(
                show_label=False,
                placeholder="Ask anything biomedical...",
                elem_id="input_box",
                elem_classes=["prompt_input"],
            )

        # Next Round button
        with gr.Row(visible=False) as next_battle_row:
            with gr.Column(scale=2):
                gr.HTML("")
            with gr.Column(scale=1):
                clear_btn = gr.Button(value="🧪 Next Battle", interactive=False)
            with gr.Column(scale=2):
                gr.HTML("")

        # Disclaimer
        gr.HTML(
            """
            <div id="disclaimer">
                <div id="disclaimer-content">
                    <div id="disclaimer-text">
                        <div class="pulse-dot"></div>
                        <span>
                            AI may make mistakes. Don't include private or 
                            sensitive information in your prompts, 
                            and please verify responses. 
                        </span>
                    </div>
                </div>
            </div>
            """
        )

    # Register listeners
    btn_list = [
        leftvote_btn,
        rightvote_btn,
        tie_btn,
        clear_btn,
    ]
    leftvote_btn.click(
        leftvote_last_response,
        states + [battle_session] + model_selectors,
        model_selectors + [textbox] + btn_list,
    )
    rightvote_btn.click(
        rightvote_last_response,
        states + [battle_session] + model_selectors,
        model_selectors + [textbox] + btn_list,
    )
    tie_btn.click(
        tievote_last_response,
        states + [battle_session] + model_selectors,
        model_selectors + [textbox] + btn_list,
    )
    clear_btn.click(
        lambda battle_session: clear_history(battle_session, None, example_prompt_ui),
        [battle_session],
        states
        + [battle_session]
        + chatbots
        + model_selectors
        + [textbox]
        + btn_list
        + [slow_warning]
        + [battle_interface, voting_row, next_battle_row]
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

    textbox.submit(
        add_text,
        states + [battle_session] + model_selectors + [textbox],
        states
        + [battle_session]
        + chatbots
        + [textbox]
        + btn_list
        + [slow_warning]
        + [battle_interface, voting_row, next_battle_row, example_prompts_group],
    ).then(
        lambda: None,  # Disable enter key
        [],
        [],
        js=disable_enter_js,
    ).then(
        bot_response_multi,
        states + [battle_session],
        states + [battle_session] + chatbots + btn_list,
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
            + btn_list
            + [slow_warning]
            + [battle_interface, voting_row, next_battle_row, example_prompts_group],
        ).then(
            lambda: None,
            [],
            [],
            js=disable_enter_js,
        ).then(
            bot_response_multi,
            states + [battle_session],
            states + [battle_session] + chatbots + btn_list,
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
    )


def build_battle_page():
    """Build the battle page

    Returns:
        tuple: (battle_page, example_prompt_ui, prompt_outputs) for hooking up navigation refresh
    """
    # Initialize the demo with empty states
    load_demo_side_by_side_anony()

    with gr.Blocks(title="BixArena - Biomedical LLM Battle") as battle_page:
        _, example_prompt_ui, prompt_outputs = build_side_by_side_ui_anony()

        # Refresh example prompts when page loads to ensure each user sees different prompts
        battle_page.load(
            example_prompt_ui.refresh_prompts,
            outputs=prompt_outputs,
        )

        # Load JavaScript for prompt card click handlers
        battle_page.load(lambda: None, None, None, js=PROMPT_CARD_CLICK_JS)

    return battle_page, example_prompt_ui, prompt_outputs
