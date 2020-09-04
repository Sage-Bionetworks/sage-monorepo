io_target_distributions_server <- function(
  input,
  output,
  session,
  cohort_obj
){

  source_files <- c(
    "R/modules/server/submodules/distribution_plot_server.R"
  )

  for (file in source_files) {
    source(file, local = T)
  }

  ns <- session$ns

  io_target_tbl <- shiny::reactive({
    iatlas.api.client::query_io_targets()
  })

  url_gene <- shiny::reactive({
    query <- shiny::parseQueryString(session$clientData$url_search)
    iatlas.app::get_gene_from_url(query)
  })

  output$gene_choice_ui <- shiny::renderUI({
    shiny::req(input$group_choice, io_target_tbl())
    shiny::selectInput(
      ns("gene_choice"),
      label = "Select or Search Gene",
      choices = create_io_target_gene_list(
        io_target_tbl(),
        input$group_choice
      ),
      selected = url_gene()
    )
  })

  gene_choice_hgnc <- shiny::reactive({
    shiny::req(input$gene_choice, io_target_tbl())
    iatlas.app::get_io_hgnc_from_tbl(io_target_tbl(), input$gene_choice)
  })

  gene_plot_label <- shiny::reactive({
    shiny::req(gene_choice_hgnc(), input$scale_method_choice)

    iatlas.app::transform_feature_string(
      gene_choice_hgnc(),
      input$scale_method_choice
    )
  })

  distplot_tbl <- shiny::reactive({
    shiny::req(
      cohort_obj(),
      input$gene_choice,
      input$scale_method_choice
    )
    build_io_target_distplot_tbl(
      cohort_obj(),
      as.integer(input$gene_choice),
      input$scale_method_choice
    )
  })

  distribution_plot_server(
    "io_targets_dist_plot",
    cohort_obj,
    distplot_tbl    = distplot_tbl,
    distplot_type   = shiny::reactive(input$plot_type_choice),
    distplot_ylab   = gene_plot_label,
    distplot_title  = gene_choice_hgnc
  )
}
