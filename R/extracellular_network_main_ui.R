extracellular_network_main_ui <- function(id){

  ns <- shiny::NS(id)

  shiny::tagList(
    textBox(
      width = 12,
      shiny::p(stringr::str_c(
        "Explore the extracellular networks modulating tumoral immune response, encompassing direct interaction among cells and communication via soluble proteins such as cytokines to mediate interactions among those cells.",
        sep = " "
      )),
      shiny::p(
        'This module uses the network of documented ligand-receptor, cell-receptor, and cell-ligand pairs published by ',
        shiny::a(href = " https://www.nature.com/articles/ncomms8866", "Ramilowski et al., 2015"), " and retrieved from",
        shiny::a(href = "http://fantom.gsc.riken.jp/5/suppl/Ramilowski_et_al_2015/", "FANTOM5.")
      )
    ),
    sectionBox(
      title = "Extracellular networks",
      messageBox(
        width = 24,
        shiny::includeMarkdown(get_markdown_path(
          "extracellular_network_main"
        ))
      ),
      shiny::fluidRow(
        shiny::column(
          width = 3,
          optionsBox(
            width=24,
            #this tags$head makes sure that the checkboxes are formatted appropriately
            shiny::tags$head(
              shiny::tags$style(
                shiny::HTML(
                  ".checkbox-inline {
                    margin-left: 0px;
                    margin-right: 10px;
                    }
                    .checkbox-inline+.checkbox-inline {
                    margin-left: 0px;
                    margin-right: 10px;
                    }
                    "
                )
              )
            ),
            shiny::conditionalPanel(
              condition = "output.show_stratify_option",
              shiny::uiOutput(ns("stratify_ui")),
              ns = ns
            ),
            shiny::uiOutput(ns("select_groups_ui")),
            shiny::conditionalPanel(
              condition = "output.stratify",
              shiny::uiOutput(ns("select_statify_groups_ui")),
              ns = ns
            ),

            shiny::numericInput(
              ns("abundance"),
              "Set Abundance Threshold (%)",
              value = 66,
              min = 0,
              max = 100
            ),
            shiny::numericInput(
              ns("concordance"),
              "Set Concordance Threshold",
              value = 2.94,
              step = 0.01
            ),

            shiny::uiOutput(ns("select_celltypes")),
            shiny::uiOutput(ns("select_genes")),

            shiny::div(
              class = "form-group shiny-input-container",
              shiny::actionButton(
                ns("calculate_button"),
                shiny::tags$b("GO"),
                width = "100%")
            ),
            shiny::hr(),
            shiny::selectInput(
              ns("do_layout"),
              "Select Layout",
              choices = c(
                "",
                "cose",
                "cola",
                "circle",
                "concentric",
                "breadthfirst",
                "grid",
                "random",
                "dagre",
                "cose-bilkent"
              ),
              selected = "cose"),

            shiny::uiOutput(ns("select_style")),
            shiny::uiOutput(ns("selectNode")),
            shiny::actionButton(ns("fitSelected"), "Fit Selected", width = "100%", style = 'white-space: pre-line'),
            shiny::actionButton(ns("fit"), "Fit Graph", width = "100%", style = 'white-space: pre-line'),
            shiny::actionButton(ns("sfn"), "Select First Neighbor", width = "100%", style = 'white-space: pre-line'),
            shiny::actionButton(ns("clearSelection"), "Unselect Nodes", width = "100%", style = 'white-space: pre-line'),
            shiny::actionButton(ns("hideSelection"), "Hide Selected Nodes", width = "100%", style = 'white-space: pre-line'),
            shiny::actionButton(ns("showAll"), "Show All Nodes", width = "100%", style = 'white-space: pre-line'),
            shiny::actionButton(ns("removeGraphButton"), "Remove Graph", width = "100%", style = 'white-space: pre-line')
          )
        ),
        shiny::column(
          width = 9,
          shiny::verticalLayout(
            plotBox(
              width = 24,
              cyjShiny::cyjShinyOutput(ns("cyjShiny"), height = 800) %>%
                shinycssloaders::withSpinner(.)
            ),
            shiny::img(src = "images/network_legend.png", width = "100%")
          )
        )
      )
    )
  )
}
