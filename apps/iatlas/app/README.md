
# iAtlas-App

The iAtlas app is an interactive web portal that provides multiple analysis modules to visualize and explore immune response characterizations across cancer types. The app is hosted on shinyapps.io at [https://isb-cgc.shinyapps.io/shiny-iatlas/](https://isb-cgc.shinyapps.io/shiny-iatlas/) and can also be accessed via the main CRI iAtlas page at [http://www.cri-iatlas.org/](http://www.cri-iatlas.org/).

The portal is built entirely in **R** and **Shiny** using the **RStudio** development environment. Layout and interactivity within the portal are achieved by heavy use of the following packages:

- [shinydashboard](https://rstudio.github.io/shinydashboard/)
- [plotly](https://plot.ly/r/)
- [crosstalk](https://rstudio.github.io/crosstalk/)

## iAtlas-App and iAtlas-Data

This app is spit into two repositories:

- [iatlas-app](https://github.com/CRI-iAtlas/iatlas-app) - for all the shiny-app R code (this repository)
- [iatlas-data](https://github.com/CRI-iAtlas/iatlas-data) - for all the iatlas-related data, DB-creation and DB-populating code

The easiest way to get start is to clone both repositories, go through the iatlas-data README, then go through this README. More or less this means:

1. install a few dependencies (~30min)
1. load iatlas-data; auto-install its packages; build the db (~15-75min depending on package install time)
1. load iatlas-app; auto-install its packages; and run shiny (~5-60min depending on package install time)

You don't need iatlas-data to run iatlas-app if you:

* Already have the database built locally
* You configure your environment variables to connect directly to the staging or production DB servers


## Install

### Requirements

- iatlas-data is needed to create and populate the database

  - `git clone https://github.com/CRI-iAtlas/iatlas-data`
  - follow the instructions in the README

- R: https://www.r-project.org/ - v3.6.2

- RStudio: https://rstudio.com/products/rstudio/download

- Docker: https://www.docker.com/products/docker-desktop

  Ensure that the location of the repository is shared via docker:

  - Mac: https://docs.docker.com/docker-for-mac/#file-sharing

  - Windows: https://docs.microsoft.com/en-us/archive/blogs/stevelasker/configuring-docker-for-windows-volumes

- git-lfs: https://git-lfs.github.com

  For installation on the various platforms, please see this [git-lfs wiki](https://github.com/git-lfs/git-lfs/wiki/Installation)

  Some feather files are _very_ large. `git-lfs` is used to store these files.

  **Please note**: `git lfs install` _must_ be executed within the repository directory immediately after cloning the repo.

- libpq (postgres): https://www.postgresql.org/download/

- lib cairo: https://www.cairographics.org/ (only required for iAtlas client)

- gfortran (libgfortran): usually installed with gcc

### MacOS Install instructions

Install package manager: [HomeBrew](https://brew.sh/) (or [MacPorts](https://www.macports.org/) or your package manager of choice)

Then:

- xcode-select --install
- brew install R
- brew install cairo
- brew install git-lfs
- brew install postgres
- download and install RStudio: https://rstudio.com/products/rstudio/download
- download and install Docker: https://www.docker.com/products/docker-desktop

### Initialize R Packages, Database and run App

To run the app locally:

1. Make sure you've created and populated the local postgres database using the iatlas-data repository (see above)

1. Clone this repository

1. Open `shiny-iatlas.Rproj`

1. Follow the instructions in your console

## Branches: Staging & Master

We recommend the following workflow. When you are starting a new feature or project:

### Create a Working Branch

Checkout and get the latest from staging:
```shell
git checkout staging
git pull
```

Create your new branch:
```shell
git checkout -b feature/my-new-feature
# 'git checkout -b' is the same as:
# > git branch feature/my-new-feature
# > git checkout feature/my-new-feature
```

Do your work and periodically commit your changes:
```shell
git add .
git commit -m "my message"
git push
```

Keep up to date with changes on staging by others:
```shell
git checkout staging
git pull
git checkout feature/my-new-feature
git merge staging
```

### Staging

When you are ready to deploy your code to staging, you'll need to create a pull request. First, push your branch to Github:

```shell
git push
```

Then go to the repository on Github, go to your branch, and create a pull-request:

* https://github.com/CRI-iAtlas/iatlas-app

Once your pull request has been accepted, our GitLab CI/CD will automatically deploy your changes to the staging server. Note: It can take 10-15 minutes to update.

* Staging Server: https://isb-cgc.shinyapps.io/iatlas-staging/

### Master

Once you validate everything is working in staging, the staging branch can be merged into master and then deployed to production.

* TODO: expand on the production deployment process

## Installing and Upgrading Packages

This project uses [renv](https://rstudio.github.io/renv/reference/install.html) to manage packages. The definitive list of required packages and versions is stored in the `renv.lock` file.

When adding any new dependencies to the application, they may be added using (where "useful_package" is the name of the package to add):

```R
# install a package
renv::install("useful_package")

# update the renv.lock file
renv::snapshot()
```

Git works will with renv. Once you validate the package should be kept, `git add renv.lock` to the repo and everyone else will automatically install it when they git-pull and re-open their R session or run renv::restore.

If you decide you don't want to include the package, just `git checkout renv.lock` to reset your dependencies to the point before you made changes.

To remove an installed package, run (where "useful_package" is the name of the package to remove):

```R
renv::remove("useful_package")
renv::snapshot()
```

And git-commit it once you are sure you want to keep the changes.


### Upgrading Packages

You can use renv::upgrade("package-name") to upgrade a package, but it'll always update to the very latest uniless you manually tell it otherwise.

* IMPORTANT: A few of the packages we currently use don't work with the latest versions, so **don't use renv::update to update all packages.**
* Known upgrade-problems:
  - don't upgrade shinycssloaders to v0.3.0
  - don't upgrade plotly to v4.9.2

### Installing Packages and RsConnect

This application is deployed using rsconnect::deployApp(). As of the current version (0.8.16-9000), rsconnect does not support using the renv.lock file to determin which packages to deploy. Instead, it uses its own till (rsconnect::appDependencies) to detect your app's dependencies from the R source files and the DESCRIPTION file. The good news is it will generally get the correct version, since it'll use the current version you have installed and renv manages that tightly. The bad news is sometimes it won't detect you need a package. Here's how you solve that problem:

* If a package is missing after you push to the staging branch and check the staging server, add that the package name to the `Imports:` section of the DESCRIPTION file.


## Deployment

The first time you deploy, go through the Deployment-Setup instructions. Afterwards, you can deploy with one line.

### Deployment Setup (First-Time-Only)

You'll need to set up your credentials for shinyapps.io. You can get your codes from:

- [https://www.shinyapps.io/admin/#/tokens](https://www.shinyapps.io/admin/#/tokens)

Paste and evaluate your tokens in the RStudio console. They look like this:

```R
# shinyapps.io example credentials
rsconnect::setAccountInfo(
  name='shiny-iatlas',
  token='xxx',
  secret='yyy'
)
```

## Configuration and Environment Variables

The database connection is configured in the `config.yml` file. We use the [config package](https://github.com/rstudio/config) to load the correct config. See init.R for exactly how this is done. You can also override the config by setting these environment variables:

```
DB_NAME=XYZ
DB_HOST=XYZ
DB_PORT=123
DB_USER=XYZ
DB_PW=XYZ
```

WARNING! `config.yml` is part of the *public* git repository. Do NOT put sensitive passwords or keys in `config.yml`. Use environment variables for any passwords or keys you do not wish to share publicly.

## Methods

While many of the results presented in tables and plots are taken directly from IRWG data (including the main **feature matrix** and various feature and group annotations), we compute some values internally. Unless otherwise noted, the following methods/tools were used to compute summary statistics:

### Correlation — Spearman's rank-order correlation

```R
stats::cor(x, y, method = "spearman", use = "pairwise.complete.obs")
```

### Concordance Index (CI)

Concordance indexes for survival endpoints with respect to different immune readouts were computed using a custom package developed by Tai-Hsien Ou Yang at Columbia University. The **concordanceIndex** package includes a single synonymous function that can be used as follows:

```R
concordanceIndex::concordanceIndex(predictions, observations)
```

... where `predictions` and `observations` are numerical vectors of the same length.
