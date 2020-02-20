with_test_db_env({
  test_that("Transform Feature String", {
      feature <- "Leukocyte Fraction"
      expect_equal(transform_feature_string(feature, "None"), feature)
      expect_equal(
          transform_feature_string(feature, "Log2"),
          "Log2( Leukocyte Fraction )"
      )
      expect_equal(
          transform_feature_string(feature, "Log2 + 1"),
          "Log2( Leukocyte Fraction + 1 )"
      )
      expect_equal(
          transform_feature_string(feature, "Log10"),
          "Log10( Leukocyte Fraction )"
      )
      expect_equal(
          transform_feature_string(feature, "Log10 + 1"),
          "Log10( Leukocyte Fraction + 1 )"
      )
      expect_equal(
          transform_feature_string(feature, "Squared"),
          "Leukocyte Fraction**2"
      )
      expect_equal(
          transform_feature_string(feature, "Reciprocal"),
          "1/Leukocyte Fraction"
      )
  })

  test_that("Transform Feature Formula", {
      feature <- "Leukocyte Fraction"
      expect_equal(transform_feature_formula(feature, "None"), feature)
      expect_equal(
          transform_feature_formula(feature, "Log10"),
          "I(log10(Leukocyte Fraction))"
      )
      expect_equal(
          transform_feature_formula(feature, "Squared"),
          "I(Leukocyte Fraction**2)"
      )
      expect_equal(
          transform_feature_formula(feature, "Reciprocal"),
          "I(1/Leukocyte Fraction)"
      )
  })

  test_that("Assert Tibble Has Columns", {
      tbl1 <- dplyr::tibble(
          col1 = c("value1", "value2"),
          col2 = c("A", "B"),
          col3 = c("C", "C")
      )
      expect_equal(assert_tbl_has_columns(tbl1, c("col1", "col2")), NULL)
      expect_error(
          assert_tbl_has_columns(tbl1, c("cola", "col2")),
          "tbl has missing columns: cola"
      )
      expect_error(
          assert_tbl_has_columns(tbl1, c("cola", "colb")),
          "tbl has missing columns: cola, colb"
      )
  })

  test_that("Assert Tibble has Rows", {
      tbl1 <- dplyr::tibble(
          "col1" = c("value1", "value2"),
          "col2" = c("A", "B"),
          "col3" = c("C", "C")
      )
      tbl2 <- filter(tbl1, col1 == "value3")
      expect_equal(assert_tbl_has_rows(tbl1), NULL)
      expect_error(assert_tbl_has_rows(tbl2), "result tbl is empty")
  })

  test_that("Create Plotly Label", {
      tbl <- dplyr::tribble(
          ~name,   ~name2,  ~group, ~v1, ~v2,
          "name1", "name5", "g1",   1,   1.1,
          "name2", "name6", "g1",   2,   2.2,
          "name3", "name7", "g2",   3,   3.3,
          "name4", "name8", "g2",   4,   4.4,
      )
      result1 <- create_plotly_label(
          tbl, name, group, c("v1", "v2"), title = "title"
      )
      result2 <- create_plotly_label(
          tbl, name2, group, c("v1", "v2"), title = "title"
      )
      expect_named(result1, c("name", "name2", "group", "label", "v1", "v2"))
      expect_named(result2, c("name", "name2", "group", "label", "v1", "v2"))
      expect_equal(
          result1$label[[1]],
          "<b>title:</b> name1 (g1)</br></br>V1: 1.000</br>V2: 1.100"
      )
      expect_equal(
          result1$label[[3]],
          "<b>title:</b> name3 (g2)</br></br>V1: 3.000</br>V2: 3.300"
      )
      expect_equal(
          result2$label[[1]],
          "<b>title:</b> name5 (g1)</br></br>V1: 1.000</br>V2: 1.100"
      )
      expect_equal(
          result2$label[[3]],
          "<b>title:</b> name7 (g2)</br></br>V1: 3.000</br>V2: 3.300"
      )
  })

  test_that("Add Plotly Value Label", {
      tbl1 <- dplyr::tribble(
          ~label,  ~V1, ~V2, ~V3,
          "l1",    1,   2,   3,
          "l2",    4,   5,   6,
          "l3",    7,   8,   9,
          "l4",    10,  11,  12
      )
      result1 <- add_plotly_value_label(tbl1, "V1")
      result2 <- add_plotly_value_label(tbl1, c("V1", "V2"))
      result3 <- add_plotly_value_label(tbl1, c("V1", "V2", "V3"))
      expect_equal(
          result1$value_label,
          c("V1: 1.000", "V1: 4.000", "V1: 7.000", "V1: 10.000")
      )
      expect_equal(
          result2$value_label,
          c("V1: 1.000</br>V2: 2.000", "V1: 4.000</br>V2: 5.000",
            "V1: 7.000</br>V2: 8.000", "V1: 10.000</br>V2: 11.000"
          )
      )
      expect_equal(
          result3$value_label,
          c("V1: 1.000</br>V2: 2.000</br>V3: 3.000",
            "V1: 4.000</br>V2: 5.000</br>V3: 6.000",
            "V1: 7.000</br>V2: 8.000</br>V3: 9.000",
            "V1: 10.000</br>V2: 11.000</br>V3: 12.000"
          )
      )
  })

  test_that("Get Unique Values from Column", {
      tbl1 <- dplyr::tibble("col" = c("value1", "value2"))
      tbl2 <- dplyr::tibble("col" = c("value1", "value1"))
      tbl3 <- dplyr::tibble("col" = c("value1", NA))
      tbl4 <- dplyr::tibble("col" = c(5, 6))
      tbl5 <- dplyr::tibble("col" = c("value2", "value1"))

      expect_equal(get_unique_values_from_col(tbl1, col), c("value1", "value2"))
      expect_equal(get_unique_values_from_col(tbl2, col), "value1")
      expect_equal(get_unique_values_from_col(tbl3, col), "value1")
      expect_equal(get_unique_values_from_col(tbl4, col), c(5, 6))
      expect_equal(get_unique_values_from_col(tbl5, col), c("value2", "value1"))
  })

  test_that("Create Feature Named List", {
      result1 <- create_feature_named_list()
      expect_equal(typeof(result1), "list")
      expect_equal(typeof(unlist(result1)), "integer")
  })

  test_that("Create Nested Named List", {
      tbl    <- dplyr::tibble(
          class   = c(rep("Class1", 3), "Class2"),
          display = c("feature1", "feature2", "feature3", "feature4"),
          feature = 1:4
      )
      result1 <- create_nested_named_list(tbl)
      expect_named(result1, c("Class1", "Class2"))
      expect_length(result1, 2)
      expect_named(result1$Class1, c("feature1", "feature2", "feature3"))
      expect_length(result1$Class1, 3)
      expect_named(result1$Class2, "feature4")
      expect_length(result1$Class2, 1)
  })

  test_that("Scale Tibble Value Column", {
      tbl <- dplyr::tibble(value = 1:5)
      result1 <- scale_tbl_value_column(tbl)
      expect_named(result1, "value")
      result2 <- scale_tbl_value_column(tbl, scale_method = "Log2")
      expect_named(result2, "value")
      result3 <- scale_tbl_value_column(tbl, scale_method = "Log2 + 1")
      expect_named(result3, "value")
      expect_error(
          scale_tbl_value_column(tbl, scale_method = "Not a method"),
          regexp = "Scale method does not exist",
          fixed = T
      )
  })

  test_that("Log Tibble Value Column", {
      tbl <- dplyr::tibble(value = 1:5)
      result <- log_tbl_value_column(tbl)
      expect_named(result, "value")
  })

  test_that("Get Values from Eventdata Dataframe", {
      df <- data.frame(x = c(rep("C1", 3)), y = 1:3, stringsAsFactors = F)
      expect_equal(get_values_from_eventdata(df), "C1")
  })
})
