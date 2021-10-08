til_map_datatable_server <- function(id, cohort_obj) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      patient_tbl <- shiny::reactive({
        patient_tbl <-
          iatlas.api.client::query_sample_patients(
            samples = cohort_obj()$sample_tbl$sample_name
          ) %>%
          dplyr::select("sample_name", "patient_name")
      })

      slide_tbl <- shiny::reactive({
        slide_tbl <-
          iatlas.api.client::query_patient_slides(
            samples = cohort_obj()$sample_tbl$sample_name
          ) %>%
          dplyr::select("patient_name", "slide_name") %>%
          tidyr::drop_na()
      })

      feature_tbl <- shiny::reactive({
        feature_tbl <-
          cohort_obj()$get_feature_values(feature_classes = "TIL Map Characteristic") %>%
          dplyr::mutate(
            "feature_value" = round(.data$feature_value, digits = 1)
          ) %>%
          dplyr::select("sample_name", "feature_display", "feature_value") %>%
          tidyr::pivot_wider(
            .,
            names_from = "feature_display",
            values_from = "feature_value"
          )
      })


      tilmap_tbl <- shiny::reactive({

        shiny::req(patient_tbl(), slide_tbl(), feature_tbl())

        sample_tbl <- cohort_obj()$sample_tbl

        tilmap_tbl <-
          dplyr::inner_join(sample_tbl, patient_tbl(), by = "sample_name") %>%
          dplyr::inner_join(feature_tbl(), by = "sample_name") %>%
          dplyr::inner_join(slide_tbl(), by = "patient_name") %>%
          dplyr::select(-"patient_name") %>%
          dplyr::mutate("Image" = create_tm_slide_link(.data$slide_name)) %>%
          dplyr::select(- "slide_name") %>%
          dplyr::select(
            "Sample" = "sample_name",
            "Selected Group" = "group_name",
            "Image",
            dplyr::everything()
          )
      })

      data_table_server("til_table", tilmap_tbl, escape = F)
    }
  )
}
