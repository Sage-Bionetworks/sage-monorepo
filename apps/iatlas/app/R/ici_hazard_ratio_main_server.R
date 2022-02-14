ici_hazard_ratio_main_server <- function(
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

      #store selected variables
      selected_vals <- shiny::reactiveValues(vars = c("IMPRES", "Vincent_IPRES_NonResponder"))
      observe({
        shiny::req(input$var2_cox)
        selected_vals$vars <- input$var2_cox
      })

      categories <- shiny::reactive(iatlas.api.client::query_tags(datasets = cohort_obj()[["dataset_names"]]) %>%
                                      dplyr::mutate(class = dplyr::case_when(
                                        tag_name %in% c( "Response", "Responder", "Progression", "Clinical_Benefit") ~ "Response to ICI",
                                        TRUE ~ "Treatment Data"))
      )

      features <- shiny::reactive({cohort_obj()$feature_tbl %>%
                                    dplyr::filter(!name %in% c("OS", "OS_time", "PFI_1", "PFI_time_1"))
        })

      shiny::observe({
        shiny::req(categories(), features())
        var_choices_clin <- create_nested_list_by_class(categories(),
                                                        class_column = "class",
                                                        internal_column = "tag_name",
                                                        display_column = "tag_short_display")

        var_choices_feat <- create_nested_list_by_class(features(),
                                                        class_column = "class",
                                                        internal_column = "name",
                                                        display_column = "display")

        var_choices <- c(var_choices_clin, var_choices_feat)

        shiny::updateSelectizeInput(session,
                          "var2_cox",
                          choices = var_choices,
                          selected = selected_vals$vars,
                          server = TRUE
                          )
      })

      mult_coxph <- shiny::reactive({
        switch(
          input$analysisvar,
          "uni_coxph" = FALSE,
          "mult_coxph" = TRUE
        )
      })

      status_column <- shiny::reactive({
        switch(
          input$timevar,
          "OS_time" = "OS",
          "PFI_time_1"= "PFI_1"
        )
      })

      dataset_displays <- reactive({
        setNames(cohort_obj()$dataset_displays, cohort_obj()$dataset_names)
      })

      #getting survival data of all ICI pre treatment samples
      OS_data <- shiny::reactive({
        iatlas.api.client::query_tag_samples(tags = "pre_sample_treatment") %>%
          dplyr::bind_rows(iatlas.api.client::query_cohort_samples(cohorts = "Prins_GBM_2019")) %>%
          dplyr::distinct(sample_name) %>%
          dplyr::inner_join(iatlas.api.client::query_feature_values(features = c("OS", "OS_time", "PFI_1", "PFI_time_1")),
                            by = c("sample_name" = "sample")) %>%
          dplyr::select(sample_name, feature_name, feature_value) %>%
          tidyr::pivot_wider(., names_from = feature_name, values_from = feature_value, values_fill = NA)
      })

      samples <- shiny::eventReactive(input$go_button, {
        shiny::req(OS_data())

        cohort_obj()$sample_tbl %>%
          dplyr::inner_join(., OS_data(), by = "sample_name") %>%
          dplyr::group_by(dataset_name) %>%
          dplyr::group_modify(~ dplyr::mutate(., has_surv_data = !all(is.na(.x[[input$timevar]])))) %>%
          dplyr::ungroup() %>%
          dplyr::filter(has_surv_data)
      })

      groups <- shiny::reactive(iatlas.api.client::query_tags_with_parent_tags(parent_tags = input$var2_cox))

      feature_df_mult <- shiny::eventReactive(input$go_button, {
        shiny::req(input$var2_cox, samples())
        shiny::validate(shiny::need(nrow(samples())>0, "Selected survival endpoint not available for selected dataset(s)"))

        #Let's assume that selected variables are a mix of features and tags, and do both queries
        new_feat <- iatlas.api.client::query_feature_values(features = input$var2_cox) %>%
          dplyr::select(sample, feature_name, feature_value) %>%
          tidyr::pivot_wider(names_from = feature_name, values_from = feature_value)

        if(sum(input$var2_cox %in% categories()$tag_name)>0){
          new_tags <- iatlas.api.client::query_tag_samples(parent_tags = input$var2_cox) %>%
             dplyr::inner_join(groups(), by = "tag_name") %>%
            dplyr::select(sample_name, tag_name, parent_tag_name) %>%
            tidyr::pivot_wider(names_from = parent_tag_name, values_from = tag_name)

          new_feat <- dplyr::inner_join(new_feat, new_tags, by = c("sample" = "sample_name"))
        }

        samples() %>%
          dplyr::inner_join(new_feat, by = c("sample_name" = "sample"))
      })


      dataset_ft <- shiny::eventReactive(input$go_button, {
        shiny::req(input$var2_cox, feature_df_mult())
        #creates a df with the dataset x feature combinations that are available
        get_feature_by_dataset(
          features = input$var2_cox,
          feature_df = features(),
          group_df = groups(),
          fmx_df = feature_df_mult(),
          datasets = cohort_obj()[["dataset_names"]],
          dataset_display = dataset_displays()
        )
      })

      coxph_df <- shiny::eventReactive(input$go_button, {
        shiny::req(input$var2_cox, dataset_ft())
        build_coxph_df(datasets = cohort_obj()[["dataset_names"]],
                                   data = feature_df_mult(),
                                   feature = input$var2_cox,
                                   time = input$timevar,
                                   status = status_column(),
                                   ft_labels = dataset_ft(),
                                   multivariate = mult_coxph())
      })

      output$mult_forest <- plotly::renderPlotly({
        shiny::validate(need(length(input$var2_cox)>0, "Select at least one variable."))
        all_forests <- purrr::map(.x = unique(coxph_df()$dataset),
                                  .f = build_forestplot_dataset,
                                  coxph_df = coxph_df(),
                                  xname = "log10(Hazard Ratio) + 95% CI")

        if(length(unique(coxph_df()$group)) == 1){
          plotly::subplot(all_forests, nrows = dplyr::n_distinct(coxph_df()$dataset), shareX = TRUE, titleX = TRUE, titleY= TRUE, margin = 0.01)
        }else{
          npannel <- ((dplyr::n_distinct(coxph_df()$dataset)+1)%/%2)
          plotly::subplot(all_forests, nrows = npannel, titleX = TRUE, titleY = TRUE, margin = 0.09)
        }
      })

      output$mult_heatmap <- plotly::renderPlotly({
        shiny::validate(need(length(input$var2_cox)>0, "Select at least one variable."))

        heatmap_df <-  build_heatmap_df(coxph_df())

        p <- create_heatmap(heatmap_df, "heatmap", scale_colors = T, legend_title = "log10(Hazard Ratio)")

        if(mult_coxph() == FALSE & length(input$var2_cox)>1){
          p <- add_BH_annotation(coxph_df(), p)
        }
        p
      })

      summary_table <- shiny::reactive({

        if(mult_coxph() == FALSE){ #for univariable models, we need to display the FDR results
          coxph_df() %>%
            dplyr::select(dataset_display, ft_label, group_label, logHR, loglower, logupper,  pvalue, logpvalue, FDR) %>%
            dplyr::rename(Dataset = dataset_display, Feature = ft_label, Variable = group_label, `log10(HR)` = logHR, `p.value` = pvalue, `Neg(log10(p.value))` = logpvalue, `BH FDR` = FDR) %>%
            dplyr::mutate_if(is.numeric, formatC, digits = 3) %>%
            dplyr::arrange(Dataset)
        }else{
          coxph_df() %>%
            dplyr::select(dataset_display, ft_label, group_label, logHR, loglower, logupper,  pvalue, logpvalue) %>%
            dplyr::rename(Dataset = dataset_display, Feature = ft_label, Variable = group_label, `log10(HR)` = logHR, `p.value` = pvalue, `Neg(log10(p.value))` = logpvalue) %>%
            dplyr::mutate_if(is.numeric, formatC, digits = 3) %>%
            dplyr::arrange(dataset_display)
        }
      })

      output$stats_summary <- DT::renderDataTable(summary_table())

      output$download_stats <- downloadHandler(
        filename = function() stringr::str_c("HR_results-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(summary_table(), con)
      )

      shiny::observeEvent(input$method_link,{
        shiny::showModal(modalDialog(
          title = "Method",
          includeMarkdown("inst/markdown/cox_regression.markdown"),
          easyClose = TRUE,
          footer = NULL
        ))
      })

      missing_os <- shiny::reactive({
        shiny::req(samples())

        ds_with_os <- unique(coxph_df()$dataset_display)

        if(dplyr::n_distinct(cohort_obj()$group_tbl$dataset_display) != length(ds_with_os)){
          setdiff(cohort_obj()$group_tbl$dataset_display, ds_with_os)
        }
      })

      output$notification <- shiny::renderText({
        shiny::req(missing_os())
        if(length(missing_os()) == 0){#no notification to display
          ""
        }else{
          paste0("Selected survival endpoint not available for ", missing_os(), collapse = "<br>")
        }
      })
    }
  )
}
