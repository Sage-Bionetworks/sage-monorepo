germline_gwas_server <- function(
    input,
    output,
    session,
    cohort_obj
){


    ns <- session$ns

    gwas_data <- reactive({
      GERMLINE_PATH = "inst/extdata/"
      feather::read_feather(paste0(GERMLINE_PATH, "GWAS_results.feather"))
    })


    output$features <- renderUI({
      shiny::selectizeInput(ns('immunefeature'), "Select Immune Feature", choices = sort(unique(gwas_data()$feature_col)))
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
           # input$go_button)
    })

    observeEvent(toReset(), {
      clicked_int$ev <- NULL
    })

    #Creating the object that stores the selected range (either by zoom or manual input)

    chr_range <- reactiveValues(range = NULL)

    observe({
      shiny::req(input$selection == "Select a region")

      if(is.null(clicked_int$ev)){ #default option is min and max positions for the chromossome
        chr_range$range = chr_size()
       } else { #Update range after a zoom in the plot - need to compare with chr_size because plot has x-axis longer than chr_size
         chr_range$range = c((floor(max(chr_size()[1], clicked_int$ev$`xaxis.range[0]`))),
                             (ceiling(min(chr_size()[2], clicked_int$ev$`xaxis.range[1]`))))
      }
    })

    #Update range in case user manually enters an interval
    observeEvent(input$go_button, {
      chr_range$range <- c(input$start,
                           input$end)
    })


    output$link_genome <- renderUI({
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
        selected_chr(),
        input$go_button
      )
    })
    don <- eventReactive(toUpdate(),{
      shiny::req(selected_chr(), selected_min(), selected_max())


      #  dplyr::filter(chr_col %in% selected_chr()) %>%
      build_manhattanplot_tbl(
          gwas_df = gwas_data(),
          chr_selected = selected_chr(),
          bp_min = 1,
          bp_max = 245246279,
          feature_selected = input$immunefeature)
        #dplyr::filter(bp_col >= selected_min() & bp_col <= selected_max())
    })

    axisdf <- reactive({# Prepare X axis
      shiny::req(don())
      don() %>%
        dplyr::group_by(chr_col) %>%
        dplyr::summarize(center=( max(BPcum) + min(BPcum) ) / 2 )
    })

    output$mht_plot <- plotly::renderPlotly({
      shiny::req(don())

      don() %>%
        dplyr::filter(chr_col %in% selected_chr()) %>%
        dplyr::filter(bp_col >= selected_min() & bp_col <= selected_max()) %>%
      create_manhattanplot(
        df = .,
        x_label = axisdf(),
        y_min = input$yrange[1],
        y_max = input$yrange[2],
        x_name = "Chromossome",
        y_name = "- log10(p-value)",
        source_name = "gwas_mht"
      )
    })

}
