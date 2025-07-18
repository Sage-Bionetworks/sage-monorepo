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
    "fschat[model_worker,webui]==0.2.36" \
    gradio-client==0.15.0 \
    gradio==4.25.0 \
    plotly==5.22.0 \
    psutil==5.9.8 \
    torch==2.2.2 \
    transformers==4.40.1

EXPOSE 8100

ENTRYPOINT ["python3.9", "-m", "fastchat.serve.gradio_web_server_multi", "--controller-url", "http://bioarena-controller:21001", "--port", "8100"]
