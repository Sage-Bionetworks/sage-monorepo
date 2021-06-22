ici_overview_category_ui <- function(
  id,
  cohort_obj
){
  ns <- shiny::NS(id)

  shiny::tagList(
    iatlas.modules::messageBox(
      width = 24,
      # shiny::includeMarkdown(get_markdown_path(
      #   "ici_overview_datasets"
      # ))
      p("Samples in each dataset can be grouped based on study characteristics.
               Here you can obtain details of these categories and the number of samples in each group per dataset."),
      actionLink(ns("method_link"), "Click to view method description.")
    ),
    iatlas.modules::optionsBox(
      width = 12,
      shiny::uiOutput(ns("select_group1"))
    ),
    iatlas.modules::plotBox(
      width = 12,
      DT::DTOutput(
        ns("ici_groups_df")
      ),
      br(),
      DT::DTOutput(
        ns("ici_per_ds_df")
      )
    ),
    #test if can be in the same submodule
    iatlas.modules::sectionBox(
      title = "Group Overlap",
      iatlas.modules::messageBox(
        width = 24,
        p("See a mosaic plot for two sample groups.")
      ),
      iatlas.modules::optionsBox(
        width = 12,
        shiny::uiOutput(ns("select_group2"))
      ),
      iatlas.modules::plotBox(
        width = 12,
        plotly::plotlyOutput(ns("ici_mosaic"), height = "600px") %>%
          shinycssloaders::withSpinner()
      )
    )
  )
}
