FROM public.ecr.aws/lambda/python:3.13

COPY hello_world/app.py requirements.txt ${LAMBDA_TASK_ROOT}/

RUN python3.13 -m pip install --no-cache-dir -r requirements.txt -t .

# Command can be overwritten by providing a different command in the template directly.
CMD ["app.lambda_handler"]
