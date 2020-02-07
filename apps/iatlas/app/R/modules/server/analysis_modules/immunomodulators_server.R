immunomodulators_server <- function(
    input,
    output,
    session,
    sample_tbl,
    group_tbl,
    group_name,
    plot_colors
){

    ns <- session$ns

    source("R/modules/server/submodules/data_table_server.R", local = T)
    source("R/modules/server/submodules/plotly_server.R", local = T)

    immunomodulator_tbl <- shiny::reactive({
        .GlobalEnv$build_immunomodultors_tbl()
    })

    # distplot ----------------------------------------------------------------

    output$gene_choice_ui <- shiny::renderUI({
        shiny::req(immunomodulator_tbl(), input$group_choice)
        choices <- immunomodulator_tbl() %>%
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

    distplot_function <- shiny::reactive({
        switch(
            input$plot_type_choice,
            "Violin" = create_violinplot,
            "Box" = create_boxplot
        )
    })

    gene_name <- shiny::reactive({
        shiny::req(input$gene_choice_id)
        .GlobalEnv$get_gene_hgnc_from_id(input$gene_choice_id)
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
            immunomodulator_tbl(),
            input$gene_choice_id,
            input$scale_method_choice
        )

        tbl <-
            .GlobalEnv$build_gene_expression_tbl_by_gene_ids(input$gene_choice_id) %>%
            dplyr::inner_join(sample_tbl(), by = "sample_id") %>%
            dplyr::select(group, value = rna_seq_expr) %>%
            scale_db_connection(input$scale_method_choice) %>%
            dplyr::select(x = group, y = value)
    })

    output$distplot <- plotly::renderPlotly({
        shiny::req(distplot_tbl(), distplot_function())

        distplot_function()(
            df = distplot_tbl(),
            source_name = "immunomodulators_dist_plot",
            fill_colors = plot_colors(),
            ylab = gene_plot_label(),
            xlab = group_name(),
            title = gene_name()
        )
    })

    distplot_eventdata <- shiny::reactive({
        plotly::event_data("plotly_click", "immunomodulators_dist_plot")
    })

    shiny::callModule(
        plotly_server,
        "immunomodulators_dist_plot",
        plot_tbl       = distplot_tbl,
        plot_eventdata = distplot_eventdata,
        group_tbl      = group_tbl,
    )

    histplot_tbl <- shiny::reactive({

        eventdata <- distplot_eventdata()
        shiny::validate(shiny::need(!is.null(eventdata), "Click plot above"))
        clicked_group <- eventdata$x[[1]]

        current_groups <- distplot_tbl() %>%
            dplyr::pull(x) %>%
            unique

        shiny::validate(
            shiny::need(clicked_group %in% current_groups, "Click plot above")
        )

        distplot_tbl() %>%
            dplyr::filter(x == clicked_group) %>%
            dplyr::select(x = y)
    })

    output$histplot <- plotly::renderPlotly({
        shiny::req(histplot_tbl())
        .GlobalEnv$create_histogram(
            df = histplot_tbl(),
            source_name = "immunomodulators_dist_plot",
            x_lab = gene_plot_label(),
            title = gene_name()
        )
    })

    shiny::callModule(
        plotly_server,
        "immunomodulators_hist_plot",
        plot_tbl = histplot_tbl
    )


    data_tbl <- shiny::reactive({
        shiny::req(immunomodulator_tbl())

        immunomodulator_tbl() %>%
            dplyr::mutate(references = stringr::str_remove_all(references, "[{}]")) %>%
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
