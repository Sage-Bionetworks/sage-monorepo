import gradio as gr
import json
import time
import datetime
from enum import Enum
from server.model_config import get_model_list
from server.model_selection import get_battle_pair


class VoteState(Enum):
    NOT_VOTED = "not_voted"
    VOTED = "voted"
    REVEALED = "revealed"


class ConversationState:
    def __init__(self):
        self.conversation_history = []
        self.models = []
        self.vote_state = VoteState.NOT_VOTED
        self.conversation_id = None
        self.start_time = None
        self.vote_data = None

    def reset(self):
        self.conversation_history = []
        self.models = []
        self.vote_state = VoteState.NOT_VOTED
        self.conversation_id = f"conv_{int(time.time() * 1000)}"
        self.start_time = datetime.datetime.now()
        self.vote_data = None


def generate_mock_response() -> str:
    """Generate mock responses for demonstration purposes."""
    return "Hello! I'm an anonymous AI assistant participating in this evaluation. This is currently a testing environment, so I won't be able to provide actual responses to your queries. I apologize for any inconvenience."


def select_random_models(register_api_endpoint_file=None):
    # Read API-based models info and set text arena
    visible_models, all_models = get_model_list(register_api_endpoint_file, False)
    if not visible_models:
        return [], []
    return get_battle_pair(visible_models)


