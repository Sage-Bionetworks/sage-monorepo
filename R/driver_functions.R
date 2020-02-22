#' Build Univariate Driver Results Tibble
#'
#' @param group_name A string in the name column of the tags table
#' @param feature_id An integer in the feature_id column of the driver_results
#' table
#' @param min_wt An integer
#' @param min_mut An integer
#' @importFrom dplyr mutate
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_udr_results_tbl <- function(group_name, feature_id, min_wt, min_mut){
    paste0(
        "SELECT dr.p_value, dr.fold_change, dr.log10_p_value, dr.gene_id, ",
        "dr.tag_id, dr.log10_fold_change, dr.mutation_code_id, ",
        "g.gene, mc.code AS mutation_code, t.group ",
        "FROM ",
        paste0(
            "(",
            "SELECT * FROM driver_results ",
            "WHERE feature_id = ", feature_id,
            " AND n_wt >= ", min_wt,
            " AND n_mut >= ", min_mut,
            ") dr "
        ),
        "LEFT JOIN ",
        "(SELECT id AS gene_id, hgnc AS gene FROM genes) g ",
        "ON dr.gene_id = g.gene_id ",
        "LEFT JOIN ",
        "mutation_codes mc ",
        "ON dr.mutation_code_id = mc.id ",
        "INNER JOIN ",
        paste0(
            "(",
            "SELECT id AS tag_id, name AS group FROM tags WHERE id IN ",
            "(SELECT tag_id FROM tags_to_tags WHERE related_tag_id = ",
            "(SELECT id FROM tags WHERE display = '",
            group_name,
            "'))",
            ") t "
        ),
        "ON dr.tag_id = t.tag_id "
    ) %>%
        perform_query("Build Univariate Driver Results Tibble") %>%
        dplyr::mutate(label = paste0(
            .data$group, "; ", .data$gene, ":", .data$mutation_code
        ))
}

#' Build Driver Violin Tibble
#'
#' @param feature_id An integer in the features_to_samples table
#' @param gene_id An interger in the genes_samples_mutations table
#' @param tag_id An integer in the samples_to_tags table
#' @param mutation_id An integer in the genes_samples_mutations table
#' @importFrom dplyr select
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_driver_violin_tbl <- function(feature_id, gene_id, tag_id, mutation_id){

    subquery1 <- paste(
        "SELECT sample_id FROM samples_to_tags",
        "WHERE tag_id = ",
        tag_id
    )

    subquery2 <- paste0(
        "SELECT sample_id, value FROM features_to_samples ",
        "WHERE feature_id = ", feature_id, " ",
        "AND sample_id IN (", subquery1, ")"
    )

    subquery3 <- paste0(
        "SELECT sample_id, status FROM genes_samples_mutations ",
        "WHERE gene_id  = ", gene_id, " ",
        "AND mutation_code_id = ", mutation_id, " ",
        "AND sample_id IN (", subquery1, ")"
    )

    paste(
        "SELECT f.value, g.status FROM",
        "(", subquery2, ") f",
        "INNER JOIN",
        "(", subquery3, ") g",
        "ON f.sample_id = g.sample_id"
    ) %>%
        perform_query("build univariate driver violin table") %>%
        dplyr::select(x = .data$status, y = .data$value)
}

#' Build Multivariate Driver Response Tibble
#'
#' @param feature_id An integer in the feature_id column of the
#' features_to_samples table
#' @importFrom dplyr select
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_md_response_tbl <- function(feature_id){
    feature_id %>%
        as.integer() %>%
        build_feature_value_tbl_from_ids() %>%
        dplyr::select(response = .data$value, .data$sample_id)
}

#' Build Multivariate Driver Status Tibble
#'
#' @importFrom magrittr %>%
build_md_status_tbl <- function(){
    paste0(
        "SELECT gsm.gene_id, gsm.sample_id, gsm.mutation_code_id, gsm.status, ",
        "g.hgnc AS gene, mc.code AS mutation_code FROM genes_samples_mutations gsm ",
        "INNER JOIN genes g on gsm.gene_id = g.id ",
        "INNER JOIN mutation_codes mc ON gsm.mutation_code_id = mc.id ",
        "WHERE g.id IN (",
        paste(
            "SELECT gene_id FROM genes_to_types WHERE type_id = (",
            "SELECT id FROM gene_types WHERE name = 'driver_mutation'",
            ") LIMIT 10" # fix!!!!!!!!!!!!
        ),
        ")"
    ) %>%
        perform_query()
}

#' Build Multivariate Driver Combined Tibble
#'
#' @param response_tbl A tibble
#' @param sample_tbl A tibble
#' @param status_tbl A tibble
#' @param mode A string, either "By group" or "Across groups"
#'
#' @importFrom purrr reduce
#' @importFrom dplyr select mutate
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_md_combined_tbl <- function(response_tbl, sample_tbl, status_tbl, mode){
    tbl <- list(response_tbl, sample_tbl, status_tbl) %>%
        purrr::reduce(dplyr::inner_join, by = "sample_id") %>%
        dplyr::select(-.data$sample_id)
    if (mode == "By group") {
        tbl <- dplyr::mutate(tbl, label = paste0(
            .data$group, "; ", .data$gene, ":", .data$mutation_code
        ))
    } else if (mode == "Across groups") {
        tbl <- tbl %>%
            dplyr::select(-.data$group) %>%
            dplyr::mutate(label = paste0(.data$gene, ":", .data$mutation_code))
    }
    return(tbl)
}

