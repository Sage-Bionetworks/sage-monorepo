with_test_db_env({
    test_that("connect_to_db", {
        pool <- connect_to_db()
        expect_type(pool, "environment")
    })
})
