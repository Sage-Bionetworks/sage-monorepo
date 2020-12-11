cohort_selection_server <- function(
  id,
  cohort_obj
){
  shiny::moduleServer(
    id,
    function(input, output, session) {

      cohort_obj_manual <- cohort_manual_selection_server(
        "cohort_manual_selection"
      )

      cohort_obj_upload <- cohort_upload_selection_server(
        "cohort_upload_selection"
      )

      cohort_obj <- shiny::reactive({
        shiny::req(input$cohort_mode_choice)
        if (input$cohort_mode_choice == "Cohort Selection") {
          shiny::req(cohort_obj_manual())
          return(cohort_obj_manual())
        } else if (input$cohort_mode_choice == "Cohort Upload") {
          shiny::req(cohort_obj_upload())
          return(cohort_obj_upload())
        } else {
          stop("Unrecognized cohort creation opion")
        }
      })

      # group key ---------------------------------------------------------------

      group_key_tbl <- shiny::reactive({
        shiny::req(cohort_obj())
        cohort_obj()$group_tbl %>%
          dplyr::select(
            `Sample Group` = group,
            `Group Name` = name,
            `Group Size` = size,
            Characteristics = characteristics,
            `Plot Color` = color
          )
      })

      data_table_server(
        "sg_table",
        group_key_tbl,
        options = list(
          dom = "tip",
          pageLength = 10,
          columnDefs = list(
            list(width = '50px',
                 targets = c(1)
            )
          )
        ),
        color = T,
        color_column = "Plot Color",
        colors = ""
      )

      # group overlap -----------------------------------------------------------
      #
      #     output$mosaic_group_select <- shiny::renderUI({
      #         shiny::req(groups_list(), group_internal_choice())
      #         shiny::radioButtons(
      #             ns("mosaic_group_choice"),
      #             "Select second sample group to view overlap:",
      #             choices = setdiff(groups_list(), group_internal_choice()),
      #             inline = TRUE
      #         )
      #     })
      #
      #     output$mosaic_subset_select <- shiny::renderUI({
      #         shiny::req(input$mosaic_group_choice)
      #         if (input$mosaic_group_choice == "TCGA_Subtype") {
      #             shiny::req(tcga_subtypes_list())
      #             selectInput(
      #                 ns("mosaic_subset_choice"),
      #                 "Select or Search for Study Subset",
      #                 choices = tcga_subtypes_list()
      #             )
      #         }
      #     })
      #
      #     mosaic_subtypes <- shiny::reactive({
      #         shiny::req(input$mosaic_group_choice)
      #         if(input$mosaic_group_choice == "TCGA_Subtype"){
      #             shiny::req(input$mosaic_subset_choice, groups_con())
      #             subtypes <- groups_con() %>%
      #                 dplyr::filter(
      #                     subtype_group == local(input$mosaic_subset_choice)
      #                 ) %>%
      #                 dplyr::pull(group)
      #         } else {
      #             subtypes <- "none"
      #         }
      #         return(subtypes)
      #     })
      #
      #     output$mosaicPlot <- plotly::renderPlotly({
      #
      #
      #         shiny::req(
      #             group_values_con(),
      #             group_internal_choice(),
      #             input$mosaic_group_choice,
      #             subtypes(),
      #             mosaic_subtypes(),
      #             groups_con(),
      #             plot_colors()
      #         )
      #         con <- group_values_con() %>%
      #             dplyr::select(
      #                 y = local(group_internal_choice()),
      #                 x = local(input$mosaic_group_choice)
      #             )
      #
      #         if(group_internal_choice() == "TCGA_Subtype"){
      #             con <- dplyr::filter(con, y %in% local(subtypes()))
      #         } else if (input$mosaic_group_choice == "TCGA_Subtype"){
      #             con <- dplyr::filter(con, x %in% local(mosaic_subtypes()))
      #         }
      #
      #         mosaic_plot_tbl <- con %>%
      #             dplyr::filter_all(dplyr::all_vars(!is.na(.))) %>%
      #             dplyr::as_tibble()
      #
      #         shiny::validate(shiny::need(
      #             nrow(mosaic_plot_tbl) > 0,
      #             "Group choices have no samples in common"
      #         ))
      #
      #         y_display_name <- groups_con() %>%
      #             translate_value(
      #                 group_internal_choice(),
      #                 "parent_group",
      #                 "parent_group_display"
      #             ) %>%
      #             unique()
      #
      #         x_display_name <- groups_con() %>%
      #             translate_value(
      #                 input$mosaic_group_choice,
      #                 "parent_group",
      #                 "parent_group_display"
      #             ) %>%
      #             unique()
      #
      #         create_mosaicplot(
      #             mosaic_plot_tbl,
      #             fill_colors = plot_colors(),
      #             title = stringr::str_c(
      #                 y_display_name,
      #                 "by",
      #                 x_display_name,
      #                 sep = " "
      #             )
      #         )
      #     })

      # return ------------------------------------------------------------------
      return(cohort_obj)
    }
  )
}

