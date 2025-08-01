import os
import re

with open("model_config.template.json") as f:
    content = f.read()

# Replace ${VAR} with actual environment values
content = re.sub(
    r"\$\{(\w+)\}", lambda m: os.environ.get(m[1], f"<missing:{m[1]}>"), content
)

with open("model_config.json", "w") as f:
    f.write(content)
