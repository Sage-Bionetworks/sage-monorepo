from bson import ObjectId
import datetime
from mongoengine import (
    DateTimeField,
    Document,
    ReferenceField,
    StringField,
    ObjectIdField,
    URLField,
    ListField,
    IntField,
    BooleanField,
)  # noqa: E501

from openapi_server.dbmodels.account import Account
from openapi_server.dbmodels.challenge_platform import ChallengePlatform
from openapi_server.dbmodels.challenge_readme import ChallengeReadme


class Challenge(Document):
    id = ObjectIdField(primary_key=True, default=ObjectId)
    name = StringField(required=True)
    displayName = StringField(min_length=3, max_length=60)
    description = StringField(required=True)
    fullName = (
        StringField()
    )  # TODO restore required=True, unique=True after fixing JSON seed # noqa: E501
    ownerId = ReferenceField(Account)
    websiteUrl = URLField()
    status = StringField(
        # required=True,
        choices=["active", "upcoming", "completed"]  # TODO: DRY
    )
    startDate = DateTimeField()
    endDate = DateTimeField()
    platformId = ReferenceField(ChallengePlatform)
    readmeId = ReferenceField(ChallengeReadme)
    topics = ListField(StringField(unique=True), default=[])
    difficulty = StringField(
        choices=["GoodForBeginners", "Intermediate", "Advanced"]  # TODO: DRY
    )
    inputDataTypes = ListField(StringField(unique=True), default=[])
    submissionTypes = ListField(
        StringField(choices=["DockerImage", "PredictionFile", "Other"])  # TODO: DRY
    )
    incentiveTypes = ListField(
        StringField(
            # TODO: DRY
            choices=[
                "Monetary",
                "Publication",
                "SpeakingEngagement",
                "Other",
            ]  # noqa: E501
        )
    )
    featured = BooleanField(default=False)
    participantCount = IntField(default=0)
    viewCount = IntField(default=0)
    starredCount = IntField(default=0)
    doi = StringField()
    createdAt = DateTimeField(required=True, default=datetime.datetime.now)
    updatedAt = DateTimeField(required=True, default=datetime.datetime.now)
    v = IntField(db_field="__v")

    meta = {
        "indexes": [
            {
                "fields": ["$name", "$description", "$topics"],
                "default_language": "english",
                "weights": {"name": 10, "description": 2, "topics": 8},
            }
        ]
    }

    def to_dict(self):
        doc = self.to_mongo().to_dict()
        doc["id"] = str(self.pk)
        return doc
