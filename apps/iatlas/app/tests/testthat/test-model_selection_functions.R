
test_that("get_numeric_covariate_ids_from_output", {
    input1 <- list(
        "element1" = list(
            "covariate_choice_id" = 1L,
            "transformation_choice" = "None"
        )
    )
    input2 <- list(
        "element1" = list(
            "covariate_choice_id" = 1L,
            "transformation_choice" = "None"
        ),
        "element2" = list(
            "covariate_choice_id" = 2L,
            "transformation_choice" = "None"
        )
    )
    expect_null(
        get_items_from_numeric_covariate_output(
            NULL,
            "covariate_choice_id"
        )
    )
    expect_null(
        get_items_from_numeric_covariate_output(
            NULL,
            "transformation_choice"
        )
    )
    expect_equal(
        get_items_from_numeric_covariate_output(
            input1,
            "covariate_choice_id"
        ),
        1L
    )
    expect_equal(
        get_items_from_numeric_covariate_output(
            input2,
            "covariate_choice_id"
        ),
        c(1L, 2L)
    )
    expect_equal(
        get_items_from_numeric_covariate_output(
            input1,
            "transformation_choice"
        ),
        "None"
    )
    expect_equal(
        get_items_from_numeric_covariate_output(
            input2,
            "transformation_choice"
        ),
        c("None", "None")
    )
})
test_that("get_names_from_categorical_covariate_output", {
    input1 <- list("element1" = "Immune_Subtype")
    input2 <- list(
        "element1" = "Immune_Subtype",
        "element2" = "TCGA_Subtype"
    )
    input3 <- list(
        "element1" = NULL,
        "element2" = "Immune_Subtype",
        "element3" = "TCGA_Subtype"
    )
    expect_null(get_names_from_categorical_covariate_output(NULL))
    expect_equal(
        get_names_from_categorical_covariate_output(input1),
        "Immune_Subtype"
    )
    expect_equal(
        get_names_from_categorical_covariate_output(input2),
        c("Immune_Subtype", "TCGA_Subtype")
    )
    expect_equal(
        get_names_from_categorical_covariate_output(input3),
        c("Immune_Subtype", "TCGA_Subtype")
    )
})

test_that("create_numerical_covariate_string", {
    expect_null(
        create_numerical_covariate_string(
            NULL,
            NULL,
            transform_feature_formula
        )
    )
    expect_type(
        create_numerical_covariate_string(
            1,
            "None",
            iatlasModules::transform_feature_formula
        ),
        "character"
    )
    expect_length(
        create_numerical_covariate_string(
            1,
            "None",
            iatlasModules::transform_feature_formula
        ),
        1L
    )
})

test_that("create_covariate_string", {
    expect_equal(
        create_covariate_string("str1", NULL, NULL),
        "str1"
    )
    expect_equal(
        create_covariate_string("str1", "str2", NULL),
        "str1 + str2"
    )
    expect_equal(
        create_covariate_string("str1", "str2", "str3"),
        "str1 + str2 + str3"
    )
})

