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

    source(
        "R/modules/server/other_modules/cohort_selection_server.R",
        local = T
    )

    cohort_obj <- shiny::callModule(
        cohort_selection_server,
        "cohort_selection"
    )


    # analysis modules --------------------------------------------------------
    analysis_module_dir   <- "R/modules/server/analysis_modules/"
    analysis_module_files <- list.files(analysis_module_dir, full.names = T)
    for (item in analysis_module_files) {
        source(item, local = T)
    }

    # output$cohort_group_text <- shiny::renderText(
    #     paste("Selected Group: ", cohort_cons()$group_name)
    # )

    shiny::callModule(
        tumor_microenvironment_server,
        "tumor_microenvironment",
        cohort_obj
    )

    shiny::callModule(
        immune_features_server,
        "immune_features",
        cohort_obj
    )

    shiny::callModule(
        til_maps_server,
        "til_maps",
        cohort_obj
    )

    shiny::callModule(
        immunomodulators_server,
        "immunomodulators",
        cohort_obj
    )

    shiny::callModule(
        clinical_outcomes_server,
        "clinical_outcomes",
        cohort_obj
    )

    shiny::callModule(
        io_targets_server,
        "io_targets",
        cohort_obj
    )

    shiny::callModule(
        driver_associations_server,
        "driver_associations",
        cohort_obj
    )

    shiny::observeEvent(input$link_to_tumor_microenvironment, {
        shinydashboard::updateTabItems(session, "explorertabs", "tumor_microenvironment")
    })

    shiny::observeEvent(input$link_to_cohort_selection, {
        shinydashboard::updateTabItems(session, "explorertabs", "cohort_selection")
    })

    shiny::observeEvent(input$link_to_clinical_outcomes, {
        shinydashboard::updateTabItems(session, "explorertabs", "clinical_outcomes")
    })

    shiny::observeEvent(input$link_to_immunomodulators, {
        shinydashboard::updateTabItems(session, "explorertabs", "immunomodulators")
    })

    shiny::observeEvent(input$link_to_immune_features, {
        shinydashboard::updateTabItems(session, "explorertabs", "immune_features")
    })

    shiny::observeEvent(input$link_to_driver_associations, {
        shinydashboard::updateTabItems(session, "explorertabs", "driver_associations")
    })

    shiny::observeEvent(input$link_to_til_maps, {
        shinydashboard::updateTabItems(session, "explorertabs", "til_maps")
    })

    shiny::observeEvent(input$link_to_io_targets, {
        shinydashboard::updateTabItems(session, "explorertabs", "io_targets")
    })



    # other modules --------------------------------------------------------

    other_module_dir   <- "R/modules/server/other_modules/"
    other_module_files <- list.files(other_module_dir, full.names = T)
    for (item in other_module_files) {
        source(item, local = T)
    }

    shiny::callModule(
        data_info_server,
        "data_info"
    )

    # tool modules --------------------------------------------------------

    tool_module_dir   <- "R/modules/server/tool_modules/"
    tool_module_files <- list.files(tool_module_dir, full.names = T)
    for (item in tool_module_files) {
        source(item, local = T)
    }

    shiny::callModule(
        immune_subtype_classifier_server,
        "immune_subtype_classifier")


    shiny::observeEvent(input$link_to_immune_subtype_classifier, {
        updateNavlistPanel(session, "toolstabs", "Immune Subtype Classifier")
    })

})


