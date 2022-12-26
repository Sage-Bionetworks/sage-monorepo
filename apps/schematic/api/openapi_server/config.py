import os
import string
import random


defaultValues = {
    "SERVER_PROTOCOL": "http://",
    "SERVER_DOMAIN": "localhost",
    "SERVER_PORT": "8080",
    "SERVER_SECRET_KEY": "".join(
        random.sample(string.ascii_letters + string.digits, 32)
    ),  # noqa: E501
    "DB_PROTOCOL": "mongodb://",
    "DB_DOMAIN": "localhost",
    "DB_PORT": "27017",
    "DB_DATABASE": "rocc",
    "DB_USERNAME": "roccmongo",
    "DB_PASSWORD": "roccmongo",
}


class AbstractConfig(object):
    """
    Parent class containing get_property to return the ENV variable of
    default value if not found.
    """

    def __init__(self):
        self._defaultValues = defaultValues

    def get_property(self, property_name):
        if os.getenv(property_name) is not None:
            return os.getenv(property_name)
        return self._defaultValues.get(property_name)


class Config(AbstractConfig):
    """
    Class providing hard-coded values to the application, first using
    environment variables, and if not found, defaulting to those values
    provided in the defaultValues dictionary above.
    """

    @property
    def server_protocol(self):
        return self.get_property("SERVER_PROTOCOL")

    @property
    def server_domain(self):
        return self.get_property("SERVER_DOMAIN")

    @property
    def server_port(self):
        return self.get_property("SERVER_PORT")

    @property
    def server_url(self):
        return "%s%s:%s" % (
            self.server_protocol,
            self.server_domain,
            self.server_port,
        )

    @property
    def server_api_url(self):
        return "{server_url}{base_path}".format(
            server_url=self.server_url, base_path="/api/v1"
        )

    @property
    def secret_key(self):
        return self.get_property("SERVER_SECRET_KEY")

    @property
    def db_protocol(self):
        return self.get_property("DB_PROTOCOL")

    @property
    def db_domain(self):
        return self.get_property("DB_DOMAIN")

    @property
    def db_port(self):
        return self.get_property("DB_PORT")

    @property
    def db_database(self):
        return self.get_property("DB_DATABASE")

    @property
    def db_username(self):
        return self.get_property("DB_USERNAME")

    @property
    def db_password(self):
        return self.get_property("DB_PASSWORD")

    @property
    def db_host(self):
        return "%s%s:%s" % (self.db_protocol, self.db_domain, self.db_port)


config = Config()
