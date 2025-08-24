---
applyTo: 'docs/updates/**'
---

# Monthly Updates Generation Instructions

This file provides instructions for generating monthly update pages for the Sage Monorepo documentation s### Step 4: Add Context and Statistics

In### Step 6: Update Navigation

Update `mkdocs.yml` to include the new monthly page:

```yaml
- Updates:
    - Overview: updates/index.md
    - [Month Year]: updates/[month-lowercase]-[year].md
    # ... other months in reverse chronological order
```

### Step 7: Update Index Page

Add the new month to `docs/updates/index.md`:

```markdown
- **[Month Year](month-year.md)** - Brief description of key highlights
```

## File Creation and Regeneration Guidelines

When recreating an existing monthly update file:

1. **Complete File Replacement**: Always replace the entire content of the file from start to finish
2. **Single Content Generation**: Ensure only one complete version of all sections exists in the file
3. **No Content Duplication**: Avoid creating multiple versions of the same section (e.g., multiple "All Pull Requests Merged" sections)
4. **Consistent Section Order**: Follow the exact section order specified in the content structure guidelines
5. **Verification**: After file creation, verify that no duplicated content exists

## Formatting Guidelines

### GitHub Username Formatting

- **Always link GitHub usernames to profile pages**: Use the format `[@username](https://github.com/username)`
- **Apply consistently**: This applies to all mentions of GitHub usernames throughout the document, including:
  - Primary Contributors in summary sections
  - Author mentions in introduction paragraphs
  - Any other references to GitHub users

### Other Formatting Rules

- Use sentence case for section titles (e.g., "Platform infrastructure", not "Platform Infrastructure")
- Format PRs as: `- [#PR_NUMBER](GitHub_URL): PR_TITLE (TICKET_NUMBER)`
- Use anchor links for PR counts: `**[X pull requests](#pull-requests-merged)**`

## Historical Context

This automation process replaces the previous git-based approach with the GitHub API for more accurate and comprehensive data extraction. The GitHub search API provides richer metadata and eliminates the need for manual commit message parsing.r of PRs merged

- Number of unique contributors
- Key metrics (if available)

### Step 5: Create Monthly Update Page

1. Create file: `docs/updates/[month-year].md`
2. Follow the template structure provided above
3. Use data from GitHub API response to populate content
4. Update the navigation in `mkdocs.yml`

Example file creation:

````markdown
# August 2025 Update

_Published on September 1, 2025_

August was a productive month with significant progress across multiple projects...

## Summary

- **Total PRs Merged**: 56
- **Contributors**: 12
- **Key Areas**: Model-AD enhancements, OpenChallenges refactoring, BixArena features

## Major Features & Improvements

[Continue with categorized PR content...]

