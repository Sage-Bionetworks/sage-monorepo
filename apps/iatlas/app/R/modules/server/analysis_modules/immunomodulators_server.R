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

    immunomodulator_tbl <- .GlobalEnv$build_immunomodultors_tbl()

    output$gene_choice_ui <- shiny::renderUI({
        shiny::req(immunomodulator_tbl, input$group_choice)
        choices <- immunomodulator_tbl %>%
            dplyr::select(
                class = input$group_choice,
                display = "hgnc",
                feature = "id"
            ) %>%
            .GlobalEnv$create_nested_named_list()
        shiny::selectInput(
            ns("gene_choice_id"),
            label = "Select or Search Gene",
            choices = choices
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

        input$gene_choice_id %>%
            as.integer() %>%
            .GlobalEnv$build_gene_expression_tbl_by_gene_ids() %>%
            dplyr::inner_join(sample_tbl(), by = "sample_id") %>%
            dplyr::select(group, value = rna_seq_expr) %>%
            .GlobalEnv$scale_tbl_value_column(input$scale_method_choice) %>%
            dplyr::select(x = group, y = value)
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

        immunomodulator_tbl %>%
            dplyr::mutate(
                references = stringr::str_remove_all(references, "[{}]")
            ) %>%
            dplyr::select(
                Hugo                  = hgnc,
                `Entrez ID`           = entrez,
                `Friendly Name`       = friendly_name,
                `Gene Family`         = gene_family,
                `Super Category`      = super_category,
                `Immune Checkpoint`   = immune_checkpoint,
                Function              = gene_function,
                `Reference(s) [PMID]` = references
            )
    })

    shiny::callModule(
        data_table_server,
        "im_table",
        data_tbl
    )
}
