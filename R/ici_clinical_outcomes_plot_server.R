ici_clinical_outcomes_plot_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

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

       df <- purrr::map(.x = cohort_obj()[["dataset_names"]], df = feature_df(), .f= function(dataset, df){
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
          shiny::renderPlot(all_kmplot()[9])
        )

      })

    }
  )
}
