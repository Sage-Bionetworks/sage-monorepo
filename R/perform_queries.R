perform_query <- function(query, string = "") {
    tictoc::tic(paste(
        "Time taken to perfrom query",
        string
    ))
    current_pool <- pool::poolCheckout(.GlobalEnv$pool)
    tbl <- query %>%
        dplyr::sql() %>%
        pool::dbGetQuery(current_pool, .) %>%
        dplyr::as_tibble()
    pool::poolReturn(current_pool)
    tictoc::toc()
    return(tbl)
}

get_feature_id_from_display <- function(display){
    display %>%
        create_get_feature_id_from_display_query() %>%
        perform_query("Get feature id from display") %>%
        dplyr::pull(id)
}

get_feature_display_from_id <- function(id){
    id %>%
        create_get_feature_display_from_id_query() %>%
        perform_query("Get feature display from id") %>%
        dplyr::pull(display)
}

get_feature_values_from_ids <- function(ids){
    ids %>%
        create_feature_value_query_from_ids() %>%
        perform_query("Get feature values from ids")
}

get_class_id_from_name <- function(name){
    name %>%
        create_get_class_id_from_name_query %>%
        perform_query("Get class id from name") %>%
        dplyr::pull(id)
}

create_class_list <- function(){
    "SELECT name, id FROM classes" %>%
        perform_query("get class id") %>%
        tibble::deframe()
}

build_immunomodultors_tbl <- function(){
    create_build_immunomodulators_tbl_query() %>%
        perform_query("Build Immunomodultors Table")
}

build_gene_expression_tbl_by_gene_ids <- function(gene_ids){
    gene_ids %>%
        create_build_get_gene_expression_tbl_by_gene_ids_query() %>%
        perform_query("Build gene expression table")
}

get_sample_ids_from_dataset <- function(dataset){
    if (dataset == "TCGA") {
        tag_display <- "TCGA Study"
    } else if (dataset == "PCAWG") {
        tag_display <- "PCAWG Study"
    } else {
        tag_display = dataset
    }

    tag_display %>%
        create_get_sample_ids_from_parent_tag_display_query() %>%
        perform_query("Get Dataset Sample Ids") %>%
        dplyr::pull(sample_id)
}

get_gene_id_from_hgnc <- function(hgnc){
    query <- paste0(
        "SELECT id FROM genes WHERE hgnc = '",
        hgnc,
        "'"
    )
    query %>%
        perform_query("get gene id") %>%
        dplyr::pull(id)
}

get_gene_hgnc_from_id <- function(gene_id){
    query <- paste0("SELECT hgnc FROM genes WHERE id = ", gene_id)
    query %>%
        perform_query("Get gene HGNC") %>%
        dplyr::pull(hgnc)
}

create_driver_mutation_list <- function(){
    paste(
        "SELECT hgnc, id FROM (",
        create_get_genes_by_type_query("driver_mutation"),
        ") a"
    ) %>%
        perform_query("Get mutation genes") %>%
        tibble::deframe()
}

build_cohort_tbl_by_group <- function(sample_ids, group){
    paste0(
        "SELECT sts.sample_id, g.name AS group, g.display AS name, g.characteristics, g.color FROM (",
        create_parent_group_query_from_display(group),
        ") g INNER JOIN samples_to_tags sts ON g.id = sts.tag_id ",
        "WHERE sample_id IN (",
        numeric_values_to_query_list(sample_ids),
        ")"
    ) %>%
        perform_query("Build Cohort Table")
}

build_cohort_tbl_by_feature_id <- function(sample_ids, feature_id){
    paste(
        "SELECT a.sample_id, a.value FROM (",
        create_feature_value_query_from_ids(feature_id),
        ") a WHERE a.sample_id IN (",
        numeric_values_to_query_list(sample_ids),
        ")"
    ) %>%
        perform_query("Build immune feature bin table")
}

build_feature_value_tbl <- function(feature_id){
    create_feature_value_query_from_ids(feature_id) %>%
        perform_query("Build feature value table")
}


