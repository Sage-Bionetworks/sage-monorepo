import datetime
from bson import ObjectId
from mongoengine import (
    Document,
    ObjectIdField,
    StringField,
    IntField,
    DateTimeField,
)  # noqa: E501


class Account(Document):
    id = ObjectIdField(primary_key=True, default=ObjectId)
    login = StringField(required=True, unique=True)
    type = StringField(required=True, choices=["User", "Organization"])
    createdAt = DateTimeField(required=True, default=datetime.datetime.now)
    updatedAt = DateTimeField(required=True, default=datetime.datetime.now)
    v = IntField(db_field="__v")

    meta = {"allow_inheritance": True}

    def to_dict(self):
        doc = self.to_mongo().to_dict()
        doc["id"] = str(self.pk)
        return doc
