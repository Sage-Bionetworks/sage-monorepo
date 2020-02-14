io_targets_server <- function(
    input,
    output,
    session,
    sample_tbl,
    group_tbl,
    group_name,
    plot_colors
) {

    ns <- session$ns

    source("R/modules/server/submodules/plotly_server.R", local = T)
    source("R/modules/server/submodules/data_table_server.R", local = T)

    io_target_tbl <- .GlobalEnv$build_io_target_tbl()

    # distplot ----------------------------------------------------------------

    url_gene <- shiny::reactive({
        query <- shiny::parseQueryString(session$clientData$url_search)
        gene  <- query[['gene']]
        if (!is.null(gene)) {
            url_gene <- gene
        } else {
            url_gene <- NA
        }
        return(url_gene)
    })

    output$gene_choice_ui <- shiny::renderUI({
        shiny::req(io_target_tbl, input$group_choice)
        choices <- io_target_tbl %>%
            dplyr::select(
                class = input$group_choice,
                display = "hgnc",
                feature = "id"
            ) %>%
            .GlobalEnv$create_nested_named_list()
        shiny::selectInput(
            ns("gene_choice_id"),
            label = "Select or Search Gene",
            choices = choices,
            selected = url_gene()
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

    output$distplot <- plotly::renderPlotly({
        shiny::req(distplot_tbl(), distplot_function())

        distplot_function()(
            df = distplot_tbl(),
            source_name = "io_targets_dist_plot",
            fill_colors = plot_colors(),
            ylab = gene_plot_label(),
            xlab = group_name(),
            title = gene_name()
        )
    })

    distplot_eventdata <- shiny::reactive({
        shiny::req(distplot_tbl(), distplot_function())
        plotly::event_data("plotly_click", "io_targets_dist_plot")
    })

    shiny::callModule(
        plotly_server,
        "io_targets_dist_plot",
        plot_tbl       = distplot_tbl,
        plot_eventdata = distplot_eventdata,
        group_tbl      = group_tbl,
    )

    # histplot ----------------------------------------------------------------

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
            source_name = "io_target_hist_plot",
            x_lab = gene_plot_label(),
            title = gene_name()
        )
    })

    shiny::callModule(
        plotly_server,
        "io_target_hist_plot",
        plot_tbl = histplot_tbl
    )




    data_tbl <- shiny::reactive({
        shiny::req(io_target_tbl)

        io_target_tbl %>%
            dplyr::select(
                Hugo            = hgnc,
                `Entrez ID`     = entrez,
                `Friendly Name` = friendly_name,
                Pathway         = pathway,
                `Therapy Type`  = therapy,
                Description     = description,
            ) %>%
            dplyr::mutate(url = stringr::str_c(
                "https://www.cancerresearch.org/scientists/immuno-oncology-landscape?2019IOpipelineDB=2019;Target;",
                `Friendly Name`
            )) %>%
            dplyr::mutate(`Link to IO Landscape` =  stringr::str_c(
                "<a href=\'",
                url,
                "\'>",
                `Friendly Name`,
                "</a>"
            )) %>%
            dplyr::select(-url)

    })



    shiny::callModule(data_table_server, "io_table", data_tbl, escape = F)

}
