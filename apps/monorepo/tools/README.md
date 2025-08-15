# OpenAPI Dependency Analyzer

A powerful script to analyze OpenAPI dependencies🔗 Used Files with Dependency Chains:

1.  📄 auth.openapi.yaml → 📄 api-keys → 🧬 ApiKey
2.  📄 challenge.openapi.yaml → 📄 challenge-platforms → 🧬 ChallengePlatformCreateRequest → 🧬 AvatarKey
3.  📄 challenge.openapi.yaml → 📄 @{challengeId} → 🗂️ challengeId
4.  📄 challenge.openapi.yaml → 📄 challenges → 🔍 challengeSearchQuery
    ...sualize file relationships, and clean up unused source files in the sage-monorepo project.

## Features

- 🔍 **Dependency Analysis**: Recursively analyzes OpenAPI `$ref` dependencies
- 📊 **Visual Reports**: Color-coded output with emojis for file types
- 🔗 **Dependency Chains**: Shows complete dependency paths
- 🗂️ **Parameter Differentiation**: Distinguishes between path and query parameters
- 🧹 **Safe Cleanup**: Dry-run mode by default with optional deletion
- 🚀 **Fast Execution**: Built with modern TypeScript and esbuild

## Installation

The tool is already set up in the monorepo. Dependencies are managed via the workspace.

## Usage

### Option 1: Direct Execution (Recommended for piped output)

```bash
# Show help
tsx apps/monorepo/tools/src/openapi-dependency-analyzer.ts --help

# Analyze files (dry run)
tsx apps/monorepo/tools/src/openapi-dependency-analyzer.ts libs/openchallenges/api-description/src

# Generate detailed report with dependency graph
tsx apps/monorepo/tools/src/openapi-dependency-analyzer.ts libs/openchallenges/api-description/src --report

# Actually delete unused files (DESTRUCTIVE!)
tsx apps/monorepo/tools/src/openapi-dependency-analyzer.ts libs/openchallenges/api-description/src --delete

# Pipe output (works correctly with EPIPE handling)
tsx apps/monorepo/tools/src/openapi-dependency-analyzer.ts libs/openchallenges/api-description/src --report | head -20
tsx apps/monorepo/tools/src/openapi-dependency-analyzer.ts libs/openchallenges/api-description/src --report | grep "🗂️"
```

### Option 2: Via Nx

```bash
# Works fine without piping
nx run monorepo-tools:analyze-openapi -- libs/openchallenges/api-description/src --report

# Note: Has EPIPE issues when piped (use direct execution instead)
```

## Example Output

```
Analyzing OpenAPI files in: libs/openchallenges/api-description/src
Entry points found: ['auth.openapi.yaml', 'challenge.openapi.yaml', 'image.openapi.yaml', 'organization.openapi.yaml']

=== OpenAPI File Usage Report ===

📊 Summary:
   Total files: 207
   Used files: 120
   Unused files: 87

📖 Legend:
   📄 Entry Point (main OpenAPI spec files)
   🛤️ Path (API endpoint definitions)
   🧬 Schema (data model definitions)
   📤 Response (HTTP response definitions)
   �️ Path Parameter (URL path parameter definitions)
   🔍 Query Parameter (URL query parameter definitions)
   � Security (authentication/authorization schemes)
   🔗 Link (OpenAPI link definitions)
   � → Dependency chain (shows reference flow)

🗂️ Files by Category:
   📄 Entry Points (4): auth.openapi.yaml, challenge.openapi.yaml, image.openapi.yaml, organization.openapi.yaml
   🛤️ Paths (24): api-keys.yaml, auth.yaml, challenge-participants.yaml, ...
   🧬 Schemas (83): ApiKey.yaml, User.yaml, Challenge.yaml, ...
   📤 Responses (9): BadRequest.yaml, InternalServerError.yaml, NotFound.yaml, ...
   🗂️ Path Parameters (8): challengeId.yaml, organizationId.yaml, ...
   🔍 Query Parameters (3): challengeSearchQuery.yaml, pageOffset.yaml, ...

🔗 Used Files with Dependency Chains:
   1. 📄 auth.openapi.yaml → 📄 api-keys → 🔧 ApiKey
   2. 📄 challenge.openapi.yaml → 📄 challenge-platforms → 🔧 ChallengePlatformCreateRequest → 🔧 AvatarKey
   ...

❌ Unused Files (87):
   - paths/auth.yaml
   - schemas/Account.yaml
   - schemas/Address.yaml
   ...
```

## Safety Features

- **Dry Run Default**: Never deletes files unless `--delete` is explicitly used
- **Entry Point Protection**: Never deletes main OpenAPI spec files
- **EPIPE Handling**: Graceful handling of broken pipes when output is piped to commands like `head` or `grep`
- **Dependency Validation**: Comprehensive analysis to ensure no referenced files are deleted

## Technical Details

- **Language**: TypeScript with Node.js
- **Dependencies**: Modern `yaml` package (v4+), `tsx` for execution
- **Performance**: Processes 200+ files in seconds
- **Architecture**: Object-oriented design with comprehensive error handling

## Analysis Results for OpenChallenges

Current analysis of `libs/openchallenges/api-description/src`:

- **Total files**: 207
- **Used files**: 120 (58%)
- **Unused files**: 87 (42%)
- **Potential cleanup**: Remove 87 unused files to significantly reduce repository size

Safe to delete 87 unused files that have no references and are not entry points.
