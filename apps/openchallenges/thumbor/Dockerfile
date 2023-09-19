FROM beeyev/thumbor-s3:7.4-alpine

RUN apk update \
    && apk --no-cache --update add build-base 
RUN pip install --upgrade pip \
    && pip install scikit-build cmake \
    && pip install --no-use-pep517 opencv-python --no-cache-dir