cohort_manual_selection_server <- function(id){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      #TODO: change back to TCGA
      default_dataset <- "PCAWG"

      selected_dataset <- cohort_dataset_selection_server(
        "dataset_selection",
        default_dataset
      )

      dedupe <- function(r) {
        shiny::makeReactiveBinding("val")
        shiny::observe(val <<- r(), priority = 10)
        shiny::reactive(val)
      }

      dataset <- dedupe(shiny::reactive({
        req(default_dataset)
        if (is.null(selected_dataset())) return(default_dataset)
        else return(selected_dataset())
      }))

      filter_object <- cohort_filter_selection_server("filter_selection", dataset)
      group_object <- cohort_group_selection_server("group_selection", dataset)
      cohort_object <- shiny::reactive({
        shiny::req(group_object(), filter_object())
        build_cohort_object_from_objects(group_object(), filter_object())
      })
      return(cohort_object)
    }
  )
}
