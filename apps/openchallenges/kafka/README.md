# OpenChallenges Kafka

## Usage

List metadata from broker `localhost:19092`:

```console
kafkacat -L -b localhost:19092
```

Consume from broker `localhost:19092` and topic `kaggle-topic` with offset set to the beginning:

```console
kafkacat -C -b localhost:19092 -t kaggle-topic -o beginning
```