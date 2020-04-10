extracellular_network_main_server <- function(
    input,
    output,
    session,
    cohort_obj
){

    ns <- session$ns

    source("R/extracellular_network_functions.R", local = T)

    group_tbl <- shiny::reactive(build_ecn_group_tbl2(cohort_obj()$group_name))

    # TODO: deal with network type and dataset tags
    results_available <- shiny::reactive({
        shiny::req(group_tbl())
        return(nrow(group_tbl()) > 0)
    })

    # TODO: deal with network type and dataset tags
    stratified_results_available <- shiny::reactive({
        shiny::req(
            !is.null(results_available()),
            group_tbl()
        )
        if (cohort_obj()$group_name == "Immune Subtype") return(F)
        else if (!results_available()) return(F)
        else {
            max_tags <- group_tbl() %>%
                dplyr::pull(.data$count) %>%
                max()
            return(max_tags > 1)
        }
    })


    output$show_stratify_option <- shiny::reactive({
        print(stratified_results_available())
        stratified_results_available()
    })

    shiny::outputOptions(
        output, "show_stratify_option", suspendWhenHidden = FALSE
    )

    output$stratify_ui <- shiny::renderUI({
        shiny::checkboxInput(
            ns("stratify"),
            "Stratify by Immune Subtype"
        )
    })

    output$select_ui <- shiny::renderUI({
        tag_ids <- group_tbl() %>%
            dplyr::filter(.data$count == 1) %>%
            dplyr::pull(.data$tag_id) %>%
            unique()

        tag_list <-
            paste0(
                "SELECT name, id FROM tags WHERE id IN (",
                numeric_values_to_query_list(tag_ids),
                ")"
            ) %>%
            perform_query() %>%
            tibble::deframe(.)

        shiny::selectInput(
            ns("group_selected"),
            "Select or Search for Subtype Subset",
            choices = tag_list
        )
    })


    #
    # output$select_ui <- shiny::renderUI({
    #
    #     shiny::req(group_tbl())
    #
    #     sample_group_vector <- tibble::deframe(group_tbl())
    #
    #     #Generating UI depending on the sample group
    #     if (cohort_obj()$group_name == "Immune Subtype") {
    #         shiny::checkboxGroupInput(
    #             ns("group_selected"),
    #             "Select Immune Subtype",
    #             choices = sample_group_vector,
    #             selected = sample_group_vector[1:2],
    #             inline = TRUE
    #         )
    #
    #     } else if (cohort_obj()$group_name == "TCGA Subtype") {
    #
    #         shiny::selectInput(
    #             ns("group_selected"),
    #             "Select or Search for Subtype Subset",
    #             choices = sample_group_vector
    #         )
    #
    #
    #     } else if (cohort_obj()$group_name == "TCGA Study") {
    #         # UI for TCGA Study and for custom groups -
    #         # both allow stratification by Immune Subtype
    #
    #         shiny::tagList(
    #             shiny::selectInput(
    #                 ns("group_selected"),
    #                 "Select or Search for Subset",
    #                 choices = sample_group_vector
    #             ),
    #
    #             shiny::checkboxInput(
    #                 ns("byImmune"),
    #                 "Stratify by Immune Subtype"
    #             ),
    #
    #             shiny::conditionalPanel(
    #                 condition = paste(
    #                     "" ,
    #                     paste0("input['", ns("byImmune"), "'] == true")
    #                 ),
    #                 shiny::checkboxGroupInput(
    #                     ns("showGroup"),
    #                     "Select Immune Subtype",
    #                     choices = tibble::deframe(
    #                         build_ecn_group_tbl("Immune Subtype")
    #                     ),
    #                     selected = c("C1", "C2"),
    #                     inline = TRUE)
    #             )
    #         )
    #     }
    # })

    output$selectStyle <- shiny::renderUI({
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

    output$selectCell <- shiny::renderUI({
        shiny::selectizeInput(
            ns("cellInterest"),
            "Select cells of interest (optional)",
            choices = get_ecn_celltypes(),
            multiple = TRUE,
            options = list(placeholder = "Default: all cells")
        )
    })

    main_scaffold <- shiny::reactive(build_ecn_scaffold_tbl())

    # output$selectGene <- shiny::renderUI({
    #     shiny::selectizeInput(
    #         ns("geneInterest"),
    #         "Select genes of interest (optional)",
    #         choices = get_ecn_genes(),
    #         multiple = TRUE,
    #         options = list(placeholder = "Default: immunomodulator genes")
    #     )
    # })
    #
    # output$selectNode <- shiny::renderUI({
    #     shiny::selectInput(
    #         ns("selectName"),
    #         "Select or Search for Node",
    #         choices = c(
    #             "",
    #             node_tbl() %>%
    #                 dplyr::select(Node = Gene) %>%
    #                 dplyr::filter(!is.na(Node))
    #         )
    #     )
    # })


    # default_groups <- unique(panimmune_data$sample_group_df$sample_group)

    ##Subsetting to cells and genes of interest

    # gois <- reactive({
    #     #if no gene is selected, all immunomodulator genes are considered genes of interest
    #     if (is.null(input$geneInterest))  return(as.vector(panimmune_data$im_direct_relationships$`HGNC Symbol`))
    #
    #     #converting the FriendlyName to HGNC Symbol
    #     gois <- data.frame(Gene = input$geneInterest) %>% merge(panimmune_data$ecn_labels) %>% dplyr::select(Obj)
    #
    #     gois$Obj
    # })
    #
    # cois <- reactive({
    #     #if no cell is selected, all cells are considered cells of interest
    #     if (is.null(input$cellInterest)) get_cells_scaffold(main_scaffold, panimmune_data$ecn_labels)
    #
    #     cois <- data.frame(Gene = input$cellInterest) %>% merge(panimmune_data$ecn_labels) %>% dplyr::select(Obj)
    #     cois$Obj
    #     #as.vector(input$cellInterest)
    # })
    #
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

    #adjusting the flag for stratification by Immune Subtype (available only for TCGA Study and Custom Groups)

    stratify <- shiny::reactiveValues(byImmune = FALSE)

    shiny::observe({
        try(
            if (input$byImmune == FALSE) {
                stratify$byImmune = FALSE
            } else if (input$byImmune == TRUE & cohort_obj()$group_name %in% c("TCGA Subtype", "Immune Subtype")) {
                stratify$byImmune = FALSE
            }else{
                stratify$byImmune = TRUE
            },
            silent = TRUE
        )
    })

    # TODO add dataset and network type as parameters
    node_group_tbl <- shiny::reactive({
        if (stratify$byImmune) {
            tag_list <- list(input$showGroup, input$group_selected)
        } else {
            tag_list <- list(input$group_selected)
        }
        create_node_group_tbl(tag_list)
    })

    node_tbl <- shiny::reactive({
        shiny::req(node_group_tbl(),input$abundance)
        tbl <- build_ecn_node_tbl(input$abundance/100, node_group_tbl()$node_id)
        shiny::validate(shiny::need(
            nrow(tbl) > 0,
            paste0(
                "No nodes for this selection. Try changing the thresholds ",
                "or selecting another subset."
            )
        ))
        return(tbl)
    })

    edge_tbl <- shiny::reactive({
        shiny::req(node_tbl(), input$concordance)
        tbl <- build_ecn_edge_tbl(node_tbl()$id, input$concordance)
        shiny::validate(shiny::need(
            nrow(tbl) > 0,
            paste0(
                "No edges for this selection. Try changing the thresholds ",
                "or selecting another subset."
            )
        ))
        return(tbl)
    })


    graph.json <- shiny::reactive({
        cyjShiny::dataFramesToJSON(
            build_ecn_edge_tbl2(edge_tbl()),
            build_ecn_node_tbl2(node_tbl(), edge_tbl())
        )
    })

    output$cyjShiny <- cyjShiny::renderCyjShiny({
        cyjShiny::cyjShiny(
            graph.json(),
            layoutName = input$doLayout,
            styleFile = "javascript/extracellular_network_stylesEdges.js"
        )
    })


    #Button with method information

    # observeEvent(input$methodButton, {
    #     showModal(modalDialog(
    #         title = "Method",
    #         includeMarkdown("data/MethodsText/Methods_AbundantConcordantNetwork.txt"),
    #         easyClose = TRUE,
    #         footer = NULL
    #     ))
    # })
    #
    # #----- Network visualization-related (from the cyjShiny examples)
    #
    # shiny::observeEvent(input$loadStyleFile, ignoreInit = TRUE, {
    #     if (input$loadStyleFile != "") {
    #         tryCatch({
    #             cyjShiny::loadStyleFile(input$loadStyleFile)
    #         }, error = function(e) {
    #             msg <- sprintf(
    #                 "ERROR in stylesheet file '%s': %s",
    #                 input$loadStyleFile,
    #                 e$message
    #             )
    #             shiny::showNotification(msg, duration = NULL, type = "error")
    #         })
    #     }
    # })
    #
    # observeEvent(input$selectName,  ignoreInit=TRUE,{
    #     snode <- as.character(panimmune_data$ecn_labels[which(panimmune_data$ecn_labels$Gene == input$selectName), "Obj"])
    #     session$sendCustomMessage(type="selectNodes", message=list(snode))
    # })
    #
    # observeEvent(input$sfn,  ignoreInit=TRUE,{
    #     session$sendCustomMessage(type="sfn", message=list())
    # })
    #
    # observeEvent(input$fit, ignoreInit=TRUE, {
    #     cyjShiny::fit(session, 80)
    # })
    #
    # observeEvent(input$fitSelected,  ignoreInit=TRUE,{
    #     cyjShiny::fitSelected(session, 100)
    # })
    #
    # observeEvent(input$hideSelection,  ignoreInit=TRUE, {
    #     session$sendCustomMessage(type="hideSelection", message=list())
    # })
    #
    # observeEvent(input$showAll,  ignoreInit=TRUE, {
    #     session$sendCustomMessage(type="showAll", message=list())
    # })
    #
    # observeEvent(input$clearSelection,  ignoreInit=TRUE, {
    #     session$sendCustomMessage(type="clearSelection", message=list())
    # })
    #
    # observeEvent(input$removeGraphButton, ignoreInit=TRUE, {
    #     cyjShiny::removeGraph(session)
    # })
    #
    # observeEvent(input$savePNGbutton, ignoreInit=TRUE, {
    #     file.name <- tempfile(fileext=".png")
    #     savePNGtoFile(session, file.name)
    #
    # })
}
