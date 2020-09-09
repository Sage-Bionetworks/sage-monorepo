plot_tbl <- shiny::reactiveVal(
  dplyr::tibble("x" = c("C1", "C2", "C3"), y = c(1:3))
)
plot_eventdata <- shiny::reactiveVal(
  dplyr::tibble("x" = "C1", y = 1)
)

group_tbl <- shiny::reactiveVal(
  dplyr::tibble(
    "group" = c("C1", "C2", "C3"),
    "name" = c("name1", "name2", "name3"),
    "characteristics" = c("c1", "c2", "c3")
  )
)

test_that("module_works", {
  shiny::testServer(
    plotly_server,
    args = list(
      "plot_tbl" = plot_tbl,
      "plot_eventdata" = plot_eventdata,
      "group_tbl" = group_tbl
    ),
    {
      expect_type(output$download_tbl, "character")
      expect_type(output$plot_group_text, "character")
      expect_equal(output$plot_group_text, "name1: c1")
    }
  )
})


