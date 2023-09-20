immune_subtype_classifier_server <- function(
  id,
  group_display_choice,
  group_internal_choice,
  subset_df,
  plot_colors
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      newData <- eventReactive(input$subtypeGObutton, {
        readNewDataTable(input$expr_file_pred, input$sepa)
      })

      ebpp_genes <- reactive({
        data(ebpp_gene)
        ebpp_genes_sig
      })

      output$genelist <- downloadHandler(
        filename = "subtypeclassifier_genes.tsv",
        content = function(con){
          write.csv(ebpp_genes(), con)
        }
      )

      geneMatchErrorReport <- function(X, geneid='symbol') {
        data(ebpp_gene)

        if (geneid == 'symbol') {
          idx <- match(table = rownames(X), x = ebpp_genes_sig$Symbol)  ### this is just for the EBPP genes ###

        } else {
          print("For geneids, please use gene symbols.")
          return(NA)
        }

        # idx will be 485 elements long... non matched ebpp_sig_genes
        # will show as NAs in the list.
        # SO... we calculate sum of NAs over size of ebpp_genes_sig

        matchError <- sum(is.na(idx)) / nrow(ebpp_genes_sig)

        # NAs in idx will enter NA rows in X2

        g <- ebpp_genes_sig[is.na(idx),]  ### Adds NA rows in missing genes

        return(list(matchError=matchError, missingGenes=g, numGenesInClassifier=nrow(ebpp_genes_sig)))
      }


      # get new calls
      getCalls <- reactive({
        shiny::validate(
          shiny::need(ncol(newData())>1,
                      "Only one column available in the uploaded file. Please check if the selected file separator is the correct one."))

        output$geneMatchCnt <- renderText({
          matchInfo <- geneMatchErrorReport(X=newData())

          n <- (round(matchInfo$matchError, digits = 3))*100
          m <- matchInfo$numGenesInClassifier

          paste0('<b> Match error: ', n, '% from a total of ', m, ' genes required by the classifier.  Missing genes shown below. </b>')
        })

        classifySubtype(newData())
      })


      output$barPlot <- renderPlot({
        counts <- table(getCalls()$Calls$BestCall)
        barplot(counts, main="New Cluster Label Calls",
                xlab="Cluster Labels")
      })


      # Filter data based on selections
      output$subtypetable <- DT::renderDataTable(
        DT::datatable(as.data.frame(getCalls()$Calls))
      )

      missing_genes <- reactive({
        as.data.frame(geneMatchErrorReport(X=newData())$missingGenes)
      })
      # Missing Genes Table
      output$missinggenetable <- DT::renderDataTable(
        DT::datatable(missing_genes()
                      #as.data.frame(geneMatchErrorReport(X=newData())$missingGenes)
        )
      )

      output$download_calls <- downloadHandler(
        filename = function() stringr::str_c("immune-calls-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(getCalls()$Calls, con)
      )

      output$download_missing <- downloadHandler(
        filename = function() stringr::str_c("missing-genes-", Sys.Date(), ".csv"),
        content = function(con) readr::write_csv(missing_genes(), con)
      )

    }
  )
}
