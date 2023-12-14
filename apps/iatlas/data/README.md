# iAtlas-Data

The iAtlas app is an interactive web portal that provides multiple analysis modules to visualize and explore immune response characterizations across cancer types. The app is hosted on shinyapps.io at [https://isb-cgc.shinyapps.io/shiny-iatlas/](https://isb-cgc.shinyapps.io/shiny-iatlas/) and can also be accessed via the main CRI iAtlas page at [http://www.cri-iatlas.org/](http://www.cri-iatlas.org/).

This repository contains the source data for building the database iAtlas uses. The code in this repository consumes the source data, partially-verifies it and builds the database.

## Status

### Staging

[![pipeline status](https://gitlab.com/cri-iatlas/iatlas-data/badges/staging/pipeline.svg)](https://gitlab.com/cri-iatlas/iatlas-data/-/commits/staging)

[![coverage report](https://gitlab.com/cri-iatlas/iatlas-data/badges/staging/coverage.svg)](https://gitlab.com/cri-iatlas/iatlas-data/-/commits/staging)

## iAtlas-App, iAtlas-Data, and iAtlas-API

This app is spit into three repositories:

- [iatlas-app](https://github.com/CRI-iAtlas/iatlas-app) - for all the shiny-app R
- [iatlas-data](https://gitlab.com/cri-iatlas/iatlas-data) - for all the iatlas-related data, DB-creation and DB-populating code (this coderepository)
- [iatlas-api](https://gitlab.com/cri-iatlas/iatlas-api) - for accessing the iatas data via GraphQL. The [GraphIQL playground](http://ec2-54-190-27-240.us-west-2.compute.amazonaws.com/graphiql) can be used to view the schemas, API documentation, and try out queries.

Follow the instructions below to create and populate your local database. Then, if you want to also run the iatlas-app, clone that repository and follow it's README to get started.


## Requirements

- R: [https://www.r-project.org/](https://www.r-project.org/) - v4.0.2

  - Please note: R compiled from source (as Homebrew on a mac is) doesn't ever use CRAN binary packages. This can create problems when installing packages.

    A Mac installation installed from a pkg file as listed on the R installation site will automatically use mac.binary packages.

- RStudio: [https://rstudio.com/products/rstudio/download](https://rstudio.com/products/rstudio/download)

- Docker: [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop)

  Ensure that the location of the repository is shared via docker:

  - Mac: [https://docs.docker.com/docker-for-mac/#file-sharing](https://docs.docker.com/docker-for-mac/#file-sharing)

  - Windows: [https://docs.microsoft.com/en-us/archive/blogs/stevelasker/configuring-docker-for-windows-volumes](https://docs.microsoft.com/en-us/archive/blogs/stevelasker/configuring-docker-for-windows-volumes)

- libpq (postgres): [https://www.postgresql.org/download/](https://www.postgresql.org/download/)

- lib cairo: [https://www.cairographics.org/](https://www.cairographics.org/) (only required for iAtlas client)

- gfortran (libgfortran): usually installed with gcc

- Download the (very large) RNA Seq Expression file.

  - Download [EBPlusPlusAdjustPANCAN_IlluminaHiSeq_RNASeqV2.geneExp.feather](https://www.dropbox.com/s/a3ok4o63glq4p3j/EBPlusPlusAdjustPANCAN_IlluminaHiSeq_RNASeqV2.geneExp.feather?dl=0) and put it in the `/feather_files` folder
  - TODO: Move this file into Synapse. This file currently lives in Shane Brinkman-Davis's Dropbox (shane@genui.com).\
    The original tsv is found at: [https://gdc.cancer.gov/node/905/](https://gdc.cancer.gov/node/905/)

- PostgreSQL (v12.4)
  The instructions below assume that there is a PostgreSQL server running locally. If this is not the case, please see information on [running PostgreSQL in Docker](#running-postgres-in-docker) below.

- To change any of the environment variables used by the app see [Environment Variables](#environment-variables) below.

### MacOS Install instructions

Install package manager: [HomeBrew](https://brew.sh/) (or [MacPorts](https://www.macports.org/) or your package manager of choice)

Then run these in your shell:

- xcode-select --install
- brew install cairo
- brew install postgres
- download and install RStudio: [https://rstudio.com/products/rstudio/download](https://rstudio.com/products/rstudio/download)
- download and install Docker: [https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop)

## Environment Variables

All the environment variables used by the app have defaults. To set the environment variables, simply run the following bash script from the root of the project. (Please note the dot - `.` at the very beginning of the command. This will "source" the script.):

```sh
. ./scripts/set_env_variables.sh
```

The default environment variables' values may be over-written by adding the value to a `.Renviron` file in the root of the project. This file is not versioned in the repository.

The [`.Renviron-SAMPLE`](./.Renviron-SAMPLE) file is an example of what the `.Renviron` could be like and may be used as a reference.

## PostgreSQL Database

### Running Postgres in Docker

A simple way to get PostgreSQL running locally is to use Docker. Here is a simple Dockerized PostgreSQL server with pgAdmin:

["postgres_docker" on Github](https://github.com/generalui/postgres_docker)

### Connecting to a different Database

Alternatively, the app may be set up to connect to the existing staging database or another database.

To connect to a different database (ie staging), the `.env-dev` file must also be used with values similar to:

```.env-dev
POSTGRES_DB=iatlas_staging
POSTGRES_HOST=iatlas-staging-us-west-2.cluster-cfb68nhqxoz9.us-west-2.rds.amazonaws.com
POSTGRES_PASSWORD={Get_the_staging_password}
POSTGRES_USER=postgres
```

### Initialize R Packages and build the Database

To build the database locally:

1. Open `iatlas-data.Rproj` in Rstudio.

1. Follow the instructions.

When built, the database will be available on `localhost:5432`. The database is called `iatlas_dev`.

## Testing

The test suite defined in tests/\* unit-tests all the support functions and does an integration test across the whole build process using a subset of the real data.

The primary way to run test is:

```R
devtools::test()
```

You can run only test-files matching a regex expression:

```R
devtools::test(filter = '_db?_')
```

You can also run the tests interactively - i.e. have the tests auto re-run whenever you make a change:

```R
testthat::auto_test_package()
```

You can view code-coverage with:

```R
covr::report()
```

### Testing with Control Data

> If `control_data/` exists in the root of your project, you'll get control-data validations automatically when you run build_iatlas_db()

If you are altering code but not data, and you don't expect the data output to change, and you want to test it against the real data... this is section is for you.

Before you make your changes, create a folder in the root of the project called "control_data", then run build_iatlas_db() with the known-good code. This will generate copies of all the output data in the control_data (currently about 400 megabytes of feather files). Then you can make your changes and re-run build_iatlas_db(). The second and subsequence passes will validate their output against the existing data in control_data.

Here are the steps:

1. `shell> mkdir control_data`
2. `R> build_iatlas_db() # builds control data first pass`
3. Make changes
4. Re-run, build_iatlas_db(), possibly using resume_at or build_only options

If output mismatches the build will abort with a nice message. It will provide a function in the global namespace to overwrite and update the control_data if the new code is considered correct. Otherwise, you can re-run your code once you fix the discrepancy with `build_iatlas_db(resume = "auto")`

> Note: `control_data/` is not checked in with the git repo. To reset your control_data, simply delete all the files in the folder. To disable control-data validation, remove the folder entirely.

## Data

### Data Model

Information on the data model can be found in the `data_model` folder which contains this [README.md](./data_model/README.md#iatlas-data-model) file.

### Data Structure

Information on the data structure the [FEATHER_FILES.md](./FEATHER_FILES.md#iatlas-data-structures) markdown file.

### Data Sources

Input data for the Shiny-iAtlas portal was accessed from multiple remote sources, including **Synapse**, the **ISB Cancer Genomics Cloud**, and **Google Drive**. The feather files derived from this data and used to populate the database are stored Synapse and create via the [iatlas-feature_files](https://github.com/CRI-iAtlas/iatlas-feather-files) app. Please see [FEATHER_FILES.md](./FEATHER_FILES.md) for more info on these files.
