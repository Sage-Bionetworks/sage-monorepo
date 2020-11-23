cohort_upload_selection_server <- function(id){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <-  session$ns

      user_group_tbl <- shiny::reactive({

        shiny::validate(shiny::need(
          input$file$datapath,
          "Use above uploader to upload group csv",
        ))
        result <- try(readr::read_csv(input$file$datapath))
        if (tibble::is_tibble(result)) {
          return(result)
        } else {
          return(NA)
        }
      })

      shiny::observeEvent(input$filehelp, {
        shiny::showModal(shiny::modalDialog(
          title = "Formatting custom groups",
          shiny::includeMarkdown(get_markdown_path("user_groups")),
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

        shiny::req(user_group_tbl(), input$user_group_choice)

        sample_tbl <- user_group_tbl() %>%
          dplyr::select("sample" = 1, "group" = input$user_group_choice)

        group_tbl <- sample_tbl %>%
          dplyr::group_by(group) %>%
          dplyr::summarise(size = dplyr::n(), .groups = "drop") %>%
          dplyr::mutate(name = "", characteristics = "") %>%
          dplyr::arrange(group) %>%
          add_plot_colors_to_tbl()

        feature_tbl <- iatlas.api.client::query_features(
          samples = sample_tbl$sample
        )

        plot_colors <- group_tbl %>%
          dplyr::select(group) %>%
          dplyr::mutate(color = viridisLite::viridis(dplyr::n())) %>%
          tibble::deframe(.)

        list(
          "sample_tbl"  = sample_tbl,
          "group_tbl"   = group_tbl,
          "group_name"  = input$user_group_choice,
          "group_type"  = "User Defined Group",
          "feature_tbl" = feature_tbl,
          "plot_colors" = plot_colors,
          "dataset"     = NA
        )
      })

      return(cohort_obj)
    }
  )
}
