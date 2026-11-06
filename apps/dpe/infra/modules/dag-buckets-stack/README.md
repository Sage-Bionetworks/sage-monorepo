# DAG buckets

This stack makes the S3 buckets that the orca-recipes DAGs write their output to. An example is the croissant files from `synapse-dataset-to-croissant.py`. These buckets do not hold DAG code. The MWAA bucket (`<mwaa_name>-s3`) holds the DAG code.

The buckets are not in the MWAA stack. This keeps the buckets and their data when you remove Airflow.

| Stack                | Unit               | Account  | Bucket list                         |
| -------------------- | ------------------ | -------- | ----------------------------------- |
| DPE DAG Buckets dev  | `dag-buckets/dev`  | DNT dev  | `dag_buckets.dev` in `project.hcl`  |
| DPE DAG Buckets prod | `dag-buckets/prod` | DPE prod | `dag_buckets.prod` in `project.hcl` |

The MWAA stack in the same account gives its execution role access to the same buckets. The role can list each bucket, and read, write and delete objects (`dag_s3_buckets` in `modules/mwaa`).

## Add a bucket for a new DAG

1. Add the bucket to `dev` and to `prod` in `dag_buckets` in `apps/dpe/infra/project.hcl`.
2. Give the dev bucket the suffix `-dev`.
3. Set `public = true` only if people outside Sage must read the objects. Otherwise, set `public = false`.
4. Merge the pull request. Every stack reads `project.hcl`, so the change starts a run of each of them.
5. In each account, confirm the DAG Buckets run first. Then confirm the Managed Airflow run. The other runs plan no changes and finish on their own.
6. In the DAG, write to S3 with `S3Hook(aws_conn_id=None)`. The hook then uses the MWAA execution role.
7. Choose the bucket name with `by_environment(dev=..., prod=...)` from `dags/src/environment.py` in orca-recipes.
