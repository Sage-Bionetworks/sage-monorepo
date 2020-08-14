# TODO: Stratification of groupsworks assuming at most 2 groups per node,
# and the second group is always Immune Subtypes. This should be generalized
# at some point.

extracellular_network_main_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    ns <- session$ns

    output$show_stratify_option <- shiny::reactive({
        cohort_obj()$dataset == "TCGA" & cohort_obj()$group_name == "TCGA_Study"
    })

    shiny::outputOptions(
        output, "show_stratify_option", suspendWhenHidden = FALSE
    )

    output$stratify_ui <- shiny::renderUI({
        shiny::checkboxInput(
            ns("stratify"),
            "Stratify by Immune Subtype",
            value = F
        )
    })

    output$stratify <- shiny::reactive({
        shiny::req(input$stratify)

    })

    shiny::outputOptions(
        output, "stratify", suspendWhenHidden = FALSE
    )

    output$select_groups_ui <- shiny::renderUI({
        choices <-
            iatlas.app::query_tags(
                cohort_obj()$dataset,
                cohort_obj()$group_name
            ) %>%
            dplyr::pull("name")
        if (cohort_obj()$group_name == "Immune_Subtype") {
            shiny::checkboxGroupInput(
                ns("group_selected"),
                "Select Immune Subtype",
                choices = choices,
                selected = c("C1", "C2"),
                inline = TRUE
            )
        } else {
            shiny::selectInput(
                ns("group_selected"),
                "Select or Search for Subtype Subset",
                choices = choices
            )
        }
    })

    output$select_statify_groups_ui <- shiny::renderUI({
        choices <-
            iatlas.app::query_tags(
                cohort_obj()$dataset,
                "Immune_Subtype"
            ) %>%
            dplyr::pull("name")
        shiny::checkboxGroupInput(
            ns("stratified_group_selected"),
            "Select Immune Subtype",
            choices = choices,
            selected = c("C1", "C2"),
            inline = TRUE
        )
    })

    output$select_style <- shiny::renderUI({
        shiny::selectInput(
            ns("loadStyleFile"),
            "Select Style",
            choices = c(
                "Edges - Immune Subtype" =
                    "javascript/extracellular_network_stylesEdges.js",
                "Black Edges" =
                    "javascript/extracellular_network_styles.js"
            )
        )
    })

    output$select_genes <- shiny::renderUI({
        shiny::selectizeInput(
            ns("selected_genes"),
            "Select genes of interest (optional)",
            choices = iatlas.app::build_ecn_gene_choice_list(),
            multiple = TRUE,
            selected = "geneset:extra_cellular_network"
        )
    })

    output$select_celltypes <- shiny::renderUI({
        shiny::selectizeInput(
            ns("selected_celltypes"),
            "Select cells of interest (optional)",
            choices = iatlas.app::build_ecn_celltype_choice_list(),
            multiple = TRUE,
            selected = "All"
        )
    })

    selected_genes <- shiny::reactive({
        shiny::req(input$selected_genes)
        iatlas.app::get_selected_gene_ids(input$selected_genes)
    })

    selected_celltypes <- shiny::reactive({
        shiny::req(input$selected_celltypes)
        iatlas.app::get_selected_celltypes(input$selected_celltypes)
    })



    # main_scaffold <- shiny::reactive(build_ecn_scaffold_tbl())

    # ##Scaffold and genes based on list of cells and genes of interest
    # scaffold <- reactive({
    #
    #     sca <- get_scaffold(panimmune_data$ecn_labels, main_scaffold, panimmune_data$ecn_expr, cois(), gois())
    #     #in case user only selected a cell of interest, get rid of the edges that only have genes
    #     if (is.null(input$geneInterest) & !(is.null(input$cellInterest))) {
    #         sca <- sca %>%
    #             dplyr::filter(From %in% cois() | To %in% cois())
    #     }
    #     return(sca)
    # })
    #
    # ##Getting list of genes and cells that are present in the selected scaffold
    # cells <- reactive({
    #     as.vector(get_cells_scaffold(scaffold(), panimmune_data$ecn_labels))
    # })
    #
    # genes <- reactive({
    #     unique(c(scaffold()$From, scaffold()$To)) %>% setdiff(cells()) #getting all the genes in the edges selected
    # })

    #------Computing scores for a custom grouping

    # ternary_info <- reactive({
    #     req(!group_internal_choice() %in% default_groups)
    #     print("Computing nodes scores.")
    #     compute_abundance(subset_df(),
    #                       subset_col = group_internal_choice(),
    #                       panimmune_data$fmx_df,
    #                       panimmune_data$ecn_expr,
    #                       cells(),
    #                       genes(),
    #                       stratify$byImmune)
    # })
    #
    # scaffold_scores <- reactive({
    #     req(!group_internal_choice() %in% default_groups, ternary_info())
    #     print("Computing edges scores.")
    #     compute_concordance(scaffold(),
    #                         ternary_info(),
    #                         stratify$byImmune) %>%
    #         as.data.frame()
    # })

    #------ Subsetting nodes and edges list based on the Sample Group Selection and cells of interest
