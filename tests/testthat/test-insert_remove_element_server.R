test_that("insert_remove_element_server", {
  shiny::testServer(
    insert_remove_element_server,
    args = list(
      "element_module" = shiny::reactiveVal(
        categorical_model_covariate_element_server
      ),
      "element_module_ui" = shiny::reactiveVal(
        categorical_model_covariate_element_ui
      )
    ),
    {
      expect_true(shiny::is.reactive(session$getReturned()))
    }
  )
})
