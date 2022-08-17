ici_neoantigen_frequency_server <- function(
  id,
  cohort_obj
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      #get the count data for the samples in the cohort_obj
      top_mhc_df <- shiny::reactive(arrow::read_feather("inst/feather/neoantigen_top_pmhc.feather"))

      clicked_point <- shiny::reactiveValues(ev="")

      output$gene_selection <- shiny::renderUI({
        shiny::req(top_mhc_df())
        shiny::selectInput(ns("gene"),
                           "Select Gene:",
                           choices = c("All", unique(top_mhc_df()$gene_name)),
                           selected = "All")
      })

      plot_df <- shiny::reactive({
        shiny::req(input$gene)
        df <- cohort_obj()$sample_tbl %>%
          dplyr::inner_join(iatlasGraphQLClient::query_sample_patients(), by = "sample_name") %>%
          dplyr::inner_join(top_mhc_df(), by = "patient_name")

        if(input$gene != "All") df <- dplyr::filter(df, gene_name == input$gene)

        df %>%
          dplyr::group_by(dataset_name, pmhc, group_name) %>%
          dplyr::summarise(n = dplyr::n(), n_pat= dplyr::n_distinct(patient_name), .groups = "keep") %>%
          dplyr::mutate(text = paste0(pmhc, "\n Group: ", group_name)) %>%
          dplyr::mutate(highlight = ifelse(pmhc == clicked_point$ev, 1, 0))
      })

      dataset_displays <- reactive({
        setNames(cohort_obj()$dataset_displays, cohort_obj()$dataset_names)
      })

      all_plots <- shiny::reactive({
        shiny::req(plot_df())

        purrr::map(cohort_obj()$dataset_names, function(x){
          dataset_df <-  plot_df() %>%
            dplyr::filter(dataset_name == x) %>%
            dplyr::mutate(n = jitter(n), n_pat = jitter(n_pat))

          if(nrow(dataset_df)>0){
            p <- ggplot2::ggplot(dataset_df, aes(x=n, y=n_pat, text=text, key = pmhc)) +
                               geom_point(data=subset(dataset_df, highlight==0), aes(colour = group_name)) +
                               scale_color_manual("Group", values = (cohort_obj()$plot_colors))+
                               theme_bw() +
                               theme(
                                 legend.position="none",
                                 panel.border = element_blank(),
                                 panel.grid.major.x = element_blank(),
                                 panel.grid.minor.x = element_blank(),
                                 plot.margin = unit(c(1, 0.5, 0, 0), "cm")
                               )+
                               labs(
                                 y = "Number of patients with pMHC",
                                 x = "Frequency of pMHC")


            if(sum(dataset_df$highlight) != 0) p <- p + geom_point(data=subset(dataset_df, highlight==1), color="black", shape = "diamond", size=4)

            plotly::ggplotly(p,
                             tooltip="text",
                             source = "neo_plot") %>%
                    add_title_subplot_plotly(unname(dataset_displays()[x]))
          }
        }) %>% Filter(Negate(is.null),.)
      })

      output$frequency_plot <- shiny::renderUI({
        shiny::req(all_plots())
        n_rows = (length(all_plots())+1)%/%2
        box_height = paste0(n_rows*300, "px")

        plotly::plotlyOutput(ns("neoantigen_frequency_plot"), height = box_height)
      })

      output$neoantigen_frequency_plot <- plotly::renderPlotly({
        shiny::req(all_plots())
        plotly::subplot(all_plots(), nrows = (length(all_plots())+1)%/%2, margin = 0.06, shareX = TRUE, shareY = TRUE)
      })

      output$legend <-  DT:: renderDT({
        tbl <- dplyr::distinct(
          data.frame(
            b = cohort_obj()$plot_colors,
            a = names(cohort_obj()$plot_colors)
          ))

        DT::datatable(
          tbl,
          rownames = FALSE,
          options = list(
            dom = 't',
            headerCallback = DT::JS(
              "function(thead, data, start, end, display){",
              "  $(thead).remove();",
              "}")
          )
        ) %>%
          DT::formatStyle('b', backgroundColor = DT::styleEqual(tbl$b, tbl$b)) %>%
          DT::formatStyle('b', color = DT::styleEqual(tbl$b, tbl$b))
      })


      #adding interactivity to select a point from the plot and render table with data for all groups
      observe({
        shiny::req(all_plots())
        eventdata <- plotly::event_data( "plotly_click", source = "neo_plot")
        if(is.null(eventdata)){
          clicked_point$ev <- ""
        }else{
          clicked_point$ev  <- eventdata$key
        }
      })

      output$pmhc_tbl <- DT::renderDT({
        shiny::req(all_plots())
        shiny::validate(
          shiny::need(unique(clicked_point$ev) %in% plot_df()$pmhc, "")
        )

        DT::datatable(
          plot_df() %>%
            dplyr::filter(pmhc == clicked_point$ev) %>%
            dplyr::mutate(Dataset = dataset_displays()[dataset_name]) %>%
            dplyr::ungroup() %>%
            dplyr::select(
              Dataset,
              Group = group_name,
              n,
              n_pat
            ),
          rownames = FALSE,
          caption = unique(clicked_point$ev),
          options = list(dom = 't')
        )
      })
    }
  )
}
