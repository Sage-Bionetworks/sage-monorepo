
test_that("Create Group Text from Eventdata Dataframe", {
    event_df  <- data.frame(x = c(rep("G1", 3)), y = 1:3, stringsAsFactors = F)
    group_tbl <- dplyr::tibble(
        group = c("G1", "G2"),
        name = c("name1", "name2"),
        characteristics = c("c1", "c2")
    )
    result1 <- iatlas.app::create_group_text_from_eventdata(
        event_df, group_tbl
    )
    expect_equal(result1, "name1: c1")
})


