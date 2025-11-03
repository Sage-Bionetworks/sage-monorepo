# Generate PR Description Prompt

## Objective
Generate a comprehensive, professional pull request description that follows the repository's GitHub template standards and provides clear documentation of all changes made in the feature branch.

## Context Requirements
Before generating the PR description, gather the following information:

### 1. Pull Request Analysis
- [ ] Get the active pull request details using GitHub MCP server
- [ ] Analyze all file changes in the PR (additions, deletions, modifications)
- [ ] Identify the scope and purpose of the changes
- [ ] Determine the primary technology stack involved (Java, TypeScript, Python, etc.)

### 2. Repository Context
- [ ] Confirm the repository name, owner, and branch information
- [ ] Identify the target merge branch (usually `main`)
- [ ] Check for related issues or Jira tickets
- [ ] Understand the monorepo structure and affected applications/libraries

### 3. Technical Analysis
- [ ] Categorize changes by type (feature, bugfix, chore, refactor, etc.)
- [ ] Identify breaking changes or API modifications
- [ ] Document configuration changes (build files, application properties, etc.)
- [ ] Note database schema changes or migrations
- [ ] Identify test updates or new test coverage

## Template Structure
Generate the PR description using this exact format:

```markdown
## Description

[Provide a clear, concise summary of what this PR accomplishes. Focus on the business value and technical impact. Explain WHY these changes were made, not just WHAT was changed.]

## Related Issue

[If applicable, link to Jira ticket or GitHub issue]
Fixes [ISSUE-NUMBER](link-to-issue)

## Changelog

[List all significant changes in bullet points. Group related changes together. Be specific but concise:]

- [Change description with technical details]
- [Change description with technical details]
- [Continue for all major changes...]

## Preview

The [component/service/application] now has:
- [Key improvement or feature]
- [Key improvement or feature]
- [Key improvement or feature]
- [Continue for all major accomplishments...]
```

## Content Guidelines

### Description Section
- Start with the primary business or technical objective
- Explain the motivation behind the changes
- Highlight the main benefits or improvements
- Mention any architectural or design decisions
- Keep it concise but comprehensive (2-4 sentences)

### Related Issue Section
- Always search for related Jira tickets (SMR-XXX format)
- Include GitHub issue numbers if applicable
- Use proper linking format: `[ISSUE-NUMBER](full-url)`
- If no related issue exists, omit this section

### Changelog Section
- **CRITICAL: Use a single-level flat list with NO sub-bullets or nested items**
- **Keep items coarse-grained and high-level** - avoid excessive technical detail
- Use imperative form (e.g., "Add feature X", "Remove deprecated Y")
- Each item should represent a significant, user-facing or system-level change
- Group multiple related low-level changes into one high-level bullet point
- Avoid listing individual file changes or implementation details
- Order from most important to least important changes
- Aim for 3-7 bullet points total, not an exhaustive list

### Preview Section
- **CRITICAL: Use a single-level flat list with NO sub-bullets or nested items**
- **Keep items coarse-grained and high-level** - focus on major outcomes, not detailed changes
- Focus on the end result and benefits from a user or system perspective
- **DO NOT use checkmark emojis (✅) or green checkboxes**
- Use simple bullet points with dashes (-)
- Highlight key improvements, features, or fixes that matter to end users
- Mention metrics if applicable (test coverage, performance, etc.)
- Aim for 3-5 bullet points total describing major accomplishments

## Technical Details to Include

### For Java/Spring Boot Changes
- JPA entity modifications
- Database schema changes
- Spring configuration updates
- Dependency updates
- Test infrastructure changes
- Build system modifications (Gradle/Maven)

### For TypeScript/Angular Changes
- Component updates
- Service modifications
- Configuration changes
- Dependency updates
- Build system updates
- Test additions or modifications

### For Python Changes
- Library/package updates
- Configuration changes
- Dependency updates
- Test infrastructure
- Build/deployment changes

### For Database Changes
- Migration files
- Schema modifications
- Index changes
- Constraint updates
- Data model alignments

## Quality Checklist
Before finalizing the PR description, ensure:

- [ ] All major file changes are documented
- [ ] Technical terminology is accurate
- [ ] Related issues are properly linked
- [ ] Changelog items are specific and clear
- [ ] Preview items highlight actual benefits
- [ ] Grammar and formatting are correct
- [ ] The description follows the exact template structure
- [ ] No implementation details are exposed unnecessarily
- [ ] The business value is clearly communicated
- [ ] **No checkmark emojis (✅) are used in the Preview section**

## Example Usage

```
Please analyze the current pull request and generate a comprehensive PR description following the template above. Include all file changes, related issues, and technical improvements in the proper format.
```

## Notes
- Always use the GitHub MCP server to get accurate PR information
- Focus on user-facing and technical benefits, not just code changes
- Maintain consistency with repository standards and terminology
- Adapt the technical details section based on the actual technologies involved
- Ensure the description is useful for both technical and non-technical reviewers
- Use simple bullet points without emojis in the Preview section
