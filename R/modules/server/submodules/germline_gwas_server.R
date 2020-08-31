germline_gwas_server <- function(
    input,
    output,
    session,
    cohort_obj
){


    ns <- session$ns

    GERMLINE_PATH = Sys.getenv("GERMLINE_PATH")

    gwas_data <- readxl::read_excel(paste0(GERMLINE_PATH, "Table S4_GWAS Results_v20200106.xlsx"), sheet = 2) %>%
      dplyr::select(feature_col = Annot.Figure.ImmuneTrait, snp_id = Annot.SNPlocs.RefSNP_id, snp_col = PLINK.SNP, chr_col = PLINK.CHR, bp_col = PLINK.BP, PLINK.P)

    chr_size <- reactive({
      max((gwas_data %>%
             dplyr::filter(chr_col == (input$chr)))$bp_col)
    })

    output$features <- renderUI({
      shiny::selectizeInput(ns('immunefeature'), "Select Immune Feature", choices = sort(unique(gwas_data$feature_col)))
    })

    output$pos_1 <- renderUI({
      shiny::req(chr_size())
      numericInput(ns("pos1"), "Initial Position", value= min((gwas_data %>% dplyr::filter(chr_col == as.character(input$chr)))$bp_col),
                   min = 1,
                   max = max((gwas_data %>% dplyr::filter(chr_col == as.character(input$chr)))$bp_col))
    })

    output$pos_2 <- renderUI({
      shiny::req(chr_size())
      numericInput(ns("pos2"), "Final Position", value= chr_size(), min = 1, max = chr_size())
    })

    output$link_genome <- renderUI({
      link_ucsc <- paste0("http://genome.ucsc.edu/cgi-bin/hgTracks?db=hg19&position=chr", input$chr, ":", input$pos1, "-", input$pos2)
      p(a(href = link_ucsc, "View this region on UCSC Genome Browser"))

    })

    selected_chr <- reactive({
      switch(
        input$selection,
        "See all chromossomes" = c(1:22),
        "Select a region" = input$chr
      )
    })

    selected_min <- reactive({
      switch(
        input$selection,
        "See all chromossomes" = 1,
        "Select a region" = input$pos1
      )
    })

    selected_max <- reactive({
      switch(
        input$selection,
        "See all chromossomes" = 245246279,
        "Select a region" = input$pos2
      )
    })

    # Prepare the dataset
    don <- reactive({
      shiny::req(selected_chr(), selected_min(), selected_max())
      build_manhattanplot_tbl(
        gwas_df = gwas_data,
        chr_selected = selected_chr(),
        bp_min = selected_min(),
        bp_max = selected_max(),
        feature_selected = input$immunefeature)
    })

    axisdf <- reactive({# Prepare X axis
      shiny::req(don())
      don() %>%
        dplyr::group_by(chr_col) %>%
        dplyr::summarize(center=( max(BPcum) + min(BPcum) ) / 2 )
    })

    output$mht_plot <- plotly::renderPlotly({
      shiny::req(don())

      create_manhattanplot(
        df = don(),
        x_label = axisdf(),
        y_min = input$yrange[1],
        y_max = input$yrange[2],
        x_name = "Chromossome",
        y_name = "- log10(p-value)",
        source_name = "gwas_mht"
      )
    })
}
