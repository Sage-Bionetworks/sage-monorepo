germline_rarevariants_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      rv_data <- reactive({
        GERMLINE_PATH = "inst/extdata/"
        feather::read_feather(paste0(GERMLINE_PATH, "germline_rare_variants.feather"))
      })

      output$features <- renderUI({
        choices <- unique(rv_data()$display)

        trait_choices <- rv_data() %>%
                          dplyr::select(display,category) %>%
                          dplyr::group_by(category) %>%
                          tidyr::nest(data = c(display))%>%
                          dplyr::mutate(data = purrr::map(data, tibble::deframe)) %>%
                          tibble::deframe()

        shiny::selectInput(ns("feature"),
                           "Search and select Immune Trait",
                           choices = trait_choices,
                           selected = trait_choices[1])
      })

      selected_data <- reactive({
        rv_data() %>%
          dplyr::filter(display == input$feature)
      })

      output$dist_plot <- plotly::renderPlotly({
        shiny::req(input$feature)

        df <- selected_data() %>% tidyr::drop_na()

        #order plots
        if(is.numeric(df[[input$order_box]]))  plot_levels <-levels(reorder(df[["pathway"]], df[[input$order_box]], sort))
        else plot_levels <- (df %>% dplyr::arrange(.[[input$order_box]], p_value))$pathway %>% as.factor()

        create_boxplot_from_summary_stats(
           df,
          "pathway",
          "q1",
          "q2",
          "q3",
          "min",
          "max",
          "mean",
          order_by = plot_levels#,
          #color_col = "p_value"#,
         # fill_colors = bar_colors
        )
      })

      output$stats_tbl <- DT::renderDataTable({
        shiny::req(input$feature)
        DT::datatable(
          selected_data() %>% dplyr::select(pathway, n_mutations, n, p_value) ,
          rownames = FALSE
        ) %>% DT::formatRound(columns= "p_value", digits=3)
      })
    }
  )
}
