cohort_selection_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      cohort_obj_manual <- cohort_manual_selection_server(
        "cohort_manual_selection"
      )

      cohort_obj_upload <- cohort_upload_selection_server(
        "cohort_upload_selection"
      )

      cohort_obj <- shiny::reactive({
        shiny::req(input$cohort_mode_choice)
        if (input$cohort_mode_choice == "Cohort Selection") {
          shiny::req(cohort_obj_manual())
          return(cohort_obj_manual())
        } else if (input$cohort_mode_choice == "Cohort Upload") {
          shiny::req(cohort_obj_upload())
          return(cohort_obj_upload())
        } else {
          stop("Unrecognized cohort creation opion")
        }
      })

      # group key ---------------------------------------------------------------

      group_key_tbl <- shiny::reactive({
        shiny::req(cohort_obj())
        cohort_obj()$group_tbl %>%
          dplyr::select(
            `Sample Group` = group,
            `Group Name` = name,
            `Group Size` = size,
            Characteristics = characteristics,
            `Plot Color` = color
          )
      })

      data_table_server(
        "sg_table",
        group_key_tbl,
        options = list(
          dom = "tip",
          pageLength = 10,
          columnDefs = list(
            list(width = '50px',
                 targets = c(1)
            )
          )
        ),
        color = T,
        color_column = "Plot Color",
        colors = ""
      )

      # return ------------------------------------------------------------------
      return(cohort_obj)
    }
  )
}

