# TODO: Stratification of groupsworks assuming at most 2 groups per node,
# and the second group is always Immune Subtypes. This should be generalized
# at some point.

extracellular_network_main_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      show_stratify_option <- shiny::reactive(
        all(
          (cohort_obj()$dataset == "TCGA"),
          (cohort_obj()$group_name == "TCGA_Study")
        )
      )

      output$show_stratify_option <- show_stratify_option

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

      stratify <- shiny::reactive({
        if(!show_stratify_option()) return(F)
        else return(input$stratify)
      })

      output$stratify <- shiny::reactive(stratify())

      shiny::outputOptions(
        output, "stratify", suspendWhenHidden = FALSE
      )

      output$select_groups_ui <- shiny::renderUI({
        choices <-
          iatlas.api.client::query_tags(
            datasets = cohort_obj()$dataset,
            parent_tags = cohort_obj()$group_name
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
          iatlas.api.client::query_tags(
            datasets = cohort_obj()$dataset,
            parent_tags = "Immune_Subtype"
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
              get_javascript_path("extracellular_network_stylesEdges"),
            "Black Edges" =
              get_javascript_path("extracellular_network_styles")
          )
        )
      })

      #TODO: fix
      output$select_genes <- shiny::renderUI({
        shiny::selectizeInput(
          ns("selected_genes"),
          "Select genes of interest (optional)",
          choices = build_ecn_gene_choice_list(),
          multiple = TRUE,
          # selected = "geneset:extra_cellular_network"
          selected = "geneset:immunomodulators"
        )
      })

      #TODO: fix
      output$select_celltypes <- shiny::renderUI({
        shiny::selectizeInput(
          ns("selected_celltypes"),
          "Select cells of interest (optional)",
          choices = build_ecn_celltype_choice_list(),
          multiple = TRUE,
          # selected = "All"
          selected = "B Cells"
        )
      })

      selected_genes <- shiny::reactive({
        shiny::req(input$selected_genes)
        get_selected_gene_ids(input$selected_genes)
      })

      selected_celltypes <- shiny::reactive({
        shiny::req(input$selected_celltypes)
        get_selected_celltypes(input$selected_celltypes)
      })

      gene_nodes <- shiny::eventReactive(input$calculate_button, {
        if(stratify()) n_tags <- 2
        else n_tags <- 1
        nodes <-
          iatlas.api.client::query_gene_nodes(
            datasets = "TCGA",
            network = "extracellular_network",
            entrez = selected_genes(),
            tags = input$group_selected,
            min_score = input$abundance / 100
          ) %>%
          dplyr::filter(purrr::map_lgl(
            purrr::map_int(.data$tags, nrow),
            ~ .x == n_tags
          ))
        if(stratify()){
          nodes <- nodes %>%
            dplyr::filter(purrr::map_lgl(
              purrr::map(.data$tags, dplyr::pull, "name"),
              ~ any(input$stratified_group_selected %in% .x)
            ))
        }
        nodes <- nodes %>%
          dplyr::select("tags", "node_name" = "name", "node_display" = "hgnc") %>%
          tidyr::unnest("tags") %>%
          dplyr::select("node_name", "node_display", "tag" = "name") %>%
          dplyr::mutate("Type" = "Gene")
        return(nodes)
      })

      feature_nodes <- shiny::eventReactive(input$calculate_button, {
        if(stratify()) n_tags <- 2
        else n_tags <- 1
        nodes <-
          iatlas.api.client::query_feature_nodes(
            datasets = "TCGA",
            network = "extracellular_network",
            features = selected_celltypes(),
            tags = input$group_selected,
            min_score = input$abundance / 100
          ) %>%
          dplyr::filter(purrr::map_lgl(
            purrr::map_int(.data$tags, nrow),
            ~ .x == n_tags
          ))
        if(stratify()){
          nodes <- nodes %>%
            dplyr::filter(purrr::map_lgl(
              purrr::map(.data$tags, dplyr::pull, "name"),
              ~any(input$stratified_group_selected %in% .x)
            ))
        }
        nodes <- nodes %>%
          dplyr::select("tags", "node_name" = "name", "node_display" = "feature_display") %>%
          tidyr::unnest("tags") %>%
          dplyr::select("node_name", "node_display", "tag" = "name") %>%
          dplyr::mutate("Type" = "Cell")
        return(nodes)
      })

      nodes <- shiny::reactive(dplyr::bind_rows(gene_nodes(), feature_nodes()))

      # TODO: Use client to filter edges
      edges <- shiny::eventReactive(input$calculate_button, {
        nodes <- nodes() %>%
          dplyr::pull("node_name") %>%
          unique()
        edges <- iatlas.api.client::query_edges(nodes, nodes) %>%
          dplyr::filter(.data$score > input$concordance) %>%
          dplyr::select("edge_name" = "name", "node1", "node2", "score") %>%
          tidyr::pivot_longer(
            cols = c("node1", "node2"),
            names_to = "node_num",
            values_to = "node_name"
          ) %>%
          dplyr::inner_join(nodes(), by = "node_name") %>%
          dplyr::select(
            "edge_name", "score", "node_num", "node_display", "tag"
          ) %>%
          tidyr::pivot_wider(
            names_from = "node_num", values_from = "node_display"
          )
      })

      scaffold <- shiny::reactive(
        edges() %>%
          dplyr::select("node1", "node2") %>%
          dplyr::distinct()
      )

      output$select_node_ui <- shiny::renderUI({
        shiny::selectInput(
          ns("node_selection"),
          "Select or Search for Node",
          choices = nodes() %>%
            dplyr::select("node_display", "node_name") %>%
            tibble::deframe(.) %>%
            c(" " = 0, .),
          selected = 0
        )
      })

      graph_json <- shiny::reactive({

        edges_nodes <-
          c(edges()$node1, edges()$node2) %>%
          unique()

        nodes <- nodes() %>%
          dplyr::filter(.data$node_display %in% edges_nodes) %>%
          dplyr::arrange("node_display") %>%
          dplyr::select(
            "id" = "node_display",
            "Type",
            "FriendlyName" = "node_display"
          ) %>%
          dplyr::distinct() %>%
          as.data.frame()

        edges <- edges() %>%
          dplyr::select(
            "source" = "node1",
            "target" = "node2",
            "score",
            "interaction" = "tag"
          ) %>%
          as.data.frame()

        cyjShiny::dataFramesToJSON(edges, nodes)
      })

      output$cyjShiny <- cyjShiny::renderCyjShiny({
        cyjShiny::cyjShiny(
            graph_json(),
            layoutName = input$do_layout,
            styleFile = get_javascript_path("extracellular_network_stylesEdges")
        )
      })

      shiny::observeEvent(input$loadStyleFile, ignoreInit = TRUE, {
        if (input$loadStyleFile != "") {
          tryCatch({
            cyjShiny::loadStyleFile(input$loadStyleFile)
          }, error = function(e) {
            msg <- sprintf(
              "ERROR in stylesheet file '%s': %s",
              input$loadStyleFile,
              e$message
            )
            shiny::showNotification(msg, duration = NULL, type = "error")
          })
        }
      })

      shiny::observeEvent(input$node_selection,  ignoreInit = TRUE, {
        print(input$node_selection)
        session$sendCustomMessage(
          type = "selectNodes",
          message = list(as.integer(input$node_selection))
        )
      })

      shiny::observeEvent(input$sfn,  ignoreInit = TRUE, {
        session$sendCustomMessage(type = "sfn", message = list())
      })

      shiny::observeEvent(input$fit, ignoreInit = TRUE, {
        cyjShiny::fit(session, 80)
      })

      shiny::observeEvent(input$fitSelected, ignoreInit = TRUE, {
        cyjShiny::fitSelected(session, 100)
      })

      shiny::observeEvent(input$hideSelection, ignoreInit = TRUE, {
        session$sendCustomMessage(type = "hideSelection", message = list())
      })

      shiny::observeEvent(input$showAll, ignoreInit = TRUE, {
        session$sendCustomMessage(type = "showAll", message = list())
      })

      shiny::observeEvent(input$clearSelection, ignoreInit = TRUE, {
        session$sendCustomMessage(type = "clearSelection", message = list())
      })

      shiny::observeEvent(input$removeGraphButton, ignoreInit = TRUE, {
        cyjShiny::removeGraph(session)
      })

      shiny::observeEvent(input$savePNGbutton, ignoreInit = TRUE, {
        file.name <- tempfile(fileext = ".png")
        shiny::savePNGtoFile(session, file.name)

      })
    }
  )
}
