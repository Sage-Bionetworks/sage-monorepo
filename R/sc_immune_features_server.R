sc_immune_features_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      gsea_df <- shiny::reactive(arrow::read_feather("inst/feather/sc_gsea_norm.feather"))
      sc_clinical <- shiny::reactive(arrow::read_feather("inst/feather/sc_clinical.feather"))

      sc_immune_features_distribution_server(
        "sc_immune_features_distribution",
        cohort_obj,
        gsea_df,
        feature_op = shiny::reactive(c( #TODO: change this when data is in cohort_obj
          "Wound Healing" = "CHANG_CORE_SERUM_RESPONSE_UP",
          "Macrophage Regulation" = "CSF1_response",
          "Lymphocyte Infiltration" = "LIexpression_score",
          "Proliferation" = "Module11_Prolif_score",
          "IFN-gamma Response" = "Module3_IFN_score",
          "TGF-beta Response" = "TGFB_score_21050467",
          "Th1 Cells" = "Th1_cells",
          "Th2 Cells" = "Th2_cells"
        )),

          #shiny::reactive(unique(gsea_df()$feature_name)),
        sc_clinical
      )


    }
  )
}
