cohort_manual_selection_server <- function(
  id
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      #TODO: change back to TCGA
      default_dataset <- "PCAWG"

      selected_dataset <- cohort_dataset_selection_server(
        "cohort_dataset_selection",
        default_dataset
      )

      dedupe <- function(r) {
        shiny::makeReactiveBinding("val")
        shiny::observe(val <<- r(), priority = 10)
        shiny::reactive(val)
      }

      dataset <- dedupe(shiny::reactive({
        if (is.null(selected_dataset())) {
          return(default_dataset)
        } else {
          return(selected_dataset())
        }
      }))

      dataset_samples <- shiny::reactive({
        shiny::req(dataset())
        iatlas.api.client::query_dataset_samples(dataset()) %>%
          dplyr::pull("name")
      })

      filter_obj <- cohort_filter_selection_server(
        "cohort_filter_selection",
        dataset,
        dataset_samples
      )

      cohort_obj <- cohort_group_selection_server(
        "cohort_group_selection",
        filter_obj,
        dataset
      )

      return(cohort_obj)
    }
  )
}
