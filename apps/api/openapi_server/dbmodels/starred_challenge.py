from mongoengine import Document, ReferenceField  # noqa: E501

from openapi_server.dbmodels.challenge import Challenge
from openapi_server.dbmodels.user import User


class StarredChallenge(Document):
    challengeId = ReferenceField(Challenge)
    userId = ReferenceField(User, unique_with="challengeId")

    def to_dict(self):
        doc = self.to_mongo().to_dict()
        doc["id"] = str(self.pk)
        return doc
