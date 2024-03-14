import base64
import json
import subprocess


# Define the paths to your SSL certificate and key
cert_file_path = "private_localhost_certificate.crt"
key_file_path = "private_localhost.key"
env_file_path = ".env"  # Path to your .env file

# Define the OpenSSL command
openssl_command = [
    "openssl",
    "req",
    "-x509",
    "-nodes",
    "-days",
    "365",
    "-subj",
    "/C=US/ST=WA/O=SAGE",
    "-newkey",
    "rsa:2048",
    "-keyout",
    key_file_path,
    "-out",
    cert_file_path,
]

# Run the OpenSSL command
try:
    subprocess.run(openssl_command, check=True)
    print("SSL certificate and key generated successfully.")
except subprocess.CalledProcessError as e:
    print(f"Error generating SSL certificate and key: {e}")


# Function to read a file and encode its contents to Base64
# Certificate has to be in base64 format otherwise can't be parse properly as environment variables
def encode_file_to_base64(file_path):
    with open(file_path, "rb") as file:
        return base64.b64encode(file.read()).decode("utf-8")


# Encode the SSL certificate and key
ssl_certificate_base64 = encode_file_to_base64(cert_file_path)
ssl_private_key_base64 = encode_file_to_base64(key_file_path)

# Combine into a JSON object
ssl_config_json = json.dumps(
    {
        "SSL_CERTIFICATE": ssl_certificate_base64,
        "SSL_PRIVATE_KEY": ssl_private_key_base64,
    }
)

# Append to .env file
with open(env_file_path, "a") as env_file:
    env_file.write(f"SECRETS_MANAGER_SECRETS={ssl_config_json}\n")

print("SSL certificate and key have been encoded and appended to the .env file.")
