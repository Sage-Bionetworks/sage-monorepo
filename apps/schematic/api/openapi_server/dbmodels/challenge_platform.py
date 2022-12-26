import datetime
from bson import ObjectId
from mongoengine import (
    DateTimeField,
    Document,
    ObjectIdField,
    StringField,
    URLField,
    IntField,
)  # noqa: E501


class ChallengePlatform(Document):
    id = ObjectIdField(primary_key=True, default=ObjectId)
    name = StringField(required=True, unique=True)
    displayName = StringField(required=True, unique=True)
    websiteUrl = URLField(required=True)
    avatarUrl = URLField()
    createdAt = DateTimeField(required=True, default=datetime.datetime.now)
    updatedAt = DateTimeField(required=True, default=datetime.datetime.now)
    v = IntField(db_field="__v")

    def to_dict(self):
        doc = self.to_mongo().to_dict()
        doc["id"] = str(self.pk)
        return doc
