# OpenChallenges Thumbor

## Usage

### Prepare the service

```console
nx prepare openchallenges-thumbor
```

### Start Thumbor and MinIO

```console
nx serve-detach openchallenges-thumbor
```

### Upload an image to MinIO

1. Open MinIO web console http://localhost:9001/.
2. Login into MinIO (see MinIO project folder for the default credentials).
3. Upload an image to the bucket `img`
    - E.g. Download the [Triforce image] and name it `triforce.png`.

### Access the image with Thumbor

Open this link and you should see the resized image:

- http://localhost:8889/unsafe/600x600/triforce.png

Open this ling when the API Gateway is running:

- http://localhost:8082/img/unsafe/triforce.png

## References

- https://github.com/thumbor/thumbor
- https://github.com/thumbor/awesome-thumbor
- https://github.com/beeyev/thumbor-s3-docker

<!-- Links -->

[Triforce image]: https://static.wikia.nocookie.net/zelda_gamepedia_en/images/7/70/ALBW_Triforce_Artwork.png/revision/latest/scale-to-width-down/1000?cb=20140604184126&format=original