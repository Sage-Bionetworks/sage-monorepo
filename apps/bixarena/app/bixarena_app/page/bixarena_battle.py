"""
BixArena Battle Page

Adapted from FastChat's gradio_web_server_multi.py and
simplified to a single function for a single-page LLM comparison arena.
"""

import logging
import time
from uuid import UUID

import gradio as gr
from bixarena_api_client import (
    ApiClient,
    BattleApi,
    BattleCreateRequest,
    BattleEvaluationApi,
    BattleEvaluationCreateRequest,
    BattleEvaluationOutcome,
    BattleUpdateRequest,
    Configuration,
)

from bixarena_app.config.constants import (
    CONVERSATION_LIMIT_MSG,
    CONVERSATION_TURN_LIMIT,
    INPUT_CHAR_LEN_LIMIT,
    MODERATION_MSG,
    SLOW_MODEL_MSG,
)
from bixarena_app.config.utils import _get_api_base_url
from bixarena_app.model import model_response
from bixarena_app.model.model_response import (
    State,
    bot_response_multi,
    disable_btn,
    enable_btn,
    get_model_list,
    invisible_btn,
    no_change_btn,
    set_global_vars_anony,
    validate_responses,
)
from bixarena_app.model.model_selection import get_battle_pair, moderation_filter
from bixarena_app.page.battle_page_css import (
    DISCLAIMER_CSS,
    EXAMPLE_PROMPTS_CSS,
    INPUT_PROMPT_CSS,
)
from bixarena_app.page.example_prompt_ui import example_prompt_cards

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

num_sides = 2
anony_names = ["", ""]
models = []
current_battle_id: UUID | None = None


def create_battle(model1: str, model2: str, title: str | None = None) -> UUID | None:
    """Create a new battle record in the database.

    Args:
        model1: Name of model 1
        model2: Name of model 2
        title: Optional battle title (first prompt snippet)

    Returns:
        Battle ID if successful, None otherwise
    """
    try:
        api_base_url = _get_api_base_url()

        # Runtime lookup model info
        model1_info = model_response.api_endpoint_info[model1]
        model2_info = model_response.api_endpoint_info[model2]

        configuration = Configuration(host=api_base_url)
        with ApiClient(configuration) as api_client:
            battle_api = BattleApi(api_client)
            battle_request = BattleCreateRequest(
                model1Id=model1_info["model_id"],
                model2Id=model2_info["model_id"],
                title=title,
            )
            battle = battle_api.create_battle(battle_request)
            if battle and battle.id:
                logger.info(f"✅ Battle created: {battle.id} - '{title}'")
                return battle.id

    except Exception as e:
        logger.warning(f"❌ Failed to create battle: {e}")

    return None


def end_battle(battle_id: UUID) -> None:
    """Update battle endedAt timestamp when voting completes."""
    if not battle_id:
        logger.warning("⚠️ No battle_id to end")
        return

    try:
        api_base_url = _get_api_base_url()
        configuration = Configuration(host=api_base_url)
        with ApiClient(configuration) as api_client:
            battle_api = BattleApi(api_client)
            # PATCH with empty body will trigger backend to set endedAt
            battle_api.update_battle(battle_id, BattleUpdateRequest())
            logger.info(f"✅ Battle ended: {battle_id}")
    except Exception as e:
        logger.warning(f"❌ Failed to end battle {battle_id}: {e}")


