import logging

import gradio as gr
import pandas as pd
from bixarena_api_client import LeaderboardApi
from bixarena_api_client.exceptions import ApiException

from bixarena_app.api.api_client_helper import create_authenticated_api_client

logger = logging.getLogger(__name__)


def fetch_leaderboard_data(jwt_token: str | None = None):
    """Fetch leaderboard data from the BixArena API

    Args:
        jwt_token: Optional JWT token for authenticated API calls
    """
    try:
        logger.info("📊 Fetching leaderboard data for 'open-source'...")
        if jwt_token:
            logger.debug("🔑 Using JWT token for authenticated API call")

        # Create API client and leaderboard API instance
        with create_authenticated_api_client(jwt_token) as api_client:
            api_instance = LeaderboardApi(api_client)

            # Fetch leaderboard entries for "open-source" leaderboard
            logger.debug("📊 Fetching leaderboard data for 'open-source'...")
            leaderboard_response = api_instance.get_leaderboard(
                leaderboard_id="open-source"
            )

            logger.info(
                f"✅ API call successful! Received {len(leaderboard_response.entries)} entries"
            )

            # Convert API response to DataFrame
            data = {
                "Rank": [],
                "Model": [],
                "BT Score": [],
                "95% CI": [],
                "Total Votes": [],
                "Organization": [],
            }

            for entry in leaderboard_response.entries:
                data["Rank"].append(entry.rank)
                data["Model"].append(entry.model_name)
                data["BT Score"].append(entry.bt_score)
                # Placeholder CI calculation
                ci_lower = entry.bt_score - 15
                ci_upper = entry.bt_score + 15
                data["95% CI"].append(f"[{ci_lower:.1f}, {ci_upper:.1f}]")
                data["Total Votes"].append(entry.vote_count)
                # API doesn't provide organization, using placeholder
                data["Organization"].append("Unknown")

            logger.info(f"📋 Converted to DataFrame with {len(data['Rank'])} rows")
            return pd.DataFrame(data)

    except ApiException as e:
        logger.error(
            f"❌ API Exception when calling LeaderboardApi->get_leaderboard: {e}"
        )
        logger.info("🔄 Falling back to dummy data...")
        # Fallback to dummy data if API call fails
        return create_dummy_leaderboard_data()
    except Exception as e:
        logger.error(f"❌ Unexpected error fetching leaderboard data: {e}")
        logger.info("🔄 Falling back to dummy data...")
        # Fallback to dummy data if any other error occurs
        return create_dummy_leaderboard_data()


def create_dummy_leaderboard_data():
    """Create dummy leaderboard data"""
    data = {
        "Rank": list(range(1, 16)),
        "Model": [
            "gpt-4o-2024-11-20",
            "claude-3.5-sonnet-20241022",
            "gemini-2.0-flash-exp",
            "deepseek-chat-v3",
            "llama-3.3-70b-instruct",
            "gpt-4o-mini-2024-07-18",
            "claude-3.5-haiku-20241022",
            "qwen2.5-72b-instruct",
            "mixtral-8x7b-instruct",
            "gemini-1.5-pro-exp-0827",
            "llama-3.1-405b-instruct",
            "mistral-large-2407",
            "deepseek-r1-distill-llama-70b",
            "yi-34b-chat",
            "codestral-latest",
        ],
        "BT Score": [
            1456.2,
            1398.7,
            1367.1,
            1334.8,
            1298.3,
            1256.9,
            1234.5,
            1198.2,
            1167.4,
            1134.7,
            1098.3,
            1067.9,
            1023.4,
            987.6,
            954.1,
        ],
        "95% CI": [
            "[1441.8, 1470.6]",
            "[1384.2, 1413.1]",
            "[1352.6, 1381.5]",
            "[1320.1, 1349.4]",
            "[1283.9, 1312.7]",
            "[1242.1, 1271.6]",
            "[1219.8, 1249.1]",
            "[1183.7, 1212.6]",
            "[1152.9, 1181.8]",
            "[1120.3, 1149.0]",
            "[1084.1, 1112.4]",
            "[1053.6, 1082.1]",
            "[1009.2, 1037.5]",
            "[973.4, 1001.7]",
            "[940.2, 968.0]",
        ],
        "Total Votes": [
            8247,
            7156,
            6934,
            5876,
            5654,
            4432,
            4298,
            3123,
            2847,
            2456,
            2134,
            1987,
            1654,
            1432,
            1298,
        ],
        "Organization": [
            "OpenAI",
            "Anthropic",
            "Google",
            "DeepSeek",
            "Meta",
            "OpenAI",
            "Anthropic",
            "Alibaba",
            "Mistral AI",
            "Google",
            "Meta",
            "Mistral AI",
            "DeepSeek",
            "01.AI",
            "Mistral AI",
        ],
    }
    return pd.DataFrame(data)


