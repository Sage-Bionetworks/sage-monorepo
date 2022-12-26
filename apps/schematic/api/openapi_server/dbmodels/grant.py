from bson import ObjectId
from mongoengine import Document, StringField, URLField, ObjectIdField


class Grant(Document):
    id = ObjectIdField(primary_key=True, default=ObjectId)
    name = StringField(required=True, unique=True)
    description = StringField(required=True)
    url = URLField()

    def to_dict(self):
        doc = self.to_mongo().to_dict()
        doc["id"] = str(self.pk)
        doc.pop("_id", None)
        return doc
