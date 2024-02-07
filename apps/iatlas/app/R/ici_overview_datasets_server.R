ici_overview_datasets_server <- function(
  id,
  ioresponse_data,
  data_group
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      dataset_info <- shiny::reactive({
        ioresponse_data$dataset_df %>%
          dplyr::filter(type == data_group())
      })

      output$sums <- shiny::renderText({
        n_samples <- sum(dataset_info()$Samples)
        n_patients <- sum(dataset_info()$Patients)
        glue::glue(
          "CRI iAtlas has {n_samples} samples, from {n_patients} patients in this category of datasets."
        )
      })

      output$ici_datasets_df <- DT::renderDT({
        DT::datatable((dataset_info() %>%
                         dplyr::mutate(
                           Reference = paste(
                             "<a href=\"",
                             Paper,"\">",
                             Citation,"</a>",
                             sep=""
                           )
                         )%>%
                         select(Dataset, Study, Antibody, Samples, Patients, `Sequencing.Method`, Reference)),
                      options = list(pageLength = 20),
                      escape= FALSE)
      })

    }
  )
}
