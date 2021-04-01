#Heritability functions

#' Prepare heritability tibble for plotting
#'
#' @param heritablity_data A tibble
#' @param parameter Parameter to be used for selection of results (eg, ancestry cluster, immune feature)
#' @param group Specific group, in the selected parameter, to be displayed (eg. European - ancestry cluster, NK cells - immune feature)
#' @param pval_thres Maximun p-value to be included
#' @importFrom magrittr %>%

create_heritability_df <- function(
  heritablity_data,
  parameter = "cluster",
  group = "European",
  pval_thres = 0.05,
  ancestry_labels
){

  ancestry_df <- names(ancestry_labels)
  names(ancestry_df) <- ancestry_labels

    df <- heritablity_data %>%
      dplyr::filter(.[[parameter]] == group) %>%
      dplyr::filter(p_value <= pval_thres) %>%
      dplyr::filter(variance >= 0) %>%
      dplyr::mutate(plot_annot = dplyr::case_when(
        fdr <= 0.001 ~"***",
        fdr <= 0.01 ~"**",
        fdr <= 0.05 ~ "*",
        fdr <= 0.1 ~"†",
        fdr > 0.1 ~ ""
      )) %>%
      iatlas.modules::create_plotly_text(
        paste(.data$feature_display, "- ", ancestry_df[cluster], "Ancestry"),
        paste("\n Immune Trait Category:",.data$feature_germline_category, "\n Immune Trait Module:", .data$feature_germline_module),
        cols = c("variance", "se", "p_value","fdr"),
        title = "Immune Trait"
      )

  #creating the y label
  if(parameter == "cluster") df <- df %>% mutate(ylabel = feature_display)
  else if (parameter == "feature_germline_category" | parameter == "feature_germline_module")
    df <- df %>% mutate(ylabel = paste(ancestry_df[cluster], feature_display, sep = " - "))
  else  df <- df %>% mutate(ylabel = paste(ancestry_df[cluster], .[[parameter]], sep = " - "))
}

format_heritability_plot <- function(p, hdf, fdr = FALSE){
  p <- p %>%
    plotly::layout(
      xaxis = list(
        tickformat = "%"
      )
    )
    if(fdr == TRUE){
      p <- p %>%
            plotly::add_annotations(x = hdf$variance+hdf$se+0.01,
                                    y = hdf$ylabel,
                                    text = (hdf$plot_annot),
                                    xref = "x",
                                    yref = "y",
                                    showarrow = F,
                                    font=list(color='black')) %>%
            plotly::add_annotations( text="LRT FDR \n † <= 0.1 \n * <= 0.05 \n ** <= 0.01 \n *** <= 0.001", xref="paper", yref="paper",
                                     x=1.03, xanchor="left",
                                     y=0, yanchor="bottom",
                                     legendtitle=TRUE, showarrow=FALSE )
    }
  p
}


format_gwas_df <- function(df){
  df %>%
    dplyr::select(SNP = snp_rsid,
                  'SNP id' = snp_name,
                  CHR = snp_chr,
                  POS = snp_bp,
                  'P.VALUE'= p_value,
                  Trait = feature_display,
                  'Immune Trait Module' = feature_germline_module,
                  'Immune Trait Category' = feature_germline_category)
}

create_snp_popup_tbl <- function(track_event){
  attribute.name.positions <- grep("name", names(track_event[1:16]))
  attribute.value.positions <- grep("value", names(track_event[1:16]))
  attribute.names <- as.character(track_event)[attribute.name.positions]
  attribute.values <- as.character(track_event)[attribute.value.positions]
  tbl <- data.frame(name=attribute.names,
                    value=attribute.values,
                    stringsAsFactors=FALSE)
  dialogContent <- renderTable(tbl)
  HTML(dialogContent())
}

get_snp_links <- function(rsid, snpid){
  dbsnp <- paste0("https://www.ncbi.nlm.nih.gov/snp/", rsid)
  gtex <- paste0("https://gtexportal.org/home/snp/", rsid)
  gwascat <- paste0("https://www.ebi.ac.uk/gwas/search?query=", rsid)
  pheweb <- paste0("http://pheweb-tcga.qcri.org/variant/", gsub(':([[:upper:]])', "-\\1", snpid))
  dice <- paste0("https://dice-database.org/eqtls/", rsid)

  shiny::p(shiny::strong(rsid), shiny::tags$br(),
          snpid, shiny::tags$br(),
          "View more SNP information at",
          shiny::tags$a(href = dbsnp, "dbSNP, "),
          shiny::tags$a(href = gtex, "GTEx, "),
          shiny::tags$a(href = gwascat, "GWAS Catalog, "),
          shiny::tags$a(href = pheweb, "PheWeb, "),
          shiny::tags$a(href = dice, "DICE")
        )
}
