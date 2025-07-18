FROM python:3.9-slim
RUN pip install --no-cache-dir \
    "fschat[model_worker,webui]==0.2.36" \
    gradio-client==0.15.0 \
    gradio==4.25.0 \
    plotly==5.22.0 \
    psutil==5.9.8 \
    torch==2.2.2 \
    transformers==4.40.1

EXPOSE 8100

ENTRYPOINT ["python", "-m", "fastchat.serve.gradio_web_server_multi", "--controller-url", "http://bioarena-controller:21001", "--port", "8100"]
