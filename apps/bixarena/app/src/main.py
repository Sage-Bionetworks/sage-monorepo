import argparse
import gradio as gr
from libs.bixarena_header import build_header
from libs.bixarena_battle import build_battle_page
from libs.bixarena_leaderboard import build_leaderboard_page
from libs.bixarena_home import build_home_page
from libs.login import build_login_page


class PageNavigator:
    """Clean navigation class for managing page visibility"""

    def __init__(self, sections):
        self.sections = sections

    def show_page(self, page_index):
        """Show only the specified page, hide others"""
        return [gr.Column(visible=(i == page_index)) for i in range(len(self.sections))]


def parse_args():
    """Parse command line arguments"""
    parser = argparse.ArgumentParser()
    parser.add_argument("--host", type=str, default="127.0.0.1")
    parser.add_argument("--port", type=int)
    parser.add_argument(
        "--share",
        action="store_true",
        help="Whether to generate a public, shareable link",
    )
    parser.add_argument(
        "--controller-url",
        type=str,
        default="",
        help="The address of the controller",
    )
    parser.add_argument(
        "--concurrency-count",
        type=int,
        default=10,
        help="The concurrency count of the gradio queue",
    )
    parser.add_argument(
        "--register-api-endpoint-file",
        type=str,
        help="Register API-based model endpoints from a JSON file",
    )
    parser.add_argument(
        "--moderate",
        action="store_true",
        help="Enable content moderation to block unsafe inputs",
    )
    parser.add_argument(
        "--gradio-root-path",
        type=str,
        help="Sets the gradio root path, eg /abc/def. Useful when running behind a reverse-proxy or at a custom URL path prefix",
    )
    return parser.parse_args()


def build_app(register_api_endpoint_file=None, controller_url=None, moderate=False):
    """Create the main application"""

    with gr.Blocks(title="BixArena - Biomedical LLM Evaluation") as app:
        # Header
        header, battle_btn, leaderboard_btn, login_btn = build_header()

        # Pages
        with gr.Column(visible=True) as home_page:  # Home visible by default
            home_content, cta_btn = build_home_page()

        with gr.Column(visible=False) as battle_page:
            battle_content = build_battle_page(
                register_api_endpoint_file, controller_url, moderate
            )

        with gr.Column(visible=False) as leaderboard_page:
            leaderboard_content = build_leaderboard_page()

        with gr.Column(visible=False) as login_page:
            login_content = build_login_page()

        # Initialize navigator
        all_pages = [
            home_page,
            battle_page,
            leaderboard_page,
            login_page,
        ]
        navigator = PageNavigator(all_pages)

        # Page navigation
        battle_btn.click(lambda: navigator.show_page(1), outputs=all_pages)  # Battle
        leaderboard_btn.click(
            lambda: navigator.show_page(2), outputs=all_pages
        )  # Leaderboard
        login_btn.click(lambda: navigator.show_page(3), outputs=all_pages)  # Login
        cta_btn.click(
            lambda: navigator.show_page(1), outputs=all_pages
        )  # Home -> Battle

    return app


# Launch the app
if __name__ == "__main__":
    args = parse_args()

    app = build_app(
        register_api_endpoint_file=args.register_api_endpoint_file,
        controller_url=args.controller_url,
        moderate=args.moderate,
    )

    app.queue(
        default_concurrency_limit=args.concurrency_count,
        status_update_rate=10,
        api_open=False,
    ).launch(
        server_name=args.host,
        server_port=args.port,
        share=args.share,
        max_threads=200,
        root_path=args.gradio_root_path,
    )
