data_table_server <- function(
  id,
  tbl,
  options = list(pageLength = 10),
  color = F,
  color_column = NULL,
  colors = NULL,
  ...
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {
      output$data_table_module <- DT::renderDT({
        dt <- DT::datatable(
          tbl(),
          options = options,
          rownames = FALSE,
          ...
        )
        if (color) {
          dt <-  DT::formatStyle(
            dt,
            color_column,
            backgroundColor = DT::styleEqual(colors, colors))
        }
        return(dt)
      })

      output$download_tbl <- shiny::downloadHandler(
        filename = function() stringr::str_c("data-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(tbl(), con)
      )
    }
  )
}
