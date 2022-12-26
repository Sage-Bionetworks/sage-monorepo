import datetime
from mongoengine import (
    Document,
    ReferenceField,
    StringField,
    IntField,
    DateTimeField,
)  # noqa: E501

from openapi_server.dbmodels.organization import Organization
from openapi_server.dbmodels.user import User


class OrgMembership(Document):
    state = StringField(required=True, choices=["active", "pending"])  # TODO: DRY
    role = StringField(required=True, choices=["admin", "member"])  # TODO: DRY
    organizationId = ReferenceField(Organization)
    userId = ReferenceField(User, unique_with="organizationId")
    createdAt = DateTimeField(required=True, default=datetime.datetime.now)
    updatedAt = DateTimeField(required=True, default=datetime.datetime.now)
    v = IntField(db_field="__v")

    def to_dict(self):
        doc = self.to_mongo().to_dict()
        doc["id"] = str(self.pk)
        return doc
