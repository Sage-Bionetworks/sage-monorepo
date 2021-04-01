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
        iatlas.modules::create_nested_named_list(
          gwas_data(),
          names_col1 = "feature_germline_category",
          names_col2 = "feature_display",
          values_col = "feature_name"
        )
      })

      shiny::updateSelectizeInput(session, 'immunefeature',
                                 choices = immune_feat(),
                                 selected = c("Wolf_MHC2_21978456", "Bindea_Th17_cells"), #default to exclude that, as suggested by manuscript authors
                                 server = TRUE)

      snp_options <- reactive({
        shiny::req(gwas_data())
        gwas_data() %>% dplyr::filter(!is.na(snp_rsid)) %>% dplyr::pull(snp_rsid)
      })

      shiny::updateSelectizeInput(session, 'snp_int',
                                 choices = c("", snp_options()),
                                 selected = "",
                                 server = TRUE)

      subset_gwas <- shiny::reactive({
        if(is.null(input$immunefeature)){
          gwas_data() %>% iatlas.app::format_gwas_df()
        }else{
          if(input$feature_action == "Exclude") gwas_data() %>% dplyr::filter(!(feature_name %in% input$immunefeature)) %>% iatlas.app::format_gwas_df()
          else gwas_data() %>% dplyr::filter(feature_name %in% input$immunefeature) %>% iatlas.app::format_gwas_df()
        }
      })

      trackname <- shiny::reactive({
        if(is.null(input$immunefeature)) "GWAS"
        else paste("GWAS -",
                   input$feature_action,
                   paste(gwas_data() %>% dplyr::filter(feature_name %in% input$immunefeature) %>% purrr::pluck("feature_display") %>% unique(),
                         collapse = ", "))
      })

      yaxis <- shiny::reactive({
        c(min(-log10(subset_gwas()$P.VALUE)-1),
          max(-log10(subset_gwas()$P.VALUE)+1)
        )
      })

      output$igv_plot <- igvShiny::renderIgvShiny({
        igvShiny::igvShiny(list(
          genomeName="hg19",
          initialLocus= "all"
        ),
        displayMode="SQUISHED")
      })

      shiny::observeEvent(input$igvReady, {
        containerID <- input$igvReady
        igvShiny::loadGwasTrack(session, id=session$ns("igv_plot"),
                                trackName=trackname(),
                                tbl=subset_gwas(),
                                ymin = yaxis()[1],
                                ymax = yaxis()[2],
                                deleteTracksOfSameName=FALSE)
      })

      shiny::observeEvent(input$addGwasTrackButton, {
        igvShiny::loadGwasTrack(session, id=session$ns("igv_plot"),
                                trackName=trackname(),
                                tbl=subset_gwas(),
                                ymin = yaxis()[1],
                                ymax = yaxis()[2],
                                deleteTracksOfSameName=FALSE)
      })

      #adding interactivity to select a SNP from the plot or from the dropdown menu
      clicked_snp <- shiny::reactiveValues(ev=NULL)

      shiny::observeEvent(input$trackClick,{
        x <- input$trackClick
        #we need to discover which track was clicked
        if(x[1] == "SNP"){
          clicked_snp$ev <- x[2]
          shiny::showModal(shiny::modalDialog(iatlas.app::create_snp_popup_tbl(x), easyClose=TRUE))
        }
      })

      snp_of_int <- shiny::reactiveValues(ev="")

      shiny::observeEvent(clicked_snp$ev,{
        snp_of_int$ev <- clicked_snp$ev
      })

      shiny::observeEvent(input$snp_int,{ #search for selected SNP
        igvShiny::showGenomicRegion(session, id=session$ns("igv_plot"), input$snp_int) #update region on IGV plot
        snp_of_int$ev <- input$snp_int
      })

      selected_snp <- shiny::reactive({
        shiny::validate(
          shiny::need(!is.null(snp_of_int$ev),
                      "Click manhattan plot to select a SNP."))

        gwas_data() %>%
          dplyr::filter(snp_rsid == snp_of_int$ev) %>%
          dplyr::mutate(nlog = round(-log10(p_value), 2)) %>%
          dplyr::select(snp_rsid, snp_name, snp_chr, snp_bp, feature_display, nlog)
      })

      output$links <- renderUI({
        shiny::validate(
          shiny::need(selected_snp()$snp_rsid %in% gwas_data()$snp_rsid, "Select SNP")
        )
        iatlas.app::get_snp_links(selected_snp()$snp_rsid[1],
                                  selected_snp()$snp_name[1])
      })

      output$snp_tbl <- DT::renderDT({
        shiny::req(gwas_data())
        shiny::validate(
          shiny::need(selected_snp()$snp_rsid %in% gwas_data()$snp_rsid, "")
        )

        DT::datatable(
          selected_snp() %>%
            dplyr::select(
              Trait = feature_display,
              `-log10(p)` = nlog),
          rownames = FALSE,
          caption = paste("GWAS hits"),
          options = list(dom = 't')
        )
      })

      #COLOCALIZATION
      # coloc_label <- reactive({
      #   switch(
      #     input$selection,
      #     "See all chromosomes" = "for all chromosomes",
      #     "Select a region" = paste("for chromosome", selected_chr())
      #   )
      # })
      ##TCGA
      col_tcga <- reactive({
        iatlas.api.client::query_colocalizations(coloc_datasets = "TCGA") %>%
          dplyr::filter(coloc_dataset_name == "TCGA") %>%
          dplyr::select("Plot" = plot_type, "SNP" = snp_rsid, Trait = feature_display, QTL = qtl_type, Gene = gene_hgnc, `Causal SNPs` = ecaviar_pp, Splice = splice_loc, CHR = snp_chr, plot_link)
      })

      output$colocalization_tcga <- DT::renderDT({

        DT::datatable(
          col_tcga() %>% dplyr::select(!plot_link),
          escape = FALSE,
          rownames = FALSE,
          caption = "TCGA colocalization plots available", #paste("TCGA colocalization plots available ", coloc_label()),
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

        link_plot <- as.character(col_tcga()[input$colocalization_tcga_rows_selected, "plot_link"])

        tags$div(
          tags$hr(),
          tags$img(src = link_plot,
                   width = "100%")
        )
      })

      ##GTEX
      col_gtex <- reactive({
        iatlas.api.client::query_colocalizations(coloc_datasets = "GTEX") %>%
          dplyr::filter(coloc_dataset_name == "GTEX") %>%
          dplyr::select("SNP" = snp_rsid, Trait = feature_display, QTL = qtl_type, Gene = gene_hgnc, Tissue = tissue, Splice = splice_loc, CHR = snp_chr, plot_link)
      })

      output$colocalization_gtex <- DT::renderDT({

        DT::datatable(
          col_gtex()%>% dplyr::select(!c(plot_link, Splice)),
          escape = FALSE,
          rownames = FALSE,
          caption = "GTEX colocalization plots available ", #paste("GTEX colocalization plots available ", coloc_label()),
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

        link_plot <- as.character(col_gtex()[input$colocalization_gtex_rows_selected, "plot_link"])

        tags$div(
          tags$hr(),
          tags$p(paste("GTEX Splice ID: ", as.character(col_gtex()[input$colocalization_gtex_rows_selected, "Splice"]))),
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
