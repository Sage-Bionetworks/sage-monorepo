cohort_upload_selection_server <- function(
  id
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <-  session$ns

      user_group_tbl <- shiny::reactive({
        shiny::validate(shiny::need(
          input$file1$datapath,
          "Use above uploader to upload group csv",
        ))
        result <- try(readr::read_csv(input$file1$datapath))
        if (tibble::is_tibble(result)) {
          return(result)
        } else {
          return(NA)
        }
      })

      shiny::observeEvent(input$filehelp, {
        shiny::showModal(shiny::modalDialog(
          title = "Formatting custom groups",
          shiny::includeMarkdown("inst/markdown/user_groups.markdown"),
          size = "l",
          easyClose = TRUE
        ))
      })

      output$dt <- DT::renderDataTable({
        shiny::validate(shiny::need(
          nrow(user_group_tbl()) > 0,
          "Use above uploader to upload group csv"
        ))
        user_group_tbl()
      })

      output$user_group_selection <- shiny::renderUI({
        shiny::req(user_group_tbl())
        shiny::selectInput(
          inputId = ns("user_group_choice"),
          label   = "Select or Search for group",
          choices = colnames(user_group_tbl()[-1])
        )
      })

      cohort_obj <- shiny::reactive({
        shiny::req(input$user_group_choice)
        sample_tbl <-
          "SELECT id AS sample_id, name AS sample_name FROM samples" %>%
          perform_query("Create Sample Tibble") %>%
          dplyr::inner_join(
            dplyr::select(
              user_group_tbl(),
              sample_name = 1,
              group = input$user_group_choice
            ),
            by = "sample_name"
          ) %>%
          dplyr::select(-sample_name)
        group_tbl <- sample_tbl %>%
          dplyr::group_by(group) %>%
          dplyr::summarise(size = dplyr::n()) %>%
          dplyr::ungroup() %>%
          dplyr::mutate(name = "", characteristics = "") %>%
          dplyr::arrange(group)
        plot_colors <- group_tbl %>%
          dplyr::select(group) %>%
          dplyr::mutate(color = viridisLite::viridis(dplyr::n())) %>%
          tibble::deframe(.)
        group_name <- paste("User Defined Group: ", input$user_group_choice)
        list(
          "sample_tbl"  = sample_tbl,
          "group_tbl"   = group_tbl,
          "group_name"  = group_name,
          "plot_colors" = plot_colors,
          "dataset"     = "TCGA"
        )
      })

      return(cohort_obj)
    }
  )
}
