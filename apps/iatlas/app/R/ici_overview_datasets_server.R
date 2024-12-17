ici_overview_datasets_server <- function(
  id,
  ioresponse_data,
  data_group
) {
  shiny::moduleServer(
    id,
    function(input, output, session) {

      ns <- session$ns

      download_text <- shiny::reactive(
        switch(
          data_group(),
          "ici" = c("https://github.com/CRI-iAtlas/iatlas-notebooks/blob/main/ici_query_iatlas_data.ipynb",
                    "Information about downloading the Immune Checkpoint Inhibition data available in iAtlas is available at this Jupyter notebook."),
          "cancer genomics" = c("https://github.com/CRI-iAtlas/iatlas-notebooks/blob/main/querying_TCGA_features_and_expression.ipynb",
                                "Information about downloading the Cancer Genomics data available in iAtlas is available at this Jupyter notebook."),
          "single-cell RNA-Seq"= c("https://github.com/CRI-iAtlas/iatlas-notebooks/blob/main/query_iatlas_single_cell_datasets.ipynb",
                                   "Information about downloading the pseudobulk single-cell RNAseq data available in iAtlas is available at this Jupyter notebook."),
        )
      )

      dataset_info <- shiny::reactive({
        ioresponse_data$dataset_df %>%
          dplyr::filter(type == data_group())
      })

      output$sums <- shiny::renderUI({
        n_samples <- sum(dataset_info()$Samples)
        n_patients <- sum(dataset_info()$Patients)

        display_text <-  glue::glue(
          "CRI iAtlas has {n_samples} samples, from {n_patients} patients in this category of datasets."
        )

        if(data_group() != "single-cell RNA-Seq"){
          tags$div(
            display_text,
            tags$a(href=download_text()[1],
                   download_text()[2])
          )
        }else{


          tags$div(
            display_text,
            shiny::p("The MSK and Vanderbilt data sets were generated as part of the NCI Human Tumor Atlas Network, shared via ",
                   a(href = "https://humantumoratlas.org/", "https://humantumoratlas.org/ .")),
            tags$a(href=download_text()[1],
                   download_text()[2])
          )
        }

      })

      output$ici_datasets_df <- DT::renderDT({
        DT::datatable((dataset_info() %>%
                         dplyr::mutate(
                           Reference = paste(
                             "<a href=\"",
                             Paper,"\">",
                             Citation,"</a>",
                             sep=""
                           )
                         )%>%
                         select(Dataset, Study, Antibody, Samples, Patients, `Sequencing.Method`, Reference)),
                      options = list(pageLength = 20),
                      escape= FALSE)
      })

    }
  )
}
