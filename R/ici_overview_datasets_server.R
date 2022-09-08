ici_overview_datasets_server <- function(
  id,
  ioresponse_data
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$sums <- shiny::renderText({
        n_samples <- sum(ioresponse_data$dataset_df$Samples)
        n_patients <- sum(ioresponse_data$dataset_df$Patients)
        glue::glue(
          "CRI iAtlas has {n_samples} ICI samples, from {n_patients} patients."
        )
      })

      output$ici_datasets_df <- DT::renderDT({
        DT::datatable((ioresponse_data$dataset_df %>%
                         dplyr::mutate(
                           Reference = paste(
                             "<a href=\"",
                             Paper,"\">",
                             Citation,"</a>",
                             sep=""
                           )
                         )%>%
                         select(Dataset, Study, Antibody, Samples, Patients, `Sequencing Method`, Reference)),
                      options = list(pageLength = 20),
                      escape= FALSE)
      })

    }
  )
}
