#' Build IO Target Tibble
#'
#' #' @importFrom magrittr %>%
build_io_target_tbl <- function(){
    paste0(
        "SELECT a.id, a.hgnc, a.entrez, a.io_landscape_name AS friendly_name, ",
        "p.name AS pathway, t.name AS therapy, a.description FROM (",
        create_get_genes_by_type_query("io_target"), ") a ",
        "LEFT JOIN pathways p ON a.pathway_id = p.id ",
        "LEFT JOIN therapy_types t ON a.therapy_type_id = t.id "
    ) %>%
        perform_query("Build IO Target Tibble")
}

get_gene_from_url <- function(url_query){
    gene  <- url_query[['gene']]
    if (!is.null(gene)) {
        url_gene <- gene
    } else {
        url_gene <- NA
    }
    return(url_gene)
}

create_io_target_gene_list <- function(tbl, group){
    tbl %>%
        dplyr::select(
            class   = tidyselect::all_of(group),
            display = "hgnc",
            feature = "id"
        ) %>%
        create_nested_named_list()
}

build_io_target_distplot <- function(gene_id, sample_tbl, scale_method){
    gene_id %>%
        as.integer() %>%
        build_gene_expression_tbl_by_gene_ids() %>%
        dplyr::inner_join(sample_tbl, by = "sample_id") %>%
        dplyr::select(.data$group, value = .data$rna_seq_expr) %>%
        scale_tbl_value_column(scale_method) %>%
        dplyr::select(x = .data$group, y = .data$value)
}

build_io_target_dt_tbl <- function(tbl){
    tbl %>%
        dplyr::select(
            Hugo            = .data$hgnc,
            `Entrez ID`     = .data$entrez,
            `Friendly Name` = .data$friendly_name,
            Pathway         = .data$pathway,
            `Therapy Type`  = .data$therapy,
            Description     = .data$description,
        ) %>%
        dplyr::mutate(url = paste0(
            "https://www.cancerresearch.org/scientists/",
            "immuno-oncology-landscape?2019IOpipelineDB=2019;Target;",
            .data$`Friendly Name`
        )) %>%
        dplyr::mutate(`Link to IO Landscape` =  paste0(
            "<a href=\'",
            .data$url,
            "\'>",
            .data$`Friendly Name`,
            "</a>"
        )) %>%
        dplyr::select(-.data$url)
}
