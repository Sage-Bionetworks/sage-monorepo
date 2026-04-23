---
name: pr-description
description: Generates comprehensive pull request descriptions following repository template standards. Use when asked to create, update, or generate PR descriptions, or when working with active pull requests that need documentation.
---

# PR Description Generator

## When to use this skill

Use this skill when:

- The user asks to update the PR description
- Working with an active pull request that needs a description
- Creating documentation for code changes in a PR
- Following the repository's GitHub template standards

## Workflow

### 1. Gather PR Context

First, get the active pull request details:

- Use `github-pull-request_activePullRequest` to retrieve PR information
- Analyze all file changes (additions, deletions, modifications)
- Identify related issues or Jira tickets (format: SMR-XXX)
- Note the target merge branch (usually `main`)

### 2. Analyze Changes

Categorize the changes by:

- **Type**: Use repository-defined PR types from [lint-pr.yml](/.github/workflows/lint-pr.yml)
- **Scope**: Use repository-defined scopes from [lint-pr.yml](/.github/workflows/lint-pr.yml)
- **Technology stack**: Java/Spring Boot, TypeScript/Angular, Python, etc.
- **Breaking changes**: API modifications, schema changes
- **Configuration**: build files, application properties
- **Database**: migrations, schema modifications

### 3. Generate Description

Create the PR description using this exact template structure:

```markdown
## Description

[2-4 sentences explaining WHY these changes were made and the business/technical impact. Focus on the objective and benefits, not just what changed.]

## Related Issue

[If applicable, link to Jira ticket or GitHub issue]
Fixes [ISSUE-NUMBER](link-to-issue)

## Changelog

[CRITICAL: Use flat, single-level bullets with NO sub-items]
[Keep items high-level and coarse-grained - avoid excessive detail]
[Group related low-level changes into one high-level bullet]
[Aim for 3-7 bullet points total]

- [High-level change description]
- [High-level change description]
- [Continue for major changes only...]

## Preview

The [component/service/application] now has:

[CRITICAL: Use flat, single-level bullets with NO sub-items]
[Keep items high-level - focus on major outcomes and benefits]
[DO NOT use checkmark emojis (✅) or green checkboxes]
[Use simple bullet points with dashes (-)]

- [Major improvement or feature outcome]
- [Major improvement or feature outcome]
- [Continue for key accomplishments only...]
```

## Content Guidelines

### Description Section

- Start with the primary business or technical objective
- Explain the motivation behind changes
- Highlight main benefits or improvements
- Mention architectural or design decisions if significant
- Keep concise but comprehensive (2-4 sentences)

### Related Issue Section

- Always search for related Jira tickets (SMR-XXX format)
- Include GitHub issue numbers if applicable
- Use proper linking format: `[ISSUE-NUMBER](full-url)`
- Omit this section if no related issue exists

### Changelog Section

**CRITICAL RULES:**

- Use ONLY single-level flat bullets with NO nested items
- Keep items coarse-grained and high-level
- Avoid excessive technical detail or implementation specifics
- Group multiple related low-level changes into one bullet
- Use imperative form: "Add feature X", "Remove deprecated Y"
- Order from most to least important
- Aim for 3-7 bullet points total, not an exhaustive list
- DO NOT list individual file changes

### Preview Section

**CRITICAL RULES:**

- Use ONLY single-level flat bullets with NO nested items
- Keep items coarse-grained - focus on major outcomes
- DO NOT use checkmark emojis (✅) or any emojis
- Use simple bullet points with dashes (-)
- Focus on end results and benefits from user/system perspective
- Highlight key improvements, features, or fixes
- Mention metrics if applicable (test coverage, performance)
- Aim for 3-5 bullet points describing major accomplishments

## Technology-Specific Details

### For Java/Spring Boot Changes

Include when relevant:

- JPA entity modifications
- Database schema changes
- Spring configuration updates
- Dependency updates (Gradle/Maven)
- Test infrastructure changes

### For TypeScript/Angular Changes

Include when relevant:

- Component updates
- Service modifications
- Configuration changes (tsconfig, angular.json)
- Dependency updates
- Build system changes

### For Python Changes

Include when relevant:

- Library/package updates
- Configuration changes (pyproject.toml)
- Dependency updates
- Test infrastructure

### For Database Changes

Include when relevant:

- Migration files
- Schema modifications
- Index changes
- Constraint updates

## Quality Checklist

Before finalizing:

- [ ] All major file changes are documented at high level
- [ ] Technical terminology is accurate
- [ ] Related issues are properly linked
- [ ] Changelog items are specific but coarse-grained
- [ ] Preview items highlight actual benefits
- [ ] NO checkmark emojis in Preview section
- [ ] NO nested bullets in Changelog or Preview
- [ ] Grammar and formatting are correct
- [ ] Description follows exact template structure
- [ ] Business value is clearly communicated
- [ ] Only 3-7 bullets in Changelog
- [ ] Only 3-5 bullets in Preview

## Example Usage

When the user says:

- "Update the PR description"
- "Generate a PR description"
- "Create documentation for this PR"

Follow the workflow above and generate a comprehensive description using the template.
