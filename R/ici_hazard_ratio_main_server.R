ici_hazard_ratio_main_server <- function(
  id
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      output$heatmap_op <- shiny::renderUI({

        non_selected_ds <- paste("Clinical data for", setdiff(unlist(datasets_options), input$datasets_mult), sep = " ")

        clin_data <- ioresponse_data$feature_df %>%
          dplyr::filter(FeatureMatrixLabelTSV != "treatment_when_collected" &
                          FeatureMatrixLabelTSV %in% ioresponse_data$categories_df$Category &
                          !`Variable Class` %in% non_selected_ds)

        var_choices_clin <- iatlas.app::create_filtered_nested_list_by_class(feature_df = clin_data,
                                                                             filter_value = "Categorical",
                                                                             class_column = "Variable Class",
                                                                             internal_column = "FeatureMatrixLabelTSV",
                                                                             display_column = "FriendlyLabel",
                                                                             filter_column = "VariableType")

        var_choices_feat <- iatlas.app::create_filtered_nested_list_by_class(feature_df = ioresponse_data$feature_df %>% dplyr::filter(`Variable Class` != "NA"),
                                                                             filter_value = "Numeric",
                                                                             class_column = "Variable Class",
                                                                             internal_column = "FeatureMatrixLabelTSV",
                                                                             display_column = "FriendlyLabel",
                                                                             filter_column = "VariableType")
        var_choices <- c(var_choices_clin, var_choices_feat)

        shiny::selectizeInput(
                  ns("var2_cox"),
                  "Select or Search for variables",
                  var_choices,
                  selected = c("IMPRES", "Vincent_IPRES_NonResponder"),
                  multiple = TRUE
                )
      })

      datasets <- shiny::reactive({
        switch(
          input$timevar,
          "OS_time" = input$datasets_mult,
          "PFI_time_1"= input$datasets_mult[input$datasets_mult %in% datasets_PFI]
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

      feature_df_mult <- shiny::reactive({

        shiny::req(input$datasets_mult, input$var2_cox)

        ioresponse_data$fmx_df %>%
          dplyr::filter(Dataset %in% datasets()) %>%
          `if`(
            !("treatment_when_collected" %in% input$var2_cox),
            dplyr::filter(., treatment_when_collected == "Pre"),
            .
          ) %>%
          dplyr::select(Sample_ID, Dataset, input$timevar, status_column(), treatment_when_collected, dplyr::one_of(input$var2_cox))
      })

      dataset_ft <- shiny::reactive({
        shiny::req(input$datasets_mult, input$var2_cox)
        #creates a df with the dataset x feature combinations that are available

        iatlas.app::get_feature_by_dataset(
          datasets = datasets(),
          features = input$var2_cox,
          feature_df = ioresponse_data$feature_df,
          group_df = ioresponse_data$sample_group_df,
          fmx_df = feature_df_mult()
        )
      })

      coxph_df <- shiny::reactive({
        shiny::req(input$datasets_mult, input$var2_cox, dataset_ft())
        iatlas.app::build_coxph_df(datasets = datasets(),
                                   data = feature_df_mult(),
                                   feature = input$var2_cox,
                                   time = input$timevar,
                                   status = status_column(),
                                   ft_labels = dataset_ft(),
                                   multivariate = mult_coxph())
      })

      output$mult_forest <- plotly::renderPlotly({
        shiny::validate(need(!is.null(input$datasets_mult), "Select at least one dataset."))
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
        shiny::validate(need(!is.null(input$datasets_mult), "Select at least one dataset."))
        shiny::validate(need(length(input$var2_cox)>0, "Select at least one variable."))

        heatmap_df <-  iatlas.app::build_heatmap_df(coxph_df())

        p <- iatlas.app::create_heatmap(heatmap_df, "heatmap", scale_colors = T, legend_title = "log10(Hazard Ratio)")

        if(mult_coxph() == FALSE & length(input$var2_cox)>1){
          p <- iatlas.app::add_BH_annotation(coxph_df(), p)
        }
        p
      })

      summary_table <- shiny::reactive({

        if(mult_coxph() == FALSE){ #for univariable models, we need to display the FDR results
          coxph_df() %>%
            dplyr::select(dataset, ft_label, group_label, logHR, loglower, logupper,  pvalue, logpvalue, FDR) %>%
            dplyr::rename(Feature = ft_label, Variable = group_label, `log10(HR)` = logHR, `p.value` = pvalue, `Neg(log10(p.value))` = logpvalue, `BH FDR` = FDR) %>%
            dplyr::mutate_if(is.numeric, formatC, digits = 3) %>%
            dplyr::arrange(dataset)
        }else{
          coxph_df() %>%
            dplyr::select(dataset, ft_label, group_label, logHR, loglower, logupper,  pvalue, logpvalue) %>%
            dplyr::rename(Feature = ft_label, Variable = group_label, `log10(HR)` = logHR, `p.value` = pvalue, `Neg(log10(p.value))` = logpvalue) %>%
            dplyr::mutate_if(is.numeric, formatC, digits = 3) %>%
            dplyr::arrange(dataset)
        }
      })

      output$stats_summary <- DT::renderDataTable(summary_table())

      output$download_stats <- downloadHandler(
        filename = function() stringr::str_c("HR_results-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(summary_table(), con)
      )

      observeEvent(input$method_link,{
        showModal(modalDialog(
          title = "Method",
          includeMarkdown("inst/markdown/cox_regression.markdown"),
          easyClose = TRUE,
          footer = NULL
        ))
      })
    }
  )
}
