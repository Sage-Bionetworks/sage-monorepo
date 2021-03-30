germline_heritability_ui <- function(id){

    ns <- shiny::NS(id)
    shiny::tagList(
        messageBox(
            width = 12,
            shiny::includeMarkdown("inst/markdown/germline_heritability.markdown"),
            shiny::actionLink(ns("method_link"), "Click to view method description.")
        ),
        optionsBox(
          width = 3,
          shiny::column(
            width = 12,
            shiny::selectizeInput(ns("parameter"), "Subset by",
                                  choices = c("Ancestry" = "cluster",
                                              "Immune Feature" = "feature_display",
                                              "Immune Category" = "feature_germline_category",
                                              "Immune Module" = "feature_germline_module"
                                  ),
                                  selected = "Ancestry"),
            shiny::uiOutput(ns("selection_options")),
            shiny::sliderInput(ns("pvalue"),
                               "Select p-value threshold",
                               min = 0, max = 0.5, value = 0.05, step = 0.01),
            shiny::selectizeInput(ns("order_bars"),
                                  "Order bars by ",
                                  choices = list("V(Genotype)/Vp" = "variance",
                                                 "LRT p-value" = "p_value",
                                                 "LRT FDR" = "fdr",
                                                 "Immune Trait Category" = "feature_germline_category",
                                                 "Immune Trait Module" = "feature_germline_module",
                                                 "Ancestry" = "cluster"
                                  ),
                                  selected = "variance")
          )
        ),
          plotBox(
            width = 9,
            plotly::plotlyOutput(ns("heritability"), height = "700px") %>%
              shinycssloaders::withSpinner(.)
          ),
          shiny::conditionalPanel(paste0("input['", ns("group"), "'] == 'European_immune'"), #& input['", ns("byImmune"), "'] == 1"),
                                  shiny::column(
                                    width = 6,
                                    messageBox(
                                      width = 12,
                                      shiny::p("Click on a bar on the plot above and see immune subtype-specific heritability analysis conducted for
                                               immune traits with significant (p < 0.05) G x Immune Subtype interaction. Heritability was calculated in three of the
                                               six immune subtype groups with sufficient cohort size: C1 Wound Healing (n=1752), C2 IFN-γ
                                               dominant (n=1813), and C3 Inflammatory (n=1737), as well as with immune subtype as an
                                               additional covariate.")
                                      )
                                      ),
                                  shiny::column(
                                    width = 6,
                                    plotBox(
                                      width = 12,
                                      plotly::plotlyOutput(ns("heritability_cov"), height = "300px") %>%
                                        shinycssloaders::withSpinner(.)
                                    )
                                  )
                                )

        #)

)
}
