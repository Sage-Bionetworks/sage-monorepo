################################################################################
# Options, default settings, and load packages
################################################################################
# By default, the file size limit is 5MB. It can be changed by
# setting this option. Here we'll raise limit to 9MB.
options(shiny.maxRequestSize = 100 * 1024^2)

################################################################################
# Begin Shiny Server definition.
################################################################################
shiny::shinyServer(function(input, output, session) {

    shiny::observe({
        query <- shiny::parseQueryString(session$clientData$url_search)
        if (!is.null(query[['module']])) {
            shinydashboard::updateTabItems(
                session,
                "explorertabs",
                query[['module']]
            )
        }
    })

    call_iatlas_module <- function(
        file,
        ...,
        observe_event = T,
        iatlas_tab = "explorertabs"
    ){
        function_string <- stringr::str_remove(basename(file), ".R")
        module_string   <- stringr::str_remove(basename(file), "_server.R")
        link_string     <- paste0("link_to_", module_string)
        source(file, local = T)
        if (observe_event) {
            shiny::observeEvent(input[[link_string]], {
                shinydashboard::updateTabItems(
                    session,
                    iatlas_tab,
                    module_string
                )
            })
        }
        shiny::callModule(
            get(function_string),
            module_string,
            ...
        )
    }

    # Non analysis modules -----------------------------------------------------

    cohort_obj <- call_iatlas_module(
        "R/modules/server/other_modules/cohort_selection_server.R"
    )

    call_iatlas_module(
        "R/modules/server/other_modules/data_info_server.R",
        observe_event = F
    )

    # Analysis modules --------------------------------------------------------
    analysis_module_dir   <- "R/modules/server/analysis_modules/"
    analysis_module_files <- list.files(analysis_module_dir, full.names = T)
    purrr::walk(analysis_module_files, call_iatlas_module, cohort_obj)


    # Tool modules --------------------------------------------------------

    tool_module_dir   <- "R/modules/server/tool_modules/"
    tool_module_files <- list.files(tool_module_dir, full.names = T)
    for (item in tool_module_files) {
        source(item, local = T)
    }

    shiny::callModule(
        immune_subtype_classifier_server,
        "immune_subtype_classifier"
    )


    shiny::observeEvent(input$link_to_immune_subtype_classifier, {
        updateNavlistPanel(session, "toolstabs", "Immune Subtype Classifier")
    })

})


