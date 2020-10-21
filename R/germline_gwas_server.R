germline_gwas_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      gwas_data <- reactive({
        GERMLINE_PATH = "inst/extdata/"
        feather::read_feather(paste0(GERMLINE_PATH, "tcga_germline_GWAS.feather"))
      })

      immune_feat <- reactive({

        gwas_data() %>%
          dplyr::select(display,`Annot.Figure.ImmuneCategory`) %>%
          dplyr::group_by(`Annot.Figure.ImmuneCategory`) %>%
          tidyr::nest(data = c(display))%>%
          dplyr::mutate(data = purrr::map(data, tibble::deframe)) %>%
          tibble::deframe()

      })

      output$features <- renderUI({
        shiny::selectizeInput(ns('immunefeature'), "Select Immune Feature(s)",
                              choices = immune_feat(),
                              selected = c("CD8 T cells", "NK cells"),
                              multiple = TRUE)
      })

      output$to_exclude <- renderUI({
        shiny::selectizeInput(ns('exclude_feat'), "Exclude Immune Feature (optional)",
                              choices = immune_feat(),
                              selected = c("MHC2 21978456", "Th17 cells"),
                              multiple = TRUE)
      })

      selected_chr <- reactive({
        switch(
          input$selection,
          "See all chromossomes" = c(1:22),
          "Select a region" = input$chr
        )
      })

      chr_size <- reactive({
        req(input$selection == "Select a region")
        c((min((gwas_data() %>% dplyr::filter(chr_col == selected_chr()))$bp_col)),
          (max((gwas_data() %>% dplyr::filter(chr_col == selected_chr()))$bp_col)))
      })

      #adding interactivity to select a region from zooming in the plot

      clicked_int <- reactiveValues(ev=NULL)

      observe({
        shiny::req(input$selection == "Select a region")
        #if a user resizes the browser, a new event data will be released, so let's guarantee that this will not affect the visualization
        if(!("width" %in% names(plotly::event_data("plotly_relayout", source = "gwas_mht", priority = "event")))){
          clicked_int$ev <- plotly::event_data("plotly_relayout", source = "gwas_mht", priority = "event")
        }
      })

      #reset region with change of chromossome or selection of all chromossomes
      toReset <- reactive({
        list(input$chr,
             input$selection)
      })

      observeEvent(toReset(), {
        clicked_int$ev <- NULL
      })

      #Creating the object that stores the selected range (either by zoom or manual input)

      chr_range <- reactiveValues(range = NULL)

      observe({
        shiny::req(input$selection == "Select a region",
                   chr_size())

        if(is.null(clicked_int$ev)){ #default option is min and max positions for the chromossome
          chr_range$range = chr_size()
        } else { #Update range after a zoom in the plot - need to compare with chr_size because plot has x-axis longer than chr_size
          chr_range$range = c((floor(max(chr_size()[1], clicked_int$ev$`xaxis.range[0]`))),
                              (ceiling(min(chr_size()[2], clicked_int$ev$`xaxis.range[1]`))))
        }
      })

      #Update range in case user manually enters an interval
      observeEvent(input$go_button, {

        if(input$reset_axes == TRUE){
          chr_range$range <- chr_size()
        }else{
          chr_range$range <- c(input$start,
                               input$end)
        }
      })


      output$link_genome <- renderUI({
        shiny::validate(
          shiny::need(!is.na(input$chr), "Select a chromossome.")
        )

        link_ucsc <- paste0("http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&position=chr", input$chr, ":", chr_range$range[1], "-", chr_range$range[2])
        shiny::verticalLayout(
          shiny::p(paste("Selected region: chr", input$chr, ":", scales::comma(chr_range$range[1]), "-", scales::comma(chr_range$range[2]))),
          shiny::p(a(href = link_ucsc, "View this region on UCSC Genome Browser"))
        )
      })

      output$xrange <- renderUI({
        shiny::verticalLayout(
          shiny::p("Update region of interest by zooming in the plot or by manually adding coordinates:"),
          shiny::splitLayout(
            shiny::numericInput(ns("start"), "Start", value= chr_range$range[1],
                                min = chr_size()[1],
                                max = chr_size()[2]),
            shiny::numericInput(ns("end"), "End", value= chr_range$range[2],
                                min = chr_size()[1],
                                max = chr_size()[2])
          ),
          shiny::checkboxInput(ns("reset_axes"), "See all GWAS hits in this chromossome"),
          shiny::actionButton(ns("go_button"), "Update")
        )
      })

      selected_min <- reactive({
        switch(
          input$selection,
          "See all chromossomes" = 1,
          "Select a region" = chr_range$range[1]
        )
      })

      selected_max <- reactive({
        switch(
          input$selection,
          "See all chromossomes" = 245246279,
          "Select a region" = chr_range$range[2]
        )
      })

      # Prepare the dataset
      toUpdate <- reactive({
        list(
          input$immunefeature,
          input$exclude_feat,
          input$only_selected,
          selected_chr(),
          input$go_button
        )
      })
      don <- eventReactive(toUpdate(),{
        shiny::req(input$immunefeature, selected_chr(), selected_min(), selected_max())

        if(input$only_selected == 0 & !is.null(input$exclude_feat)) gwas_df <- gwas_data() %>% filter(!(display %in% input$exclude_feat))
        else if(input$only_selected == 1) gwas_df <- gwas_data() %>% filter(display %in% input$immunefeature)
        else gwas_df <- gwas_data()

        if(input$only_selected == 0) gwas_df <- gwas_df %>% dplyr::mutate(is_highlight=ifelse(display %in% input$immunefeature, "yes", "no"))
        else gwas_df <- gwas_df %>% dplyr::mutate(is_highlight = "no")

        build_manhattanplot_tbl(
          gwas_df = gwas_df,
          chr_selected = selected_chr(),
          bp_min = selected_min(),
          bp_max = selected_max(),
          feature_selected = input$immunefeature)
      })

      axisdf <- reactive({# Prepare X axis
        shiny::req(don())
        if(input$selection == "See all chromossomes"){
          don() %>%
            dplyr::group_by(chr_col) %>%
            dplyr::summarize(center=( max(BPcum) + min(BPcum) ) / 2 ) %>%
            dplyr::rename(label = chr_col)
        }else{
          breaks <- c(chr_range$range[1], (chr_range$range[1] + chr_range$range[2])/2, chr_range$range[2])
          data.frame(
            label = paste(format(round(breaks / 1e6, 1), trim = TRUE), "Mb"),
            center = breaks,
            stringsAsFactors = FALSE
          )
        }
      })

      x_title <- reactive({
        if(input$selection == "See all chromossomes") "Chromossome"
        else(paste("chr", selected_chr()))
      })

      output$mht_plot <- plotly::renderPlotly({
        shiny::req(don(), axisdf())

        don() %>%
          dplyr::filter(chr_col %in% selected_chr()) %>%
          dplyr::filter(bp_col >= selected_min() & bp_col <= selected_max()) %>%
          create_manhattanplot(
            df = .,
            x_label = axisdf(),
            y_min = input$yrange[1],
            y_max = input$yrange[2],
            x_name = x_title(),
            y_name = "- log10(p-value)",
            source_name = "gwas_mht"
          )
      })

    }
  )
}
