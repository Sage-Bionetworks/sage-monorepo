germline_gwas_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      gwas_data <- reactive({
        GERMLINE_PATH = "inst/extdata/"
        feather::read_feather(paste0(GERMLINE_PATH, "tcga_germline_GWAS_fullIFN.feather"))
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

      output$search_snp <- renderUI({
        shiny::req(subset_gwas())
        shiny::selectInput(ns("snp_int"), "Click on the plot for more SNP information, or search for a SNP id:",
                           choices = c("", (subset_gwas() %>% dplyr::filter(snp_id != "NA"))$snp_id), selected = snp_of_int$ev)
      })

      selected_chr_reactive <- reactiveValues(ev = 1)

      observeEvent(input[[sprintf("currentGenomicRegion.%s", "igv_plot")]], {
        shiny::req(input$selection == "Select a region")
        selected_chr_reactive$ev <-  as.numeric(sub("chr(.*):.*", "\\1", input[[sprintf("currentGenomicRegion.%s", "igv_plot")]]))
      })

      selected_chr <- reactive({
        shiny::validate(
          shiny::need(selected_chr_reactive$ev %in% c(1:22), "Data available for chromosomes 1 to 22.")
        )
        switch(
          input$selection,
          "See all chromosomes" = c(1:22),
          "Select a region" = selected_chr_reactive$ev
        )
      })

      chr_size <- reactive({
        shiny::req(input$selection == "Select a region", selected_chr())

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

      #reset region with change of chromosome or selection of all chromosomes
      toReset <- reactive({
        list(input$chr,
             input$selection)
      })

      observeEvent(toReset(), {
        clicked_int$ev <- NULL
      })

      #Creating the object that stores the selected range (either by zoom on manhattan plot or change in the IGV plot)
      chr_range <- reactiveValues(range = NULL)

      observe({
        shiny::req(input$selection == "Select a region",
                   chr_size())

        if(is.null(clicked_int$ev)){ #default option is min and max positions for the chromosome
          chr_range$range = chr_size()
        } else { #Update range after a zoom in the plot - need to compare with chr_size because plot has x-axis longer than chr_size
          chr_range$range = c((floor(max(chr_size()[1], clicked_int$ev$`xaxis.range[0]`))),
                              (ceiling(min(chr_size()[2], clicked_int$ev$`xaxis.range[1]`))))
        }
      })

      observeEvent(input[[sprintf("currentGenomicRegion.%s", "igv_plot")]], { #this observer is only activated when a change is made on the IGV plot
        newLoc <- input[[sprintf("currentGenomicRegion.%s", "igv_plot")]]

        pattern <- "chr(.*):(.*)-(.*)"
        sel_start <- as.numeric(gsub(",", "", sub(pattern, "\\2", newLoc)))
        sel_end <- as.numeric(gsub(",", "", sub(pattern, "\\3", newLoc)))

        chr_range$range <- c(sel_start,
                             sel_end)
      })

      selected_min <- reactive({
        switch(
          input$selection,
          "See all chromosomes" = 1,
          "Select a region" = chr_range$range[1]
        )
      })

      selected_max <- reactive({
        switch(
          input$selection,
          "See all chromosomes" = 245246279,
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

      gwas_mht <- eventReactive(toUpdate(),{
        shiny::req(immune_feat(), selected_chr(), selected_min(), selected_max())

        if(input$only_selected == 0 & !is.null(input$exclude_feat)) gwas_df <- gwas_data() %>% filter(!(display %in% input$exclude_feat))
        else if(input$only_selected == 1) gwas_df <- gwas_data() %>% filter(display %in% input$immunefeature)
        else gwas_df <- gwas_data()

        build_manhattanplot_tbl(
              gwas_df = gwas_df,
              chr_selected = selected_chr(),
              bp_min = selected_min(),
              bp_max = selected_max())
      })

      subset_gwas <- reactive({
        shiny::req(gwas_mht())
        gwas_mht() %>%
          dplyr::filter(chr_col %in% selected_chr()) %>%
          dplyr::filter(bp_col >= selected_min() & bp_col <= selected_max())
      })

      axisdf <- eventReactive(gwas_mht(), {# Prepare X axis
        shiny::req(gwas_mht(), subset_gwas())
        if(input$selection == "See all chromosomes"){
          gwas_mht() %>%
            dplyr::group_by(chr_col) %>%
            dplyr::summarize(center=( max(x_col) + min(x_col) ) / 2 ) %>%
            dplyr::rename(label = chr_col)
        }else{
          breaks <- c(selected_min(), (selected_min()+selected_max())/2, selected_max()) #c(min(subset_gwas()$bp_col), (min(subset_gwas()$bp_col) + max(subset_gwas()$bp_col))/2, max(subset_gwas()$bp_col))
          data.frame(
            label = paste(format(round(breaks / 1e6, 2), trim = TRUE), "Mb"),
            center = breaks,
            stringsAsFactors = FALSE
          )
        }
      })

      x_title <- reactive({
        if(input$selection == "See all chromosomes") "Chromosome"
        else ""
      })

      plot_title <- reactive({
        if(input$selection == "See all chromosomes") ""
        else paste0("chr", selected_chr(), ":", scales::comma(selected_min()), "-", scales::comma(selected_max()),
                                                   "<a href = 'http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&position=chr", input$chr, ":", chr_range$range[1], "-", chr_range$range[2],
                                                   "'> - View on UCSC Genome Browser</a>")
      })

      output$mht_plot <- plotly::renderPlotly({
        shiny::req(subset_gwas(), axisdf())

        shiny::validate(
          shiny::need(nrow(subset_gwas()) > 0, "Select a region with a GWAS hit.")
        )

        if(input$only_selected == 0) gwas_df <- subset_gwas() %>% dplyr::mutate(is_highlight=ifelse(display %in% input$immunefeature, "yes", "no"))
        else gwas_df <- subset_gwas() %>% dplyr::mutate(is_highlight = "no")

        gwas_df %>%
          dplyr::mutate(is_highlight = replace(is_highlight, snp_id == snp_of_int$ev, "snp")) %>%
          create_manhattanplot(
            df = .,
            x_label = axisdf(),
            y_min = input$yrange[1],
            y_max = input$yrange[2],
            x_limits = c(selected_min(), max(selected_max(), subset_gwas()$x_col)),
            x_name = x_title(),
            y_name = "- log10(p-value)",
            plot_title = plot_title(),
            source_name = "gwas_mht"
          )
      })

      output$igv_plot <- igvShiny::renderIgvShiny({
        shiny::req(subset_gwas())
        shiny::req(input$selection == "Select a region")

        igvShiny::igvShiny(list(
          genomeName="hg19",
          initialLocus= paste0("chr", selected_chr(), ":", scales::comma(selected_min()), "-", scales::comma(selected_max()))
        ),
        displayMode="SQUISHED")
      })

      #adding interactivity to select a SNP from the plot or from the dropdown menu

      clicked_snp <- reactiveValues(ev=NULL)

      observe({
        eventdata <- plotly::event_data( "plotly_click", source = "gwas_mht")
        if(is.null(eventdata)){
          clicked_snp$ev <- NULL
        }else{
          x_pos <- eventdata$x
          y_pos <- round(eventdata$y, 2)

          check_df <- gwas_mht()
          check_df$log10p <- round(check_df$log10p, 2)

          clicked_snp$ev <- as.character(check_df %>%
                                          dplyr::filter(x_col == x_pos & log10p  == y_pos) %>%
                                          dplyr::select(snp_id))
        }
      })

      snp_of_int <- reactiveValues(ev=NULL)

      shiny::observeEvent(clicked_snp$ev,{
        snp_of_int$ev <- clicked_snp$ev
      })

      shiny::observeEvent(input$snp_int,{
        snp_of_int$ev <- input$snp_int
      })

      ## clear selected snp on double click
      observeEvent(plotly::event_data("plotly_doubleclick", source = "gwas_mht"), {
        snp_of_int$ev <- NULL
      })


  selected_snp <- reactive({
    shiny::req(gwas_mht(), axisdf())

      shiny::validate(
        shiny::need(!is.null(snp_of_int$ev),
                    "Click manhattan plot to select a SNP."))

      shiny::validate(
        shiny::need(snp_of_int$ev != "NA",
                    "Selected SNP has no SNP id"))

        gwas_mht() %>%
          dplyr::filter(snp_id == snp_of_int$ev) %>%
          dplyr::select(snp_id, snp_col, chr_col, bp_col) %>%
          dplyr::distinct()

  })

   output$links <- renderUI({
     shiny::validate(
       shiny::need(selected_snp()$snp_id %in% gwas_mht()$snp_id, "")
     )
        #creating the links for external sources
        dbsnp <- paste0("https://www.ncbi.nlm.nih.gov/snp/", selected_snp()$snp_id)
        gtex <- paste0("https://gtexportal.org/home/snp/", selected_snp()$snp_id)
        gwascat <- paste0("https://www.ebi.ac.uk/gwas/search?query=", selected_snp()$snp_id)
        pheweb <- paste0("http://pheweb.sph.umich.edu/SAIGE-UKB/variant/",  gsub(':([[:upper:]])', "-\\1", selected_snp()$snp_col))
        dice <- paste0("https://dice-database.org/eqtls/",  selected_snp()$snp_id)

        p(strong(selected_snp()$snp_id), tags$br(),
          selected_snp()$snp_col, tags$br(),
          "View more SNP information at",
          tags$a(href = dbsnp, "dbSNP, "),
          tags$a(href = gtex, "GTEx, "),
          tags$a(href = gwascat, "GWAS Catalog, "),
          tags$a(href = pheweb, "PheWeb, "),
          tags$a(href = dice, "DICE")
        )
      })

   output$snp_tbl <- DT::renderDT({
     shiny::req(selected_snp())
     shiny::validate(
       shiny::need(selected_snp()$snp_id %in% gwas_mht()$snp_id, "Select SNP")
     )

     DT::datatable(
       gwas_mht() %>%
         dplyr::filter(snp_id == selected_snp()$snp_id) %>%
         dplyr::mutate(nlog = round(log10p, 2)) %>%
         dplyr::select(Trait = display, #`Trait Category` = `Annot.Figure.ImmuneCategory`, `PLINK MAF` = maf,
                        `Neg (log10(p))` = nlog),
       rownames = FALSE,
       caption = paste("GWAS hits - ", selected_snp()$snp_id),
       options = list(dom = 't'))
   })

   colocalization_plot <- reactive({ #get files with the selected SNP
     shiny::req(selected_snp())

     GERMLINE_PATH = "inst/extdata/"
     feather::read_feather(paste0(GERMLINE_PATH, "colocalization_df.feather"))
   })

   col_snp <- reactive({
     colocalization_plot() %>%
       dplyr::filter(snp_id == snp_of_int$ev) %>%
       dplyr::mutate(View =  paste(
         "<a href=\"",
         link_plot,"\"> View plot</a>",
         sep=""
       )
       ) %>%
       dplyr::select(Trait = display, gene, QTL, C1C2, TCGA.Splice.ID, View, link_plot)
   })

   output$colocalization <- DT::renderDT({
     shiny::validate(
       shiny::need(
         snp_of_int$ev != "", "Select a SNP"))

     shiny::validate(
       shiny::need(
         snp_of_int$ev %in% colocalization_plot()$snp_id, "No colocalization plot for the selected SNP."))

     DT::datatable(
       col_snp() %>% dplyr::select(!link_plot),
       escape = FALSE,
       rownames = FALSE,
       caption = paste("Colocalization plots available - ", selected_snp()$snp_id),
       selection = 'single',
       options = list(dom = 't'))

   })

   output$colocalization_plot <- shiny::renderUI({
     shiny::req(selected_snp(), input$colocalization_rows_selected)
     shiny::validate(
       shiny::need(
         snp_of_int$ev != "", "Select a SNP"))

     shiny::validate(
       shiny::need(
         !is.null(input$colocalization_rows_selected), "Click on table to see plot"))

     link_plot <- as.character(col_snp()[input$colocalization_rows_selected, "link_plot"])

     tags$img(src = "https://pbs.twimg.com/media/EoZzj8OXYAAv8C-?format=png&name=small")
   })

    }
  )
}
