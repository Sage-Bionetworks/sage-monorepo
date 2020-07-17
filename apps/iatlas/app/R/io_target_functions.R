#' Get Gene From URL
#'
#' @param url_query A list with named value gene
get_gene_from_url <- function(url_query){
    gene  <- url_query[['gene']]
    if (!is.null(gene)) return(gene)
    return(NA)
}

#' Create IO Target Gene List
#'
#' @param tbl A Tibble with columns hgnc, id, and the value of the group var
#' @param group A Column in the tibble
#' @importFrom magrittr %>%
#' @importFrom dplyr select
#' @importFrom tidyselect all_of
create_io_target_gene_list <- function(tbl, group){
    tbl %>%
        dplyr::select(
            class   = tidyselect::all_of(group),
            display = "hgnc",
            feature = "entrez"
        ) %>%
        create_nested_named_list(.)
}

#' Build IO Target Distplot Tibble
#'
#' @param gene_id An Integer in the gene_id column of the genes_to_samples table
#' @param sample_tbl A tibble with columns sample_id, and group
#' @param scale_method A string
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr select inner_join
build_io_target_distplot_tbl <- function(gene_id, sample_tbl, scale_method){
    gene_id %>%
        as.integer() %>%
        build_gene_expression_tbl_by_gene_ids() %>%
        dplyr::inner_join(sample_tbl, by = "sample_id") %>%
        dplyr::select(.data$group, value = .data$rna_seq_expr) %>%
        scale_tbl_value_column(scale_method) %>%
        dplyr::select(x = .data$group, y = .data$value)
}

#' Build IO Target Datatable Tibble
#'
#' @param tbl A tibble
#' @importFrom magrittr %>%
#' @importFrom rlang .data
#' @importFrom dplyr select mutate
build_io_target_dt_tbl <- function(tbl){
    tbl %>%
        dplyr::select(
            Hugo            = .data$hgnc,
            `Entrez ID`     = .data$entrez,
            `Friendly Name` = .data$io_landscape_name,
            Pathway         = .data$pathway,
            `Therapy Type`  = .data$therapy_type,
            Description     = .data$description,
        ) %>%
        dplyr::mutate(`Link to IO Landscape` = create_io_landscape_links(
            .data$`Friendly Name`
        ))
}

#' Create IO Landscape Links
#'
#' @param genes A vector of strings
#' paste0("<a href='",mydata$url,"'>",mydata$url,"</a>")
create_io_landscape_links <- function(io_gene_names){
    stringr::str_c(
        "<a href='",
        "https://www.cancerresearch.org/scientists/",
        "immuno-oncology-landscape?viz1572545060618=2017;Target;",
        io_gene_names,
        "'>",
        io_gene_names,
        "</a>"
    )
}
