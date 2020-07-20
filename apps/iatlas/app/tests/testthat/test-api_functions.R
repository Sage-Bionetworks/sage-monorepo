with_test_api_env({

    query_dir  <- system.file("queries", package = "iatlas.app")
    test_query <- stringr::str_c(query_dir,  "/cohort_selection.txt")


    test_that("add_ghql_query_from_text_file", {
        ghql_query_object <- ghql::Query$new()
        expect_length(ghql_query_object$queries, 0)
        add_ghql_query_from_text_file(
            "test_query1",
            test_query,
            ghql_query_object
        )
        expect_length(ghql_query_object$queries, 1)
        expect_named(ghql_query_object$queries, "test_query1")
        add_ghql_query_from_text_file(
            "test_query2",
            test_query,
            ghql_query_object
        )
        expect_length(ghql_query_object$queries, 2)
        expect_named(ghql_query_object$queries, c("test_query1", "test_query2"))
    })

    test_that("create_global_ghql_query_object", {
        if (exists("ghql_query_object", envir = .GlobalEnv)) {
            rm(ghql_query_object, envir = .GlobalEnv)
        }
        create_global_ghql_query_object()
        expect_true(exists("ghql_query_object", envir = .GlobalEnv))
        expect_length(.GlobalEnv$ghql_query_object$queries, 0)
        rm(ghql_query_object, envir = .GlobalEnv)
    })

    test_that("create_and_add_all_queries_to_qry_obj", {
        if (exists("ghql_query_object", envir = .GlobalEnv)) {
            rm(ghql_query_object, envir = .GlobalEnv)
        }
        n_queries <- length(list.files(query_dir))
        create_and_add_all_queries_to_qry_obj(query_dir = query_dir)
        expect_length(.GlobalEnv$ghql_query_object$queries, n_queries)
    })

    test_that("perform_api_query", {
        ghql_query_object <- ghql::Query$new()
        add_ghql_query_from_text_file(
            "test_query1",
            test_query,
            ghql_query_object
        )
        result <- perform_api_query(
            "test_query1",
            list(
                dataSet = "TCGA",
                related = "Immune_Subtype",
                feature = list(),
                featureClass = list()
            ),
            ghql_query_object
        )
        expect_named(result, "tags")
    })
})




