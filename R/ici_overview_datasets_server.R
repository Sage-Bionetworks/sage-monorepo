ici_overview_datasets_server <- function(
  id,
  cohort_obj,
  ioresponse_data
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

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
                         select(Dataset, Study, Antibody, `Primary Sample` = `PrimarySample(s)`, Samples, Patients, Reference)),
                      escape= FALSE)
      })

      output$download_metadata <- downloadHandler(
        filename = function() stringr::str_c("iatlas-io-metadata-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(ioresponse_data$dataset_df, con)
      )

      output$download_data <- downloadHandler(
        filename = function() stringr::str_c("iatlas-io-data-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(ioresponse_data$fmx_df, con)
      )

      output$download_expr <- downloadHandler(
        filename = function() stringr::str_c("iatlas-io-im-expr", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(ioresponse_data$im_expr, con)
      )
    }
  )
}
