# Using Nx

## Developer Workflow

Embracing the monorepo-style development often requires some changes to the
development workflow.

**Our CI should run the following checks:**

- It checks that the changed code is formatted properly (`nx format:check`).
- It runs lint checks for all the projects affected by a PR/commit.
- It runs unit tests for all the projects affected by a PR/commit.
- It runs e2e tests for all the apps affected by a PR/commit.
- It rebuilds all the apps affected by a PR/commit.

**Note all the projects affected by a PR/commit.** This is very important.
Monorepo-style development only works if we rebuild, retest, and relint only the
projects that can be affected by our changes. If we instead retest everything,
we will get the the following problems:


- The performance of CI checks will degrade over time. The time it takes to run
  the CI checks should be proportional to the impact of the change, not the size
  of the repo.
- We will be affected by the code your change didn’t touch

We should utilize `affected:*` commands to build and test projects. Read more
about them [here](https://nx.dev/cli/affected).

### Trunk-based development

Monorepo-style development works best when used with trunk-based development.

When using trunk-based development, we have a single main branch (say `main`)
where every team submits their code. And they do it as soon as possible. So if
someone works on a large feature, they split it into a few small changes that
can be integrated into `main` in a week. In other words, when using trunk-based
development, teams can create branches, but they are short-lived and focus on a
specific user story.

One issue folks often raise in regards to trunk-based development is "things
change under you while you are trying to create a release". This can definitely
happen, especially when manual testing is involved. To mitigate we can create a
release branch where we would cherry-pick commits from `main` to. With this, we
can still frequently merge code into `main` and have our release isolated from
changes made by other teams.

## Code Organization & Naming Conventions

### Apps and Libs

- Apps configure dependency injection and wire up libraries. They should not
  contain any components, services, or business logic.
- Libs contain services, components, utilities, etc. They have well-defined
  public API.

A typical Nx workspace has many more libs than apps, so pay especially careful
attention to the organization of the libs directory.

### Scope (Where a library lives, who owns it)

It's a good convention to put applications-specific libraries into the directory
matching the application name. This provides enough organization for small to
mid-size applications.

For example:

```
apps/web-app          <---- app
libs/web-app          <---- app-specific libraries
```

If more than one web app was added to the monorepo, say `booking` and `check-in`
for an airline, the file structure would look something like this:

```
apps/booking               <---- app
apps/check-in              <---- app
libs/booking               <---- app-specific libraries
libs/check-in              <---- app-specific libraries
```

### Type (What is in the library)

With Nx, we can partition our code into small libraries with well-defined public
API. So we can categorize our libraries based on what they contain.

- Utility libraries contain utilities and services.
- Data-access can contain NgRx-related code.
- Component libraries should contain presentational components and directives.
- Feature libraries contain business logic, application screens, etc.

This categorization is a good starting point, but other library types are quite
common too (e.g., mock libraries). It's a good idea to establish naming
conventions (e.g., utilities-testing, components-buttons). Having them helps
developers explore the code and feel comfortable no matter where they are in the
repository.

### Managing Dependencies

For a large organization it's crucial to establish how projects can depend on
each other. For instance:

- Libraries with a broader scope (e.g., `shared/ui`) should not depend on the
  libraries with narrower scope (e.g., `happynrwlapp/search/utils-testing`).
- Component libraries should only depend on other component libraries and
  utility libraries, but should not depend feature libraries.

Nx provides a feature called tags that can be used to codify and
statically-enforce these rules. Read more about tags
[here](https://nx.dev/structure/monorepo-tags).

## Code Ownership

It's crucial for a large company with multiple teams contributing to the same
repository to establish clear code ownership.

Since Nx allows us to group apps and libs in directories, those directories can
become code-ownership boundaries. That's why the structure of an Nx workspace
often reflects the structure of an organization. GitHub users can use the
CODEOWNERS file for that.

```
/libs/happynrwlapp          julie
/apps/happynrwlapp          julie
/libs/shared/ui             hank
/libs/shared/utils-testing  julie,hank
```

If you want to know more about code ownership on Github, please check the
[documentation on the CODEOWNERS
file](https://docs.github.com/en/repositories/managing-your-repositorys-settings-and-features/customizing-your-repository/about-code-owners).

## Cheat Sheet

This [cheat sheet](cheat-sheet.md) provides an overview of the commands needed
when developing in this monorepo.

## References

The content of this document was initially copied-pasted from:

- [Using Nx at Enterprises](https://nx.dev/guides/monorepo-nx-enterprise)