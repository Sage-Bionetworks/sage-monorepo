data_info_server <- function(id){
  shiny::moduleServer(
    id,
    function(input, output, session) {
      ns <- session$ns

      class_list <- shiny::reactive({
        iatlas.api.client::query_features() %>%
          dplyr::pull("class") %>%
          c("All classes", .)
      })

      output$classes <- shiny::renderUI({
        shiny::selectInput(
          ns("class_choice"),
          label = "Select or Search for Class",
          choices = class_list(),
          selected = "All classes"
        )
      })

      feature_tbl <- shiny::reactive({
        shiny::req(input$class_choice)
        if(input$class_choice == "All classes") {
          iatlas.api.client::query_features()
        } else {
          iatlas.api.client::query_features(
            feature_class = input$class_choice
          )
        }

      })

      output$feature_table <- DT::renderDT({
        shiny::req(feature_tbl())
        DT::datatable(
          format_feature_tbl(feature_tbl()),
          selection = list(mode = 'single'),
          options   = list(scrollX = TRUE, autoWidth = F ),
          rownames  = FALSE
        )
      }, server = FALSE)


      filtered_feature_tbl <- shiny::reactive({
        shiny::req(feature_tbl(), input$feature_table_rows_selected)
        filter_feature_tbl(feature_tbl(), input$feature_table_rows_selected)
      })

      output$variable_class_table <- shiny::renderTable({
        shiny::req(filtered_feature_tbl())
        format_filtered_feature_tbl(filtered_feature_tbl())
      })

      selected_method_tags <- shiny::reactive({
        shiny::req(filtered_feature_tbl())
        iatlas.modules::get_unique_values_from_col(
          filtered_feature_tbl(),
          method_tag
        )
      })

      output$method_buttons <- shiny::renderUI({
        shiny::req(selected_method_tags())
        selected_method_tags() %>%
          purrr::map(
            ~fluidRow(actionButton(ns(paste0("show_", .x)), .x))
          ) %>%
          shiny::tagList()
      })

      show_method <- function(tag){
        shiny::observeEvent(input[[paste0("show_", tag)]], {
          shiny::showModal(shiny::modalDialog(
            title = "Methods",
            shiny::includeMarkdown(paste0(
              "markdown/methods/",
              tag,
              ".markdown"
            )),
            size = "l",
            easyClose = TRUE
          ))
        })
      }

      shiny::observeEvent(input$feature_table_rows_selected, {
        shiny::req(selected_method_tags())
        purrr::walk(selected_method_tags(), show_method)
      })

      shiny::observeEvent(input$feature_table_rows_selected, {
        output$variable_details_section <- shiny::renderUI({
          iatlas.modules::sectionBox(
            title = "Variable Class Details",
            iatlas.modules::messageBox(
              width = 12,
              shiny::p("Here is additional information about the variables in the Variable Class you selected. To the right you can access description of the methods used to obtain the variables.")
            ),
            shiny::fluidRow(
              iatlas.modules::tableBox(
                width = 9,
                shiny::tableOutput(ns('variable_class_table'))
              ),
              shiny::column(
                width = 3,
                shiny::h5(shiny::strong("Click to view methods")),
                shiny::uiOutput(ns("method_buttons"))
              )
            )
          )
        })
      })
    }
  )
}
