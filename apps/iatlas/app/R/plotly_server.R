plotly_server <- function(
  id,
  plot_tbl,
  plot_eventdata  = shiny::reactive(NULL),
  group_tbl       = shiny::reactive(NULL),
  show_group_text = T
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      # This is so that the conditional panel can see output$show_group_text
      output$show_group_text <- shiny::reactive(show_group_text)
      shiny::outputOptions(output, "show_group_text", suspendWhenHidden = FALSE)

      output$plot_group_text <- shiny::renderText({
        shiny::req(show_group_text, group_tbl())
        shiny::validate(shiny::need(
          plot_eventdata(),
          "Click plot to see group information."
        ))
        create_group_text_from_eventdata(plot_eventdata(), group_tbl())
      })

      output$download_tbl <- shiny::downloadHandler(
        filename = function() stringr::str_c("data-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(plot_tbl(), con)
      )
    }
  )
}

