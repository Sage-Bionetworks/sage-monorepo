# Creating a commit with multiple authors

## Introduction

You can attribute a commit to more than one author by adding one or more `Co-authored-by` trailers
to the commit's message. Co-authored commits are visible on GitHub.

## When to add co-authors to a commit?

Annotating commits with co-authors is required when:

- You are about to commit previously-untracked code written by other users.
  - Example:
    1. User A develops a new feature and submits it as a PR.
    2. User B is tasked with refactoring the PR from User A into multiple PRs.
    3. User B copy-paste code written by User A and adapt it.
    4. User B adds User A as a co-author when the commits include code written by User A.

## Adding co-authors to a commit

Follow the instructions described [here](https://docs.github.com/en/pull-requests/committing-changes-to-your-project/creating-and-editing-commits/creating-a-commit-with-multiple-authors).

Preview of the commit creation:

```
$ git commit -m "Refactor usability tests.
>
>
Co-authored-by: NAME <EMAIL>
Co-authored-by: ANOTHER-NAME <ANOTHER-EMAIL>"
```

## Names and emails of Sage Monorepo contributors

The name and "no-reply" emails of the Sage Monorepo contributors sorted alphabetically:

- Co-authored-by: andrewelamb <7220713+andrewelamb@users.noreply.github.com>
- Co-authored-by: gaiaandreoletti <46945609+gaiaandreoletti@users.noreply.github.com>
- Co-authored-by: Lingling <55448354+linglp@users.noreply.github.com>
- Co-authored-by: mdsage1 <122999770+mdsage1@users.noreply.github.com>
- Co-authored-by: Rongrong Chai <73901500+rrchai@users.noreply.github.com>
- Co-authored-by: sagely1 <114952739+sagely1@users.noreply.github.com>
- Co-authored-by: Thomas Schaffter <3056480+tschaffter@users.noreply.github.com>
- Co-authored-by: Verena Chung <9377970+vpchung@users.noreply.github.com>

> [!NOTE]
> The names and user ID are collected from the profile page of the contributors. If a contributor
does not specify their name on the profile page, the listing below uses their GitHub username
instead of their name. The user ID can be found in the URL of the user's avatar.

## References

- [Creating a commit with multiple
  authors](https://docs.github.com/en/pull-requests/committing-changes-to-your-project/creating-and-editing-commits/creating-a-commit-with-multiple-authors)