def create_battle_evaluation(
    battle_id: UUID,
    outcome: BattleEvaluationOutcome,
    is_valid: bool | None = None,
    validation_error: str | None = None,
) -> UUID | None:
    """Create a battleevaluation record for the battle.

    Args:
        battle_id: The battle ID to evaluate
        outcome: BattleEvaluationOutcome enum (MODEL_1, MODEL_2, or TIE)
        is_valid: boolean indicating whether the responses passed validation
        validation_error: validation error message

    Returns:
        Battle Evaluation ID if created successfully, None otherwise
    """
    if not battle_id:
        logger.warning("⚠️ No battle_id to evaluate")
        return None

    try:
        api_base_url = _get_api_base_url()
        configuration = Configuration(host=api_base_url)
        with ApiClient(configuration) as api_client:
            evaluation_api = BattleEvaluationApi(api_client)
            evaluation_request = BattleEvaluationCreateRequest(
                outcome=outcome,
                is_valid=is_valid,
                validation_error=validation_error,
            )
            evaluation = evaluation_api.create_battle_evaluation(
                battle_id, evaluation_request
            )
            if evaluation and evaluation.id:
                logger.info(
                    f"✅ Evaluation created: {evaluation.id} for battle {battle_id}"
                )
                return evaluation.id
    except Exception as e:
        logger.warning(f"❌ Failed to create evaluation for battle {battle_id}: {e}")

    return None


def load_demo_side_by_side_anony(models_, _):
    global models
    models = models_

    states = (None,) * num_sides
    selector_updates = (
        gr.Markdown(visible=True),
        gr.Markdown(visible=True),
    )

    return states + selector_updates


def vote_last_response(states, outcome, model_selectors, _: gr.Request):
    global current_battle_id

    # Create evaluation record and end the battle
    if current_battle_id:
        # TODO: Move the messages validation for identify leaking to backend
        is_valid, validation_error = validate_responses(states)
        create_battle_evaluation(
            current_battle_id,
            outcome,
            is_valid=is_valid,
            validation_error=validation_error,
        )
        end_battle(current_battle_id)
        current_battle_id = None

    # # Log the exact same data to console instead of file
    # data = {
    #     "tstamp": round(time.time(), 4),
    #     "type": outcome.value,
    #     "models": model_selectors,
    #     "states": [state.dict() for state in states],
    #     "is_valid": is_valid,
    #     "invalid_reason": reason or "",
    # }

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
    state0, state1, model_selector0, model_selector1, request: gr.Request
):
    logger.info("leftvote (anony).")
    yield from vote_last_response(
        [state0, state1],
        BattleEvaluationOutcome.MODEL1,
        [model_selector0, model_selector1],
        request,
    )


def rightvote_last_response(
    state0, state1, model_selector0, model_selector1, request: gr.Request
):
    logger.info("rightvote (anony).")
    yield from vote_last_response(
        [state0, state1],
        BattleEvaluationOutcome.MODEL2,
        [model_selector0, model_selector1],
        request,
    )


def tievote_last_response(
    state0, state1, model_selector0, model_selector1, request: gr.Request
):
    logger.info("tievote (anony).")
    yield from vote_last_response(
        [state0, state1],
        BattleEvaluationOutcome.TIE,
        [model_selector0, model_selector1],
        request,
    )


def clear_history(request: gr.Request):
    """Clear battle history and end the active battle."""
    global current_battle_id

    logger.info("clear_history (anony).")

    # End the active battle if one exists
    if current_battle_id:
        end_battle(current_battle_id)
        current_battle_id = None

    return (
        [None] * num_sides  # states
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
        + [gr.Column(visible=True)]  # show suggested_prompts_group
    )


