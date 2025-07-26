FROM python:3.10-slim

RUN pip install --no-cache-dir \
    "fschat[model_worker,webui]==0.2.36" \
    gradio==5.34.2 \
    gradio_client==1.10.3 \
    openai==1.91.0 \
    plotly==6.1.2 \
    psutil==5.9.8 \
    torch==2.2.2 \
    transformers==4.52.4 && \
    apt-get update && \
    apt-get install -y --no-install-recommends gettext && \
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY entrypoint.sh .
COPY models.template.json .
RUN chmod +x /app/entrypoint.sh

EXPOSE 8100

ENTRYPOINT ["/app/entrypoint.sh"]