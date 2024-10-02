#!/bin/bash

REPO="Sage-Bionetworks/sage-monorepo"  # Replace with your GitHub repository, e.g., "owner/repo"
START_DATE="2024-09-01"
END_DATE="2024-09-30"

# Fetch issues and PRs closed between two dates
issues=$(gh issue list --repo "$REPO" --state closed --limit 100 --search "closed:$START_DATE..$END_DATE" --json number,title)
prs=$(gh pr list --repo "$REPO" --state closed --limit 100 --search "closed:$START_DATE..$END_DATE" --json number,title)

# Combine issue and PR numbers and titles into a single JSON array
all_items=$(jq -s 'add' <(echo "$issues") <(echo "$prs"))
echo $all_items

# Loop through each issue and PR, collecting comments
echo "$all_items" | jq -c '.[]' | while read -r item; do
  number=$(echo "$item" | jq -r '.number' 2>/dev/null)
  title=$(echo "$item" | jq -r '.title' 2>/dev/null)

  # Initialize an empty array to store the filtered comments
  comments_array="[]"

  # Check if both number and title are valid
  if [[ -n "$number" && -n "$title" ]]; then
    echo "Fetching comments for issue/PR #$number: $title"

    # Fetch comments for issues
    issue_comments=$(gh api "repos/$REPO/issues/$number/comments" 2>/dev/null)
    if [[ $? -eq 0 && "$issue_comments" != "" && "$issue_comments" != "[]" ]]; then
      filtered_comments=$(echo "$issue_comments" | jq -c '.[]' | while read -r comment; do
        body=$(echo "$comment" | jq -r '.body' 2>/dev/null)
        login=$(echo "$comment" | jq -r '.user.login' 2>/dev/null)

        # Filter out comments made by 'sonarcloud[bot]' and skip comments starting with the specific phrase
        if [[ "$login" != "sonarcloud[bot]" && "$body" != "" && ! "$body" =~ ^"The task complete successfully when I run it locally" ]]; then
          echo "$comment"
        fi
      done)

      # Append filtered comments to the comments array
      if [[ -n "$filtered_comments" ]]; then
        comments_array=$(echo "$filtered_comments" | jq -s '.')
      fi
    fi

    # Fetch comments for pull requests (PR-specific comments)
    pr_comments=$(gh api "repos/$REPO/pulls/$number/comments" 2>/dev/null)
    if [[ $? -eq 0 && "$pr_comments" != "" && "$pr_comments" != "[]" ]]; then
      filtered_comments=$(echo "$pr_comments" | jq -c '.[]' | while read -r comment; do
        body=$(echo "$comment" | jq -r '.body' 2>/dev/null)
        login=$(echo "$comment" | jq -r '.user.login' 2>/dev/null)

        # Filter out comments made by 'sonarcloud[bot]' and skip comments starting with the specific phrase
        if [[ "$login" != "sonarcloud[bot]" && "$body" != "" && ! "$body" =~ ^"The task complete successfully when I run it locally" ]]; then
          echo "$comment"
        fi
      done)

      # Append filtered PR comments to the comments array
      if [[ -n "$filtered_comments" ]]; then
        pr_comments_array=$(echo "$filtered_comments" | jq -s '.')
        comments_array=$(jq -s 'add' <(echo "$comments_array") <(echo "$pr_comments_array"))
      fi
    fi

    # Add the comments array as a new property in the original item
    item_with_comments=$(echo "$item" | jq --argjson comments "$comments_array" '. + {comments: $comments}')
    echo "$item_with_comments"
  else
    echo "Skipping invalid item: $item"
  fi
done
