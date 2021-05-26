ici_hazard_ratio_main_ui <- function(id){
  ns <- shiny::NS(id)

  shiny::tagList(
    messageBox(
      width = 24,
      p("Analyze survival data with Cox proportional hazard models. Select the datasets of interest, variables, and outcome in terms of either overall survival (OS) or progression free interval (PFI)
        endpoints." ),
      p("Then, select whether the models should be univariable or multivariable. The univariable analysis will compute the Cox proportional hazard ratio (HR) for each combination of selected variable and dataset. The multivariable analysis will compute the HR considering all the selected features
        as predictors of time to survival outcome."),
      p("The results are summarized in a heatmap with the log10 of Hazard Ratio. For the univariable analysis, when more than one variable is selected, a Benjamini-Hochberg FDR correction is made, and variables with a p-value smaller than 0.05 and BH pvalue smaller than 0.05 are indicated in the heatmap.
        In addition, forest plot with the log10 of the Cox Proportional Hazard Ratio with 95th confidence intervals for each variable is displayed.")
      ),
    optionsBox(
      width = 12,
      column(
        width = 3,
        checkboxGroupInput(ns("datasets_mult"), "Select Datasets", choices = datasets_options,
                           selected =  c("Gide 2019", "Hugo 2016", "Riaz 2017", "Van Allen 2015"))
      ),
      column(
        width = 4,
        selectInput(
          ns("timevar"),
          "Survival Endpoint",
          c("Overall Survival" = "OS_time",
            "Progression Free Interval" = "PFI_time_1"),
          selected = "OS_time"
        ),
        selectInput(
          ns("analysisvar"),
          "Select Type of Analysis",
          c("Univariable Cox Proportional" = "uni_coxph",
            "Multivariable Cox Proportional" = "mult_coxph"
          ),
          selected = "uni_coxph"
        ),
        actionLink(ns("method_link"), "Click to view method description for each type.")
      ),
      column(
        width = 5,
        shiny::selectizeInput(
                      ns("var2_cox"),
                      label = "Select or Search for variables",
                      choices = NULL,
                      multiple = TRUE
                    )
      )
    ),
    conditionalPanel(condition = paste0("input['", ns("timevar"), "'] == 'PFI_time_1'"),
                     helpText("There is no PFI annotation for Hugo 2016, Riaz 2017, and IMVigor210.")),
    plotBox(
      width = 12,
      plotly::plotlyOutput(ns("mult_heatmap"), width = "100%", height = "600px")%>%
        shinycssloaders::withSpinner()
    ),
    plotBox(
      width = 12,
      plotly::plotlyOutput(ns("mult_forest"), width = "100%", height = "700px")%>%
        shinycssloaders::withSpinner()
    ),
    messageBox(
      width = 12,
      p("The table shows the computed data used to create the visualizations above.")
    ),
    tableBox(
      width = 12,
      DT::dataTableOutput(ns("stats_summary")),
      downloadButton(ns('download_stats'), 'Download')
    )
  )#taglist
}
