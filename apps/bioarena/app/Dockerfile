# This file is originally modified from:
# https://github.com/lm-sys/FastChat/blob/main/docker/Dockerfile

FROM nvidia/cuda:12.2.0-runtime-ubuntu20.04

RUN apt-get update -y && apt-get install -y \
    python3.9 python3.9-distutils curl && \
    curl https://bootstrap.pypa.io/get-pip.py -o get-pip.py && \
    python3.9 get-pip.py
    
RUN python3.9 -m pip install fschat

RUN python3.9 -m pip install fschat[model_worker,webui]

RUN python3.9 -m pip install psutil torch transformers plotly

RUN python3.9 -m pip install "gradio==4.25.0" "gradio-client==0.15.0"


EXPOSE 8100

ENTRYPOINT ["python3.9", "-m", "fastchat.serve.gradio_web_server_multi", "--controller-url", "http://bioarena-controller:21001", "--port", "8100"]