#
#
#     # TODO add dataset and network type as parameters
#     node_group_tbl <- shiny::reactive({
#         shiny::req(
#             !is.null(stratify())
#         )
#         if (stratify()) {
#             shiny::req(input$group_selected2, input$group_selected)
#             tag_list <- list(input$group_selected2, input$group_selected)
#         } else {
#             shiny::req(input$group_selected)
#             tag_list <- list(input$group_selected)
#         }
#         build_node_group_tbl(tag_list)
#     })
#
#     node_tbl <- shiny::reactive({
#         shiny::req(
#             node_group_tbl(),
#             input$abundance,
#             selected_gene_ids(),
#             selected_cell_ids()
#         )
#         tbl <- build_ecn_node_tbl(
#             input$abundance/100,
#             node_group_tbl()$node_id,
#             selected_gene_ids(),
#             selected_cell_ids()
#         )
#         shiny::validate(shiny::need(
#             nrow(tbl) > 0,
#             paste0(
#                 "No nodes for this selection. Try changing the thresholds ",
#                 "or selecting another subset."
#             )
#         ))
#         return(tbl)
#     })
#
#     edge_tbl <- shiny::reactive({
#         shiny::req(node_tbl(), input$concordance)
#         tbl <- build_ecn_edge_tbl(node_tbl()$id, input$concordance)
#         shiny::validate(shiny::need(
#             nrow(tbl) > 0,
#             paste0(
#                 "No edges for this selection. Try changing the thresholds ",
#                 "or selecting another subset."
#             )
#         ))
#         return(tbl)
#     })
#
#     node_tbl2 <- shiny::reactive({
#         build_ecn_node_tbl2(node_tbl(), edge_tbl()) %>%
#             print(n = 1000)
#     })
#
#     output$selectNode <- shiny::renderUI({
#         shiny::selectInput(
#             ns("node_selection"),
#             "Select or Search for Node",
#             choices = node_tbl2() %>%
#                 dplyr::select(.data$node, .data$id) %>%
#                 tibble::deframe(.) %>%
#                 c(" " = 0, .),
#             selected = 0
#         )
#     })
#
#     graph.json <- shiny::reactive({
#         cyjShiny::dataFramesToJSON(
#             format_ecn_edge_tbl(edge_tbl()),
#             format_ecn_node_tbl(node_tbl2())
#         )
#     })
#
    output$cyjShiny <- cyjShiny::renderCyjShiny({
        print(selected_genes())
        print(selected_celltypes())
        # cyjShiny::cyjShiny(
        #     graph.json(),
        #     layoutName = input$doLayout,
        #     styleFile = "javascript/extracellular_network_stylesEdges.js"
        # )
    })
#
#
#     # #----- Network visualization-related (from the cyjShiny examples)
#     #
#     shiny::observeEvent(input$loadStyleFile, ignoreInit = TRUE, {
#         if (input$loadStyleFile != "") {
#             tryCatch({
#                 cyjShiny::loadStyleFile(input$loadStyleFile)
#             }, error = function(e) {
#                 msg <- sprintf(
#                     "ERROR in stylesheet file '%s': %s",
#                     input$loadStyleFile,
#                     e$message
#                 )
#                 shiny::showNotification(msg, duration = NULL, type = "error")
#             })
#         }
#     })
#
#     shiny::observeEvent(input$node_selection,  ignoreInit = TRUE, {
#         print(input$node_selection)
#         session$sendCustomMessage(
#             type = "selectNodes",
#             message = list(as.integer(input$node_selection))
#         )
#     })
#
#     shiny::observeEvent(input$sfn,  ignoreInit = TRUE, {
#         session$sendCustomMessage(type = "sfn", message = list())
#     })
#
#     shiny::observeEvent(input$fit, ignoreInit = TRUE, {
#         cyjShiny::fit(session, 80)
#     })
#
#     shiny::observeEvent(input$fitSelected, ignoreInit = TRUE, {
#         cyjShiny::fitSelected(session, 100)
#     })
#
#     shiny::observeEvent(input$hideSelection, ignoreInit = TRUE, {
#         session$sendCustomMessage(type = "hideSelection", message = list())
#     })
#
#     shiny::observeEvent(input$showAll, ignoreInit = TRUE, {
#         session$sendCustomMessage(type = "showAll", message = list())
#     })
#
#     shiny::observeEvent(input$clearSelection, ignoreInit = TRUE, {
#         session$sendCustomMessage(type = "clearSelection", message = list())
#     })
#
#     shiny::observeEvent(input$removeGraphButton, ignoreInit = TRUE, {
#         cyjShiny::removeGraph(session)
#     })
#
#     shiny::observeEvent(input$savePNGbutton, ignoreInit = TRUE, {
#         file.name <- tempfile(fileext = ".png")
#         shiny::savePNGtoFile(session, file.name)
#
#     })
}
