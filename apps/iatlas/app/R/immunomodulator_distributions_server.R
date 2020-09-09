immunomodulator_distributions_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      im_tbl <- shiny::reactive(iatlas.api.client::query_immunomodulators())

      output$gene_choice_ui <- shiny::renderUI({
        shiny::req(input$gene_group_choice, im_tbl())
        shiny::selectInput(
          ns("gene_choice_entrez"),
          label = "Select or Search Gene",
          choices = create_im_gene_list(
            im_tbl(),
            input$gene_group_choice
          )
        )
      })

      gene_choice_hgnc <- shiny::reactive({
        shiny::req(input$gene_choice_entrez, im_tbl())
        get_im_hgnc_from_tbl(im_tbl(), input$gene_choice_entrez)
      })

      gene_plot_label <- shiny::reactive({
        shiny::req(gene_choice_hgnc(), input$scale_method_choice)

        transform_feature_string(
          gene_choice_hgnc(),
          input$scale_method_choice
        )
      })

      distplot_tbl <- shiny::reactive({
        shiny::req(input$gene_choice_entrez, input$scale_method_choice)

        tbl <- build_im_distplot_tbl(
          cohort_obj(),
          as.integer(input$gene_choice_entrez),
          input$scale_method_choice
        )

        shiny::validate(need(
          nrow(tbl) > 0,
          paste0(
            "Current selected cohort has no expression data for current ",
            "selected gene."
          )
        ))

        return(tbl)
      })

      distribution_plot_server(
        "immunomodulators_dist_plot",
        cohort_obj,
        distplot_tbl    = distplot_tbl,
        distplot_type   = shiny::reactive(input$plot_type_choice),
        distplot_ylab   = gene_plot_label,
        distplot_title  = gene_choice_hgnc
      )

    }
  )
}
