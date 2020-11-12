insert_remove_element_server <- function(
  id,
  element_module,
  element_module_ui,
  remove_ui_event = shiny::reactive(NULL)
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      params  <- shiny::reactiveValues(ui_numbers = c())
      results <- shiny::reactiveValues()

      ns <- session$ns

      shiny::observeEvent(input$add_button, {

        ui_number           <- input$add_button
        params$ui_numbers   <- c(params$ui_numbers, ui_number)
        remove_button_id    <- paste0('remove_button', ui_number)
        ui_id               <- ns(paste0("ui", ui_number))
        add_button_selector <- paste0("#", ns("add_button"))
        ui_selector         <- paste0("#", ui_id)
        module_id           <- paste0("element", ui_number)

        shiny::insertUI(
          selector = add_button_selector,
          where    = "afterEnd",
          ui       = shiny::div(
            id = ui_id,
            shiny::actionButton(ns(remove_button_id), 'Remove')
          )
        )
        shiny::insertUI(
          selector = add_button_selector,
          where    = "afterEnd",
          ui       = shiny::div(
            id = ui_id,
            element_module_ui()(ns(module_id))
          )
        )

        element_module()(
          module_id,
          results,
          module_id
        )

        shiny::observeEvent(input[[remove_button_id]], {
          shiny::removeUI(selector = ui_selector)
          shiny::removeUI(selector = ui_selector)
          results[[module_id]] <- NULL
          params$ui_numbers <- params$ui_numbers %>%
            purrr::discard(. == ui_number)
        })
      })

      shiny::observeEvent(remove_ui_event(), {
        button_selectors <-
          stringr::str_c("ui", params$ui_numbers) %>%
          ns() %>%
          stringr::str_c("#", .)
        purrr::walk(button_selectors, shiny::removeUI)
        purrr::walk(button_selectors, shiny::removeUI)
        params$ui_numbers <- c()
        names <- results %>%
          shiny::reactiveValuesToList(.) %>%
          names()
        for(name in names){
          results[[name]] <- NULL
        }
      })

      return(shiny::reactive(results))
    }
  )
}
