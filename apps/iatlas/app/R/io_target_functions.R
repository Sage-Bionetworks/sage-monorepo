
get_gene_from_url <- function(url_query){
    gene  <- url_query[['gene']]
    if (!is.null(gene)) return(gene)
    return(NA)
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
