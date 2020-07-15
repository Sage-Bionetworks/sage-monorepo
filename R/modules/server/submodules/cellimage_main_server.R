cellimage_main_server <- function(
    input,
    output,
    session,
    cohort_obj
){
#
#     ns <- session$ns
#
#     get_groups_ui <- shiny::reactive({
#         req(
#             panimmune_data$sample_group_df,
#             group_internal_choice()
#         )
#
#         if(group_internal_choice() == "Subtype_Curated_Malta_Noushmehr_et_al"){
#             req(study_subset_choice())
#         }
#
#         if(group_internal_choice() %in% c("Study", "Subtype_Curated_Malta_Noushmehr_et_al", "Subtype_Immune_Model_Based")){
#             sample_group_vector <-  panimmune_data$sample_group_df %>%
#                 dplyr::filter(sample_group ==  group_internal_choice()) %>%
#                 `if`(
#                     group_internal_choice() == "Subtype_Curated_Malta_Noushmehr_et_al",
#                     dplyr::filter(., `TCGA Studies`== study_subset_choice()),
#                     .
#                 ) %>% dplyr::pull(FeatureValue) %>%
#                 sort()
#         }else{ #Custom group
#             sample_group_vector <- sample_group_df() %>%
#                 dplyr::select_(group_internal_choice()) %>%
#                 unique()
#         }
#
#         sample_group_vector
#     })
#
#
#     output$select_ui <- shiny::renderUI({
#         shiny::req(get_groups_ui())
#
#         shiny::selectInput(
#             ns("groupselect_method1"),
#             "Select Group",
#             choices = get_groups_ui()
#         )
#     })
#
#     output$select_ui2 <- shiny::renderUI({
#         shiny::req(get_groups_ui())
#
#         shiny::selectInput(
#             ns("groupselect_method2"),
#             "Select Group",
#             choices = get_groups_ui()
#         )
#     })
#
#     #organizing abundance data for easier referencing
#
#     nodes_ratio <- function(selected_group){
#         if(selected_group == "Subtype_Immune_Model_Based") panimmune_data$ecn_df$immune$upbin_ratio
#         else if(selected_group == "Study") panimmune_data$ecn_df$study$upbin_ratio
#         else if(selected_group == "Subtype_Curated_Malta_Noushmehr_et_al") panimmune_data$ecn_df$subtype$upbin_ratio
#         else compute_abundance(sample_group_df(),
#                                subset_col = group_internal_choice(),
#                                panimmune_data$fmx_df,
#                                panimmune_data$ecn_expr,
#                                cois = get_cells_from_image(panimmune_data$cellimage_base),
#                                gois = get_genes_from_image(panimmune_data$cellimage_base)) %>%
#             dplyr::select(Node, IncludeFeature) %>%
#             tidyr::unnest(c(IncludeFeature))
#     }
#
#     #Output depending on the option selected by the user
#
#     output$plot1 <- shiny::renderUI({
#         shiny::req(input$groupselect_method1)
#
#         nodes_ratio <- shiny::isolate(
#             nodes_ratio(selected_group = group_internal_choice())
#         )
#
#         if(input$ui1 == "Illustration"){
#             output$cellPlot1 <- shiny::renderPlot({
#                 shiny::validate(
#                     shiny::need(
#                         (input$groupselect_method1 %in% nodes_ratio$Group),
#                         "Please select another subtype - this one has limited data."
#                     )
#                 )
#                 image_grob <- get_cell_image_object(
#                     cellimage_base = panimmune_data$cellimage_base,
#                     subtype_selected = input$groupselect_method1,
#                     vals_for_cellplot = nodes_ratio
#                 )
#                 grid::grid.draw(image_grob)
#             })
#             shiny::plotOutput(ns("cellPlot1"), height = 600)
#
#         } else if(input$ui1 == "Network"){
#
#             output$imageNetwork1 <- cyjShiny::renderCyjShiny({
#                 shiny::validate(
#                     shiny::need(
#                         (input$groupselect_method1 %in% nodes_ratio$Group),
#                         "Please select another subtype - this one has limited data."
#                     )
#                 )
#                 graph.json <- get_network_object(
#                     input$groupselect_method1,
#                     nodes = nodes_ratio,
#                     friendly_df = panimmune_data$ecn_labels,
#                     positions_df = panimmune_data$ci_coord,
#                     scaffold = panimmune_data$ci_scaffold
#                 )
#                 cyjShiny::cyjShiny(
#                     graph.json,
#                     layoutName = "preset",
#                     styleFile = "data/javascript/style_network_cellimage.js"
#                 )
#             })
#             cyjShiny::cyjShinyOutput(ns("imageNetwork1"), height = 600)
#         }
#     })
#
#     output$plot2 <- shiny::renderUI({
#         shiny::req(input$groupselect_method2)
#
#         nodes_ratio <- shiny::isolate(
#             nodes_ratio(selected_group = group_internal_choice())
#         )
#
#         if(input$ui2 == "Illustration"){
#             output$cellPlot2 <- shiny::renderPlot({
#                 shiny::validate(
#                     shiny::need(
#                         (input$groupselect_method2 %in% nodes_ratio$Group),
#                         "Please select another subtype - this one has limited data."
#                     )
#                 )
#                 image_grob <- get_cell_image_object(
#                     cellimage_base = panimmune_data$cellimage_base,
#                     subtype_selected = input$groupselect_method2,
#                     vals_for_cellplot = nodes_ratio
#                 )
#                 grid::grid.draw(image_grob)
#             })
#             shiny::plotOutput(ns("cellPlot2"), height = 600)
#
#         } else if(input$ui2 == "Network"){
#
#             output$imageNetwork2 <- cyjShiny::renderCyjShiny({
#                 shiny::validate(
#                     shiny::need(
#                         (input$groupselect_method2 %in% nodes_ratio$Group),
#                         "Please select another subtype - this one has limited data."
#                     )
#                 )
#                 graph.json <- get_network_object(
#                     input$groupselect_method2,
#                     nodes = nodes_ratio,
#                     friendly_df = panimmune_data$ecn_labels,
#                     positions_df = panimmune_data$ci_coord,
#                     scaffold = panimmune_data$ci_scaffold
#                 )
#                 cyjShiny::cyjShiny(
#                     graph.json,
#                     layoutName = "preset",
#                     styleFile = "data/javascript/style_network_cellimage.js"
#                 )
#             })
#             cyjShiny::cyjShinyOutput(ns("imageNetwork2"), height = 600)
#         }
#     })
#
#     #Button with method information
#
#     shiny::observeEvent(input$methodButton, {
#         shiny::showModal(shiny::modalDialog(
#             title = "Method",
#             shiny::includeMarkdown("data/MethodsText/Methods_CellImage.txt"),
#             easyClose = TRUE,
#             footer = NULL
#         ))
#     })
#
#     shiny::observeEvent(input$method_link,{
#         shiny::showModal(shiny::modalDialog(
#             title = "Method",
#             shiny::includeMarkdown("data/MethodsText/Methods_CellImage.txt"),
#             easyClose = TRUE,
#             footer = NULL
#         ))
#     })
}
