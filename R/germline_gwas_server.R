germline_gwas_server <- function(id, cohort_obj){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      if(!dir.exists("tracks"))
        dir.create("tracks")
      addResourcePath("tracks", "tracks")

      gwas_data <- reactive({
        iatlas.api.client::query_germline_gwas_results(datasets = "TCGA")
      })

      immune_feat <- reactive({
        gwas_data() %>%
          dplyr::select(feature_display, category) %>%
          dplyr::group_by(category) %>%
          tidyr::nest(data = c(feature_display))%>%
          dplyr::mutate(data = purrr::map(data, tibble::deframe)) %>%
          tibble::deframe()
      })

      updateSelectizeInput(session, 'immunefeature', choices = immune_feat(), selected = c("MHC2 21978456", "Th17 cells"), server = TRUE)

      output$search_snp <- renderUI({
        snp_options <- (gwas_data() %>% dplyr::filter(!is.na(snp_rsid)))$snp_rsid
        shiny::selectInput(ns("snp_int"), "Click on the plot or search for a SNP id:",
                           choices = c("", snp_options))
      })

      observe({
        updateSelectInput(session, "snp_int", selected = snp_of_int$ev)
      })

      subset_gwas <- reactive({
        shiny::req(input$immunefeature)
        if(input$feature_action == "Exclude") gwas_data() %>% dplyr::filter(!(feature_display %in% input$immunefeature)) %>% dplyr::select(SNP = snp_rsid, `SNP id` = snp_name, CHR = snp_chr, POS = snp_bp, 'P.VALUE'= p_value, Trait = feature_display, 'Immune Trait Module' = module, 'Immune Trait Category' = category)
        else gwas_data() %>% dplyr::filter(feature_display %in% input$immunefeature) %>% dplyr::select(SNP = snp_rsid, `SNP id` = snp_name, CHR = snp_chr, POS = snp_bp, 'P.VALUE'= p_value, Trait = feature_display, 'Immune Trait Module' = module, 'Immune Trait Category' = category)
      })

      output$igv_plot <- igvShiny::renderIgvShiny({
        igvShiny::igvShiny(list(
          genomeName="hg19",
          initialLocus= "all"
        ),
        displayMode="SQUISHED")
      })

      observeEvent(input$addGwasTrackButton, {
        igvShiny::loadGwasTrack(session, id=session$ns("igv_plot"), trackName="GWAS",
                                tbl=subset_gwas(),
                                deleteTracksOfSameName=FALSE)
      })

      #adding interactivity to select a SNP from the plot or from the dropdown menu
      clicked_snp <- reactiveValues(ev=NULL)

      shiny::observeEvent(input$trackClick,{
        x <- input$trackClick
        #we need to discover which track was clicked
        if(x[1] == "SNP"){
          clicked_snp$ev <- x[2]

          #show a pop-up window with more data
          attribute.name.positions <- grep("name", names(x[1:16]))
          attribute.value.positions <- grep("value", names(x[1:16]))
          attribute.names <- as.character(x)[attribute.name.positions]
          attribute.values <- as.character(x)[attribute.value.positions]
          tbl <- data.frame(name=attribute.names,
                            value=attribute.values,
                            stringsAsFactors=FALSE)
          dialogContent <- renderTable(tbl)
          html <- HTML(dialogContent())
          showModal(modalDialog(html, easyClose=TRUE))
        }
        if(x[1] == "Name")  print("eh gene")
      })

      snp_of_int <- reactiveValues(ev="")

      shiny::observeEvent(clicked_snp$ev,{
        snp_of_int$ev <- clicked_snp$ev
      })

      shiny::observeEvent(input$snp_int,{ #search for selected SNP
        snp_of_int$ev <- input$snp_int
        igvShiny::showGenomicRegion(session, id=session$ns("igv_plot"), input$snp_int) #update region on IGV plot
      })

      selected_snp <- reactive({
        shiny::validate(
          shiny::need(!is.null(snp_of_int$ev),
                      "Click manhattan plot to select a SNP."))

        gwas_data() %>%
          dplyr::filter(snp_rsid == snp_of_int$ev) %>%
          dplyr::select(snp_rsid, snp_name, snp_chr, snp_bp) %>%
          dplyr::distinct()

      })

      output$links <- renderUI({
        shiny::validate(
          shiny::need(selected_snp()$snp_rsid %in% gwas_data()$snp_rsid, "Select SNP")
        )
        #creating the links for external sources
        dbsnp <- paste0("https://www.ncbi.nlm.nih.gov/snp/", selected_snp()$snp_rsid)
        gtex <- paste0("https://gtexportal.org/home/snp/", selected_snp()$snp_rsid)
        gwascat <- paste0("https://www.ebi.ac.uk/gwas/search?query=", selected_snp()$snp_rsid)
        pheweb <- paste0("http://pheweb-tcga.qcri.org/variant/",  gsub(':([[:upper:]])', "-\\1", selected_snp()$snp_name))
        dice <- paste0("https://dice-database.org/eqtls/",  selected_snp()$snp_rsid)

        p(strong(selected_snp()$snp_rsid), tags$br(),
          selected_snp()$snp_name, tags$br(),
          "View more SNP information at",
          tags$a(href = dbsnp, "dbSNP, "),
          tags$a(href = gtex, "GTEx, "),
          tags$a(href = gwascat, "GWAS Catalog, "),
          tags$a(href = pheweb, "PheWeb, "),
          tags$a(href = dice, "DICE")
        )
      })

      output$snp_tbl <- DT::renderDT({
        shiny::req(gwas_data())
        shiny::validate(
          shiny::need(selected_snp()$snp_rsid %in% gwas_data()$snp_rsid, "")
        )

        snp_df <- gwas_data() %>%
          dplyr::filter(snp_rsid == selected_snp()$snp_rsid) %>%
          dplyr::mutate(nlog = round(-log10(p_value), 2)) %>%
          dplyr::select(
            Trait = feature_display,
            `-log10(p)` = nlog)

        DT::datatable(
          snp_df,
          rownames = FALSE,
          caption = paste("GWAS hits"),
          options = list(dom = 't')
        )
      })

      #COLOCALIZATION
      coloc_label <- reactive({
        switch(
          input$selection,
          "See all chromosomes" = "for all chromosomes",
          "Select a region" = paste("for chromosome", selected_chr())
        )
      })
      ##TCGA
      col_tcga <- reactive({
        germline_data$coloc_tcga %>%
          dplyr::filter(snp_chr %in% selected_chr()) %>%
          dplyr::select("Plot" = plot_type, "SNP" = snp_rsid, Trait = feature_display, QTL, Gene = gene, `Causal SNPs` = C1C2, Splice = splice, snp_chr, link_plot)
      })

      output$colocalization_tcga <- DT::renderDT({

        shiny::req(selected_chr())

        DT::datatable(
          col_tcga() %>% dplyr::select(!link_plot),
          escape = FALSE,
          rownames = FALSE,
          caption = paste("TCGA colocalization plots available ", coloc_label()),
          selection = 'single',
          options = list(
            pageLength = 5,
            lengthMenu = c(5, 10, 15, 20)
          )
        )
      })

      output$tcga_colocalization_plot <- shiny::renderUI({
        shiny::req(selected_snp(), input$colocalization_tcga_rows_selected)

        shiny::validate(
          shiny::need(
            !is.null(input$colocalization_tcga_rows_selected), "Click on table to see plot"))

        link_plot <- as.character(col_tcga()[input$colocalization_tcga_rows_selected, "link_plot"])

        tags$div(
          tags$hr(),
          tags$img(src = link_plot,
                   width = "100%")
        )
      })

      ##GTEX

      output$colocalization_gtex <- DT::renderDT({
        shiny::req(selected_chr())

        DT::datatable(
          germline_data$coloc_gtex %>%
            dplyr::filter(snp_chr %in% selected_chr()) %>%
            dplyr::select("SNP" = snp_rsid, Trait = feature_display, QTL, Tissue = tissue, Gene = gene, snp_chr),
          escape = FALSE,
          rownames = FALSE,
          caption = paste("GTEX colocalization plots available ", coloc_label()),
          selection = 'single',
          options = list(
            pageLength = 5,
            lengthMenu = c(5, 10, 15, 20)
          )
        )
      })

      output$gtex_colocalization_plot <- shiny::renderUI({
        shiny::req(input$colocalization_gtex_rows_selected)

        shiny::validate(
          shiny::need(
            !is.null(input$colocalization_gtex_rows_selected), "Click on table to see plot"))

        link_plot <- as.character(germline_data$coloc_gtex[input$colocalization_gtex_rows_selected, "link_plot"])

        tags$div(
          tags$hr(),
          tags$p(paste("GTEX Splice ID: ", as.character(germline_data$coloc_gtex[input$colocalization_gtex_rows_selected, "splice"]))),
          tags$img(src = link_plot,
                   width = "100%")
        )
      })

   observeEvent(input$method_link_gwas,{
     shiny::showModal(modalDialog(
       title = "Method",
       includeMarkdown("inst/markdown/methods/germline-gwas.md"),
       easyClose = TRUE,
       footer = NULL
     ))
   })

   observeEvent(input$method_link_colocalization,{
     shiny::showModal(modalDialog(
       title = "Method",
       includeMarkdown("inst/markdown/methods/germline-colocalization.md"),
       easyClose = TRUE,
       footer = NULL
     ))
   })
    }
  )
}
