devtools::load_all(devtools::as.package(".")$path)

# load_config(Sys.getenv("R_CONFIG_ACTIVE", unset = "dev"))

cat(crayon::blue("SUCCESS: iatlas.app is ready to go.\n"))
cat(crayon::blue(paste0("For more info, open README.md\n")))
cat(crayon::blue(paste0("TEST: ",crayon::bold("devtools::test() or testthat::auto_test_package()\n"))))
cat(crayon::blue(paste0("RUN:  ",crayon::bold("shiny::runApp()\n"))))
