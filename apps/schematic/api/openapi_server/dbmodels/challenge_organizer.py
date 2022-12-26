import datetime
from bson import ObjectId
from mongoengine import (
    DateTimeField,
    Document,
    ObjectIdField,
    StringField,
    ListField,
    ReferenceField,
    IntField,
)  # noqa: E501

from openapi_server.dbmodels.challenge import Challenge


class ChallengeOrganizer(Document):
    id = ObjectIdField(primary_key=True, default=ObjectId)
    name = StringField(required=True)
    login = StringField()
    roles = ListField(
        StringField(choices=["ChallengeLead", "InfrastructureLead"]),
        default=[],
    )  # noqa: E501
    challengeId = ReferenceField(Challenge, required=True)
    createdAt = DateTimeField(required=True, default=datetime.datetime.now)
    updatedAt = DateTimeField(required=True, default=datetime.datetime.now)
    v = IntField(db_field="__v")

    def to_dict(self):
        doc = self.to_mongo().to_dict()
        doc["id"] = str(self.pk)
        doc["challengeId"] = str(self.challengeId["id"])
        # doc.pop('challengeId', None)
        return doc
