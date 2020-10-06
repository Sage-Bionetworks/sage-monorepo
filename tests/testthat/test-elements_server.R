test_that("numeric_filter_element_server", {
  shiny::testServer(
    numeric_filter_element_server,
    args = list(
      "reactive_values" = shiny::reactiveValues(),
      "module_id" = "element1",
      "feature_named_list" = shiny::reactiveVal(
        list("B Cells" =  "B_cells_Aggregate2")
      ),
      "dataset" = shiny::reactiveVal("PCAWG")
    ),
    {
      expect_type(output$select_ui, "list")
      session$setInputs("feature_choice" = "B_cells_Aggregate2")
      expect_type(features_tbl(), "list")
      expect_equal(nrow(features_tbl()), 1)
      expect_type(feature_min(), "double")
      expect_type(feature_max(), "double")
      expect_length(feature_min(), 1)
      expect_length(feature_max(), 1)
      expect_type(output$slider_ui, "list")
      session$setInputs("range" = c(0.0, 1.0))
      expect_true(shiny::is.reactivevalues(session$getReturned()))
    }
  )
})

test_that("numeric_filter_element_server", {
  shiny::testServer(
    tag_filter_element_server,
    args = list(
      "reactive_values" = shiny::reactiveValues(),
      "module_id" = "element1",
      "tag_named_list" = shiny::reactiveVal(
        list("Immune Subtype" = "Immune_Subtype")
      ),
      "dataset" = shiny::reactiveVal("PCAWG")
    ),
    {
      expect_type(output$select_ui, "list")
      session$setInputs("parent_tag_choice" = "Immune_Subtype")
      expect_type(output$checkbox_ui, "list")
      session$setInputs("tag_choices" = "C1")
      expect_true(shiny::is.reactivevalues(session$getReturned()))
    }
  )
})

test_that("numeric_model_covariate_element_server", {
  shiny::testServer(
    numeric_model_covariate_element_server,
    args = list(
      "reactive_values" = shiny::reactiveValues(),
      "module_id" = "element1",
      "covariate_list" = shiny::reactiveVal(
        list("B Cells" =  "B_cells_Aggregate2")
      )
    ),
    {
      expect_type(output$select_covariate_ui, "list")
      session$setInputs("covariate_choice_name" = "B_cells_Aggregate2")
      expect_type(output$select_transformation_ui, "list")
      session$setInputs("transformation_choice" = "None")
      expect_true(shiny::is.reactivevalues(session$getReturned()))
    }
  )
})

test_that("categorical_model_covariate_element_server", {
  shiny::testServer(
    categorical_model_covariate_element_server,
    args = list(
      "reactive_values" = shiny::reactiveValues(),
      "module_id" = "element1",
      "covariate_list" = shiny::reactiveVal("C1", "C2")
    ),
    {
      expect_type(output$select_covariate_ui, "list")
      session$setInputs("covariate_choice" = c("C1", "C2"))
      expect_true(shiny::is.reactivevalues(session$getReturned()))
    }
  )
})
