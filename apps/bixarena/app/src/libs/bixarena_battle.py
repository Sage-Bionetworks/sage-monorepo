"""
BixArena Battle Page Module
A simple battle page component for biomedical LLM evaluation.
"""

import gradio as gr
import json
import time
import datetime
import random
from enum import Enum


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


# Mock model responses (replace with actual model API calls in production)
MOCK_MODELS = [
    "gpt-4-turbo",
    "claude-3-opus",
    "gemini-1.5-pro",
    "gpt-4",
    "claude-3-sonnet",
    "mixtral-8x7b",
    "llama-2-70b-chat",
    "deepseek-coder",
    "yi-34b-chat",
    "qwen-max",
]


def generate_mock_response(prompt: str, model_name: str) -> str:
    """Generate mock responses for demonstration purposes."""
    responses = {
        "gpt-4-turbo": f"GPT-4 Turbo response to: '{prompt[:50]}...' - This is a comprehensive biomedical analysis considering multiple factors including clinical efficacy, safety profiles, and current research findings.",
        "claude-3-opus": f"Claude-3 Opus analysis: '{prompt[:50]}...' - From a biomedical perspective, this requires careful consideration of evidence-based practices and current literature.",
        "gemini-1.5-pro": f"Gemini 1.5 Pro evaluation: '{prompt[:50]}...' - Based on current medical knowledge and research findings, here's my assessment of this biomedical query.",
        "gpt-4": f"GPT-4 response: '{prompt[:50]}...' - This biomedical question involves several key considerations that I'll address systematically.",
        "claude-3-sonnet": f"Claude-3 Sonnet: '{prompt[:50]}...' - In the biomedical context, this requires analysis of clinical data and therapeutic implications.",
    }

    base_response = responses.get(
        model_name,
        f"{model_name} response to: '{prompt[:50]}...' - Detailed biomedical analysis follows.",
    )

    # Add some variation to make responses feel more realistic
    variations = [
        "\n\nKey clinical considerations include:\n• Patient safety profiles\n• Efficacy data from recent studies\n• Contraindications and interactions",
        "\n\nBased on current literature:\n• Meta-analysis results show...\n• Clinical trials indicate...\n• Best practice guidelines recommend...",
        "\n\nImportant factors to consider:\n• Dosage and administration\n• Patient population specifics\n• Long-term outcomes data",
    ]

    return base_response + random.choice(variations)


def select_random_models() -> list[str]:
    """Select two random models for comparison."""
    return random.sample(MOCK_MODELS, 2)


def build_battle_page():
    """
    Build and return the BixArena battle page component.
    This function can be imported and called from main application.
    """

    # Battle page CSS
    battle_css = """
    .battle-container { 
        padding: 20px; 
        max-width: 1200px; 
        margin: 0 auto;
    }
    .model-response { 
        border: 1px solid #ddd; 
        border-radius: 8px; 
        padding: 15px; 
        margin: 10px 0;
        background-color: #f9f9f9;
        min-height: 100px;
    }
    .voting-buttons {
        display: flex;
        justify-content: center;
        gap: 15px;
        margin: 20px 0;
    }
    .instructions {
        background-color: #f0f8ff;
        padding: 20px;
        border-radius: 10px;
        border-left: 4px solid #007acc;
        margin-bottom: 20px;
    }
    .reveal-section {
        background-color: #f0fff0;
        padding: 20px;
        border-radius: 10px;
        border-left: 4px solid #28a745;
        margin-top: 20px;
    }
    """

    with gr.Column(elem_classes="battle-container") as battle_page:
        # State management
        models_state = gr.State([])
        conversation_state = gr.State(ConversationState())

        # Instructions Section
        with gr.Group(elem_classes="instructions"):
            gr.Markdown("""
            # 🥊 BixArena: Biomedical LLM Battle
            
            **How to participate in LLM comparison:**
            1. **Ask a biomedical question** - Enter your prompt about medical topics, research, diagnostics, treatments, etc.
            2. **Review responses** - Two anonymous models will provide answers side-by-side
            3. **Vote for the better response** - Compare quality, accuracy, and helpfulness
            4. **Ask follow-ups** - Continue the conversation to make an informed decision
            5. **Submit your vote** - Help improve biomedical AI by sharing your preference
            """)

        # Input Section
        with gr.Group():
            prompt_input = gr.Textbox(
                placeholder="Ask any biomedical relevant questions...",
                label="Your Question",
                lines=3,
                interactive=True,
            )

            with gr.Row():
                submit_btn = gr.Button("Send", variant="primary", size="lg")
                new_chat_btn = gr.Button("New Chat", variant="secondary", size="lg")

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

            with gr.Row():
                continue_btn = gr.Button(
                    "Continue Evaluating", variant="primary", size="lg"
                )
                view_leaderboard_btn = gr.Button(
                    "View Leaderboard", variant="secondary", size="lg"
                )

        # Helper Functions
        def start_new_conversation():
            """Start a new conversation with random models."""
            models = select_random_models()
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

        def submit_prompt(prompt, models, conv_state):
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
                models = select_random_models()
                conv_state.reset()
                conv_state.models = models

            # Generate responses (replace with actual model API calls)
            response_a = generate_mock_response(prompt, models[0])
            response_b = generate_mock_response(prompt, models[1])

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

        submit_btn.click(
            submit_prompt,
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
            submit_prompt,
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

        continue_btn.click(
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

    return battle_page, battle_css
