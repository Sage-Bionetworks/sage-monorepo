cohort_upload_selection_server <- function(
    input,
    output,
    session,
    feature_named_list
){

    user_group_tbl <- shiny::reactive({
        if (is.null(input$file1$datapath)) {
            return(NA)
        }
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
            shiny::includeMarkdown("markdown/user_groups.markdown"),
            size = "l",
            easyClose = TRUE
        ))
    })
}
