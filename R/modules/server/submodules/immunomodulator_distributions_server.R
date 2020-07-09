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

    im_tbl <- shiny::reactive({
        iatlas.app::query_immunomodulators()
    })

    output$gene_choice_ui <- shiny::renderUI({
        shiny::req(input$gene_group_choice, im_tbl())
        shiny::selectInput(
            ns("gene_choice_entrez"),
            label = "Select or Search Gene",
            choices = iatlas.app::create_im_gene_list(im_tbl(), input$gene_group_choice)
        )
    })

    gene_choice_hgnc <- shiny::reactive({
        shiny::req(input$gene_choice_entrez, im_tbl())
        im_tbl() %>%
            dplyr::filter(.data$entrez == input$gene_choice_entrez) %>%
            dplyr::pull("hgnc")
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
