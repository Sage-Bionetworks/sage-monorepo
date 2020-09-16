volcano_plot_tbl <- shiny::reactiveVal(
  dplyr::tibble(
    "log10_fold_change" = 1:10,
    "log10_p_value" = 1:10,
    "label" = "l"
  )
)

test_that("volcano_plot_server", {
  shiny::testServer(
    volcano_plot_server,
    args = list("volcano_plot_tbl" = volcano_plot_tbl),
    {
      expect_type(output$volcano_plot, "character")
      expect_error(selected_volcano_result())
    }
  )
})
