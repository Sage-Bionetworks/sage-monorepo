try(source("renv/activate.R"))

cat("
--------------------------------------------------------------------------------
Welcome to the iAtlas Shiny App
--------------------------------------------------------------------------------
\n")

if (Sys.getenv("DOCKERBUILD") == "1") {
  try(renv::restore())
  try(startup::startup())
  local({
   options(shiny.port = 3838, shiny.host = "0.0.0.0")
  })
} else if (length(find.package("devtools", quiet = T)) == 0 || length(find.package("renv", quiet = T)) == 0) {
  # prompt instead since RStudio won't show progress for slow .RProfile scripts...
  cat("TODO: Install package requirements. This may take up to an hour the first time.\n")
  cat("RUN: source('./install.R')\n")
} else {
  # auto-run since it should be quick, but makes sure any new requirements are installed.
  source('./install.R')
}

if (interactive()) {
  suppressMessages(require(devtools))
  suppressMessages(require(testthat))
  suppressMessages(require(usethis))
}
