# This file is originally modified from:
# https://github.com/lm-sys/FastChat/blob/main/docker/Dockerfile

FROM nvidia/cuda:12.2.0-runtime-ubuntu20.04

RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        curl \
        python3.9 \
        python3.9-distutils && \
    curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py && \
    python3.9 get-pip.py && \
    rm -rf /var/lib/apt/lists/* get-pip.py && \
    python3.9 -m pip install --no-cache-dir \
        fschat==0.2.36

HEALTHCHECK --interval=5s --timeout=3s --retries=5 --start-period=5s \
  CMD curl --fail --silent --show-error -X POST http://localhost:21001/list_language_models || exit 1

EXPOSE 21001

ENTRYPOINT ["python3.9", "-m", "fastchat.serve.controller", "--host", "0.0.0.0", "--port", "21001"]
