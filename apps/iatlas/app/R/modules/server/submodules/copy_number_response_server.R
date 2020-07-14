copy_number_response_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    ns <- session$ns

    group_tbl <- shiny::reactive({
        iatlas.app::get_cnv_group_tbl(
            cohort_obj()$dataset,
            cohort_obj()$group_name
        )
    })

    output$select_cn_group_ui <- shiny::renderUI({
        shiny::selectInput(
            inputId  = ns("cn_group_point_filter"),
            label    = "Select Group Filter",
            choices  = iatlas.app::get_cnv_group_list(group_tbl()),
            selected = "All",
            multiple = T
        )
    })

    output$response_option_ui <- shiny::renderUI({
        shiny::selectInput(
            inputId  = ns("response_variable"),
            label    = "Select or Search for Response Variable",
            choices  = build_cnv_feature_list(),
            selected = iatlas.app::get_feature_id_from_display(
                "Leukocyte Fraction"
            )
        )
    })

    gene_tbl  <- shiny::reactive(iatlas.app::build_cnv_gene_tbl())

    output$select_cn_gene_ui <- shiny::renderUI({
        shiny::req(gene_tbl())
        shiny::selectInput(
            ns("cn_gene_point_filter"),
            "Select Gene Filter",
            choices = iatlas.app::get_cnv_gene_list(gene_tbl()),
            selected = 0,
            multiple = T
        )
    })

    gene_ids <- shiny::reactive({
        shiny::req(input$cn_gene_point_filter)
        id_choices <- as.integer(input$cn_gene_point_filter)
        if (0 %in% id_choices) return(0)
        gene_ids <- purrr::keep(id_choices, id_choices > 0)
        if (-1 %in% id_choices) {
            genesets_gene_ids <- get_cnv_im_ids()
        } else {
            genesets_gene_ids <- c()
        }
        base::union(gene_ids, genesets_gene_ids)
    })

    tag_ids <- shiny::reactive({
        shiny::req(input$cn_group_point_filter)
        if (0 %in% input$cn_group_point_filter) {
            shiny::req(group_tbl())
            ids <- dplyr::pull(group_tbl(), .data$id)
        } else {
            ids <- input$cn_group_point_filter
        }
        return(ids)
    })

    result_tbl <- shiny::reactive({

        shiny::req(
            tag_ids(),
            gene_ids(),
            input$response_variable,
            input$cn_dir_point_filter
        )
        result_tbl <- build_cnv_result_tbl(
            tag_ids(),
            gene_ids(),
            input$response_variable,
            input$cn_dir_point_filter
        )
        shiny::validate(need(
            all(!is.null(result_tbl), nrow(result_tbl) > 0),
            paste0(
                "Members in current selected groupings do not have ",
                "driver CNV results"
            )
        ))
        return(result_tbl)
    })

    output$text <- shiny::renderText({
        shiny::req(result_tbl())
        create_cnv_results_string(result_tbl())
    })

    output$cnvPlot <- plotly::renderPlotly({
        shiny::req(result_tbl())
        create_histogram(
            dplyr::select(result_tbl(), x = t_stat),
            x_lab = 'T statistics, Positive if normal value higher',
            y_lab = 'Number of tests',
            title = 'Distribution of T statistics',
            source_name = "cnv_hist"
        )
    })

    output$cnvtable <- DT::renderDataTable({
        shiny::req(result_tbl())
        DT::datatable(
            build_cnv_dt_tbl(result_tbl()),
            extensions = 'Buttons', options = list(
                scrollY = '300px',
                paging = TRUE,
                scrollX = TRUE,
                dom = 'Bfrtip',
                buttons =
                    list('copy', 'print',
                         list(
                             extend = 'collection',
                             buttons = c('csv', 'excel', 'pdf'),
                             text = 'Download')
                    )
            )
        )
    })
}

