
get_gene_from_url <- function(url_query){
    gene  <- url_query[['gene']]
    if (!is.null(gene)) return(gene)
    return(NA)
}

create_io_target_gene_list <- function(tbl, group){
    tbl %>%
        dplyr::select(
            class   = tidyselect::all_of(group),
            display = "hgnc",
            feature = "entrez"
        ) %>%
        create_nested_named_list(.)
}

get_io_hgnc_from_tbl <- function(tbl, .entrez){
    tbl %>%
        dplyr::filter(.data$entrez == .entrez) %>%
        dplyr::pull("hgnc")
}

build_io_target_distplot_tbl <- function(cohort_object, gene, scale_method){
    cohort_object %>%
        query_gene_expression_with_cohort_object(entrez = gene) %>%
        dplyr::inner_join(cohort_object$sample_tbl, by = "sample") %>%
        dplyr::select(.data$group, "value" = .data$rna_seq_expr, "label" = "sample") %>%
        scale_tbl_value_column(scale_method) %>%
        dplyr::select("x" = .data$group, "y" = .data$value, "label")
}

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
