test_that("numeric_filter_element_ui", {
  expect_type(numeric_filter_element_ui("id"), "list")
})

test_that("group_filter_element_ui", {
  expect_type(group_filter_element_ui("id"), "list")
})

test_that("numeric_model_covariate_element_ui", {
  expect_type(numeric_model_covariate_element_ui("id"), "list")
})

test_that("categorical_model_covariate_element_ui", {
  expect_type(categorical_model_covariate_element_ui("id"), "list")
})
