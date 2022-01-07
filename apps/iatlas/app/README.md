
# iAtlas-App

The iAtlas app is an interactive web portal that provides multiple analysis modules to visualize and explore immune response characterizations across cancer types. The app is hosted on shinyapps.io at [https://isb-cgc.shinyapps.io/iatlas-app/](https://isb-cgc.shinyapps.io/iatlas-app/) and can also be accessed via the main CRI iAtlas page at [http://www.cri-iatlas.org/](http://www.cri-iatlas.org/).

The portal is built entirely in **R** and **Shiny** using the **RStudio** development environment. Layout and interactivity within the portal are achieved by heavy use of the following packages:

- [shinydashboard](https://rstudio.github.io/shinydashboard/)
- [plotly](https://plot.ly/r/)
- [crosstalk](https://rstudio.github.io/crosstalk/)

## Install

### Requirements

- R: https://www.r-project.org/ - v4.0+

- RStudio: https://rstudio.com/products/rstudio/download

### MacOS Install instructions

Install package manager:
- [HomeBrew](https://brew.sh/) (the instructions below are for HomeBrew)
- or [MacPorts](https://www.macports.org/), which is very similar to use for installing packages

Then in the terminal, run:

- xcode-select --install
- brew install cairo
- brew install R or install R .pkg from https://cran.r-project.org/bin/macosx/
- download and install RStudio: https://rstudio.com/products/rstudio/download

### Initialize R Packages and run App

To run the app locally:

1. Clone this repository

1. Open `iatlas-app.Rproj`

1. Follow the instructions in your console

1. Restart R (ctrl+shift+F10)

1. Run the command shiny::runApp() in your console

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

## Installing and Upgrading Packages

This project uses [renv](https://rstudio.github.io/renv/reference/install.html) to manage packages. The definitive list of required packages and versions is stored in the `renv.lock` file.

When adding any new dependencies to the application, they may be added using (where "useful_package" is the name of the package to add):

```R
# install a package
renv::install("useful_package")

# update the renv.lock file
renv::snapshot()
```

Git works well with renv. Once you validate the package should be kept, `git add renv.lock` to the repo and everyone else will automatically install it when they git-pull and re-open their R session or run renv::restore().

If you decide you don't want to include the package, just `git checkout renv.lock` to reset your dependencies to the point before you made changes.

To remove an installed package, run (where "useful_package" is the name of the package to remove):

```R
renv::remove("useful_package")
renv::snapshot()
```

And git-commit it once you are sure you want to keep the changes.


### Upgrading Packages

You can use renv::upgrade("package-name") to upgrade a package, but it'll always update to the very latest uniless you manually tell it otherwise.

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
  name='iatlas-app',
  token='xxx',
  secret='yyy'
)
```

### Deploy
```R
rsconnect::deployApp()
```

## Methods

While many of the results presented in tables and plots are taken directly from Immune Response Working Group (IRWG) data (including the main **feature matrix** and various feature and group annotations), we compute some values internally. Unless otherwise noted, the following methods/tools were used to compute summary statistics:

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
