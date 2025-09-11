# Creating a commit with multiple authors

## Introduction

You can attribute a commit to more than one author by adding one or more `Co-authored-by` trailers
to the commit's message. Co-authored commits are visible on GitHub.

## When to add co-authors to a commit?

Annotating commits with `Co-authored-by` is generally encouraged when copying code from other
developers.

Annotating commits with `Co-authored-by` is **required** when you are committing
previously-untracked code written by other users.

!!! example "Example"

    1. Bea develops a new feature and submits it as a pull request (PR).
    2. Eddy is tasked with refactoring Bea's PR into multiple PRs.
    3. Eddy copies and pastes code written by Bea and adapts it.
    4. Eddy adds Bea as a co-author to the commits that include code she wrote.

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

The name and "no-reply" emails of the Sage Monorepo contributors (sorted alphabetically):

```
Co-authored-by: andrewelamb <7220713+andrewelamb@users.noreply.github.com>
```

```
Co-authored-by: gaiaandreoletti <46945609+gaiaandreoletti@users.noreply.github.com>
```

```
Co-authored-by: Lingling <55448354+linglp@users.noreply.github.com>
```

```
Co-authored-by: mdsage1 <122999770+mdsage1@users.noreply.github.com>
```

```
Co-authored-by: Rongrong Chai <73901500+rrchai@users.noreply.github.com>
```

```
Co-authored-by: sagely1 <114952739+sagely1@users.noreply.github.com>
```

```
Co-authored-by: Thomas Schaffter <3056480+tschaffter@users.noreply.github.com>
```

```
Co-authored-by: Verena Chung <9377970+vpchung@users.noreply.github.com>
```

!!! note

    The names, usernames and user IDs of the contributors are collected from their GitHub profile
    pages. If a contributor does not specify their name on the profile page, the listing uses their
    username instead of their name. The user ID can be found in the URL of the user's avatar.

## References

- [Creating a commit with multiple
  authors](https://docs.github.com/en/pull-requests/committing-changes-to-your-project/creating-and-editing-commits/creating-a-commit-with-multiple-authors)
