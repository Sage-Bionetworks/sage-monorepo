germline_gwas_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      gwas_data <- reactive({
        GERMLINE_PATH = "inst/extdata/"
        feather::read_feather(paste0(GERMLINE_PATH, "tcga_germline_GWAS_fullIFN.feather"))
      })

      coloc_data <- reactive({
        GERMLINE_PATH = "inst/extdata/"
        feather::read_feather(paste0(GERMLINE_PATH, "TCGA_colocalication.feather"))
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

      output$xrange <- renderUI({
        shiny::verticalLayout(

          shiny::splitLayout(
            shiny::numericInput(ns("start"), "Start", value= selected_min(), #min(subset_gwas()$bp_col),
                                min = chr_size()[1],
                                max = chr_size()[2]),
            shiny::numericInput(ns("end"), "End", value= selected_max(),  #max(subset_gwas()$bp_col),
                                min = chr_size()[1],
                                max = chr_size()[2]),
            shiny::checkboxInput(ns("reset_axes"), "Reset"),
            shiny::actionButton(ns("go_button"), "Update")
          )
        )
      })

      selected_chr <- reactive({
        switch(
          input$selection,
          "See all chromossomes" = c(1:22),
          "Select a region" = shiny::req(input$chr)
        )
      })

      chr_size <- reactive({
        shiny::req(input$selection == "Select a region")

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

      subset_gwas <- reactive({
        shiny::req(don())
        don() %>%
          dplyr::filter(chr_col %in% selected_chr()) %>%
          dplyr::filter(bp_col >= selected_min() & bp_col <= selected_max())
      })

      axisdf <- eventReactive(don(), {# Prepare X axis
        shiny::req(don(), subset_gwas())
        if(input$selection == "See all chromossomes"){
          don() %>%
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
        if(input$selection == "See all chromossomes") "Chromossome"
        else "" #(paste0("chr", selected_chr()))
      })

      plot_title <- reactive({
        if(input$selection == "See all chromossomes") ""
        else paste0("chr", input$chr, ":", scales::comma(selected_min()), "-", scales::comma(selected_max()),
                                                   "<a href = 'http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&position=chr", input$chr, ":", chr_range$range[1], "-", chr_range$range[2],
                                                   "'> - View on UCSC Genome Browser</a>")
      })

      output$mht_plot <- plotly::renderPlotly({
        shiny::req(subset_gwas(), axisdf())

        shiny::validate(
          shiny::need(nrow(subset_gwas()) > 0, "Select a region with a GWAS hit. \n(select Reset and Update to see all GWAS hits)")
        )

          create_manhattanplot(
            df = subset_gwas(),
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

      output$genome_plot <- renderPlot({
        shiny::req(subset_gwas())
        shiny::req(input$selection == "Select a region")

        shiny::validate(
          shiny::need(nrow(subset_gwas()) > 0, "Select a region with a GWAS hit.")
        )

        from_bp <- selected_min() #min(subset_gwas()$bp_col)
        to_bp <- selected_max() #max(subset_gwas()$bp_col)
        if(from_bp == to_bp) to_bp <- from_bp + 5000

        print(from_bp)
        print(to_bp)
        shiny::validate(
          shiny::need((to_bp - from_bp) < 1000000, "Select a region in the manhattan plot for genomic info
                      (only supported for intervals with less than 1Mbp).")
        )

        library(org.Hs.eg.db)
        library(TxDb.Hsapiens.UCSC.hg19.knownGene)
        library(GO.db)

        range <- GenomicRanges::GRanges((paste0("chr", selected_chr())), IRanges::IRanges(start = from_bp, end = to_bp))
        p.txdb <- ggbio::autoplot(Homo.sapiens::Homo.sapiens, which = range)

        shiny::validate(
          shiny::need(length(p.txdb@ggplot[["layers"]]) > 0, "No transcripts found at this region.")
        )

       p.txdb +
        ggbio::xlim(range)+
        ggbio::theme_alignment()
      })

  selected_snp <- reactive({
    shiny::req(don(), axisdf())
    eventdata <- plotly::event_data( "plotly_click", source = "gwas_mht")

    shiny::validate(
      shiny::need(!is.null(eventdata),
                  "Click manhattan plot to select a SNP."))

    x_pos <- eventdata$x
    y_pos <- round(eventdata$y, 2)

    check_df <- don()
    check_df$log10p <- round(check_df$log10p, 2)

    check_df %>%
      dplyr::filter(x_col == x_pos & log10p  == y_pos)
  })

   output$clicked <- renderUI({
     shiny::validate(
       shiny::need(selected_snp()$snp_id %in% don()$snp_id, "")
     )
        #creating the links for external sources
        dbsnp <- paste0("https://www.ncbi.nlm.nih.gov/snp/", selected_snp()$snp_id)
        gtex <- paste0("https://gtexportal.org/home/snp/", selected_snp()$snp_id)
        gwascat <- paste0("https://www.ebi.ac.uk/gwas/search?query=", selected_snp()$snp_id)
        pheweb <- paste0("http://pheweb.sph.umich.edu/SAIGE-UKB/variant/",  gsub(':([[:upper:]])', "-\\1", selected_snp()$snp_col))
        dice <- paste0("https://dice-database.org/eqtls/",  selected_snp()$snp_id)


        p(
          #paste("Selected SNP:", selected_snp()$snp_id, ". View more SNP information at \n"),
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
       shiny::need(selected_snp()$snp_id %in% don()$snp_id, "Select SNP")
     )

     DT::datatable(
       don() %>%
         dplyr::filter(snp_id == selected_snp()$snp_id) %>%
         dplyr::mutate(nlog = round(log10p, 2)) %>%
         dplyr::select(SNP = snp_col, Trait = display, #`Trait Category` = `Annot.Figure.ImmuneCategory`, `PLINK MAF` = maf,
                        `Neg (log10(p))` = nlog),
       rownames = FALSE,
       caption = paste("Selected SNP: ", selected_snp()$snp_id),
       options = list(dom = 't'))
   })

   output$colocalization <- plotly::renderPlotly({
     shiny::req(selected_snp())
     shiny::validate(
       shiny::need(selected_snp()$snp_id %in% don()$snp_id, "Select SNP")
     )

     #plot1

     gwas_df <- coloc_data() %>%
       dplyr::filter(CHR == selected_snp()$chr_col) %>%
       dplyr::mutate(is_highlight = dplyr::case_when(
                       Annot.SNPlocs.RefSNP_id == selected_snp()$snp_id ~ "yes",
                       TRUE ~ "no"
                     ),
                     text = paste("<b>", Annot.SNPlocs.RefSNP_id, "</b>",
                                  "\n Mapped Gene: <b>", Gene.37, "</b>",
                                  "\nSNP name: ", Annot.SNPlocs.RefSNP_id, "\nSNP: ", PLINK.SNP, "\nPosition: ", BP.37, "\nChromosome: ", CHR,
                                  "\nPLINK MAF: ", TCGA.GWAS.MAF, sep=""))%>%
       dplyr::rename(x_col = BP.37,
                     chr_col = CHR)

     breaks <- c(min(gwas_df$BP.37), (min(gwas_df$BP.37)+max(gwas_df$BP.37))/2, max(gwas_df$BP.37)) #c(min(subset_gwas()$bp_col), (min(subset_gwas()$bp_col) + max(subset_gwas()$bp_col))/2, max(subset_gwas()$bp_col))
     axis_df <- data.frame(
       label = paste(format(round(breaks / 1e6, 2), trim = TRUE), "Mb"),
       center = breaks,
       stringsAsFactors = FALSE
     )


     plot1 <- gwas_df %>%
      dplyr::mutate(log10p = -log10(TCGA.GWAS.P)) %>%
      create_manhattanplot(
         df = .,
         x_label = axis_df,
         y_min = min(.$log10p),
         y_max = max(.$log10p),
         #x_limits = c(selected_min(), max(selected_max(), subset_gwas()$x_col)),
         #x_name = x_title(),
         y_name = "- log10p(GWAS)"#,
         #plot_title = plot_title(),
         #source_name = "gwas_mht"
       )

     plot2 <- gwas_df %>%
       dplyr::mutate(log10p = -log10(TCGA.QTL.P)) %>%
       create_manhattanplot(
         df = .,
         x_label = axis_df,
         y_min = min(.$log10p),
         y_max = max(.$log10p),
         #x_limits = c(selected_min(), max(selected_max(), subset_gwas()$x_col)),
         #x_name = x_title(),
         y_name = "- log10p(QTL)"#,
         #plot_title = plot_title(),
         #source_name = "gwas_mht"
       )

     plot3 <- gwas_df %>%
       dplyr::mutate(log10p = Region_CLPP_c1) %>%
       create_manhattanplot(
         df = .,
         x_label = axis_df,
         y_min = min(.$log10p),
         y_max = max(.$log10p),
         #x_limits = c(selected_min(), max(selected_max(), subset_gwas()$x_col)),
         #x_name = x_title(),
         y_name = "C1 CLPP"#,
         #plot_title = plot_title(),
         #source_name = "gwas_mht"
       )

     plotly::subplot(plot1, plot2, plot3, nrows = 3, shareX = TRUE, titleY = TRUE)
   })

    }
  )
}
