import gradio as gr


def create_intro_section():
    """Create the intro section of the homepage"""

    with gr.Row():
        with gr.Column():
            gr.HTML("""
            <div style="text-align: center; padding: 45px 20px;">
                <h1 style="font-size: 3rem; margin-bottom: 30px; color: white;">
                    Welcome to BixArena
                </h1>
                <p style="font-size: 1.2rem; line-height: 1.6; max-width: 800px; margin: 0 auto; color: #e5e7eb;">
                    Anyone with a computer and an internet connection can evaluate and benchmark Large 
                    Language Models (LLMs) to solve major biomedical problems through a community-driven 
                    platform known as "BixArena." This web-based evaluation arena helps biomedical researchers 
                    compare LLMs on domain-specific tasks, fostering collaboration and establishing best 
                    practices. The top-performing models might just accelerate the next breakthrough in clinical 
                    technology or drug discovery.
                </p>
            </div>
            """)


def build_stats_section():
    """Create the statistics section with metrics"""

    with gr.Row():
        with gr.Column():
            with gr.Group():
                gr.HTML("""
                <div style="text-align: center; padding: 20px;">
                    <p style="color: #9ca3af; text-transform: uppercase; font-size: 0.9rem; margin-bottom: 10px;">
                        MODELS SUPPORTED
                    </p>
                    <h2 style="color: #2dd4bf; font-size: 3rem; margin: 0;">15</h2>
                </div>
                """)

        with gr.Column():
            with gr.Group():
                gr.HTML("""
                <div style="text-align: center; padding: 20px;">
                    <p style="color: #9ca3af; text-transform: uppercase; font-size: 0.9rem; margin-bottom: 10px;">
                        VOTES COLLECTED
                    </p>
                    <h2 style="color: #2dd4bf; font-size: 3rem; margin: 0;">12,500</h2>
                </div>
                """)

        with gr.Column():
            with gr.Group():
                gr.HTML("""
                <div style="text-align: center; padding: 20px;">
                    <p style="color: #9ca3af; text-transform: uppercase; font-size: 0.9rem; margin-bottom: 10px;">
                        PARTICIPANTS
                    </p>
                    <h2 style="color: #2dd4bf; font-size: 3rem; margin: 0;">450</h2>
                </div>
                """)


def build_cta_section():
    """Create the call-to-action section"""

    with gr.Row():
        with gr.Column():
            gr.HTML("""
            <div style="text-align: center; padding: 40px 20px;">
                <h2 style="font-size: 2rem; margin-bottom: 20px; color: white;">
                    Ready to Shape the Future of Biomedical AI?
                </h2>
                <p style="font-size: 1.1rem; color: #e5e7eb; margin-bottom: 30px;">
                    Join our community of researchers and help evaluate the next generation of AI models for healthcare breakthroughs.
                </p>
            </div>
            """)

    # Start Evaluating Button
    with gr.Row():
        with gr.Column(scale=2):
            pass
        with gr.Column(scale=1):
            start_btn = gr.Button(
                "Start Evaluating Models", variant="primary", size="lg"
            )
        with gr.Column(scale=2):
            pass

    return start_btn


def build_home_page():
    """Create the complete home page layout"""

    with gr.Column() as home_page:
        # Add some spacing after header
        gr.HTML("<br>")

        # Intro Section
        create_intro_section()

        # Stats Section
        build_stats_section()

        # Call to Action Section
        start_btn = build_cta_section()

    return home_page, start_btn
