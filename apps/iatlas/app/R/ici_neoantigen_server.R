ici_neoantigen_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      #getting neoantigen data
      top_mhc_df <- shiny::reactive(iatlasGraphQLClient::query_neoantigens())

      count_df <- shiny::reactive(iatlasGraphQLClient::query_feature_values(feature_classes = "Neoantigen"))

      #vector with dataset labels for easy reference
      dataset_displays <- reactive({
        setNames(cohort_obj()$dataset_displays, cohort_obj()$dataset_names)
      })

      # #adding warning for user of dataset selected in ICI Cohort Selection that doesn't have neoantigen data in iAtlas
      output$excluded_dataset <- shiny::renderText({
        if(any(!cohort_obj()$sample_tbl$sample_name %in% unique(count_df()$sample))){
          absent_samples <- cohort_obj()$sample_tbl %>%
            dplyr::mutate(flag = dplyr::if_else(
              sample_name %in% count_df()$sample,
              1,
              0
            )) %>%
            dplyr::group_by(dataset_name) %>%
            dplyr::summarise(total_samples = dplyr::n_distinct(sample_name),
                             n_included = sum(flag)) %>%
            dplyr::mutate(text = glue::glue("<li>Dataset {dataset_displays()[dataset_name]} has {n_included} samples from a total of {total_samples} included in this module."))

          paste(
            "<ul><i> There are samples selected in ICI Cohort Selection that do not have neoantigen data present in this module: </i><br>",
            paste(absent_samples$text, collapse = "</li>"),
            "</ul>"
          )
        }else{
          ""
        }
      })

      plot_legend <- shiny::reactive({
        tbl <- dplyr::distinct(
          data.frame(
            b = cohort_obj()$plot_colors,
            a = names(cohort_obj()$plot_colors)
          ))

        DT::datatable(
          tbl,
          rownames = FALSE,
          class = "",
          callback = DT::JS("$('table.dataTable.no-footer').css('border-bottom', 'none');"),
          options = list(
            dom = 't',
            lengthChange = FALSE,
            headerCallback = DT::JS(
              "function(thead, data, start, end, display){",
              "  $(thead).remove();",
              "}")
          )
        ) %>%
          DT::formatStyle('b', backgroundColor = DT::styleEqual(tbl$b, tbl$b)) %>%
          DT::formatStyle('b', color = DT::styleEqual(tbl$b, tbl$b))

      })

      ici_neoantigen_classes_server(
        "ici_neoantigen_classes",
        cohort_obj,
        count_df(),
        shiny::reactive(dataset_displays()),
        shiny::reactive(plot_legend())
      )
      ici_neoantigen_correlations_server(
        "ici_neoantigen_correlations",
        cohort_obj,
        count_df(),
        shiny::reactive(dataset_displays())
      )
      ici_neoantigen_frequency_server(
        "ici_neoantigen_frequency",
        cohort_obj,
        top_mhc_df(),
        shiny::reactive(dataset_displays()),
        shiny::reactive(plot_legend())
      )

      shiny::observeEvent(input$method_link,{
        shiny::showModal(modalDialog(
          title = "Method",
          includeMarkdown("inst/markdown/methods/neoantigen-prediction.markdown"),
          easyClose = TRUE,
          footer = NULL
        ))
      })
    }
  )
}
