with_test_api_env({

  test_that("Format Feature Tibble", {
      result1 <- format_feature_tbl(dplyr::tibble(
          display = "", unit = "", class = ""
      ))
      expect_named(result1, c("Feature Name", "Variable Class", "Unit"))
  })

  test_that("Filter Feature Tibble", {
      tbl <- dplyr::tibble(class = c("class1", "class1", "class2"))
      result1 <- filter_feature_tbl(tbl, 1L)
      result2 <- filter_feature_tbl(tbl, 2L)
      result3 <- filter_feature_tbl(tbl, 3L)
      expect_named(result1, "class")
      expect_named(result2, "class")
      expect_named(result3, "class")
      expect_equal(result1$class, c("class1", "class1"))
      expect_equal(result2$class, c("class1", "class1"))
      expect_equal(result3$class, "class2")
  })

  test_that("Format Filtered Feature Tibble", {
      result1 <- format_filtered_feature_tbl(dplyr::tibble(
          display = "", unit = "", method_tag = "", order = 1
      ))
      expect_named(
          result1,
          c("Variable Class Order", "Feature Name", "Unit", "Methods Tag")
      )
  })
})
