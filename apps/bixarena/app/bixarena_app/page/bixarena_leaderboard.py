import logging
from datetime import datetime

import gradio as gr
import pandas as pd
from bixarena_api_client import LeaderboardApi, StatsApi
from bixarena_api_client.api_client import ApiClient
from bixarena_api_client.exceptions import ApiException

from bixarena_app.api.api_client_helper import (
    create_authenticated_api_client,
    get_api_configuration,
)

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
        # Return empty DataFrame if API call fails
        return pd.DataFrame(
            {
                "Rank": [],
                "Model": [],
                "BT Score": [],
                "95% CI": [],
                "Total Votes": [],
                "Organization": [],
            }
        )
    except Exception as e:
        logger.error(f"❌ Unexpected error fetching leaderboard data: {e}")
        # Return empty DataFrame if any other error occurs
        return pd.DataFrame(
            {
                "Rank": [],
                "Model": [],
                "BT Score": [],
                "95% CI": [],
                "Total Votes": [],
                "Organization": [],
            }
        )


def fetch_public_stats() -> dict:
    """Fetch public platform statistics from the API.

    Returns:
        Dictionary with models_evaluated and total_battles.
        Returns default values if API call fails.
    """
    try:
        configuration = get_api_configuration()
        with ApiClient(configuration) as client:
            api = StatsApi(client)
            stats = api.get_public_stats()
            logger.info(
                f"Fetched Leaderboard public stats: {stats.models_evaluated} models, "
                f"{stats.total_battles} battles"
            )
            return {
                "models_evaluated": stats.models_evaluated,
                "total_battles": stats.total_battles,
            }
    except Exception as e:
        logger.error(f"Error fetching public stats: {e}")
        return {
            "models_evaluated": 0,
            "total_battles": 0,
        }


def filter_dataframe(df, model_filter):
    """Filter dataframe by model name"""
    if model_filter:
        mask = df["Model"].str.contains(model_filter, case=False, na=False)
        return df[mask]
    return df


def load_leaderboard_stats_on_page_load() -> dict:
    """Load leaderboard stats and update the metrics HTML.

    Returns:
        Gradio update dict for the metrics HTML component
    """
    stats = fetch_public_stats()
    total_battles = stats["total_battles"]
    models_evaluated = stats["models_evaluated"]
    last_updated = datetime.now().strftime("%b %d, %Y")

    metrics_html = f"""
        <div style="display: flex;">
            <div style="flex: 1;">
                <div style="text-align: center; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 12px; color: white; margin: 10px;">
                    <div style="font-size: 2rem; font-weight: bold; margin-bottom: 5px;">{last_updated}</div>
                    <div style="font-size: 1.1rem; opacity: 0.9;">Last Updated</div>
                </div>
            </div>
            <div style="flex: 1;">
                <div style="text-align: center; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 12px; color: white; margin: 10px;">
                    <div style="font-size: 2rem; font-weight: bold; margin-bottom: 5px;">{total_battles:,}</div>
                    <div style="font-size: 1.1rem; opacity: 0.9;">Total Battles</div>
                </div>
            </div>
            <div style="flex: 1;">
                <div style="text-align: center; padding: 20px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); border-radius: 12px; color: white; margin: 10px;">
                    <div style="font-size: 2rem; font-weight: bold; margin-bottom: 5px;">{models_evaluated}</div>
                    <div style="font-size: 1.1rem; opacity: 0.9;">Models Evaluated</div>
                </div>
            </div>
        </div>
        """

    return gr.update(value=metrics_html)


def build_leaderboard_page():
    """Build the BixArena leaderboard page"""
    # Get initial data from API
    # df = fetch_leaderboard_data()
    # logger.info(
    #     f"📈 Leaderboard built with {len(df)} models"
    # )

    with gr.Column():
        # Title and stats
        gr.Markdown("# 🏆 BixArena Leaderboard")
        gr.Markdown("Community-driven evaluation of biomedical LLMs by Synapse users")

        # Metrics - will be populated dynamically on page load
        leaderboard_metrics = gr.HTML("")

        # Coming soon message
        gr.HTML("""
        <div style="
            background: rgba(15, 15, 15, 0.5);
            border: 2px solid rgba(255, 255, 255, 0.2);
            border-radius: 12px;
            padding: 64px 48px;
        ">
            <div style="max-width: 800px; margin: 0 auto; text-align: center;">
                <!-- Icon -->
                <div style="display: flex; justify-content: center; margin-bottom: 24px;">
                    <div style="
                        width: 56px;
                        height: 56px;
                        border-radius: 50%;
                        background: linear-gradient(135deg, rgba(249, 115, 22, 0.2) 0%, rgba(6, 182, 212, 0.2) 100%);
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    ">
                        <span style="font-size: 28px;">⏰</span>
                    </div>
                </div>

                <!-- Title -->
                <h3 style="
                    font-size: 1.25rem;
                    font-weight: 500;
                    margin-bottom: 16px;
                    line-height: 1.5;
                ">
                    Leaderboard Rankings Coming Soon
                </h3>

                <!-- Description -->
                <p style="
                    color: rgba(229, 231, 235, 0.7);
                    line-height: 1.625;
                    margin-bottom: 0;
                    font-size: 1rem;
                ">
                    The leaderboard will be published once we have sufficient evaluations to 
                    ensure statistically meaningful model rankings.
                </p>
                <div style="padding-top: 16px;">
                    <p style="
                        font-size: 0.875rem;
                        color: rgba(229, 231, 235, 0.5);
                        line-height: 1.625;
                        margin: 0;
                    ">
                        Keep battling to help us build a comprehensive benchmark
                    </p>
                </div>
            </div>
        </div>
        """)

        # # Disclaimer sections
        # with gr.Accordion("⚠️ Important Disclaimer", open=True):
        #     gr.Markdown("""
        #     **This is demonstration data only.** The scores and rankings shown are for demo purposes
        #     and do not represent actual performance comparisons of these models.
        #     """)

        # # Filter controls
        # with gr.Row():
        #     model_filter = gr.Textbox(
        #         show_label=False, placeholder="Search models...", scale=3
        #     )

        # # Main leaderboard table
        # leaderboard_table = gr.Dataframe(
        #     value=df,
        #     interactive=False,
        #     wrap=True,
        #     headers=[
        #         "Rank",
        #         "Model",
        #         "BT Score",
        #         "95% CI",
        #         "Total Votes",
        #         "Organization",
        #     ],
        # )

        # # Update functions
        # def update_table(filter_text):
        #     filtered_df = filter_dataframe(df, filter_text)
        #     return filtered_df

        # # Event handlers
        # model_filter.change(
        #     fn=update_table, inputs=[model_filter], outputs=[leaderboard_table]
        # )

    return leaderboard_metrics
