tumor_microenvironment_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      show_ocp_submodule <- shiny::reactive({
        function(cohort_obj) {
          cohort_obj$has_features(
            c("leukocyte_fraction", "Stromal_Fraction", "Tumor_fraction")
          )
        }
      })

      call_module_server(
        "tumor_microenvironment_cell_proportions",
        cohort_obj,
        test_function = show_ocp_submodule,
        server_function = tumor_microenvironment_cell_proportions_server,
        ui_function = tumor_microenvironment_cell_proportions_ui
      )

      show_ctf_submodule <- shiny::reactive({
        function(cohort_obj){
          fraction_classes <- c(
            "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class",
            "Immune Cell Proportion - Differentiated Lymphoid and Myeloid Cell Derivative Class",
            "Immune Cell Proportion - Multipotent Progenitor Cell Derivative Class",
            "Immune Cell Proportion - Original"
          )
          cohort_obj$has_classes(
            classes = fraction_classes,
            all_classes = F
          )
        }
      })

      call_module_server(
        "tumor_microenvironment_type_fractions",
        cohort_obj,
        test_function = show_ctf_submodule,
        server_function = tumor_microenvironment_type_fractions_server,
        ui_function = tumor_microenvironment_type_fractions_ui
      )
    }
  )
}
