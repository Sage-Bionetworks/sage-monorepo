distplot_tbl <- shiny::reactiveVal(
  dplyr::tibble("x" = c("C1", "C2", "C3"), y = c(1:3))
)

test_that("module_works_violin", {
  shiny::testServer(
    distribution_plot_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(pcawg_immune_subtype_cohort_obj),
      "distplot_tbl" = distplot_tbl,
      "distplot_type" = shiny::reactiveVal("Violin"),
      "distplot_ylab" = shiny::reactiveVal("ylab"),
      "distplot_title" = shiny::reactiveVal("title")
    ),
    {
      expect_equal(distplot_function(), create_violinplot)
      expect_type(output$distplot, "character")
      expect_error(output$histplot, class = "shiny.silent.error")
    }
  )
})

test_that("module_works_box", {
  shiny::testServer(
    distribution_plot_server,
    args = list(
      "cohort_obj" = shiny::reactiveVal(pcawg_immune_subtype_cohort_obj),
      "distplot_tbl" = distplot_tbl,
      "distplot_type" = shiny::reactiveVal("Box"),
      "distplot_ylab" = shiny::reactiveVal("ylab"),
      "distplot_title" = shiny::reactiveVal("title")
    ),
    {
      expect_equal(distplot_function(), create_boxplot)
      expect_type(output$distplot, "character")
      expect_error(output$histplot, class = "shiny.silent.error")
    }
  )
})
