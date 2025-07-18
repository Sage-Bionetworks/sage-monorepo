# This file is originally modified from:
# https://github.com/lm-sys/FastChat/blob/main/docker/Dockerfile

FROM nvidia/cuda:12.2.0-runtime-ubuntu20.04

RUN apt-get update -y && apt-get install -y \
    python3.9 python3.9-distutils curl && \
    curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py && \
    python3.9 get-pip.py && \
    python3.9 -m pip install fschat && \
    python3.9 -m pip install fschat[model_worker,webui]

EXPOSE 21001

ENTRYPOINT ["python3.9", "-m", "fastchat.serve.controller", "--host", "0.0.0.0", "--port", "21001"]