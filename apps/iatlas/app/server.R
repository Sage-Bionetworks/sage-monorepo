################################################################################
# Options, default settings, and load packages
################################################################################
# By default, the file size limit is 5MB. It can be changed by
# setting this option. Here we'll raise limit to 9MB.
options(shiny.maxRequestSize = 100 * 1024^2)
options(shiny.usecairo=FALSE)

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

    source("R/functions/call_iatlas_module.R")

    # Non analysis modules -----------------------------------------------------

    cohort_obj <- call_iatlas_module(
        "R/modules/server/other_modules/cohort_selection_server.R",
        input,
        session
    )

    call_iatlas_module(
        "R/modules/server/other_modules/data_info_server.R",
        input,
        session,
        observe_event = F
    )

    # Analysis modules --------------------------------------------------------
    "R/modules/server/analysis_modules/" %>%
        list.files(full.names = T) %>%
        purrr::walk(call_iatlas_module, input, session, cohort_obj)

    # Tool modules --------------------------------------------------------

    "R/modules/server/tool_modules/" %>%
        list.files(full.names = T) %>%
        purrr::walk(call_iatlas_module, input, session, tab_id = "toolstabs")
})


