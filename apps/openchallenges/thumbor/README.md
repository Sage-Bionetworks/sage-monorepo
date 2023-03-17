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

## S3 IAM Policy

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "OpenChallengesReadOnlyS3BucketPolicy",
            "Effect": "Allow",
            "Action": [
                "s3:ListBucket",
                "s3:GetObject*"
            ],
            "Resource": [
                "arn:aws:s3:::openchallenges-asset",
                "arn:aws:s3:::openchallenges-asset/*"
            ]
        }
    ]
}
```

OpenChallengesAssetS3BucketReadWriteAccess:

```json
{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Sid": "ListObjectsInBucket",
            "Effect": "Allow",
            "Action": ["s3:ListBucket"],
            "Resource": ["arn:aws:s3:::openchallenges-asset"]
        },
        {
            "Sid": "AllObjectActions",
            "Effect": "Allow",
            "Action": "s3:*Object",
            "Resource": ["arn:aws:s3:::openchallenges-asset/*"]
        }
    ]
}
```

--conf=/usr/local/etc/thumbor.conf

By default, Thumbor starts as many processes as the number of cores available.

## References

- https://github.com/thumbor/thumbor
- https://github.com/thumbor/thumbor-aws
- https://github.com/beeyev/thumbor-s3-docker
- https://github.com/thumbor/awesome-thumbor

<!-- Links -->

[Triforce image]: https://static.wikia.nocookie.net/zelda_gamepedia_en/images/7/70/ALBW_Triforce_Artwork.png/revision/latest/scale-to-width-down/1000?cb=20140604184126&format=original