build_custom_group_tbl <- function(.dataset){
  dplyr::tribble(
    ~name,                 ~dataset, ~type,
    "Immune Feature Bins", "TCGA",    "custom",
    "Driver Mutation",     "TCGA",    "custom",
    "Immune Feature Bins", "PCAWG",   "custom",
  ) %>%
    dplyr::filter(.data$dataset == .dataset) %>%
    dplyr::mutate("display" = .data$name) %>%
    dplyr::select("display", "name")
}

build_clinical_group_tbl <- function(.dataset){
  .dataset %>%
    iatlas.api.client::query_patients(datasets = .) %>%
    dplyr::select("ethnicity", "gender", "race") %>%
    tidyr::pivot_longer(cols = dplyr::everything()) %>%
    tidyr::drop_na() %>%
    dplyr::select("name") %>%
    dplyr::distinct() %>%
    dplyr::mutate(
      "display" = stringr::str_replace_all(.data$name, "_", " "),
      "display" = stringr::str_to_title(.data$display)
    ) %>%
    dplyr::select("display", "name")
}

build_cohort_group_list <- function(tag_tbl, custom_tbl, clinical_tbl){
  dplyr::bind_rows(tag_tbl, custom_tbl, clinical_tbl) %>%
    dplyr::select("display", "name") %>%
    tibble::deframe(.)
}

build_cohort_mutation_tbl <- function(){
  iatlas.api.client::query_mutations(type = "driver_mutation") %>%
    dplyr::rename("mutation" = "mutation_name")
}
