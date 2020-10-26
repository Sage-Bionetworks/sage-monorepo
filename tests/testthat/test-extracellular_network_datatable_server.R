test_that("extracellular_network_datatable_server", {
  shiny::testServer(
    extracellular_network_datatable_server,
    args = list(
      "cohort_obj" = shiny::reactive(NULL),
      "network_output" = shiny::reactiveVal(
        list(
          "nodes" = dplyr::tibble(
            "Node" = character(),
            "Friendly Name" = character(),
            "Type" = character(),
            "Group" = character(),
            "Abundance" = double()
          ),
          "edges" = dplyr::tibble(
            "From" = character(),
            "From (Friendly Name)" = character(),
            "To" = character(),
            "To (Friendly Name)" = character(),
            "Group" = character(),
            "Concordance" = double()
          )
        )
      )
    ),
    {
      expect_type(output$edges_dt, "character")
      expect_type(output$nodes_dt, "character")
    }
  )
})
