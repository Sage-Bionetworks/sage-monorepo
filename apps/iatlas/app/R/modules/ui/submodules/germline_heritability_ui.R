germline_heritability_ui <- function(id){

    ns <- shiny::NS(id)
    shiny::tagList(
        iatlas.app::messageBox(
            width = 12,
            shiny::p("Explore the percentage of variance explained by common genetic variance across different ancestry groups."),
            shiny::p("Heritability analyses were performed using genomic-relatedness-based restricted maximum-likelihood (GREML) and provide estimates of the proportion of phenotypic variance explained by the genetic variance, V(Genotype)/Vp."),
            shiny::p("The analyses were conducted separately within each ancestral subgroup (NEuropean=7,813, NAfrican=863, NAsian=570, and NAmerican=209 individuals), which were derived from ancestry analysis using the genotype data."),
            shiny::p("Select the ancestry cluster of interest for a bar plot summarizing the V(Genotype)/Vp for the immune traits with p-values lower than the selected p-value threshold."),
            shiny::p("For the European ancestry cluster, it is also possible to visualize the percentage of variance of immune traits accounted for by interaction between germline genotypes and immune subtypes (G x Immune Subtype).")
        ),
        iatlas.app::optionsBox(
          width = 3,
          shiny::column(
            width = 12,
            #shiny::uiOutput(ns("selectAncestry"))
            shiny::selectizeInput(ns("ancestry"),
                                  "Select Ancestry Cluster",
                                  choices = c("Ad Mixed American" = "AMR",
                                              "African" = "AFR",
                                              "Asian" = "ASIAN",
                                              "European"= "EUR"),
                                  selected = "EUR"),
            shiny::conditionalPanel(paste0("input['", ns("ancestry"), "'] == 'EUR'"),
                                    shiny::checkboxInput(ns("byImmune"),
                                                         "Account for interaction betweem germline genotypes and immune subtypes")),
            shiny::sliderInput(ns("pvalue"),
                               "Select p-value threshold",
                               min = 0, max = 0.5, value = 0.05, step = 0.01),
            shiny::selectizeInput(ns("order_bars"),
                                  "Order bars by ",
                                  choices = c("Variance", "pval", "FDR"),
                                  selected = "Variance")
          )
        ),
        #shiny::fluidRow(
          iatlas.app::plotBox(
            width = 9,
            plotly::plotlyOutput(ns("heritability"), height = "700px") %>%
              shinycssloaders::withSpinner(.)
          ),
          shiny::conditionalPanel(paste0("input['", ns("ancestry"), "'] == 'EUR' & input['", ns("byImmune"), "'] == 1"),
                                  shiny::column(
                                    width = 6,
                                    iatlas.app::messageBox(
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
                                    iatlas.app::plotBox(
                                      width = 12,
                                      plotly::plotlyOutput(ns("heritability_cov"), height = "300px") %>%
                                        shinycssloaders::withSpinner(.)
                                    )
                                  )
                                )

        #)

)
}
