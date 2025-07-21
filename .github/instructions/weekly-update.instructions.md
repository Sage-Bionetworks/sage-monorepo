# Weekly Update Instructions

This document provides instructions for generating a standardized weekly update for the sage-monorepo project.

## Purpose

The weekly update aims to:

1. Summarize progress across multiple projects in the monorepo
2. Highlight key contributions by team members
3. Provide a clear overview for both technical and non-technical stakeholders
4. Maintain consistent formatting and structure across updates

## Structure

The weekly update should follow this consistent structure:

### 1. Header

```markdown
# Sage-Bionetworks/sage-monorepo Weekly Update

[Current Date Range, e.g., July 5-12, 2025]
```

### 2. Highlights (🌟)

A brief paragraph (2-3 sentences) summarizing the main achievements of the week. This should be high-level and accessible to all readers.

### 3. Project Updates (📊)

Break down updates by project area. For each project:

- Include a brief introduction sentence
- List key updates as bullet points with:
  - Bold headers for each update
  - Brief description of the change/feature
  - Contributors in parentheses (@username)

Common project areas include:

- Model-AD Platform
- OpenChallenges Platform
- Agora Platform
- Explorers Framework
- BixArena Project

### 4. Technical Improvements (🛠️)

Group technical improvements by category:

- Infrastructure Enhancements
- Angular Modernization
- Testing Infrastructure
- Other relevant categories based on the week's work

For each category, include bullet points with:

- Bold headers for specific improvements
- Brief description
- Contributors (@username)

### 5. Contributors (👥)

List all contributors for the week with their PR counts:

```markdown
This week's progress was made possible by the following contributors:

- @username1 - X PRs
- @username2 - Y PRs
  ...
```

### 6. Coming Next (🔮)

A brief paragraph about upcoming focus areas and expected work for the next week.

## Formatting Guidelines

1. **Use emoji** for section headers to make the document visually engaging
2. **Bold key terms** in bullet points for scannability
3. **Include contributor handles** (@username) for all work
4. **Keep descriptions concise** but informative
5. **Use consistent styling** throughout the document

## Data Collection Process

1. Review PRs merged in the specified date range:

   - Use `mcp_github_list_commits` to fetch commits for each PR
   - Analyze commit messages for details on changes

2. Identify contributors:

   - Note PR creators
   - Check for co-authors mentioned in commit messages
   - Count total PRs per contributor

3. Group changes by project area and type:
   - Look for prefixes in commit messages (e.g., "feat(openchallenges)")
   - Group similar technical improvements together

## Example Format

```markdown
# Sage-Bionetworks/sage-monorepo Weekly Update

July 5-12, 2025

## 🌟 Highlights

This week saw significant progress across multiple projects in the sage-monorepo, with improvements to data infrastructure, API enhancements, and technical modernization efforts.

## 📊 Project Updates

### Model-AD Platform

The Model-AD team made significant progress updating their data infrastructure:

- **Data Version Upgrade**: Data release updated to version 63, with improved collection organization and indexing (@hallieswan)
- **MongoDB Schema Tools**: Added new tooling to generate schema files for improved documentation and validation (@sagely1)

[Additional project sections...]

## 🛠️ Technical Improvements

### Infrastructure Enhancements

- **Development Environment**: Updated dev container Nx environment variables and configuration for improved developer experience (@tschaffter)

[Additional technical improvement sections...]

## 👥 Contributors

This week's progress was made possible by the following contributors:

- @hallieswan - 4 PRs
- @tschaffter - 12 PRs
- @sagely1 - 2 PRs
- @vpchung - 1 PR
- @rrchai - 1 PR

## 🔮 Coming Next

The team continues to focus on improving data infrastructure, modernizing Angular implementations, and enhancing API capabilities across multiple platforms. Stay tuned for continued progress on the BixArena project and further infrastructure optimizations.
```

## Common PR Types to Look For

1. **Feature Additions**: Look for "feat" prefixes in commit messages
2. **Bug Fixes**: Look for "fix" prefixes
3. **Infrastructure Updates**: Look for "chore" or "infra" prefixes
4. **Refactoring**: Look for "refactor" prefixes
5. **Documentation**: Look for "docs" prefixes

## Best Practices

1. **Verify PR counts** against actual GitHub data
2. **Acknowledge all contributors**, even for small contributions
3. **Group related changes** together for readability
4. **Highlight cross-cutting improvements** that benefit multiple projects
5. **Use consistent terminology** with the development team
6. **Keep the audience in mind** - make it accessible to both technical and non-technical readers

By following these instructions, you'll create consistent, informative weekly updates that effectively communicate progress across the sage-monorepo project.
