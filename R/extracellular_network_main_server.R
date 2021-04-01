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

      show_stratify_option <- shiny::reactive({
        shiny::req(cohort_obj())
        all(
          (cohort_obj()$dataset == "TCGA"),
          (cohort_obj()$group_name == "TCGA_Study")
        )
      })

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

      gene_choice_list <- shiny::reactive({
        build_ecn_gene_choice_list()
      })

      shiny::updateSelectizeInput(session, 'selected_genes',
                                  choices = gene_choice_list(),
                                  selected = "geneset:immunomodulator",
                                  server = TRUE)


      output$select_celltypes <- shiny::renderUI({
        shiny::selectizeInput(
          ns("selected_celltypes"),
          "Select cells of interest (optional)",
          choices = build_ecn_celltype_choice_list(),
          selected = "All",
          multiple = TRUE
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
        get_gene_nodes(
          stratify(),
          cohort_obj()$dataset,
          selected_genes(),
          input$group_selected,
          input$stratified_group_selected,
          input$abundance
        )
      })

      feature_nodes <- shiny::eventReactive(input$calculate_button, {
        get_feature_nodes(
          stratify(),
          cohort_obj()$dataset,
          selected_celltypes(),
          input$group_selected,
          input$stratified_group_selected,
          input$abundance
        )
      })

      nodes <- shiny::reactive({
        shiny::req(gene_nodes(), feature_nodes())
        dplyr::bind_rows(gene_nodes(), feature_nodes())
      })

      edges <- shiny::reactive({
        shiny::req(nodes(), input$concordance)
        edges <- get_edges(nodes(), input$concordance)
        shiny::validate(shiny::need(
          nrow(edges) > 0,
          "No network for this selection. Try changing the thresholds or selecting another subset."
        ))
        return(edges)
      })

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

      filtered_nodes <- shiny::reactive({
        filter_nodes(nodes(), edges())
      })

      graph_json <- shiny::reactive({
        create_graph_json(edges(), filtered_nodes())
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

      edges_output <- shiny::reactive({
        edges() %>%
          dplyr::mutate(Concordance = round(score, 3)) %>%
          dplyr::select(
            "From" = "node_display1",
            "From (Friendly Name)" =  "node_friendly1",
            "To" = "node_display2",
            "To (Friendly Name)" = "node_friendly2",
            "Group" = "tag",
            "Concordance"
          )
      })

      nodes_output <- shiny::reactive({
        filtered_nodes() %>%
          dplyr::mutate(Abundance = round(Abundance, 3)) %>%
          dplyr::select(
            "Node",
            "Friendly Name" = "FriendlyName",
            "Type",
            "Group",
            "Abundance"
          )
      })

      output$download_edges <- shiny::downloadHandler(
        filename = function() stringr::str_c("edges-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(edges_output(), con)
      )

      output$download_nodes <- shiny::downloadHandler(
        filename = function() stringr::str_c("nodes-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(nodes_output(), con)
      )

      output$edges_dt <- DT::renderDataTable({
        shiny::req(edges_output())

        DT::datatable(
          edges_output(),
          caption = "Edges Table",
          width = "100%",
          rownames = FALSE
        )
      })

      output$nodes_dt <- DT::renderDataTable({
        shiny::req(nodes_output())
        DT::datatable(
          nodes_output(),
          caption = "Nodes Table",
          width = "100%",
          rownames = FALSE
        )
      })

    }
  )
}