def filter_dataframe(df, model_filter):
    """Filter dataframe by model name"""
    if model_filter:
        mask = df["Model"].str.contains(model_filter, case=False, na=False)
        return df[mask]
    return df


def build_leaderboard_page():
    """Build the BixArena leaderboard page"""
    logger.info("📊 Building leaderboard page...")

    # Get initial data from API
    df = fetch_leaderboard_data()
    total_votes = df["Total Votes"].sum()
    total_models = len(df)
    logger.info(
        f"📈 Leaderboard built with {total_models} models and {total_votes} total votes"
    )

    with gr.Column():
        # Title and stats
        gr.Markdown("# 🏆 BixArena Leaderboard")
        gr.Markdown("Community-driven evaluation of biomedical LLMs by Synapse users")

        # Stats row
        with gr.Row():
            with gr.Column(scale=1):
                gr.HTML("""
                <div style="text-align: center; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 12px; color: white; margin: 10px;">
                    <div style="font-size: 2rem; font-weight: bold; margin-bottom: 5px;">Aug 16, 2025</div>
                    <div style="font-size: 0.9rem; opacity: 0.9;">Last Updated</div>
                </div>
                """)
            with gr.Column(scale=1):
                gr.HTML(f"""
                <div style="text-align: center; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 12px; color: white; margin: 10px;">
                    <div style="font-size: 2rem; font-weight: bold; margin-bottom: 5px;">{total_votes:,}</div>
                    <div style="font-size: 0.9rem; opacity: 0.9;">Total Votes</div>
                </div>
                """)
            with gr.Column(scale=1):
                gr.HTML(f"""
                <div style="text-align: center; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 12px; color: white; margin: 10px;">
                    <div style="font-size: 2rem; font-weight: bold; margin-bottom: 5px;">{total_models}</div>
                    <div style="font-size: 0.9rem; opacity: 0.9;">Total Models</div>
                </div>
                """)

        gr.Markdown("---")

        # Disclaimer sections
        with gr.Accordion("⚠️ Important Disclaimer", open=True):
            gr.Markdown("""
            **This is demonstration data only.** The scores and rankings shown are for demo purposes
            and do not represent actual performance comparisons of these models.
            """)

        # Filter controls
        with gr.Row():
            model_filter = gr.Textbox(
                show_label=False, placeholder="Search models...", scale=3
            )

        # Main leaderboard table
        leaderboard_table = gr.Dataframe(
            value=df,
            interactive=False,
            wrap=True,
            headers=[
                "Rank",
                "Model",
                "BT Score",
                "95% CI",
                "Total Votes",
                "Organization",
            ],
        )

        # Update functions
        def update_table(filter_text):
            filtered_df = filter_dataframe(df, filter_text)
            return filtered_df

        # Event handlers
        model_filter.change(
            fn=update_table, inputs=[model_filter], outputs=[leaderboard_table]
        )

    return leaderboard_table
