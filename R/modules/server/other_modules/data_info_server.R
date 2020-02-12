data_info_server <- function(
    input,
    output,
    session
){
    ns <- session$ns

    source("R/data_info_functions.R", local = T)

    class_list <- create_class_list()

    output$classes <- shiny::renderUI({
        shiny::selectInput(
            ns("class_choice_id"),
            label = "Select or Search for Class",
            choices = class_list,
            selected = -1
        )
    })

    feature_tbl <- shiny::reactive({
        shiny::req(input$class_choice_id)
        create_feature_tbl(as.integer(input$class_choice_id))
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
        get_selected_method_tags(filtered_feature_tbl())
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
            .GlobalEnv$sectionBox(
                title = "Variable Class Details",
                .GlobalEnv$messageBox(
                    width = 12,
                    p("Here is additional information about the variables in the Variable Class you selected. To the right you can access description of the methods used to obtain the variables.")
                ),
                shiny::fluidRow(
                    .GlobalEnv$tableBox(
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
