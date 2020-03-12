io_targets_server <- function(
    input,
    output,
    session,
    cohort_obj
) {

    ns <- session$ns

    source("R/modules/server/submodules/distribution_plot_server.R", local = T)
    source("R/modules/server/submodules/data_table_server.R", local = T)
    source("R/io_target_functions.R", local = T)

    io_target_tbl <- build_io_target_tbl()

    # distplot ----------------------------------------------------------------

    url_gene <- shiny::reactive({
        query <- shiny::parseQueryString(session$clientData$url_search)
        get_gene_from_url(query)
    })

    output$gene_choice_ui <- shiny::renderUI({
        shiny::req(input$group_choice)
        shiny::selectInput(
            ns("gene_choice_id"),
            label = "Select or Search Gene",
            choices = create_io_target_gene_list(
                io_target_tbl,
                input$group_choice
            ),
            selected = url_gene()
        )
    })

    gene_name <- shiny::reactive({
        shiny::req(input$gene_choice_id)
        .GlobalEnv$get_gene_hgnc_from_id(as.integer(input$gene_choice_id))
    })

    gene_plot_label <- shiny::reactive({
        shiny::req(gene_name(), input$scale_method_choice)

        .GlobalEnv$transform_feature_string(
            gene_name(),
            input$scale_method_choice
        )
    })

    distplot_tbl <- shiny::reactive({
        shiny::req(
            cohort_obj(),
            input$gene_choice_id,
            input$scale_method_choice
        )
        build_io_target_distplot_tbl(
            input$gene_choice_id,
            cohort_obj()$sample_tbl,
            input$scale_method_choice
        )
    })

    shiny::callModule(
        distribution_plot_server,
        "io_targets_dist_plot",
        cohort_obj,
        distplot_tbl    = distplot_tbl,
        distplot_type   = shiny::reactive(input$plot_type_choice),
        distplot_ylab   = gene_plot_label,
        distplot_title  = gene_name
    )

    data_tbl <- shiny::reactive({
        build_io_target_dt_tbl(io_target_tbl)
    })

    shiny::callModule(data_table_server, "io_table", data_tbl, escape = F)
}
