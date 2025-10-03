# BixArena Infra

## Configure an AWS SSO Profile

Add the following profiles to `~/.aws/config`:

```ini
[profile bixarena-Administrator]
sso_session = org-sagebase
sso_account_id = ***
sso_role_name = Administrator
region = us-east-1
output = json

[sso-session org-sagebase]
sso_start_url = https://d-906769aa66.awsapps.com/start
sso_region = us-east-1
sso_registration_scopes = sso:account:access
```

## Login with AWS SSO

```bash
aws sso login --profile bixarena-Administrator
```
