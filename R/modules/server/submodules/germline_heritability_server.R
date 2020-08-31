germline_heritability_server <- function(
    input,
    output,
    session,
    cohort_obj
){
#
#     source_files <- c(
#         "R/modules/server/submodules/distribution_plot_server.R"
#     )
#
#     for (file in source_files) {
#         source(file, local = T)
#     }
  GERMLINE_PATH = Sys.getenv("GERMLINE_PATH")

  heritability <- list(
    EUR =  feather::read_feather(paste0(GERMLINE_PATH, "European_hdf.feather")),
    AFR =  feather::read_feather(paste0(GERMLINE_PATH, "African_hdf.feather")),
    ASIAN =  feather::read_feather(paste0(GERMLINE_PATH, "Asian_hdf.feather")),
    AMR = feather::read_feather(paste0(GERMLINE_PATH, "American_hdf.feather")),
    EUR_IMMUNE = feather::read_feather(paste0(GERMLINE_PATH, "immune_hdf.feather")),
    BY_IMMUNE = feather::read_feather(paste0(GERMLINE_PATH, "hdf_byImmune.feather"))
  )

    ns <- session$ns

    plot_title <- reactive({
      if( is.null( input$byImmune) |  input$byImmune == 0) "V(Genotype)/Vp"
      else "V(Genotype x Immune Subbtype)/Vp"
    })

    hdf <- reactive({
      shiny::req(input$ancestry)
      #Reading in the table with computed statistics
      if(input$ancestry == "European" & input$byImmune == TRUE){
        df <- heritability$EUR_IMMUNE
      }else{
        df <- heritability[[input$ancestry]]
      }

      df %>%
        dplyr::filter(pval <= input$pvalue) %>%
        create_plotly_label(
          ., .$Trait, paste(input$ancestry, "Ancestry"), c("Variance", "SE", "pval","FDR"), title = "Immune Trait"
        )
    })

    output$heritability <- plotly::renderPlotly({
      shiny::req(hdf())
      shiny::validate(
        shiny::need(nrow(hdf())>0, "No Immune Trait with a p-value lower than selected.")
      )

      plot_levels <-levels(reorder(hdf()[["Trait"]], hdf()[[input$order_bars]], sort))

      iatlas.app::create_barplot_horizontal(
        df = hdf(),
        x_col = "Variance",
        y_col = "Trait",
        error_col = "SE",
        key_col = NA,
        color_col = "pval",
        label_col = "label",
        xlab = "% Heritability",
        ylab = "",
        order_by = plot_levels,
        title = plot_title(),
        source_name = "heritability_plot",
        bar_colors = NULL
      ) %>%
        plotly::add_annotations(x = hdf()$Variance+hdf()$SE+0.01,
                                y = hdf()$Trait,
                                text = (hdf()$plot_annot),
                                xref = "x",
                                yref = "y",
                                showarrow = F,
                                font=list(color='black')) %>%
        plotly::add_annotations( text="LRT FDR \n † <= 0.1 \n * <= 0.05 \n ** <= 0.01 \n *** <= 0.001", xref="paper", yref="paper",
                                 x=1.03, xanchor="left",
                                 y=0, yanchor="bottom",
                                 legendtitle=TRUE, showarrow=FALSE )

    })

    output$heritability_cov <- plotly::renderPlotly({

 eventdata <- plotly::event_data( "plotly_click", source = "heritability_plot")

 shiny::validate(
   shiny::need(!is.null(eventdata),
               "Click bar plot"))
 selected_plot_trait <- eventdata$y[[1]]

 hdf_plot <-heritability$BY_IMMUNE%>%
   dplyr::filter(Trait == selected_plot_trait)

plot_colors <- c("#bebebe", "#FF0000", "#FFFF00", "#00FF00")
names(plot_colors) <- c("Covar:Immune Subtype", "C1", "C2", "C3")

hdf_plot$subtypes <- factor(hdf_plot$subtypes, levels = c("C3", "C2", "C1", "Covar:Immune Subtype" ))

      iatlas.app::create_barplot_horizontal(
        df = hdf_plot,
        x_col = "Variance",
        y_col = "subtypes",
        error_col = "SE",
        key_col = NA,
        color_col = "subtypes",
        label_col = NA,
        xlab = "",
        ylab = "",
        title = "Random data!!!!!",
        showLegend = FALSE,
        source_name = NULL,
        bar_colors = plot_colors
      )

    })
}
