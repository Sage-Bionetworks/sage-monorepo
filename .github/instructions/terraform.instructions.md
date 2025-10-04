---
applyTo:
  - '**/*.tf'
  - '**/*.tfvars'
  - '**/*.hcl'
---

# Terraform & Terragrunt Monorepo Conventions (BixArena Focus)

These instructions apply to all Terraform (`*.tf`, `*.tfvars`) and Terragrunt (`*.hcl`) files in this monorepo. They describe how infrastructure code (especially for BixArena) is organized so that Copilot (and contributors) can produce consistent, compliant changes.

## Layered File Hierarchy

There are three principal configuration layers for Terragrunt projects in this repository:

1. Global Workspace Layer (`workspace.hcl` – repo root)

   - Declares global context (e.g., organization, default tags, reusable registry of module source paths, and a list of infrastructure "projects").
   - Provides shared locals that any project can inherit (naming helpers, tag maps, region defaults, etc.).
   - Should NOT contain environment‑specific values.

2. Project Layer (`project.hcl` inside each infra project directory)

   - A project corresponds to an Nx project (e.g., `apps/bixarena/infra/bootstrap`).
   - `project.hcl` always includes / references `workspace.hcl` (via `include` or `read_terragrunt_config`) to inherit global settings.
   - Centralizes project‑wide locals: project name, environment list, tagging schema, naming prefixes.
   - Loads structured module configuration from an adjacent `config.yaml` (see below).
   - Defines default remote state & provider generation logic for all modules in the project EXCEPT bootstrap modules that create that remote state (e.g., the `terraform-backend` module).

3. Module Layer (`<module>/terragrunt.hcl` per logical component)
   - Each module folder within a project (e.g., `terraform-backend`, `github-oidc-provider`, `network`) has its own `terragrunt.hcl`.
   - Always points to the closest `project.hcl` (the parent project root) to inherit backend/provider/locals.
   - Supplies module‑specific inputs, and (when needed) `dependency` blocks to reference outputs from sibling modules.
   - Uses Terraform modules that may live:
     - Locally inside the project under `modules/`
     - Shared libraries under `libs/` (referenced through paths or git sources recorded in `workspace.hcl`)
     - External registries (public or private) when justified.

## `config.yaml` (Project Configuration)

Each infra project includes a `config.yaml` co-located with `project.hcl` that:

- Enumerates all Terragrunt modules (logical components) in the project.
- Declares per‑environment overrides (e.g., CIDR blocks, replica counts, feature toggles).
- Defines the canonical remote state backend parameters (S3 bucket, DynamoDB table, key prefix) for modules—except for the bootstrap module that _creates_ those backend resources.
- May include variable maps grouped by category (network, iam, monitoring, etc.) to avoid scattering literals across many `terragrunt.hcl` files.

Copilot should read/parse (conceptually) from `config.yaml` when generating or updating module inputs rather than duplicating values inline.

## Bootstrapping Exception Pattern

The `terraform-backend` module is used to create the Terraform remote state storage (S3 bucket + DynamoDB lock table). This module:

- MUST NOT declare or rely on remote state that assumes the bucket/table already exist.
- Should use a local state initially (or an alternate temporary backend) until first apply.
- After the backend resources exist, other modules rely on the shared remote state config injected via `project.hcl`.

When authoring new bootstrap modules (e.g., for CI OIDC providers) that depend on the backend, ensure they inherit the remote state from `project.hcl` and do not recreate or mutate backend primitives.

## Include / Inheritance Pattern

Module `terragrunt.hcl` files follow this pattern (illustrative):

```hcl
include "project" {
  path = find_in_parent_folders("project.hcl")
}

terraform {
  # Source comes from one of:
  # - a local modules directory under this project
  # - a shared library path declared in workspace.hcl
  # - an external registry source
  source = "../../../modules/vpc" # example relative path
}

locals {
  # Derive inputs from project-level locals or config.yaml data
  env        = local.project_env
  cidr_block = local.config.network[local.env].cidr
}

inputs = {
  cidr_block = local.cidr_block
  tags       = local.common_tags
}
```

## Dependencies Between Modules

Use Terragrunt `dependency` blocks instead of hardcoding ARNs / IDs:

```hcl
dependency "network" {
  config_path = "../network"
}

inputs = {
  vpc_id = dependency.network.outputs.vpc_id
}
```

Rules:

- Only depend “downwards” on foundational modules to avoid cycles.
- Avoid deep dependency chains (prefer passing core outputs once at an aggregation boundary).
- If multiple modules need a shared reference (e.g., KMS key), create a single-purpose module that exports it.

## Naming & Tagging

Consistent naming aids discoverability and IAM scoping:

