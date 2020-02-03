try(startup::startup())
cat(crayon::blue("SUCCESS: iatlas.app is ready to go.\n"))
cat(crayon::blue(paste0("RUN: ",crayon::bold("shiny::runApp()\n"))))