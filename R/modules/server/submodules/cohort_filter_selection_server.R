cohort_filter_selection_server <- function(
  id,
  selected_dataset,
  samples
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {
      source(
        "R/modules/server/submodules/insert_remove_element_server.R",
        local = T
      )
      source("R/modules/ui/submodules/elements_ui.R", local = T)
      source("R/modules/server/submodules/elements_server.R", local = T)

      # tag filters -----------------------------------------------------------
      tag_named_list <- shiny::reactive({
        iatlas.api.client::query_dataset_tags(selected_dataset()) %>%
          dplyr::select("display", "name") %>%
          tibble::deframe(.)
      })

      tag_element_module_server <- shiny::reactive({
        shiny::req(tag_named_list())
        purrr::partial(
          tag_filter_element_server,
          tag_named_list = tag_named_list,
          dataset = selected_dataset
        )
      })

      tag_element_module_ui <- shiny::reactive(tag_filter_element_ui)

      tag_filter_output <- insert_remove_element_server(
        "tags_filter",
        element_module = tag_element_module_server,
        element_module_ui = tag_element_module_ui,
        remove_ui_event = shiny::reactive(selected_dataset())
      )

      valid_tag_filter_obj <- shiny::reactive({
        shiny::req(tag_filter_output())
        tag_filter_output() %>%
          shiny::reactiveValuesToList(.) %>%
          get_valid_tag_filters()
      })

      tag_filter_samples <- shiny::reactive({
        shiny::req(samples, selected_dataset())
        get_filtered_tag_samples(
          valid_tag_filter_obj(),
          samples(),
          selected_dataset()
        )
      })

      # # numeric_filters -------------------------------------------------------

      feature_named_list <- shiny::reactive({
        selected_dataset() %>%
          iatlas.api.client::query_features_by_class() %>%
          dplyr::select("class", "display", "feature" = "name") %>%
          create_nested_named_list()
      })

      numeric_element_module_server <- shiny::reactive({
        purrr::partial(
          numeric_filter_element_server,
          feature_named_list = feature_named_list,
          dataset = selected_dataset
        )
      })

      numeric_element_module_ui <- shiny::reactive(numeric_filter_element_ui)

      numeric_filter_output <- insert_remove_element_server(
        "numeric_filter",
        element_module = numeric_element_module_server,
        element_module_ui = numeric_element_module_ui,
        remove_ui_event = shiny::reactive(selected_dataset())
      )

      valid_numeric_filter_obj <- shiny::reactive({
        shiny::req(numeric_filter_output())
        numeric_filter_output() %>%
          shiny::reactiveValuesToList(.) %>%
          get_valid_numeric_filters()
      })

      numeric_filter_samples <- shiny::reactive({
        shiny::req(samples)
        iatlas.app::get_filtered_feature_samples(
          valid_numeric_filter_obj(),
          samples(),
          selected_dataset()
        )
      })

      selected_samples <- shiny::reactive({
        shiny::req(numeric_filter_samples(), tag_filter_samples())
        intersect(numeric_filter_samples(), tag_filter_samples())
      })

      output$samples_text <- shiny::renderText({
        c("Number of current samples:", length(selected_samples()))
      })

      filter_obj <- shiny::reactive({
        list(
          "samples" = selected_samples(),
          valid_numeric_filter_obj(),
          valid_tag_filter_obj()
        )
      })

      return(filter_obj)
    }
  )
}
