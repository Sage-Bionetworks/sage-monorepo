til_map_datatable_server <- function(id, cohort_obj) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      tilmap_tbl <- shiny::reactive({

        sample_tbl <- cohort_obj()$sample_tbl

        patient_tbl <-
          iatlas.api.client::query_sample_patients(
            samples = sample_tbl$sample
          ) %>%
          dplyr::select("sample" = "sample_name", "patient" = "patient_name")

        slide_tbl <-
          iatlas.api.client::query_patient_slides(
            samples = sample_tbl$sample
          ) %>%
          dplyr::select("patient" = "patient_name", "slide" = "slide_name") %>%
          tidyr::drop_na()

        feature_tbl <-
          iatlas.modules2::query_feature_values_with_cohort_object(
            cohort_object = cohort_obj(),
            feature_classes = "TIL Map Characteristic"
          ) %>%
          dplyr::mutate(
            "feature_value" = round(.data$feature_value, digits = 1)
          ) %>%
          dplyr::select("sample", "feature_display", "feature_value") %>%
          tidyr::pivot_wider(
            .,
            names_from = "feature_display",
            values_from = "feature_value"
          )

        tilmap_tbl <-
          dplyr::inner_join(sample_tbl, patient_tbl, by = "sample") %>%
          dplyr::inner_join(feature_tbl, by = "sample") %>%
          dplyr::inner_join(slide_tbl, by = "patient") %>%
          dplyr::select(-"patient") %>%
          dplyr::mutate("Image" = create_tm_slide_link(.data$slide)) %>%
          dplyr::select(- "slide") %>%
          dplyr::select(
            "Sample" = "sample",
            "Selected Group" = "group",
            "Image",
            dplyr::everything()
          )
      })

      data_table_server("til_table", tilmap_tbl, escape = F)
    }
  )
}
