#' Create Cohort Module String
#'
#' @param .dataset a string in the dataset column of the tibble
#' @param tbl A tibble with columns dataset, and module
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr filter pull
create_cohort_module_string <- function(.dataset, tbl = NULL){
    dataset_to_module_tbl <- dplyr::tribble(
        ~module,                  ~dataset,
        "Sample Group Overview",  "TCGA",
        "Tumor Microenvironment", "TCGA",
        "Immune Feature Trends",  "TCGA",
        "Clinical Outcomes",      "TCGA",
        "IO Targets",             "TCGA",
        "TIL Maps",               "TCGA",
        "Driver Associations",    "TCGA",
        "Sample Group Overview",  "PCAWG",
        "Tumor Microenvironment", "PCAWG",
        "Immune Feature Trends",  "PCAWG",
        "IO Targets",             "PCAWG"
    )

    if (is.null(tbl)) tbl <- dataset_to_module_tbl

    modules <- tbl %>%
        dplyr::filter(.data$dataset == .dataset) %>%
        dplyr::pull(.data$module)
    if (length(modules) == 0){
        return("No modules currently available for selected dataset")
    } else {
        msg <- modules %>%
            paste0(collapse = ", ") %>%
            paste0(
                "Modules available for dataset ",
                .dataset,
                ": ",
                .
            )
        return(msg)
    }

}
