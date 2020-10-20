cellimage_main_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      cohort_groups <- shiny::reactive({
        iatlas.api.client::query_tags(
          datasets = cohort_obj()$dataset,
          parent_tags = cohort_obj()$group_name
        ) %>%
          dplyr::pull("name")
      })

      output$select_group1_ui <- shiny::renderUI({
        shiny::req(cohort_groups())

        shiny::selectInput(
          ns("group_selected1"),
          "Select Group",
          choices = cohort_groups()
        )
      })

      output$select_group2_ui <- shiny::renderUI({
        shiny::req(cohort_groups())

        shiny::selectInput(
          ns("group_selected2"),
          "Select Group",
          choices = cohort_groups()
        )
      })

      gene_nodes1 <- shiny::reactive({
        genes <-
          iatlas.api.client::query_genes(gene_types = "immunomodulator") %>%
          dplyr::pull("entrez")

        nodes <-
          iatlas.api.client::query_gene_nodes(
            datasets = "TCGA",
            network = "cellimage_network",
            entrez = genes,
            tags = input$group_selected1
          ) %>%
          print() %>%
          dplyr::select("tags", "node_name" = "name", "node_display" = "hgnc") %>%
          tidyr::unnest("tags") %>%
          dplyr::select("node_name", "node_display", "tag" = "name") %>%
          dplyr::mutate("Type" = "Gene")
        return(nodes)
      })

      feature_nodes1 <- shiny::reactive({
        features <-
          iatlas.api.client::query_features(
            feature_class = "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class"
          ) %>%
          dplyr::pull("display") %>%
          print()

        nodes <-
          iatlas.api.client::query_feature_nodes(
            datasets = "TCGA",
            network = "cellimage_network",
            features = features,
            tags = input$group_selected2
          ) %>%
          print() %>%
          dplyr::select("tags", "node_name" = "name", "node_display" = "feature_display") %>%
          tidyr::unnest("tags") %>%
          dplyr::select("node_name", "node_display", "tag" = "name") %>%
          dplyr::mutate("Type" = "Cell")
        return(nodes)
      })


      #organizing abundance data for easier referencing

      nodes_ratio <- function(selected_group){
        if(selected_group == "Subtype_Immune_Model_Based") panimmune_data$ecn_df$immune$upbin_ratio
        else if(selected_group == "Study") panimmune_data$ecn_df$study$upbin_ratio
        else if(selected_group == "Subtype_Curated_Malta_Noushmehr_et_al") panimmune_data$ecn_df$subtype$upbin_ratio
        else compute_abundance(sample_group_df(),
                               subset_col = group_internal_choice(),
                               panimmune_data$fmx_df,
                               panimmune_data$ecn_expr,
                               cois = get_cells_from_image(panimmune_data$cellimage_base),
                               gois = get_genes_from_image(panimmune_data$cellimage_base)) %>%
          dplyr::select(Node, IncludeFeature) %>%
          tidyr::unnest(c(IncludeFeature))
      }

      #Output depending on the option selected by the user

      output$plot1 <- shiny::renderUI({
        shiny::req(input$groupselect_method1)

        nodes_ratio <- shiny::isolate(
          nodes_ratio(selected_group = group_internal_choice())
        )

        if(input$ui1 == "Illustration"){
          output$cellPlot1 <- shiny::renderPlot({
            shiny::validate(
              shiny::need(
                (input$groupselect_method1 %in% nodes_ratio$Group),
                "Please select another subtype - this one has limited data."
              )
            )
            image_grob <- get_cell_image_object(
              cellimage_base = panimmune_data$cellimage_base,
              subtype_selected = input$groupselect_method1,
              vals_for_cellplot = nodes_ratio
            )
            grid::grid.draw(image_grob)
          })
          shiny::plotOutput(ns("cellPlot1"), height = 600)

        } else if(input$ui1 == "Network"){

          output$imageNetwork1 <- cyjShiny::renderCyjShiny({
            shiny::validate(
              shiny::need(
                (input$groupselect_method1 %in% nodes_ratio$Group),
                "Please select another subtype - this one has limited data."
              )
            )
            graph.json <- get_network_object(
              input$groupselect_method1,
              nodes = nodes_ratio,
              friendly_df = panimmune_data$ecn_labels,
              positions_df = panimmune_data$ci_coord,
              scaffold = panimmune_data$ci_scaffold
            )
            cyjShiny::cyjShiny(
              graph.json,
              layoutName = "preset",
              styleFile = "data/javascript/style_network_cellimage.js"
            )
          })
          cyjShiny::cyjShinyOutput(ns("imageNetwork1"), height = 600)
        }
      })

      output$plot2 <- shiny::renderUI({
        shiny::req(input$groupselect_method2)

        nodes_ratio <- shiny::isolate(
          nodes_ratio(selected_group = group_internal_choice())
        )

        if(input$ui2 == "Illustration"){
          output$cellPlot2 <- shiny::renderPlot({
            shiny::validate(
              shiny::need(
                (input$groupselect_method2 %in% nodes_ratio$Group),
                "Please select another subtype - this one has limited data."
              )
            )
            image_grob <- get_cell_image_object(
              cellimage_base = panimmune_data$cellimage_base,
              subtype_selected = input$groupselect_method2,
              vals_for_cellplot = nodes_ratio
            )
            grid::grid.draw(image_grob)
          })
          shiny::plotOutput(ns("cellPlot2"), height = 600)

        } else if(input$ui2 == "Network"){

          output$imageNetwork2 <- cyjShiny::renderCyjShiny({
            shiny::validate(
              shiny::need(
                (input$groupselect_method2 %in% nodes_ratio$Group),
                "Please select another subtype - this one has limited data."
              )
            )
            graph.json <- get_network_object(
              input$groupselect_method2,
              nodes = nodes_ratio,
              friendly_df = panimmune_data$ecn_labels,
              positions_df = panimmune_data$ci_coord,
              scaffold = panimmune_data$ci_scaffold
            )
            cyjShiny::cyjShiny(
              graph.json,
              layoutName = "preset",
              styleFile = "data/javascript/style_network_cellimage.js"
            )
          })
          cyjShiny::cyjShinyOutput(ns("imageNetwork2"), height = 600)
        }
      })

      #Button with method information

      shiny::observeEvent(input$methodButton, {
        shiny::showModal(shiny::modalDialog(
          title = "Method",
          shiny::includeMarkdown("data/MethodsText/Methods_CellImage.txt"),
          easyClose = TRUE,
          footer = NULL
        ))
      })

      shiny::observeEvent(input$method_link,{
        shiny::showModal(shiny::modalDialog(
          title = "Method",
          shiny::includeMarkdown("data/MethodsText/Methods_CellImage.txt"),
          easyClose = TRUE,
          footer = NULL
        ))
      })
    }
  )
}