Pattern: `${project}-${env}-${component}` → e.g., `bixarena-staging-vpc`.

Inject default tags via a provider `default_tags` block generated at the project level:

- `Project` (e.g., `BixArena`)
- `Environment` (e.g., `staging`, `prod`)
- `ManagedBy = terraform`
- `Module` (set per module using a local)
- Additional org governance tags (CostCenter, Owner) as required.

## IAM & Security Authoring Guidelines

- Separate _role creation_ modules (IAM principals, trust policies) from _permission policy_ attachment logic where practical.
- Use least privilege; do not grant wildcard `*` unless tightly scoped by resource or condition.
- Prefer permissions boundaries for developer/deploy roles to prevent privilege drift.
- OIDC roles for GitHub Actions MUST restrict `sub` claims (e.g., repository + branch or workflow) and audience (`sts.amazonaws.com`).

## Environment Handling

Environments (e.g., `staging`, `prod`) are represented by separate Terragrunt executions (often just separate runs at the same project tree). Inputs differing by environment should be stored centrally in `config.yaml` and consumed via locals—avoid duplicating environment conditionals in every module file.

## Shared Modules (Libraries)

Reusable Terraform modules that are generic (e.g., VPC, ECS cluster, KMS key set) may live under `libs/` to enable cross‑project reuse. `workspace.hcl` maintains a registry or mapping so module sources can reference an abstracted path (e.g., via locals) instead of brittle relative traversals.

Guidelines for shared modules:

- Keep input surface minimal and strongly typed (use `variable` blocks with validation where applicable).
- Provide explicit, well-named outputs (avoid leaking entire objects unless necessary).
- Document assumptions (e.g., expects existing KMS key) in a `README.md` within the module directory.

## Adding a New Module (Checklist)

1. Define the functional boundary (what resources & why). Avoid scope creep.
2. If reusable elsewhere, create it under `libs/<domain>/<module-name>`; otherwise under the project’s `modules/`.
3. Update `config.yaml` with any new configurable inputs (environment overrides, feature flags).
4. Create the module directory with Terraform code (`main.tf`, `variables.tf`, `outputs.tf`, `README.md`).
5. Add a `terragrunt.hcl` in the project referencing `project.hcl` and the module source.
6. Introduce any `dependency` blocks (verify no cycles).
7. Run `terragrunt init` / `plan` (locally) using the appropriate SSO profile.
8. Commit changes; ensure CI (plan workflow) produces no unexpected drifts in unrelated modules.

## Remote State Conventions

Remote state configuration (S3 bucket, DynamoDB lock table) is defined once at the project layer, injected via generated backend blocks. All modules (except the backend-creating module) must not override backend settings locally.

State key pattern example:

```
<project>/<environment>/<module>/terraform.tfstate
```

Do NOT share a single state file across unrelated modules.

## Error Prevention & Anti-Patterns

- Avoid copying variable values manually between modules; prefer outputs + dependencies or centralized config.
- Don’t duplicate provider blocks in module-level Terraform unless a real multi-provider scenario (e.g., cross-region) is required.
- Never hardcode account IDs or ARNs; derive from data sources or variables so future multi-account expansion is simpler.
- Do not place secrets (passwords, keys) directly in `.hcl` or `.tf`; use SSM Parameter Store / Secrets Manager modules.

## Testing & Validation Practices

While Terraform lacks a built-in unit test framework, we encourage:

- `terraform validate` / `opentofu validate` in CI.
- `tflint` (future) with custom rules (naming, tags).
- `infracost` (future) for cost diffs on PRs.
- Lightweight policy checks (e.g., OPA/Conftest) to ensure no broad wildcards or missing tags.

## Output & Variable Guidelines

- Expose only stable identifiers (IDs, ARNs) required by consumers.
- Avoid outputting secrets or ephemeral data (passwords, random seeds).
- Use descriptive variable names; include `description` and, where useful, `validation` blocks.

## Migration / Refactor Guidance

When refactoring:

- Preserve state addresses (resource names) to avoid forced recreation; if renaming is needed, use `moved` blocks.
- Split monolithic modules incrementally: create new module(s), reference outputs, migrate resources with `moved` declarations.

## Copilot Authoring Rules (TL;DR)

- Always anchor new `terragrunt.hcl` files to the nearest `project.hcl` via `include`.
- Pull configuration values from `config.yaml` via locals—don’t inline duplicates.
- Respect the backend bootstrap exception (do not apply remote state settings inside the backend-creating module before it exists).
- Use dependency blocks instead of hardcoding identifiers.
- Maintain consistent tagging and naming patterns.
- Keep IAM policies least‑privilege and scoped.

---

End of Terraform/Terragrunt monorepo instructions.
