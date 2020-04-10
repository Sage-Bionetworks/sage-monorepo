extracellular_network_main_ui <- function(id){

    ns <- shiny::NS(id)

    tagList(
        textBox(
            width = 12,
            p(stringr::str_c(
                "Explore the extracellular networks modulating tumoral immune response, encompassing direct interaction among cells and communication via soluble proteins such as cytokines to mediate interactions among those cells.",
                sep = " "
            )),
            p('This module uses the network of documented ligand-receptor, cell-receptor, and cell-ligand pairs published by ',
              a(href = " https://www.nature.com/articles/ncomms8866", "Ramilowski et al., 2015"), " and retrieved from", a(href = "http://fantom.gsc.riken.jp/5/suppl/Ramilowski_et_al_2015/", "FANTOM5."))

        ),

        sectionBox(
            title = "Extracellular networks",

            messageBox(
                width = 24,
                includeMarkdown("markdown/extracellular_network_main.markdown")
            ),
            fluidRow(
                column(
                    width = 3,
                    optionsBox(
                        width=24,
                        #this tags$head makes sure that the checkboxes are formatted appropriately
                        tags$head(
                            tags$style(
                                HTML(
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
                        shiny::uiOutput(ns("select_ui")),

                        numericInput(ns("abundance"), "Set Abundance Threshold (%)", value = 66, min = 0, max = 100),
                        numericInput(ns("concordance"), "Set Concordance Threshold", value = 2.94, step = 0.01),

                        uiOutput(ns("selectCell")),
                        uiOutput(ns("selectGene")),

                        div(class = "form-group shiny-input-container", actionButton(ns("calculate_button"), tags$b("GO"), width = "100%")),

                        hr(),

                        selectInput(
                            ns("doLayout"),
                            "Select Layout",
                            choices=c("",
                                      "cose",
                                      "cola",
                                      "circle",
                                      "concentric",
                                      "breadthfirst",
                                      "grid",
                                      "random",
                                      "dagre",
                                      "cose-bilkent"),
                            selected = "cose"),

                        uiOutput(ns("selectStyle")),

                        uiOutput(ns("selectNode")),
                        actionButton(ns("fitSelected"), "Fit Selected", width = "100%", style = 'white-space: pre-line'),
                        actionButton(ns("fit"), "Fit Graph", width = "100%", style = 'white-space: pre-line'),
                        actionButton(ns("sfn"), "Select First Neighbor", width = "100%", style = 'white-space: pre-line'),
                        actionButton(ns("clearSelection"), "Unselect Nodes", width = "100%", style = 'white-space: pre-line'),
                        actionButton(ns("hideSelection"), "Hide Selected Nodes", width = "100%", style = 'white-space: pre-line'),
                        actionButton(ns("showAll"), "Show All Nodes", width = "100%", style = 'white-space: pre-line'),
                        #actionButton(ns("savePNGbutton"), "Save PNG"),
                        actionButton(ns("removeGraphButton"), "Remove Graph", width = "100%", style = 'white-space: pre-line')
                    ) #
                ),
                column(
                    width = 9,
                    verticalLayout(
                        plotBox(
                            width = 24,
                            cyjShiny::cyjShinyOutput(ns("cyjShiny"), height =800)%>%
                                shinycssloaders::withSpinner()
                        ),
                        img(src = "images/network_legend.png", width = "100%")
                    )
                )
            )
        )
    )
}
