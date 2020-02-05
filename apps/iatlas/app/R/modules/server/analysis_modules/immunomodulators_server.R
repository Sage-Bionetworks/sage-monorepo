immunomodulators_server <- function(
    input,
    output,
    session,
    sample_tbl,
    group_tbl,
    group_name,
    cohort_colors
){

    ns <- session$ns

    source("R/modules/server/submodules/data_table_server.R", local = T)
    source("R/modules/server/submodules/distribution_plot_server.R", local = T)

    immunomodulator_tbl <- shiny::reactive({
        .GlobalEnv$build_immunomodultors_tbl()
    })

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

    expression_tbl <- shiny::reactive({
        shiny::req(immunomodulator_tbl(), input$gene_choice_id)
        .GlobalEnv$build_gene_expression_tbl_by_gene_ids(
            input$gene_choice_id
        )
    })

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

    # shiny::callModule(
    #     distributions_plot_server,
    #     "dist",
    #     "immunomodulators_dist_plot",
    #     expression_con,
    #     relationship_con,
    #     group_con,
    #     group_name,
    #     cohort_colors,
    #     key_col = "label"
    # )

    shiny::callModule(
        data_table_server,
        "im_table",
        data_tbl
    )
}
