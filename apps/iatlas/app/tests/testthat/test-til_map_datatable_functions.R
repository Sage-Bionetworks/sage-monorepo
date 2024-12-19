
    # test_that("Build Tilmap Datatbale Tibble", {
    #     til_features <- 'TIL Map Characteristic' %>%
    #         get_class_id_from_name() %>%
    #         build_feature_value_tbl_from_class_ids %>%
    #         dplyr::pull(feature) %>%
    #         unique()
    #
    #     tbl1 <- dplyr::tibble(
    #         sample_id = 1:10000,
    #         group = "G1",
    #         sample_name = paste0("S", 1:10000),
    #         slide_barcode = paste0("BS", 1:10000),
    #     )
    #     result1 <- build_tm_dt_tbl(tbl1)
    #     expect_named(
    #         result1,
    #         c("Sample", "Selected Group", "Image", til_features)
    #     )
    # })
    #
    # test_that("Create Tilmap Datatbale Tibble Query", {
    #     expect_equal(
    #         create_build_tm_dt_tbl_query(),
    #         paste0(
    #             "SELECT a.sample_id, a.value, b.display FROM ( ",
    #             "SELECT feature_id, sample_id, value FROM features_to_samples ",
    #             "WHERE feature_id IN ( ",
    #             "SELECT id AS feature FROM features WHERE class_id = ",
    #             "( SELECT id FROM classes WHERE name = ",
    #             "'TIL Map Characteristic' ) ) ) a INNER JOIN ",
    #             "(SELECT id, display from features) b ON a.feature_id = b.id"
    #         )
    #     )
    # })
    #
    # test_that("Create Tilmap Slide Link", {
    #     expect_equal(
    #         create_tm_slide_link("barcode"),
    #         paste0(
    #             "<a href=\"",
    #             "https://quip1.bmi.stonybrook.edu:443/",
    #             "camicroscope/osdCamicroscope.php?tissueId=",
    #             "barcode\">barcode</a>"
    #         )
    #     )
    # })

