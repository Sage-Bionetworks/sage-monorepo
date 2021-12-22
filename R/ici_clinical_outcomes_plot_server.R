ici_clinical_outcomes_plot_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$excluded_dataset <- shiny::renderText({
        if(identical(unique(cohort_obj()$group_tbl$dataset_display), cohort_obj()$dataset_displays)){
          ""
        }else{
          excluded_datasets <- setdiff(cohort_obj()$dataset_displays, unique(cohort_obj()$group_tbl$dataset_display))
          paste(
            paste(excluded_datasets, collapse = ", "),
            " not included because all samples were filtered in ICI Cohort Selection."
          )
        }
      })

      feature_df <- shiny::reactive({
        pre_treat_samples <- iatlas.api.client::query_tag_samples(cohorts = cohort_obj()[["dataset_names"]], tags = "pre_sample_treatment") %>%
          dplyr::bind_rows(iatlas.api.client::query_cohort_samples(cohorts = "Prins_GBM_2019")) %>%
          dplyr::select(sample_name)

        cohort_obj()$sample_tbl %>%
          dplyr::inner_join(pre_treat_samples, by = "sample_name") %>%
          dplyr::inner_join(iatlas.api.client::query_feature_values(features = c("OS", "OS_time", "PFI_1", "PFI_time_1")), by = c("sample_name" = "sample"))
      })

      all_survival <- shiny::reactive({
       shiny::req(!is.null(feature_df()), cancelOutput = T)

       df <- purrr::map(.x = unique(cohort_obj()$group_tbl$dataset_name), df = feature_df(), .f= function(dataset, df){
          dataset_df <- df %>%
            dplyr::filter(dataset_name == dataset)

          if(!all(is.na(dataset_df$group_name)) & dplyr::n_distinct(dataset_df$group_name)>1){
            build_survival_df(
              df = dataset_df,
              group_column = "group_name",
              time_column = input$timevar
            )
           }
        })
        names(df) <- unique(cohort_obj()$group_tbl$dataset_display)
        Filter(Negate(is.null), df)
      })

      all_fit <- shiny::reactive({
        shiny::req(all_survival())
        shiny::validate(need(length(all_survival())>0, "Variable has only one level in the selected dataset(s). Select other datasets or check ICI Datasets Overview for more information."))
        purrr::map(all_survival(), function(df) survival::survfit(survival::Surv(time, status) ~ measure, data = df))
      })

      all_kmplot <- shiny::reactive({
        shiny::req(all_fit(), all_survival())


        create_kmplot(
          fit = all_fit(),
          df = all_survival(),
          confint = input$confint,
          risktable = input$risktable,
          title = names(all_survival()),
          group_colors = get_group_colors(cohort_obj()),
          facet = TRUE)
      })

      # survminer::ggsurvplot_list object does not work if using a for loop, or
      # purrr::map, or lapply
      output$plots <- shiny::renderUI({
        shiny::tagList(
          shiny::renderPlot(all_kmplot()[1]),
          shiny::renderPlot(all_kmplot()[2]),
          shiny::renderPlot(all_kmplot()[3]),
          shiny::renderPlot(all_kmplot()[4]),
          shiny::renderPlot(all_kmplot()[5]),
          shiny::renderPlot(all_kmplot()[6]),
          shiny::renderPlot(all_kmplot()[7]),
          shiny::renderPlot(all_kmplot()[8]),
          shiny::renderPlot(all_kmplot()[9]),
          shiny::renderPlot(all_kmplot()[10]),
          shiny::renderPlot(all_kmplot()[11]),
          shiny::renderPlot(all_kmplot()[12])
        )

      })

      missing_plot <- shiny::reactive({
        shiny::req(all_fit(), feature_df())

        if(length(all_survival())>0 & dplyr::n_distinct(cohort_obj()$group_tbl$dataset_name) != length(all_survival())){ #some dataset has only one category for the selected grouping variable

          missing_datasets <- setdiff(cohort_obj()$group_tbl$dataset_display, names(all_survival()))

          #check if there is survival annotation or more than one group level for the missing dataset
          missing_annot <- purrr::map_df(.x = missing_datasets, function(x){

            surv_data <- feature_df() %>%
              dplyr::filter(dataset_name == x)

            if(nrow(surv_data) == 0) c(dataset = x,
                                       error = "Selected survival endpoint not available for ",
                                       variable = input$timevar)
            else if(dplyr::n_distinct(surv_data$group_name) == 1) c(dataset = x,
                                                                    error = "Selected variable has only one level for ",
                                                                    variable = cohort_obj()[["group_display"]])
          })
        }
      })

      output$notification <- shiny::renderText({
        shiny::req(missing_plot())
        if(length(cohort_obj()[["dataset_names"]]) == length(all_survival()) | length(all_survival()) == 0){#no notification to display
          ""
        }else{
          paste0(missing_plot()$error, missing_plot()$dataset, collapse = "<br>")
        }
      })

    }
  )
}
