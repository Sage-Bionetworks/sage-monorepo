#!/usr/bin/env python3
"""
Script to start FastChat with database-driven model configuration.
This replaces the need for envsubst and model_config.template.json.
"""

import sys
import subprocess
from src.model.fastchat_integration import create_fastchat_config_file


def main():
    """Generate config file and start FastChat with it."""
    if len(sys.argv) < 2:
        print("Usage: start_fastchat.py <fastchat_module> [fastchat_args...]")
        print("Example: start_fastchat.py main --port 8080")
        print(
            "Example: start_fastchat.py fastchat.serve.gradio_web_server_multi --port 8080 --controller-url ''"
        )
        sys.exit(1)

    # Generate the configuration file
    try:
        config_file = create_fastchat_config_file()
        print(f"Generated FastChat config at: {config_file}")
    except Exception as e:
        print(f"Error generating config file: {e}")
        sys.exit(1)

    # Build the command
    fastchat_module = sys.argv[1]
    fastchat_args = sys.argv[2:]

    cmd = ["uv", "run", "python", "-m", fastchat_module]
    cmd.extend(fastchat_args)
    cmd.extend(["--register-api-endpoint-file", config_file])

    print(f"Starting FastChat: {' '.join(cmd)}")

    # Start FastChat
    try:
        subprocess.run(cmd, check=True)
    except subprocess.CalledProcessError as e:
        print(f"FastChat failed with exit code {e.returncode}")
        sys.exit(e.returncode)
    except KeyboardInterrupt:
        print("\nShutting down...")
    finally:
        # Clean up the config file
        import os

        try:
            os.unlink(config_file)
            print(f"Cleaned up config file: {config_file}")
        except:
            pass


if __name__ == "__main__":
    main()
