ici_clinical_outcomes_plot_server <- function(
  id
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      #getting dropdown menu options
      ici_datasets <- shiny::reactive({
        x <- iatlas.api.client::query_datasets(types = "ici")
        setNames(as.character(x$name), x$display)
        })

      categories <- shiny::reactive(iatlas.api.client::query_tags(datasets = ici_datasets()) %>%
                                      dplyr::mutate(class = dplyr::case_when(
                                        tag_name %in% c( "Response", "Responder", "Progression", "Clinical_Benefit") ~ "Response to ICI",
                                        TRUE ~ "Treatment Data"))
      )

      features <- shiny::reactive(iatlas.api.client::query_features(cohorts = ici_datasets()) %>% dplyr::filter(!class %in% c( "Survival Status", "Survival Time")))


      output$list_datasets <- shiny::renderUI({
        checkboxGroupInput(ns("datasets"), "Select Datasets", choices = ici_datasets(),
                           selected =  c("Gide_Cell_2019", "HugoLo_IPRES_2016"))
      })


      output$survplot_op <- shiny::renderUI({
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

        shiny::selectInput(
          ns("var1_surv"),
          "Variable",
          var_choices,
          selected = "IMPRES"
        )
      })

      #getting survival data of all ICI pre treatment samples
      OS_data <- shiny::reactive({
        #shiny::req(input$surv1)
        samples <- iatlas.api.client::query_tag_samples(tags = "pre_sample_treatment")
        os <- iatlas.api.client::query_feature_values(features = c("OS", "OS_time", "PFI_1", "PFI_time_1"))

        dplyr::inner_join(samples, os, by = c("sample_name" = "sample")) %>%
          dplyr::select(sample_name, feature_name, feature_value) %>%
          tidyr::pivot_wider(., names_from = feature_name, values_from = feature_value, values_fill = NA)
      })

      feature_df <- shiny::reactive({
        shiny::validate(need(!is.null(input$datasets), "Select at least one dataset."))
        shiny::req(input$var1_surv)
        shiny::req(OS_data())

        #getting samples from selected datasets
        samples <- iatlas.api.client::query_dataset_samples(datasets = input$datasets) %>%
          dplyr::inner_join(., OS_data(), by = "sample_name")

        if(input$var1_surv %in% features()$name) new_feat <- iatlas.api.client::query_feature_values(features = input$var1_surv) %>%
                                                                  dplyr::select(sample_name = sample, sample_groups = feature_value) %>%
                                                                  dplyr::mutate(color = " ")
        else new_feat <- iatlas.api.client::query_tag_samples(parent_tags = input$var1_surv) %>%
                                    dplyr::select(sample_name, sample_groups = tag_short_display, color = tag_color)

        dplyr::inner_join(samples, new_feat, by = "sample_name")
      })


      all_survival <- shiny::reactive({
        shiny::req(input$var1_surv, !is.null(feature_df()), cancelOutput = T)

        df <- purrr::map(.x = input$datasets, df = feature_df(), .f= function(dataset, df){

          dataset_df <- df %>%
            dplyr::filter(dataset_name == dataset)

          if(!all(is.na(dataset_df$sample_groups)) & !all(is.na(dataset_df[[input$timevar]])) & dplyr::n_distinct(dataset_df$sample_groups)>1){
            build_survival_df(
              df = dataset_df,
              group_column = "sample_groups",
              time_column = input$timevar,
              k = input$divk,
              div_range = input$div_range
            )
          }
        })

        names(df) <- names(ici_datasets())[ici_datasets() %in% input$datasets]
        Filter(Negate(is.null), df)
      })

      all_fit <- shiny::reactive({
        shiny::validate(need(length(all_survival())>0, "Variable not annotated in the selected dataset(s). Select other datasets or check ICI Datasets Overview for more information."))
        purrr::map(all_survival(), function(df) survival::survfit(survival::Surv(time, status) ~ variable, data = df))
      })

      all_kmplot <- shiny::reactive({

        if (input$var1_surv %in% categories()$name) {

          colors <- feature_df() %>%
            dplyr::select(color, sample_groups) %>%
            dplyr::distinct() %>%
            dplyr::mutate(color = replace(color, is.na(color), randomcoloR::randomColor(dplyr::n())))

          group_colors <- colors$color
          names(group_colors) <- sapply(colors$sample_groups, function(a) paste('variable=',a,sep=''))

        } else if(input$div_range == "median") {
          group_colors <- viridisLite::viridis(2)
        }else{
          group_colors <- viridisLite::viridis(input$divk)
        }

        create_kmplot(
          fit = all_fit(),
          df = all_survival(),
          confint = input$confint,
          risktable = input$risktable,
          title = names(all_survival()),
          group_colors = group_colors,
          facet = TRUE)
      })

      #the KM Plots are stored as a list, so a few adjustments are necessary to plot everything
      shiny::observe({
        output$plots <- shiny::renderUI({
          shiny::req(input$var1_surv)

          plot_output_list <-
            lapply(1:length(all_survival()), function(i) {
              plotname <- names(all_survival())[i]
              plotOutput(ns(plotname), height = 600)
            })
          do.call(tagList, plot_output_list)
        })
      })

      shiny::observe({
        lapply(1:length(all_survival()), function(i){
          my_dataset <- names(all_survival())[i]
          output[[my_dataset]] <- shiny::renderPlot({
            shiny::req(input$var1_surv, all_kmplot())
            all_kmplot()[i]
          })
        })
      })

      shiny::observeEvent(all_survival(),{
        shiny::req(all_fit(), feature_df())

        if(length(all_survival())>0 & length(input$datasets) != length(all_survival())){ #some dataset has only one category for the selected grouping variable

          missing_datasets <- setdiff(input$datasets, ici_datasets()[names(all_survival())])

          #check if there is survival annotation or mroe than one group level for the missing dataset
          missing_annot <- purrr::map_df(.x = missing_datasets, function(x){

            surv_data <- feature_df() %>%
              dplyr::filter(dataset_name == x) %>%
              dplyr::select(input$timevar, sample_groups)

            if(all(is.na(surv_data[[input$timevar]]))) c(dataset = names(ici_datasets())[ici_datasets() == x],
                                                         error = "Selected survival endpoint not available for ",
                                                         variable = input$timevar)
            else if(dplyr::n_distinct(surv_data$sample_groups) == 1) c(dataset = names(ici_datasets())[ici_datasets() == x],
                                                                       error = "Selected variable has only one level for ",
                                                                       variable = input$var1_surv)
          })
          output$notification <- shiny::renderText({
              paste0(missing_annot$error, missing_annot$dataset, collapse = "<br>")
          })
        }
        if(length(input$datasets) == length(all_survival()) | length(all_survival()) == 0){ #no notification to display
          output$notification <- renderUI({
          })
        }
      })
    }
  )
}
