# BixArena Infra

## Configure an AWS SSO Profile

Add the following profiles to `$AWS_CONFIG_FILE` (`<workspace>/.aws/config`):

```ini
[profile bixarena-Administrator]
sso_session = org-sagebase
sso_account_id = ***
sso_role_name = Administrator
region = us-east-1
output = json
cli_pager =

[sso-session org-sagebase]
sso_start_url = https://d-906769aa66.awsapps.com/start
sso_region = us-east-1
sso_registration_scopes = sso:account:access
```

> [!TIP]
> Add `cli_pager =` to your profile in `$AWS_CONFIG_FILE` to disable the use of `less`.

## Login with AWS SSO

```bash
aws sso login --profile bixarena-Administrator
```
