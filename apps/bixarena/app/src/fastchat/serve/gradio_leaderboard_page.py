"""
BixArena Leaderboard Page
Separate file for leaderboard functionality
"""

import gradio as gr
import pandas as pd


def create_dummy_leaderboard_data():
    """Create dummy leaderboard data for testing"""
    data = {
        "Rank": [1, 2, 3, 4, 5, 6, 7, 8],
        "Model": [
            "gpt-4-turbo",
            "claude-3-opus",
            "gpt-4",
            "claude-3-sonnet",
            "gemini-pro",
            "llama-2-70b",
            "mixtral-8x7b",
            "llama-2-13b",
        ],
        "Score": [1245, 1189, 1167, 1134, 1098, 1045, 1012, 987],
        "Votes": [2847, 2156, 2934, 1876, 1654, 1432, 1298, 1123],
        "Organization": [
            "OpenAI",
            "Anthropic",
            "OpenAI",
            "Anthropic",
            "Google",
            "Meta",
            "Mistral AI",
            "Meta",
        ],
    }
    return pd.DataFrame(data)


def build_leaderboard_page():
    """Build the BixArena leaderboard tab"""
    with gr.Column():

        df = create_dummy_leaderboard_data()

        gr.Markdown("# 🏆 BixArena Leaderboard")

        gr.Markdown(f"Comparing **{len(df)} LLMs** ranked by community votes")

        leaderboard_table = gr.Dataframe(value=df, interactive=False, wrap=True)

        gr.Markdown("*Updated daily based on community votes*")

    return leaderboard_table
