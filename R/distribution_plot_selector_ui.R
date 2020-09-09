distribution_plot_selector_ui <- function(id, scale_default = "None"){

    ns <- shiny::NS(id)

    shiny::tagList(
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
                selected = scale_default,
                choices = c(
                    "None",
                    "Log2",
                    "Log2 + 1",
                    "Log10",
                    "Log10 + 1"
                )
            )
        )
    )
}
