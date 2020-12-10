test_that("numeric_filter_element_server_pcawg", {
  shiny::testServer(
    numeric_filter_element_server,
    args = list(
      "reactive_values" = shiny::reactiveValues(),
      "module_id" = "element1",
      "numeric_named_list" = shiny::reactiveVal(
        list(
          "B Cells" =  "feature:B_cells_Aggregate2",
          "Gender" = "clinical:height"
        )
      ),
      "dataset" = shiny::reactiveVal("PCAWG")
    ),
    {
      expect_type(output$select_ui, "list")

      session$setInputs("numeric_choice" = "feature:B_cells_Aggregate2")
      expect_equal(numeric_type(), "feature")
      expect_equal(numeric_name(), "B_cells_Aggregate2")
      expect_type(features_tbl(), "list")
      expect_equal(nrow(features_tbl()), 1)
      expect_type(feature_min(), "double")
      expect_type(feature_max(), "double")
      expect_length(feature_min(), 1)
      expect_length(feature_max(), 1)
      expect_type(output$slider_ui, "list")
      session$setInputs("range" = c(100, 120))
      expect_true(shiny::is.reactivevalues(session$getReturned()))

    }
  )
})

test_that("numeric_filter_element_server_tcga", {
  shiny::testServer(
    numeric_filter_element_server,
    args = list(
      "reactive_values" = shiny::reactiveValues(),
      "module_id" = "element1",
      "numeric_named_list" = shiny::reactiveVal(
        list(
          "Gender" = "clinical:height"
        )
      ),
      "dataset" = shiny::reactiveVal("TCGA")
    ),
    {
      expect_type(output$select_ui, "list")

      session$setInputs("numeric_choice" = "clinical:height")
      expect_equal(numeric_type(), "clinical")
      expect_equal(numeric_name(), "height")
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



#
# test_that("group_filter_element_server", {
#   shiny::testServer(
#     group_filter_element_server,
#     args = list(
#       "reactive_values" = shiny::reactiveValues(),
#       "module_id" = "element1",
#       "group_named_list" = shiny::reactiveVal(
#         list(
#           "Immune Subtype" = "tag:Immune_Subtype",
#           "Gender" = "clinical:gender"
#         )
#       ),
#       "dataset" = shiny::reactiveVal("TCGA")
#     ),
#     {
#       expect_type(output$select_ui, "list")
#
#       session$setInputs("parent_group_choice" = "tag:Immune_Subtype")
#       expect_equal(group_type(), "tag")
#       expect_equal(parent_group(), "Immune_Subtype")
#       expect_type(group_choices(), "character")
#       expect_equal(group_choices(), c("C1", "C2", "C3", "C4", "C5", "C6"))
#       expect_type(output$checkbox_ui, "list")
#       session$setInputs("tag_choices" = "C1")
#       expect_true(shiny::is.reactivevalues(session$getReturned()))
#
#
#       session$setInputs("parent_group_choice" = "clinical:gender")
#       expect_equal(group_type(), "clinical")
#       expect_equal(parent_group(), "gender")
#       expect_type(group_choices(), "character")
#       expect_equal(group_choices(), c("female", "male"))
#       expect_type(output$checkbox_ui, "list")
#       expect_true(shiny::is.reactivevalues(session$getReturned()))
#     }
#   )
# })
#
# test_that("numeric_model_covariate_element_server", {
#   shiny::testServer(
#     numeric_model_covariate_element_server,
#     args = list(
#       "reactive_values" = shiny::reactiveValues(),
#       "module_id" = "element1",
#       "covariate_list" = shiny::reactiveVal(
#         list("B Cells" =  "B_cells_Aggregate2")
#       )
#     ),
#     {
#       expect_type(output$select_covariate_ui, "list")
#       session$setInputs("covariate_choice_name" = "B_cells_Aggregate2")
#       expect_type(output$select_transformation_ui, "list")
#       session$setInputs("transformation_choice" = "None")
#       expect_true(shiny::is.reactivevalues(session$getReturned()))
#     }
#   )
# })
#
# test_that("categorical_model_covariate_element_server", {
#   shiny::testServer(
#     categorical_model_covariate_element_server,
#     args = list(
#       "reactive_values" = shiny::reactiveValues(),
#       "module_id" = "element1",
#       "covariate_list" = shiny::reactiveVal("C1", "C2")
#     ),
#     {
#       expect_type(output$select_covariate_ui, "list")
#       session$setInputs("covariate_choice" = c("C1", "C2"))
#       expect_true(shiny::is.reactivevalues(session$getReturned()))
#     }
#   )
# })