#' Filter Multivariate Driver Labels
#'
#' @param tbl A tibble
#' @param min_mutants A positive integer
#' @param min_wildtype A positive integer
#'
#' @importFrom dplyr group_by mutate filter ungroup pull summarise n
#' @importFrom magrittr %>%
#' @importFrom rlang .data
filter_md_labels <- function(tbl, min_mutants, min_wildtype){
    tbl %>%
        dplyr::group_by(.data$label) %>%
        dplyr::mutate(status = dplyr::if_else(
            .data$status == "Wt",
            1L,
            0L
        )) %>%
        dplyr::summarise(n_total = dplyr::n(), n_wt = sum(.data$status)) %>%
        dplyr::mutate(n_mut = .data$n_total - .data$n_wt) %>%
        dplyr::filter(
            .data$n_mut >= local(min_mutants),
            .data$n_wt >= local(min_wildtype),
        ) %>%
        dplyr::ungroup() %>%
        dplyr::pull(.data$label)
}

#' Build Multivariate Driver Pvalue Tibble
#'
#' @param tbl A tibble
#' @param formula_string A string that could be converted to a formula
#'
#' @importFrom dplyr select mutate
#' @importFrom tidyr nest drop_na
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_md_pvalue_tbl <- function(tbl, formula_string){
    tbl %>%
        dplyr::select(.data$label, .data$response, .data$status) %>%
        tidyr::nest(.tbl = c(.data$response, .data$status)) %>%
        dplyr::mutate(p_value = purrr::map_dbl(
            .data$.tbl,
            calculate_lm_pvalue,
            formula_string,
            "statusWt"
        )) %>%
        tidyr::drop_na() %>%
        dplyr::select(-.data$.tbl) %>%
        dplyr::mutate(log10_p_value = -log10(.data$p_value))
}

#' Calculate Linear Model Pvalue
#'
#' @param data A dataframe
#' @param lm_formula A string or formula
#' @param term A string
#'
#' @importFrom stats lm
#' @importFrom magrittr %>% extract extract2
calculate_lm_pvalue <- function(data, lm_formula, term){
    data %>%
        stats::lm(formula = lm_formula, data = .) %>%
        summary() %>%
        magrittr::extract2("coefficients") %>%
        magrittr::extract(term, "Pr(>|t|)") %>%
        as.double()
}

#' Build Multivariate Driver Effect Size Tibble
#'
#' @param tbl A tibble
#'
#' @importFrom dplyr group_by ungroup select mutate rename
#' @importFrom tidyr nest pivot_wider drop_na
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_md_effect_size_tbl <- function(tbl){
    tbl %>%
        dplyr::select(.data$label, .data$response, .data$status) %>%
        dplyr::group_by(.data$label, .data$status) %>%
        dplyr::summarise(responses = list(.data$response)) %>%
        dplyr::ungroup() %>%
        tidyr::pivot_wider(
            .,
            names_from = .data$status,
            values_from = .data$responses
        ) %>%
        dplyr::rename(g1 = .data$Mut, g2 = .data$Wt) %>%
        tidyr::nest(data = c(.data$g1, .data$g2)) %>%
        dplyr::mutate(fold_change = purrr::map_dbl(
            .data$data,
            get_effect_size_from_tbl
        )) %>%
        dplyr::mutate(log10_fold_change = -log10(.data$fold_change)) %>%
        dplyr::select(-.data$data) %>%
        tidyr::drop_na()
}

#' Get Effect Size From Tibble
#'
#' @param tbl A tibble
#' @param method A function
get_effect_size_from_tbl <- function(tbl, method = calculate_ratio_effect_size){
    method(unlist(tbl$g1), unlist(tbl$g2))
}

#' Calculate Ratio Effect Size
#'
#' @param v1 A numeric vector
#' @param v2 A numeric vector
calculate_ratio_effect_size <- function(v1, v2){
    mean1 <- mean(v1)
    mean2 <- mean(v2)
    if (any(mean1 <= 0, mean2 <= 0)) return(NA)
    mean1 / mean2
}

#' Build Multivariate Driver Results Tibble
#'
#' @param filtered_tbl A tibble
#' @param pvalue_tbl A tibble
#' @param effect_size_tbl A tibble
#'
#' @importFrom dplyr select distinct inner_join
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_md_results_tbl <- function(filtered_tbl, pvalue_tbl, effect_size_tbl){
    filtered_tbl %>%
        dplyr::select(-c(.data$response, .data$status)) %>%
        dplyr::distinct() %>%
        dplyr::inner_join(pvalue_tbl, by = "label") %>%
        dplyr::inner_join(effect_size_tbl, by = "label")
}

#' Build Multivariate Driver Violin Tibble
#'
#' @param tbl A tibble with columns label, status and response
#' @param .label A string
#'
#' @importFrom dplyr select filter
#' @importFrom magrittr %>%
#' @importFrom rlang .data
build_md_driver_violin_tbl <- function(tbl, .label){
    tbl %>%
        dplyr::filter(.data$label %in% .label) %>%
        dplyr::select(x = .data$status, y = .data$response)
}

#' Create Multivariate Driver Violin Plot Title
#'
#' @param tbl A one row tibble with columns p_value and log10_fold_change
#' if mode == "By group" The tibble must has a group column
#' @param mode A string, either "By group" or "Across groups"
#' @importFrom rlang .data
create_md_violin_plot_title <- function(tbl, mode){
    title <- paste(
        "P-value:",
        round(tbl$p_value, 4), ";",
        "Log10(Fold Change):",
        round(tbl$log10_fold_change, 4)
    )
    if (mode == "By group") title <- paste("Group:", tbl$group, ";", title)
    return(title)
}


