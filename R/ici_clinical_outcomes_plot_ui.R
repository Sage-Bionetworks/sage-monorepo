ici_clinical_outcomes_plot_ui <- function(id){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      p("Select the datasets of interest, variable, and outcome in terms of either overall survival (OS) or progression free interval (PFI) endpoints to generate a Kaplan-Meier plot.
        For a continuous (numeric) variable, the range can be split in the median of the interval, or into equal intervals of the value range.
        For the latter, the slider can be used to specify how the range of values of that variable is split. Selecting 2 splits the values by the middle of the range, 3 splits the range into three even intervals and so on.")
      ),

    iatlas.modules::optionsBox(
      width=3,
      shiny::verticalLayout(
        shiny::fluidRow(
          column(
            width = 12,
            shiny::uiOutput(ns("list_datasets"))
          )
        ),
        shiny::uiOutput(ns("survplot_op")),
        shiny::checkboxInput(ns("confint"), "Confidence Intervals", value = F),
        shiny::checkboxInput(ns("risktable"), "Risk Table", value = T),

        shiny::selectInput(
          ns("timevar"),
          "Survival Endpoint",
          c("Overall Survival" = "OS_time",
            "Progression Free Interval" = "PFI_time_1"),
          selected = "OS_time"
        ),
        shiny::radioButtons(ns("div_range"), "Divide value range",
                     choices = c("In the median" = "median", "In equal intervals" = "intervals"),
                     inline = TRUE, selected = "median"),
        shiny::conditionalPanel(condition = paste0("input['", ns("div_range"), "'] == 'intervals'"),
                                shiny::sliderInput(
                                   ns("divk"),
                                   "Value Range Divisions",
                                   min = 2,
                                   max = 10,
                                   value = 2
                                 ))
      )
    ),#optionsBox
    shiny::column(
      width = 9,
      shiny::htmlOutput(ns("notification")),
      iatlas.modules::plotBox(
        width = 12,
        shiny::uiOutput(ns("plots")) %>%
          shinycssloaders::withSpinner()
      )
    )
  )
}
