ici_immunomodulators_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ici_datasets <- shiny::reactive({
        x <- iatlas.api.client::query_datasets(types = "ici")
        setNames(as.character(x$name), x$display)
      })

      categories_df <- shiny::reactive(iatlas.api.client::query_tags(datasets = ici_datasets()) %>%
                                         dplyr::mutate(class = dplyr::case_when(
                                           tag_name %in% c( "Response", "Responder", "Progression", "Clinical_Benefit") ~ "Response to ICI",
                                           TRUE ~ "Treatment Data")) %>%
                                         create_nested_list_by_class(.,
                                                                     class_column = "class",
                                                                     internal_column = "tag_name",
                                                                     display_column = "tag_short_display")
      )

      genes <- shiny::reactive({
        iatlas.api.client::query_immunomodulators() %>%
          dplyr::select(
            "feature_name" = "entrez",
            "feature_display" = "hgnc",
            "Gene Family" = "gene_family",
            "Gene Function" = "gene_function",
            "Immune Checkpoint" = "immune_checkpoint",
            "Super Category" = "super_category"
          )
      })

      var_choices <- reactive({
        genes() %>%
          dplyr::select(
            name = feature_name,
            display = feature_display,
            class = `Gene Family`
          )
      })

      features_df <- shiny::reactive({
        shiny::req(genes())
        iatlas.api.client::query_gene_expression(cohorts = ici_datasets(), entrez = genes()$feature_name) %>%
          dplyr::select(
            sample,
            "feature_name" = "entrez",
            "feature_display" = "hgnc",
            "feature_value" = "rna_seq_expr"
          )
      })

      ici_distribution_server(
        "ici_immunomodulators_distribution",
        cohort_obj,
        metadata_feature_df = categories_df(),
        feature_df = var_choices()
      )
    }
  )
}
