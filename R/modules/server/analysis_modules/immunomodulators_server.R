immunomodulators_server <- function(
    input,
    output,
    session,
    sample_tbl,
    group_tbl,
    group_name,
    plot_colors
) {

    ns <- session$ns

    source("R/modules/server/submodules/data_table_server.R", local = T)
    source("R/modules/server/submodules/distribution_plot_server.R", local = T)
    source("R/immunomodulators_functions.R")

    immunomodulator_tbl <- .GlobalEnv$build_immunomodultors_tbl()

    # output$gene_choice_ui <- shiny::renderUI({
    #     shiny::req(immunomodulator_tbl, input$group_choice)
    #     choices <- immunomodulator_tbl %>%
    #         dplyr::select(
    #             class = input$group_choice,
    #             display = "hgnc",
    #             feature = "id"
    #         ) %>%
    #         .GlobalEnv$create_nested_named_list()
    #     shiny::selectInput(
    #         ns("gene_choice_id"),
    #         label = "Select or Search Gene",
    #         choices = choices
    #     )
    # })

    output$gene_choice_ui <- shiny::renderUI({
        shiny::req(input$group_choice)
        shiny::selectInput(
            ns("gene_choice_id"),
            label = "Select or Search Gene",
            choices = create_im_gene_list(
                immunomodulator_tbl,
                input$group_choice
            )
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
            sample_tbl(),
            input$gene_choice_id,
            input$scale_method_choice
        )

        build_im_distplot(
            input$gene_choice_id,
            sample_tbl(),
            input$scale_method_choice
        )
    })

    shiny::callModule(
        distribution_plot_server,
        "immunomodulators_dist_plot",
        distplot_tbl    = distplot_tbl,
        group_tbl       = group_tbl,
        distplot_type   = shiny::reactive(input$plot_type_choice),
        distplot_colors = plot_colors,
        distplot_xlab   = group_name,
        distplot_ylab   = gene_plot_label,
        distplot_title  = gene_name
    )

    data_tbl <- shiny::reactive({
        shiny::req(immunomodulator_tbl)
        build_im_target_dt_tbl(immunomodulator_tbl)
    })

    shiny::callModule(
        data_table_server,
        "im_table",
        data_tbl
    )
}
