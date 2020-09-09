tbl <- shiny::reactiveVal(
  dplyr::tibble("x" = c("C1", "C2", "C3"), y = c(1:3))
)

test_that("module_works", {
  shiny::testServer(
    data_table_server,
    args = list("tbl" = tbl, color = T),
    {
      expect_type(output$download_table, "character")
      expect_type(output$download_table, "character")
    }
  )
})