def build_battle_page(register_api_endpoint_file=None):
    """
    Build and return the BixArena battle page component.
    This function can be imported and called from main application.
    """

    with gr.Column(elem_classes="battle-container") as battle_page:
        # State management
        models_state = gr.State([])
        conversation_state = gr.State(ConversationState())

        # Instructions Section
        with gr.Group(elem_classes="instructions"):
            gr.Markdown("""
            # 🥊 BixArena: Biomedical LLM Battle
            """)

        # Input Section - Minimal button width for closer alignment
        with gr.Row():
            prompt_input = gr.Textbox(
                placeholder="Ask any biomedical relevant questions...",
                lines=3,
                interactive=True,
                show_label=False,
                scale=10,
            )
            with gr.Column(scale=0, min_width=35):
                gr.HTML("")
                send_btn = gr.Button("↑", variant="primary", size="sm")
                gr.HTML("")

        # Model Responses Section
        with gr.Row():
            with gr.Column():
                gr.Markdown("### Assistant A")
                model_a_response = gr.Markdown(
                    "*Waiting for your question...*", elem_classes="model-response"
                )

            with gr.Column():
                gr.Markdown("### Assistant B")
                model_b_response = gr.Markdown(
                    "*Waiting for your question...*", elem_classes="model-response"
                )

        # Voting Section (initially hidden)
        with gr.Group(visible=False) as voting_section:
            gr.Markdown("### 🗳️ Which response is better?")

            with gr.Row(elem_classes="voting-buttons"):
                vote_a_btn = gr.Button(
                    "← Assistant A is Better", variant="secondary", size="lg"
                )
                vote_tie_btn = gr.Button(
                    "🤝 It's a Tie", variant="secondary", size="lg"
                )
                vote_b_btn = gr.Button(
                    "Assistant B is Better →", variant="secondary", size="lg"
                )

            vote_status = gr.Markdown("", visible=False)

        # Model Reveal Section (initially hidden)
        with gr.Group(visible=False, elem_classes="reveal-section") as reveal_section:
            gr.Markdown("### 🎭 Models Revealed!")
            model_reveal = gr.Markdown("")

            view_leaderboard_btn = gr.Button(
                "View Leaderboard", variant="secondary", size="lg"
            )

        # New Chat Button - Always available
        with gr.Row():
            with gr.Column(scale=2):
                pass
            with gr.Column(scale=1):
                new_chat_btn = gr.Button("Start New Chat", variant="primary", size="lg")
            with gr.Column(scale=2):
                pass

        # Helper Functions
        def start_new_conversation():
            """Start a new conversation with random models."""
            models = select_random_models(register_api_endpoint_file)
            new_state = ConversationState()
            new_state.reset()
            new_state.models = models

            return (
                models,  # models_state
                new_state,  # conversation_state
                "",  # prompt_input
                "*Waiting for your question...*",  # model_a_response
                "*Waiting for your question...*",  # model_b_response
                gr.update(visible=False),  # voting_section
                gr.update(visible=False),  # reveal_section
                "",  # vote_status
                "",  # model_reveal
            )

        def send_prompt(prompt, models, conv_state):
            """Submit prompt and get responses from both models."""
            if not prompt.strip():
                return (
                    models,
                    conv_state,
                    prompt,
                    "*Please enter a question*",
                    "*Please enter a question*",
                    gr.update(visible=False),
                    gr.update(visible=False),
                    "",
                )

            if not models:
                models = select_random_models(register_api_endpoint_file)
                conv_state.reset()
                conv_state.models = models

            # Generate responses (replace with actual model API calls)
            response_a = generate_mock_response()
            response_b = generate_mock_response()

            # Add to conversation history
            conv_state.conversation_history.append(
                {
                    "prompt": prompt,
                    "response_a": response_a,
                    "response_b": response_b,
                    "timestamp": datetime.datetime.now().isoformat(),
                }
            )

            return (
                models,  # models_state
                conv_state,  # conversation_state
                "",  # clear prompt_input
                response_a,  # model_a_response
                response_b,  # model_b_response
                gr.update(visible=True),  # show voting_section
                gr.update(visible=False),  # hide reveal_section
                "",  # vote_status
            )

        def vote_for_model(choice, models, conv_state):
            """Handle voting and reveal models."""
            if not models or not conv_state.conversation_history:
                return (
                    conv_state,
                    "",
                    gr.update(visible=False),
                    gr.update(visible=False),
                    "",
                )

            # Prepare vote data according to BixArena schema
            vote_data = {
                "id": conv_state.conversation_id,
                "timestamp": datetime.datetime.now().isoformat(),
                "username": "anonymous_user",  # Replace with actual Synapse user
                "model_a": models[0],
                "model_b": models[1],
                "vote": choice,
                "prompt": conv_state.conversation_history[-1]["prompt"],
                "round": len(conv_state.conversation_history),
                "language": "en",  # Detect language in production
            }

            # Print vote data to console (replace with database save in production)
            print("=== VOTE SUBMITTED ===")
            print(json.dumps(vote_data, indent=2))
            print("=====================")

            conv_state.vote_data = vote_data
            conv_state.vote_state = VoteState.VOTED

            # Prepare reveal message
            choice_text = {
                "model_a": "You chose **Assistant A**",
                "model_b": "You chose **Assistant B**",
                "tie": "You declared it a **Tie**",
            }

            reveal_text = f"""
            {choice_text[choice]}
            
            **Assistant A:** `{models[0]}`  
            **Assistant B:** `{models[1]}`
            
            ✅ Thank you for your evaluation! Your vote helps improve biomedical AI.
            
            The conversation is now locked. Start a new chat to evaluate more models.
            """

            return (
                conv_state,  # conversation_state
                "Vote submitted successfully! ✅",  # vote_status
                gr.update(visible=False),  # hide voting_section
                gr.update(visible=True),  # show reveal_section
                reveal_text,  # model_reveal
            )

        # Event Handlers
        new_chat_btn.click(
            start_new_conversation,
            outputs=[
                models_state,
                conversation_state,
                prompt_input,
                model_a_response,
                model_b_response,
                voting_section,
                reveal_section,
                vote_status,
                model_reveal,
            ],
        )

        send_btn.click(
            send_prompt,
            inputs=[prompt_input, models_state, conversation_state],
            outputs=[
                models_state,
                conversation_state,
                prompt_input,
                model_a_response,
                model_b_response,
                voting_section,
                reveal_section,
                vote_status,
            ],
        )

        prompt_input.submit(
            send_prompt,
            inputs=[prompt_input, models_state, conversation_state],
            outputs=[
                models_state,
                conversation_state,
                prompt_input,
                model_a_response,
                model_b_response,
                voting_section,
                reveal_section,
                vote_status,
            ],
        )

        vote_a_btn.click(
            lambda models, conv_state: vote_for_model("model_a", models, conv_state),
            inputs=[models_state, conversation_state],
            outputs=[
                conversation_state,
                vote_status,
                voting_section,
                reveal_section,
                model_reveal,
            ],
        )

        vote_tie_btn.click(
            lambda models, conv_state: vote_for_model("tie", models, conv_state),
            inputs=[models_state, conversation_state],
            outputs=[
                conversation_state,
                vote_status,
                voting_section,
                reveal_section,
                model_reveal,
            ],
        )

        vote_b_btn.click(
            lambda models, conv_state: vote_for_model("model_b", models, conv_state),
            inputs=[models_state, conversation_state],
            outputs=[
                conversation_state,
                vote_status,
                voting_section,
                reveal_section,
                model_reveal,
            ],
        )

    return battle_page