def add_text(
    state0, state1, model_selector0, model_selector1, text, request: gr.Request
):
    global current_battle_id

    logger.info(f"add_text (anony). len: {len(text)}")
    states = [state0, state1]

    # Init states if necessary
    if states[0] is None:
        assert states[1] is None

        left_model, right_model = get_battle_pair(models)
        states = [
            State(left_model),
            State(right_model),
        ]

    if len(text) <= 0:
        for i in range(num_sides):
            states[i].skip_next = True
        return (
            states
            + [x.to_gradio_chatbot() for x in states]
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

    model_list = [states[i].model_name for i in range(num_sides)]
    flagged = moderation_filter(text, model_list)
    if flagged:
        logger.info(f"violate moderation (anony). text: {text}")
        # overwrite the original text
        text = MODERATION_MSG

    conv = states[0].conv
    if (len(conv.messages) - conv.offset) // 2 >= CONVERSATION_TURN_LIMIT:
        logger.info(f"conversation turn limit. text: {text}")
        for i in range(num_sides):
            states[i].skip_next = True
        return (
            states
            + [x.to_gradio_chatbot() for x in states]
            + [CONVERSATION_LIMIT_MSG]
            + [
                no_change_btn,
            ]
            * 4
            + [""]  # slow_warning
            + [gr.Group(visible=True)]  # show battle_interface (conversation exists)
            + [gr.Row(visible=True)]  # show voting_row
            + [gr.Row(visible=True)]  # show next_battle_row
            + [gr.Column(visible=False)]  # hide suggested_prompts_group
        )

    text = text[:INPUT_CHAR_LEN_LIMIT]  # Hard cut-off

    # Create battle with first prompt as title (only for first message)
    if not current_battle_id and states[0] and states[1]:
        model1 = states[0].model_name
        model2 = states[1].model_name
        # Use first 50 characters of prompt as battle title
        battle_title = text[:50] + "..." if len(text) > 50 else text
        current_battle_id = create_battle(model1, model2, battle_title)

    for i in range(num_sides):
        states[i].conv.append_message(states[i].conv.roles[0], text)
        states[i].conv.append_message(states[i].conv.roles[1], None)  # type: ignore
        states[i].skip_next = False

    hint_msg = ""
    for i in range(num_sides):
        if "deluxe" in states[i].model_name:
            hint_msg = SLOW_MODEL_MSG
    return (
        states
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
    model_selectors = []
    chatbots = []

    # Page content
    with gr.Column(elem_classes=["content-wrapper"]):
        gr.HTML(page_header_html)
        # Example prompts (cards + arrows) now provided by helper (textbox bound later)
        (
            example_prompts_group,
            prompt_cards,
            _prev_btn,
            _next_btn,
        ) = example_prompt_cards(textbox=None)

        # Battle interface - will appear once a prompt is submitted
        with gr.Group(elem_id="share-region-anony", visible=False) as battle_interface:
            with gr.Row():
                for i in range(num_sides):
                    label = "Model 1" if i == 0 else "Model 2"
                    with gr.Column():
                        chatbot = gr.Chatbot(
                            label=label,
                            elem_id="chatbot",
                            height=550,
                            show_copy_button=True,
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

        # Prompt input - always visible
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
            <div id="disclaimer-footer">
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
        states + model_selectors,
        model_selectors + [textbox] + btn_list,
    )
    rightvote_btn.click(
        rightvote_last_response,
        states + model_selectors,
        model_selectors + [textbox] + btn_list,
    )
    tie_btn.click(
        tievote_last_response,
        states + model_selectors,
        model_selectors + [textbox] + btn_list,
    )
    clear_btn.click(
        clear_history,
        None,
        states
        + chatbots
        + model_selectors
        + [textbox]
        + btn_list
        + [slow_warning]
        + [battle_interface, voting_row, next_battle_row, example_prompts_group],
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
        states + model_selectors + [textbox],
        states
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
        states,
        states + chatbots + btn_list,
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
            states + model_selectors + [textbox],
            states
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
            states,
            states + chatbots + btn_list,
        ).then(
            lambda: None,
            [],
            [],
            js=enable_enter_js,
        )

    return states + model_selectors


def build_battle_page(
    moderate=False,
):
    """Build the battle page

    Args:
        moderate (bool): Enable content moderation
    """
    # Set global variables
    set_global_vars_anony(moderate)

    # Load models once (text-only models)
    models, _ = get_model_list()

    # Initialize the demo (this sets up global variables in the original module)
    load_demo_side_by_side_anony(models, {})

    with gr.Blocks(title="BixArena - Biomedical LLM Battle") as battle_page:
        build_side_by_side_ui_anony()

    return battle_page
