import datetime
from bson import ObjectId
from mongoengine import (
    DateTimeField,
    Document,
    ObjectIdField,
    StringField,
    IntField,
)  # noqa: E501


class ChallengeReadme(Document):
    id = ObjectIdField(primary_key=True, default=ObjectId)
    text = StringField(required=True)
    createdAt = DateTimeField(required=True, default=datetime.datetime.now)
    updatedAt = DateTimeField(required=True, default=datetime.datetime.now)
    v = IntField(db_field="__v")

    def to_dict(self):
        doc = self.to_mongo().to_dict()
        doc["id"] = str(self.pk)
        return doc
