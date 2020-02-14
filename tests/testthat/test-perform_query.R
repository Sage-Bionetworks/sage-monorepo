with_test_db_env({
    test_that("perform_query", {
        pool <- connect_to_db()
        tbl <- perform_query("SELECT id, display from features", db_pool = pool)
        expect_named(tbl, c("id", "display"))
    })
})
