from mongoengine import EmailField, StringField, URLField  # noqa: E501

from openapi_server.dbmodels.account import Account


class Organization(Account):
    email = EmailField()
    name = StringField()
    avatarUrl = URLField()
    websiteUrl = URLField()
    description = StringField()

    def to_dict(self):
        doc = self.to_mongo().to_dict()
        doc["id"] = str(self.pk)
        return doc
