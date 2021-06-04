cohort_group_selection_server <- function(id, selected_dataset) {
  shiny::moduleServer(
    id,
    function(input, output, session) {
      ns <- session$ns

      # Select group type ----

      tag_group_tbl <- shiny::reactive({
        shiny::req(selected_dataset())
        selected_dataset() %>%
          iatlas.api.client::query_dataset_tags() %>%
          dplyr::select("display" = "short_display", "name")
      })

      custom_group_tbl <- shiny::reactive({
        shiny::req(selected_dataset())
        build_custom_group_tbl(selected_dataset())
      })

      clinical_group_tbl <- shiny::reactive({
        shiny::req(selected_dataset())
        selected_dataset() %>%
          iatlas.api.client::query_patients(datasets = .) %>%
          dplyr::select("ethnicity", "gender", "race") %>%
          tidyr::pivot_longer(cols = dplyr::everything()) %>%
          tidyr::drop_na() %>%
          dplyr::select("name") %>%
          dplyr::distinct() %>%
          dplyr::mutate(
            "display" = stringr::str_replace_all(.data$name, "_", " "),
            "display" = stringr::str_to_title(.data$display)
          ) %>%
          dplyr::select("display", "name")

      })

      available_groups_list <- shiny::reactive({
        shiny::req(tag_group_tbl(), custom_group_tbl(), clinical_group_tbl())
        build_cohort_group_list(
          tag_group_tbl(),
          custom_group_tbl(),
          clinical_group_tbl()
        )
      })

      default_group <- shiny::reactive({
        shiny::req(available_groups_list())
        available_groups_list()[[1]]
      })

      output$select_group_ui <- shiny::renderUI({
        shiny::req(available_groups_list(), default_group())
        shiny::selectInput(
          inputId = ns("group_choice"),
          label = shiny::strong("Select or Search for Grouping Variable"),
          choices = available_groups_list(),
          selected = default_group()
        )
      })

      dedupe <- function(r) {
        shiny::makeReactiveBinding("val")
        shiny::observe(val <<- r(), priority = 10)
        shiny::reactive(val)
      }

      group_choice <- dedupe(shiny::reactive({
        req(default_group())
        if (is.null(input$group_choice)) return(default_group())
        else return(input$group_choice)
      }))

      # Driver Mutations ----

      display_driver_mutation_ui <- shiny::reactive({
        shiny::req(group_choice())
        group_choice() == "Driver Mutation"
      })

      # This is so that the conditional panel can see the various shiny::reactives
      output$display_driver_mutation <- shiny::reactive({
        display_driver_mutation_ui()
      })

      shiny::outputOptions(
        output,
        "display_driver_mutation",
        suspendWhenHidden = FALSE
      )

      mutation_tbl <- shiny::reactive(build_cohort_mutation_tbl())

      output$select_driver_mutation_group_ui <- shiny::renderUI({
        shiny::req(group_choice() == "Driver Mutation", mutation_tbl())
        shiny::selectInput(
          inputId  = ns("driver_mutation_choice_id"),
          label    = "Select or Search for Driver Mutation",
          choices  = mutation_tbl() %>%
            dplyr::select("mutation", "id") %>%
            tibble::deframe(.)
        )
      })


      # Immune feature bins ----

      display_immune_feature_bins_ui <- shiny::reactive({
        shiny::req(group_choice())
        group_choice() == "Immune Feature Bins"
      })

      output$display_immune_feature_bins <- shiny::reactive({
        display_immune_feature_bins_ui()
      })

      shiny::outputOptions(
        output,
        "display_immune_feature_bins",
        suspendWhenHidden = FALSE
      )

      feature_bin_tbl <- shiny::reactive({
        shiny::req(group_choice() == "Immune Feature Bins", selected_dataset())
        selected_dataset() %>%
          iatlas.api.client::query_features() %>%
          dplyr::select("class", "display", "name")
      })

      # TODO: use sample names from feature object to query features, not dataset
      output$select_immune_feature_bins_group_ui <- shiny::renderUI({
        shiny::req(feature_bin_tbl())

        shiny::selectInput(
          inputId = ns("bin_immune_feature_choice"),
          label = "Select or Search for feature",
          choices = iatlas.modules::create_nested_named_list(
            feature_bin_tbl(), values_col = "name"
          )
        )
      })

      # Group Object ----

      group_object <- shiny::reactive({
        shiny::req(selected_dataset(), group_choice())

        group_object <- list(
          "dataset" = selected_dataset(),
          "group_name" = group_choice()
        )

        if (group_choice() == "Driver Mutation") {
          shiny::req(input$driver_mutation_choice_id)
          group_object$group_display <- "Driver Mutation"
          group_object$group_type <- "custom"
          group_object$mutation_id <- as.integer(
            input$driver_mutation_choice_id
          )
        } else if (group_choice() == "Immune Feature Bins") {
          shiny::req(
            input$bin_immune_feature_choice,
            input$bin_number_choice
          )
          group_object$group_display <- "Immune Feature Bins"
          group_object$group_type <- "custom"
          group_object$bin_immune_feature <- input$bin_immune_feature_choice
          group_object$bin_number <- input$bin_number_choice
        } else if (group_choice() %in% c("gender", "race", "etnicity")) {
          group_object$group_display <- group_choice() %>%
            stringr::str_replace_all("_", " ") %>%
            stringr::str_to_title(.)
          group_object$group_type <- "clinical"
        } else {
          group_object$group_display <- group_choice() %>%
            iatlas.api.client::query_tags(tags = .) %>%
            dplyr::pull("short_display")
          group_object$group_type <- "tag"
        }


        return(group_object)
      })

      return(group_object)
    }
  )
}
