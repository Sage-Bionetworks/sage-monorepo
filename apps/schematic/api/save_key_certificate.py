import os
import base64
import json

secret_manager_secrets = os.environ["SECRETS_MANAGER_SECRETS"]

ssl_private_key = json.loads(secret_manager_secrets)["SSL_PRIVATE_KEY"]
ssl_certificate_key = json.loads(secret_manager_secrets)["SSL_CERTIFICATE"]

# save the key and certificate as files
test_private_key_file_path = "/etc/ssl/private/localhost.key"
test_certificate_key_file_path = "/etc/ssl/certs/localhost.crt"

with open(test_private_key_file_path, "wb") as file:
    decoded_private_key = base64.b64decode(ssl_private_key)
    file.write(decoded_private_key)

with open(test_certificate_key_file_path, "wb") as file:
    decoded_ssl_certificate_key = base64.b64decode(ssl_certificate_key)
    file.write(decoded_ssl_certificate_key)
