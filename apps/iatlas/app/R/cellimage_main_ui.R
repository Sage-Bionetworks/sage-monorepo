cellimage_main_ui <- function(id){

  ns <- shiny::NS(id)
  shiny::tagList(
    iatlas.modules::messageBox(
      width = 12,
      shiny::includeMarkdown(get_markdown_path("cellimage")),
      shiny::actionLink(ns("method_link"), "Click to view method.")
    ),
    shiny::fluidRow(
      iatlas.modules::optionsBox(
        shiny::column(
          width = 6,
          shiny::radioButtons(
            ns("viz_selection1"),
            "Select type of visualization:",
            choices = c("Illustration", "Network"),
            selected = "Illustration"
          )
        ),
        shiny::column(
          width = 6,
          shiny::uiOutput(ns("select_group1_ui"))
        )
      ),
      iatlas.modules::optionsBox(
        shiny::column(
          width = 6,
          shiny::radioButtons(
            ns("viz_selection2"),
            "Select type of visualization:",
            choices = c("Illustration", "Network"),
            selected = "Network"
          )
        ),
        shiny::column(
          width = 6,
          shiny::uiOutput(ns("select_group2_ui"))
        )
      )
    ),
    shiny::fluidRow(
      shiny::conditionalPanel(
        condition = "input.viz_selection1 == 'Illustration'",
        cellimage_plot_ui(ns("cellimage_plot1")),
        ns = ns
      ),
      shiny::conditionalPanel(
        condition = "input.viz_selection1 == 'Network'",
        cellimage_network_ui(ns("cellimage_network1")),
        ns = ns
      ),
      shiny::conditionalPanel(
        condition = "input.viz_selection2 == 'Illustration'",
        cellimage_plot_ui(ns("cellimage_plot2")),
        ns = ns
      ),
      shiny::conditionalPanel(
        condition = "input.viz_selection2 == 'Network'",
        cellimage_network_ui(ns("cellimage_network2")),
        ns = ns
      ),
    ),
    shiny::img(src = "images/cell-image-legend.png", width = "100%")
  )
}
