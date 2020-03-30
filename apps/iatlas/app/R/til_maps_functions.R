show_tilmap_submodules <- function(cohort_obj){
    til_features <- cohort_obj %>%
        purrr::pluck("feature_tbl") %>%
        dplyr::filter(.data$class %in% "TIL Map Characteristic") %>%
        dplyr::pull(.data$display)

    length(til_features > 0)
}



#' Build Tilmap Sample Tibble
#'
#' @param sample_tbl A tibble with columns sample_id and group
#' @importFrom magrittr %>%
#' @importFrom dplyr inner_join
build_tm_sample_tbl <- function(sample_tbl){
    tbl <-
        paste(
            "SELECT s.id AS sample_id, s.name AS sample_name, ",
            "sl.name AS slide_barcode FROM samples s ",
            "INNER JOIN slides sl ON s.patient_id = sl.id ",
            "WHERE sl.name IS NOT NULL"
        ) %>%
        perform_query("Get sample table") %>%
        dplyr::inner_join(sample_tbl, by = "sample_id")
    return(tbl)
}
