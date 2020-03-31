immunomodulator_distributions_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    source_files <- c(
        "R/modules/server/submodules/distribution_plot_server.R",
        "R/immunomodulators_functions.R"
    )

    for (file in source_files) {
        source(file, local = T)
    }

    ns <- session$ns

    output$gene_choice_ui <- shiny::renderUI({
        shiny::req(input$group_choice)
        shiny::selectInput(
            ns("gene_choice_id"),
            label = "Select or Search Gene",
            choices = create_im_gene_list(
                build_im_tbl(),
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
            cohort_obj(),
            input$gene_choice_id,
            input$scale_method_choice
        )

        tbl <- build_im_distplot_tbl(
            input$gene_choice_id,
            cohort_obj()$sample_tbl,
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

    shiny::callModule(
        distribution_plot_server,
        "immunomodulators_dist_plot",
        cohort_obj,
        distplot_tbl    = distplot_tbl,
        distplot_type   = shiny::reactive(input$plot_type_choice),
        distplot_ylab   = gene_plot_label,
        distplot_title  = gene_name
    )

}
