if (file.exists("renv/activate.R")) {
  source("renv/activate.R")

  cat("
--------------------------------------------------------------------------------
Welcome to the iAtlas Shiny App
--------------------------------------------------------------------------------
\n")

  if (length(find.package("devtools", quiet=T)) == 0 || length(find.package("renv", quiet=T)) == 0) {
    # prompt instead since RStudio won't show progress for slow .RProfile scripts...
    cat("TODO: Install package requirements. This may take up to an hour the first time.\n")
    cat("RUN: source('./install.R')\n")
  } else {
    # auto-run since it should be quick, but makes sure any new requirements are installed.
    source('./install.R')
  }
} else if (Sys.getenv("R_CONFIG_ACTIVE") == "shinyapps") {
  cat("
--------------------------------------------------------------------------------
iAtlas Shiny App: Production
--------------------------------------------------------------------------------
\n")
  source('./init.R')
}

if (interactive()) {
  suppressMessages(require(devtools))
  suppressMessages(require(testthat))
  suppressMessages(require(usethis))
}
