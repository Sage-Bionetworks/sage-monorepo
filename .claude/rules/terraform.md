---
paths:
  - '**/*.tf'
  - '**/*.tfvars'
  - '**/*.hcl'
---

# Terraform & Terragrunt Monorepo Conventions (BixArena Focus)

These conventions apply to all Terraform (`*.tf`, `*.tfvars`) and Terragrunt (`*.hcl`) files in this monorepo, with a focus on BixArena infrastructure.

## Layered File Hierarchy

There are three principal configuration layers for Terragrunt projects:

1. **Global Workspace Layer** (`workspace.hcl` – repo root)

   - Declares global context (organization, default tags, reusable registry of module source paths, list of infrastructure "projects")
   - Provides shared locals that any project can inherit (naming helpers, tag maps, region defaults)
   - Should NOT contain environment-specific values

2. **Project Layer** (`project.hcl` inside each infra project directory)

   - Corresponds to an Nx project (e.g., `apps/bixarena/infra/bootstrap`)
   - Always includes/references `workspace.hcl` to inherit global settings
   - Centralizes project-wide locals: project name, environment list, tagging schema, naming prefixes
   - Loads structured module configuration from an adjacent `config.yaml`
   - Defines default remote state & provider generation logic for all modules EXCEPT bootstrap modules that create that remote state

3. **Module Layer** (`<module>/terragrunt.hcl` per logical component)
   - Always points to the closest `project.hcl` to inherit backend/provider/locals
   - Supplies module-specific inputs and `dependency` blocks to reference sibling module outputs
   - Module sources may be local (`modules/`), shared (`libs/`), or external registries

## `config.yaml` (Project Configuration)

Each infra project includes a `config.yaml` co-located with `project.hcl` that:

- Enumerates all Terragrunt modules in the project
- Declares per-environment overrides (CIDR blocks, replica counts, feature toggles)
- Defines canonical remote state backend parameters (S3 bucket, DynamoDB table, key prefix) — except for the bootstrap module that creates those resources
- May include variable maps grouped by category (network, iam, monitoring) to avoid scattering literals across many `terragrunt.hcl` files

Always read/derive values from `config.yaml` via locals — do not duplicate values inline.

## Bootstrapping Exception Pattern

The `terraform-backend` module creates the Terraform remote state storage (S3 bucket + DynamoDB lock table):

- MUST NOT declare or rely on remote state that assumes the bucket/table already exist
- Uses local state initially until first apply
- After backend resources exist, all other modules rely on the shared remote state config injected via `project.hcl`

## Include / Inheritance Pattern

```hcl
include "project" {
  path = find_in_parent_folders("project.hcl")
}

terraform {
  source = "../../../modules/vpc" # local, libs/, or external registry
}

locals {
  env        = local.project_env
  cidr_block = local.config.network[local.env].cidr
}

inputs = {
  cidr_block = local.cidr_block
  tags       = local.common_tags
}
```

## Dependencies Between Modules

Use Terragrunt `dependency` blocks instead of hardcoding ARNs/IDs:

```hcl
dependency "network" {
  config_path = "../network"
}

inputs = {
  vpc_id = dependency.network.outputs.vpc_id
}
```

- Only depend "downwards" on foundational modules to avoid cycles
- Avoid deep dependency chains — prefer passing core outputs at an aggregation boundary
- If multiple modules need a shared reference (e.g., KMS key), create a single-purpose module that exports it

## Naming & Tagging

Pattern: `${project}-${env}-${component}` → e.g., `bixarena-staging-vpc`

Inject default tags via a provider `default_tags` block at the project level:

- `Project` (e.g., `BixArena`)
- `Environment` (e.g., `staging`, `prod`)
- `ManagedBy = terraform`
- `Module` (set per module using a local)
- Additional org governance tags (CostCenter, Owner) as required

## IAM & Security Guidelines

- Separate role creation modules from permission policy attachment logic
- Use least privilege; avoid wildcard `*` unless tightly scoped by resource or condition
- Prefer permissions boundaries for developer/deploy roles to prevent privilege drift
- OIDC roles for GitHub Actions MUST restrict `sub` claims (repository + branch or workflow) and audience (`sts.amazonaws.com`)

## Environment Handling

Environments (`staging`, `prod`) are represented by separate Terragrunt executions. Environment-specific inputs should live in `config.yaml` and be consumed via locals — avoid duplicating environment conditionals across module files.

## Shared Modules (Libraries)

Reusable modules (VPC, ECS cluster, KMS key) live under `libs/` for cross-project reuse. `workspace.hcl` maintains a registry so module sources reference abstracted paths instead of brittle relative traversals.

Guidelines for shared modules:

- Keep input surface minimal and strongly typed (use `variable` blocks with validation)
- Provide explicit, well-named outputs — avoid leaking entire objects
- Document assumptions in a `README.md` within the module directory

## Adding a New Module (Checklist)

1. Define the functional boundary — avoid scope creep
2. If reusable, create under `libs/<domain>/<module-name>`; otherwise under the project's `modules/`
3. Update `config.yaml` with new configurable inputs (environment overrides, feature flags)
4. Create the module directory with `main.tf`, `variables.tf`, `outputs.tf`, `README.md`
5. Add a `terragrunt.hcl` referencing `project.hcl` and the module source
6. Introduce `dependency` blocks (verify no cycles)
7. Run `terragrunt init` / `plan` locally using the appropriate SSO profile
8. Commit; ensure CI plan workflow produces no unexpected drifts in unrelated modules

## Remote State Conventions

Remote state config (S3 bucket, DynamoDB lock table) is defined once at the project layer and injected via generated backend blocks. Modules must not override backend settings locally.

State key pattern: `<project>/<environment>/<module>/terraform.tfstate`

Do NOT share a single state file across unrelated modules.

## Anti-Patterns to Avoid

- Do not copy variable values manually between modules — use outputs + dependencies or centralized config
- Do not duplicate provider blocks unless a real multi-provider scenario (e.g., cross-region) is required
- Never hardcode account IDs or ARNs — derive from data sources or variables
- Do not place secrets in `.hcl` or `.tf` — use SSM Parameter Store / Secrets Manager modules

## Testing & Validation

- `terraform validate` / `opentofu validate` in CI
- `tflint` (future) with custom rules (naming, tags)
- `infracost` (future) for cost diffs on PRs
- Lightweight policy checks (OPA/Conftest) to ensure no broad wildcards or missing tags

## Output & Variable Guidelines

- Expose only stable identifiers (IDs, ARNs) required by consumers
- Do not output secrets or ephemeral data (passwords, random seeds)
- Use descriptive variable names with `description` and `validation` blocks where useful

## Migration / Refactor Guidance

- Preserve state addresses (resource names) to avoid forced recreation; use `moved` blocks when renaming
- Split monolithic modules incrementally: create new module(s), reference outputs, migrate resources with `moved` declarations
