from environs import Env
import subprocess
import json
import base64
import os

# Create an instance of Env
env = Env()

# Load the .env file
env.read_env(path=".env")

# Define key and certificate file path
test_private_key_file_path = "test_private_key.key"
test_certificate_key_file_path = "test_certificate.crt"

# Access the variables
secret_manager_secrets = os.environ["SECRETS_MANAGER_SECRETS"]

ssl_private_key = json.loads(secret_manager_secrets)["SSL_PRIVATE_KEY"]
ssl_certificate_key = json.loads(secret_manager_secrets)["SSL_CERTIFICATE"]

# delete preivous results if necessary
if os.path.exists(test_private_key_file_path):
    os.remove(test_private_key_file_path)
if os.path.exists(test_certificate_key_file_path):
    os.remove(test_certificate_key_file_path)

# make sure that key and certificate can be decoded in correct format
with open(test_private_key_file_path, "wb") as file:
    decoded_private_key = base64.b64decode(ssl_private_key)
    file.write(decoded_private_key)

with open(test_certificate_key_file_path, "wb") as file:
    decoded_ssl_certificate_key = base64.b64decode(ssl_certificate_key)
    file.write(decoded_ssl_certificate_key)


# Make sure that certificate and key match each other
def get_md5_cert(file):
    openssl_x509_command = ["openssl", "x509", "-noout", "-modulus", "-in", file]
    openssl_md5_command = ["openssl", "md5"]

    x509_process = subprocess.Popen(openssl_x509_command, stdout=subprocess.PIPE)
    md5_process = subprocess.Popen(
        openssl_md5_command, stdin=x509_process.stdout, stdout=subprocess.PIPE
    )

    output, error = md5_process.communicate()

    if error:
        print("error getting md5", error.decode("utf-8"))

    return output.decode("utf-8").strip()


def get_md5_private_key(file):
    openssl_rsa_command = ["openssl", "rsa", "-noout", "-modulus", "-in", file]
    openssl_md5_command = ["openssl", "md5"]

    rsa_process = subprocess.Popen(openssl_rsa_command, stdout=subprocess.PIPE)
    md5_process = subprocess.Popen(
        openssl_md5_command, stdin=rsa_process.stdout, stdout=subprocess.PIPE
    )

    output, error = md5_process.communicate()

    if error:
        print(error.decode("utf-8"))
    return output.decode("utf-8").strip()


md5_key = get_md5_private_key(test_private_key_file_path)
md5_cert = get_md5_cert(test_certificate_key_file_path)

assert md5_key == md5_cert
