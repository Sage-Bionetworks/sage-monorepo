from mongoengine import EmailField, StringField, URLField  # noqa: E501
from werkzeug.security import check_password_hash, generate_password_hash

from openapi_server.dbmodels.account import Account


class User(Account):
    email = EmailField(required=True)
    name = StringField(required=True)
    avatarUrl = URLField()
    bio = StringField()
    passwordHash = StringField(required=True)

    def to_dict(self):
        doc = self.to_mongo().to_dict()
        doc["id"] = str(self.pk)
        doc.pop("passwordHash", None)
        return doc

    def verify_password(self, password) -> bool:
        return check_password_hash(self.passwordHash, password)

    @staticmethod
    def generate_password_hash(password) -> None:
        return generate_password_hash(password)
