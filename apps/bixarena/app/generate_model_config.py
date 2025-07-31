import os
import re
from dotenv import load_dotenv

load_dotenv()  # Automatically loads from .env

with open("data/model_config.template.json") as f:
    content = f.read()

# Replace ${VAR} with actual environment values
content = re.sub(
    r"\$\{(\w+)\}", lambda m: os.environ.get(m[1], f"<missing:{m[1]}>"), content
)

with open("data/model_config.json", "w") as f:
    f.write(content)
