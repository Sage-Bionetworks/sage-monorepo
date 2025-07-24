FROM python:3.9-slim

RUN pip install --no-cache-dir fschat==0.2.36

EXPOSE 21001

ENTRYPOINT ["python", "-m", "fastchat.serve.controller", "--host", "0.0.0.0", "--port", "21001"]
