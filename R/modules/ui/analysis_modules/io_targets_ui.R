io_targets_ui <- function(id) {

    ns <- shiny::NS(id)

    source("R/modules/ui/submodules/data_table_ui.R", local = T)
    source("R/modules/ui/submodules/plotly_ui.R", local = T)

    shiny::tagList(
        .GlobalEnv$titleBox("iAtlas Explorer — IO Targets"),
        .GlobalEnv$textBox(
            width = 12,
            shiny::includeMarkdown("markdown/io_target.markdown")
        ),
        .GlobalEnv$sectionBox(
            title = "IO Target Gene Expression Distributions",
            messageBox(
                width = 12,
                shiny::includeMarkdown(
                    "markdown/immunomodulators_distributions.markdown"
                )
            ),
            shiny::fluidRow(
                .GlobalEnv$optionsBox(
                    width = 12,
                    shiny::column(
                        width = 3,
                        shiny::selectInput(
                            inputId = ns("group_choice"),
                            label = "Select Group",
                            choices = c(
                                "Pathway" = "pathway",
                                "Therapy Type" = "therapy_type"
                            )
                        )
                    ),
                    shiny::column(
                        width = 3,
                        shiny::uiOutput(ns("gene_choice_ui"))
                    ),
                    shiny::column(
                        width = 3,
                        shiny::selectInput(
                            ns("plot_type_choice"),
                            "Select or Search for Plot Type",
                            choices = c("Violin", "Box")
                        )
                    ),
                    shiny::column(
                        width = 3,
                        shiny::selectInput(
                            ns("scale_method_choice"),
                            "Select or Search for variable scaling",
                            selected = "Log10",
                            choices = c(
                                "None",
                                "Log2",
                                "Log2 + 1",
                                "Log10",
                                "Log10 + 1"
                            ),
                        )
                    )
                )
            ),
            shiny::fluidRow(
                .GlobalEnv$plotBox(
                    width = 12,
                    "distplot" %>%
                        ns() %>%
                        plotly::plotlyOutput() %>%
                        shinycssloaders::withSpinner(),
                    plotly_ui(ns("io_targets_dist_plot"))
                )
            ),
            shiny::fluidRow(
                .GlobalEnv$plotBox(
                    width = 12,
                    "histplot" %>%
                        ns() %>%
                        plotly::plotlyOutput() %>%
                        shinycssloaders::withSpinner(),
                    plotly_ui(ns("io_targets_hist_plot"))
                )
            )
        ),
        data_table_ui(
            ns("io_table"),
            title = "IO Target Annotations",
            message_html = shiny::includeMarkdown(
                "markdown/io_target_dt.markdown"
            )
        )
    )
}
