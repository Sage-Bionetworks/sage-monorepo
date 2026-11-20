# Contents

- [Purpose](#purpose)
- [Usage](#usage)
- [DAG deployment](#dag-deployment)
- [Python requirements](#python-requirements)
- [Accessing the Airflow UI](#accessing-the-airflow-ui)
- [Airflow connections and variables](#airflow-connections-and-variables)
- [Troubleshooting](#troubleshooting)
  - [DAG uploaded to S3 does not appear in the Airflow UI](#dag-uploaded-to-s3-does-not-appear-in-the-airflow-ui)
  - [`ModuleNotFoundError` in a task for a package listed in requirements.txt](#modulenotfounderror-in-a-task-for-a-package-listed-in-requirementstxt)
  - [MWAA stack run fails downloading requirements-airflow.txt](#mwaa-stack-run-fails-downloading-requirements-airflowtxt)
  - [Changing `airflow_version` fails](#changing-airflow_version-fails)
  - [Applying a branch to dev before merging](#applying-a-branch-to-dev-before-merging)

# Purpose

Deploys an Amazon Managed Workflows for Apache Airflow (MWAA) environment. It is a thin
wrapper around [cloudposse/mwaa/aws](https://search.opentofu.org/module/cloudposse/mwaa/aws/latest),
which also creates the S3 bucket for DAGs, the execution IAM role, and the security group.

# Usage

```terraform
module "mwaa" {
  source     = "../mwaa"
  enabled    = true
  name       = "mwaa-dev"
  region     = var.region
  vpc_id     = var.vpc_id
  subnet_ids = var.subnet_ids

  orca_recipes_sha       = "<orca-recipes commit SHA>"
  deployment_environment = "dev"
}
```

`enabled` defaults to `false` so the module can be wired up before any AWS resources exist.
While it is `false` nothing is created and `vpc_id`/`subnet_ids` may be left unset.

MWAA requires exactly two private subnets in different availability zones. DAGs are read from
the `dags/` prefix of the bucket created by this module. New or changed DAG files take a few
minutes to appear in the Airflow UI.

# DAG deployment

DAGs are deployed by the `Deploy DAGs to MWAA` workflow in
[orca-recipes](https://github.com/Sage-Bionetworks-Workflows/orca-recipes/blob/main/mwaa/README.md),
not by this module. Pushes to orca-recipes `main` deploy to prod. Running the workflow manually
deploys any orca-recipes branch, tag or commit to dev.

The module creates the role the workflow assumes, `<name>-dag-deploy`:

- It trusts only GitHub Actions jobs in the orca-recipes GitHub environment
  `mwaa-<deployment_environment>`. The GitHub OIDC provider is created in every account by
  organizations-infra.
- It can only list, read, write and delete under the `dags/` prefix of the bucket.

After the stack is applied, set these variables on the GitHub environment from the stack outputs:

| GitHub environment variable | Stack output          |
| --------------------------- | --------------------- |
| `MWAA_BUCKET`               | `s3_bucket_name`      |
| `MWAA_ROLE_ARN`             | `dag_deploy_role_arn` |

`deployment_environment` is also exposed to DAGs as the `AIRFLOW__ORCA__ENVIRONMENT` environment
variable, so DAGs can choose dev or prod values.

# Python requirements

The module downloads `requirements-airflow.txt` from orca-recipes at `orca_recipes_sha`, adds a
`--constraint` line for `mwaa/constraints.txt` at the same commit, and uploads the result as
`requirements.txt` in the bucket. Do not upload `requirements.txt` manually. The next stack run
replaces it.

To change the installed packages, follow
[Changing dependencies](https://github.com/Sage-Bionetworks-Workflows/orca-recipes/blob/main/mwaa/README.md#changing-dependencies)
in orca-recipes. The prod plan fails if `orca_recipes_sha` is not on orca-recipes `main`. The
check uses the GitHub compare API.

# Accessing the Airflow UI

Dev and prod use `PRIVATE_ONLY` access: the URL is only reachable while connected to the
[Sage VPN](https://sagebionetworks.jira.com/wiki/spaces/IT/pages/1705246745). The VPC must be a
Transit Gateway spoke (organizations-infra `710-tgw`) and authorized for VPN users
(`720-client-vpn`).

1. Log into the AWS Console for the account the environment is deployed to (`org_sagebase_dnt_dev`
   for dev, `org_sagebase_dpe_prod` for prod) via JumpCloud.
2. Open **Amazon MWAA** → **Environments** in `us-east-1`, select `synapse-mwaa-dev` or
   `synapse-mwaa-prod` and click **Open Airflow UI**.

# Airflow connections and variables

Airflow reads connections and variables from AWS Secrets Manager in the same account and region
as the environment:

| Airflow lookup           | Secret name                     |
| ------------------------ | ------------------------------- |
| Connection `<conn_id>`   | `airflow/connections/<conn_id>` |
| `Variable.get("<name>")` | `airflow/variables/<name>`      |

Anything not found in Secrets Manager falls back to the values added in the Airflow UI under
**Admin** → **Connections** / **Variables**. See [Secrets](https://github.com/Sage-Bionetworks-Workflows/orca-recipes/blob/main/CONTRIBUTING.md#secrets) for details on adding / modifying our current secrets in the AWS accounts.

# Troubleshooting

See AWS's MWAA troubleshooting page first for [common troubleshooting issues](https://docs.aws.amazon.com/mwaa/latest/userguide/t-apache-airflow-202.html#access-secrets-manager)

Start by checking the environment's state:

```bash
aws mwaa get-environment --region us-east-1 --name synapse-mwaa-dev \
  --query 'Environment.{status:Status,mode:WebserverAccessMode,url:WebserverUrl,airflow:AirflowVersion,reqPath:RequirementsS3Path,reqVersion:RequirementsS3ObjectVersion,lastUpdate:LastUpdate}'
```

Logs are in CloudWatch under the `airflow-<name>-Scheduler`, `-Worker` and `-Task` log groups.

## DAG uploaded to S3 does not appear in the Airflow UI

MWAA syncs the `dags/` prefix and re-scans it periodically, so new or changed files take a few
minutes to show up. If a DAG is still missing after that:

- Check the red **DAG Import Errors** banner at the top of the DAGs page. A DAG whose imports fail
  is not listed, and the banner shows the traceback.
- `No module named 'src'` (or another folder of shared helpers): the folder is missing from the
  bucket. The `dags/` folder is on `sys.path`, so `from src.x import y` needs
  `s3://<bucket>/dags/src/x.py`, including for DAGs in subfolders of `dags/`.
- `No module named '<package>'`: the package is not installed. See
  [`ModuleNotFoundError`](#modulenotfounderror-in-a-task-for-a-package-listed-in-requirementstxt).

## `ModuleNotFoundError` in a task for a package listed in requirements.txt

Check which orca-recipes commit is installed:

```bash
aws s3 cp s3://<bucket>/requirements.txt - | head -1
```

- **Older commit than expected**: the new `orca_recipes_sha` has not been applied. It is set in
  `modules/mwaa-stack/main.tf`. If the change is only on a feature branch, see
  [Applying a branch to dev before merging](#applying-a-branch-to-dev-before-merging). If it is
  on `main`, confirm the MWAA stack's tracked run.
- **Expected commit**: the install failed. MWAA keeps running with the previous requirements.
  The pip error is in the `requirements_install_*` stream of the Scheduler log group. Fix it in
  orca-recipes as described in its `mwaa/README.md`, then bump the SHA through a PR again.

## MWAA stack run fails downloading requirements-airflow.txt

`orca_recipes_sha` must be a full commit SHA that has been pushed to orca-recipes. Open
`https://raw.githubusercontent.com/Sage-Bionetworks-Workflows/orca-recipes/<orca_recipes_sha>/requirements-airflow.txt`
to check.

## Changing `airflow_version` fails

- **Major version upgrade (for example 2.11.2 to 3.3.1)**: the AWS provider replaces the
  environment, even though MWAA can upgrade 2.11.x in place. The plan shows
  `aws_mwaa_environment.default[0]` must be replaced. Terraform deletes the environment and
  creates a new one with the same name, 20–30 minutes each, so Airflow is unavailable in
  between. The new environment starts with an empty metadata database: run history is gone,
  every DAG starts paused, and Connections and Variables added in the UI are lost. The ones in
  Secrets Manager, the bucket, the roles and the security group are kept.
- **The update fails installing requirements**: `requirements.txt` is installed on the new
  version, so bump `orca_recipes_sha` in the same change to an orca-recipes commit whose
  `mwaa/constraints.txt` is for that version. See
  [Upgrading the MWAA Airflow version](https://github.com/Sage-Bionetworks-Workflows/orca-recipes/blob/main/mwaa/README.md#upgrading-the-mwaa-airflow-version)
  in orca-recipes.

## Applying a branch to dev before merging

The MWAA stacks track `main`. The dev stack allows run promotion: promote the
"DPE Managed Airflow dev" proposed run of the pull request (from the run in Spacelift, or from the
pull request check), then confirm it. The prod stack does not allow promotion, so it only
applies `main`.