```## Overview

Each monthly update page should aggregate all pull requests (PRs) merged to the `main` branch during a specific month and present them in a user-friendly format suitable for the Updates section of the documentation site.

## Content Structure Guidelines

Structure the monthly update with the following sections in this exact order:

1. **Title and Introduction**
   - Title format: "# [Month] [Year]" (e.g., "# July 2025", not "# July 2025 Update")
   - Include publication date: "_Published on [Month] [Day], [Year]_"
   - Brief welcome paragraph with key statistics (total PRs, main contributors with links to their GitHub profiles)
   - Add anchor link to PR count: **[X pull requests](#pull-requests-merged)** to link to detailed PR list
   - **GitHub Username Formatting**: Always link usernames to their GitHub profile pages using the format `[@username](https://github.com/username)`

2. **Summary**
   - Use bullet point format with key statistics:
     * **Total Pull Requests**: X merged PRs
     * **Key Focus**: Brief summary of main areas of work
     * **Major Projects**: Highlight significant initiatives
   - Keep concise and factual

3. **Technical Architecture Overview**
   - Use subsections (###) to organize different architectural areas
   - Start with human-friendly descriptions of key technical decisions and architectural highlights
   - Focus on the "why" and "what" rather than implementation details
   - Make it accessible to both technical and non-technical readers
   - Cover topics like: platform foundations, development approaches, quality assurance, cross-platform compatibility
   - Examples of subsections: Platform evolution, User experience enhancements, API modernization, Infrastructure updates, Developer experience
   - **Section Title Capitalization**: Use sentence case for all section titles (###). Only capitalize the first word, proper nouns, and acronyms (e.g., "API modernization initiative", "OpenChallenges platform evolution", "Model-AD user interface enhancements")
   - **Readability Guidelines**:
     * Keep paragraphs short (2-3 sentences max when possible)
     * Break long sentences into shorter, more digestible ones
     * Use natural paragraph breaks to separate related but distinct concepts
     * Aim for 1-2 paragraphs per subsection maximum
     * Use parallel structure and consistent tone across subsections

4. **Pull Requests Merged**
   - Comprehensive list of every PR merged during the month
   - Organize PRs into logical categories (e.g., Platform infrastructure, Applications, Documentation, etc.)
   - **Category Title Capitalization**: Use sentence case for category titles. Only capitalize the first word, proper nouns, and acronyms (e.g., "Platform infrastructure", "OpenChallenges platform infrastructure", "Model-AD user interface enhancements")
   - Do NOT include PR counts in section titles (e.g., use "Platform infrastructure" not "Platform infrastructure (14 PRs)")
   - Format each PR as: `- [#PR_NUMBER](GitHub_URL): PR_TITLE (TICKET_NUMBER)`
   - Use only the PR title from GitHub, do not add additional descriptions or summaries
   - Include ticket numbers (e.g., SMR-xxx, MG-xxx) when present in the PR title
   - Ensure all PRs are accounted for and properly categorized
   - Common categories include:
     * Platform infrastructure: Core setup, CI/CD, tooling, build systems, cross-platform compatibility
     * [Application Name]: Application-specific features, API development, UI components, database integration
     * Containerization & Docker: Docker setup, container orchestration, development environments
     * Documentation & governance: Documentation, community guidelines, setup guides

5. **Community Impact**
   - Broader impact and significance of the month's work
   - Focus on values, community, and long-term vision

6. **Thank You**
   - Start with: "Last but certainly not least, a big **_Thank You_** to the contributors of the Sage monorepo."
   - Follow with a bullet list of GitHub usernames linked to their profiles
   - Format each contributor as: `- [@username](https://github.com/username)`
   - List all unique contributors who had PRs merged during the month

7. **Closing Statement**
   - Brief inspirational paragraph in italics
   - Include link to repository: [Join us](https://github.com/Sage-Bionetworks/sage-monorepo)
```
````

````

## How to Generate a Monthly Update Page

### Step 1: Gather PR Data

Use the GitHub search API to find all PRs merged during the target month:

```typescript
// Use mcp_github_search_pull_requests tool with date range
mcp_github_search_pull_requests({
  owner: 'Sage-Bionetworks',
  repo: 'sage-monorepo',
  query: 'repo:Sage-Bionetworks/sage-monorepo is:merged merged:YYYY-MM-01..YYYY-MM-31',
});
```

The API returns comprehensive PR data including:

- PR number, title, and description
- Author information and assignees
- Merge date and timestamps
- Labels and project associations
- Related issue links (Jira tickets)

Example query for August 2025:

```typescript
mcp_github_search_pull_requests({
  owner: 'Sage-Bionetworks',
  repo: 'sage-monorepo',
  query: 'repo:Sage-Bionetworks/sage-monorepo is:merged merged:2025-08-01..2025-08-31',
});
```

### Step 2: Categorize PRs

Analyze the PR data from the GitHub API response to group PRs by the following categories:

**Advantages of GitHub API over git commands:**

- Access to PR descriptions and body content for better categorization
- Author and assignee information readily available
- Direct access to Jira ticket links in PR descriptions
- Merge timestamps and precise date information
- No need to parse commit messages manually

Group PRs by the following categories:

1. **Major Features & Improvements**

   - New product features
   - Significant enhancements
   - New integrations

2. **Product-Specific Updates**

   - Agora improvements
   - OpenChallenges features
   - AMP-ALS updates
   - BixArena changes
   - Model-AD enhancements
   - Synapse improvements

3. **Developer Experience**

   - Build system improvements
   - Development tooling
   - Code quality enhancements
   - Testing improvements

4. **Infrastructure & Operations**

   - CI/CD pipeline updates
   - Deployment improvements
   - Performance optimizations
   - Security patches

5. **Documentation**

   - Documentation updates
   - API documentation
   - Tutorial improvements

6. **Bug Fixes**
   - Critical bug fixes
   - Performance fixes
   - UI/UX improvements

### Step 3: Analyze and Extract Information

Use the GitHub API response to extract:

1. **Project Categories**: Look for prefixes like `feat(project-name)`, `fix(project-name)`, `chore(project-name)`
2. **Jira Tickets**: Extract ticket numbers from PR descriptions (e.g., MG-358, SMR-385)
3. **Contributors**: Get unique authors and assignees
4. **Change Types**: Categorize by feat/fix/chore/docs/refactor
5. **Impact Areas**: Identify affected projects (agora, model-ad, openchallenges, etc.)

**Example Analysis**:

```typescript
// From the API response, extract categorization data:
const prsByProject = groupBy(prs, (pr) => extractProjectFromTitle(pr.title));
const prsByType = groupBy(prs, (pr) => extractChangeType(pr.title));
const contributors = unique(prs.map((pr) => pr.user.login));
const jiraTickets = prs.flatMap((pr) => extractJiraTickets(pr.body));
```

For each PR:

- Use the PR title exactly as it appears in GitHub
- Include the PR number with a link to GitHub
- Include ticket numbers when present in the title (e.g., SMR-xxx, MG-xxx)
- Do not add additional descriptions or summaries beyond what's in the title

### Step 4: Add Context and Statistics

Include:

- Total number of PRs merged
- Number of unique contributors
- Key metrics (if available)
- Links to relevant product pages
- Community feedback incorporation

### Step 5: File Naming Convention

- File name: `docs/updates/[month-lowercase]-[year].md`
- Examples: `february-2022.md`, `march-2022.md`, `december-2024.md`

### Step 6: Update Navigation

**CRITICAL**: Add the new page to `mkdocs.yml` in the Updates section in reverse chronological order (newest first). The page will NOT be accessible without this step.

```yaml
- Updates:
    - Overview: updates/index.md
    - [Month Year]: updates/[month-lowercase]-[year].md
    # ... other months in reverse chronological order
```

Example for March 2022:
```yaml
- Updates:
    - Overview: updates/index.md
    - August 2025: updates/august-2025.md
    - July 2025: updates/july-2025.md
    - January 2024: updates/january-2024.md
    - September 2023: updates/september-2023.md
    - June 2023: updates/june-2023.md
    - January 2023: updates/january-2023.md
    - March 2022: updates/march-2022.md  # <- Add new entry here
    - February 2022: updates/february-2022.md
```

**Important**: Ensure the new update is placed in chronological order with the most recent months first.

### Step 7: Update Index Page

Add the new month to `docs/updates/index.md` in reverse chronological order (newest first):

```markdown
- **[Month Year](month-year.md)** - Brief description of key highlights
```

**Important**: Place the new entry in the correct chronological position. The index should list updates from newest to oldest.

### Step 8: Final Formatting Check

Ensure the monthly update page includes:

1. **Separator line between Thank You and Closing Statement**: Add a line with just "---" between the Thank You section and the closing statement
2. **Proper section ordering**: Verify all sections follow the specified order
3. **GitHub username linking**: Confirm all GitHub usernames are properly linked using `[@username](https://github.com/username)` format
4. **Sentence case titles**: Verify all section and subsection titles use sentence case capitalization

## Example Git Commands for Specific Months

## Quality Guidelines

1. **User-Focused**: Write for end users, not just developers
2. **Clear Categories**: Organize changes logically
3. **Consistent Format**: Follow the template structure
4. **Links**: Include links to PRs, issues, and relevant documentation
5. **Completeness**: Don't skip minor but user-visible changes
6. **Context**: Explain why changes matter to users
7. **Forward-Looking**: Include preview of upcoming work when possible

## Tips for Effective Updates

- **Aggregate Related PRs**: Group small related changes together

## Quality Guidelines

1. **User-Focused**: Write for end users, not just developers
2. **Clear Categories**: Organize changes logically
3. **Consistent Format**: Follow the template structure
4. **Links**: Include links to PRs, issues, and relevant documentation
5. **Completeness**: Don't skip minor but user-visible changes
6. **Context**: Explain why changes matter to users
7. **Forward-Looking**: Include preview of upcoming work when possible

## Tips for Effective Updates

- **Aggregate Related PRs**: Group small related changes together
- **Highlight Impact**: Focus on user benefits and improvements
- **Use Active Voice**: "Added feature X" rather than "Feature X was added"
- **Include Visuals**: Reference screenshots or demos when applicable
- **Community Focus**: Acknowledge contributors and community feedback
- **Cross-Reference**: Link to relevant documentation and resources

## Automation Opportunities

Consider creating scripts to:

- Extract PR data automatically
- Generate initial categorization
- Create draft update pages
- Update navigation files
- Validate link integrity

This process ensures comprehensive, user-friendly monthly updates that showcase the continuous improvement of the Sage Monorepo platform.
````
