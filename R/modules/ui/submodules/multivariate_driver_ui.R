multivariate_driver_ui <- function(id){

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/volcano_plot_ui.R", local = T)
    source("R/modules/ui/submodules/model_selection_ui.R", local = T)

    .GlobalEnv$sectionBox(
        title = "Immune Response Association With Driver Mutations -- multivariate",
        .GlobalEnv$messageBox(
            width = 12,
            shiny::includeMarkdown("markdown/driver_multi.markdown")
        ),
        shiny::fluidRow(
            .GlobalEnv$optionsBox(
                width = 12,
                shiny::column(
                    width = 3,
                    shiny::uiOutput(ns("response_options"))
                ),
                shiny::column(
                    width = 3,
                    shiny::numericInput(
                        ns("min_mutants"),
                        "Minimum number of mutant samples per group:",
                        20,
                        min = 2
                    )
                ),
                shiny::column(
                    width = 3,
                    shiny::numericInput(
                        ns("min_wildtype"),
                        "Minimum number of wild type samples per group:",
                        20,
                        min = 2
                    )
                ),
                shiny::column(
                    width = 3,
                    shiny::selectInput(
                        ns("group_mode"),
                        "Select or Search for Mode",
                        choices = c("By group", "Across groups"),
                        selected = "Across groups"
                    )
                ),
            )
        ),
        model_selection_ui(ns("module1")),
        shiny::fluidRow(
            .GlobalEnv$optionsBox(
                width = 12,
                shiny::column(
                    width = 6,
                    shiny::textOutput(ns("model_text"))
                ),
                shiny::column(
                    width = 6,
                    shiny::actionButton(ns("calculate_button"), "Calculate")
                )
            )
        ),
        volcano_plot_ui(ns("multivariate_driver"))
    )
}
