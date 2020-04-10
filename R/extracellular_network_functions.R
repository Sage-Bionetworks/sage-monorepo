build_ecn_group_tbl <- function(group){
    paste0(
        "SELECT name, id FROM tags WHERE id IN ",
        "(SELECT tag_id FROM nodes_to_tags) ",
        "AND id IN ",
        "(SELECT tag_id FROM tags_to_tags WHERE related_tag_id = ",
        "(SELECT id FROM tags WHERE display = '",
        "Immune Subtype",
        # group,
        "'))"
    ) %>%
        perform_query()
}

build_ecn_group_tbl2 <- function(group){
    paste0(
        "SELECT * FROM nodes_to_tags WHERE node_id IN (",
        "SELECT node_id FROM nodes_to_tags WHERE tag_id IN (",
        "SELECT tag_id FROM tags_to_tags WHERE related_tag_id = ",
        "(SELECT id FROM tags WHERE display = '",
        # "TCGA Study",
        group,
        "'",
        ")",
        ")",
        ")",
        "ORDER BY node_id"
    ) %>%
        perform_query() %>%
        dplyr::group_by(.data$node_id) %>%
        dplyr::mutate(count = dplyr::n()) %>%
        dplyr::ungroup()
}

build_ecn_scaffold_tbl <- function(){
    paste0(
        "SELECT DISTINCT node_1_id, node_2_id FROM edges WHERE node_1_id IN ",
        "(SELECT node_id FROM nodes_to_tags WHERE tag_id = ",
        "(SELECT id FROM tags where name = 'C1')) ",
        "ORDER BY node_1_id, node_2_id"
    ) %>%
        perform_query()
}

get_ecn_celltypes <- function(){
    paste0(
        "SELECT display FROM features WHERE id IN ",
        "(SELECT feature_id FROM nodes)"
    ) %>%
        perform_query()
}

get_ecn_genes <- function(){
    paste0(
        "SELECT hgnc FROM genes WHERE id IN ",
        "(SELECT gene_id FROM nodes)"
    ) %>%
        perform_query()
}



build_ecn_node_tbl <- function(score_threshold, node_ids){
    paste0(
        "SELECT id, feature_id, gene_id, score, ",
        create_id_to_hgnc_subquery(),
        " FROM nodes a ",
        "WHERE score > ",
        score_threshold,
        "AND id IN (",
        numeric_values_to_query_list(node_ids),
        ")"
    ) %>%
        perform_query()
}

create_node_group_tbl <- function(tag_list){
    paste0(
        "SELECT * FROM nodes_to_tags ",
        "WHERE tag_id IN (",
        numeric_values_to_query_list(tag_list[[1]]),
        ") ",
        "AND node_id IN (",
        "SELECT node_id FROM nodes_to_tags ",
        "WHERE node_id IN (",
        create_get_nodes_with_n_tags_query(length(tag_list)),
        ") ",
        "AND ",
        create_get_nodes_with_tag_id_lists_query(tag_list),
        ") ",
        "ORDER BY node_id, tag_id"
    ) %>%
        perform_query()
}

create_get_nodes_with_tag_id_lists_query <- function(lst){
    lst %>%
        purrr::map_chr(create_get_nodes_with_tag_ids_query) %>%
        paste0("node_id IN (", ., ")", collapse = " AND ")
}

create_get_nodes_with_tag_ids_query <- function(ids){
    paste0(
        "SELECT node_id FROM nodes_to_tags ",
        "WHERE tag_id IN(",
        numeric_values_to_query_list(ids),
        ")"
    )
}

create_get_nodes_with_n_tags_query <- function(n){
    paste0(
        "SELECT node_id FROM nodes_to_tags ",
        "GROUP BY node_id ",
        "HAVING count(*) = ",
        n
    )
}


build_ecn_node_tbl2 <- function(node_tbl, edge_tbl){
    node_tbl %>%
        dplyr::filter(
            !is.na(.data$gene_id),
            .data$id %in% c(edge_tbl$node_1_id, edge_tbl$node_2_id)
        ) %>%
        dplyr::select(.data$id, Gene = .data$gene) %>%
        dplyr::mutate(
            name = .data$Gene,
            Type = "Gene",
            FriendlyName = .data$Gene
        ) %>%
        as.data.frame()
}

build_ecn_edge_tbl <- function(node_ids, score_threshold){
    node_ids %>%
        numeric_values_to_query_list() %>%
        paste0(
            "SELECT node_1_id, node_2_id, score ",
            "FROM edges ",
            "WHERE score > ",
            score_threshold,
            " AND node_1_id in (",
            .,
            ")",
            "AND node_2_id in (",
            .,
            ")"
        ) %>%
        perform_query()

}

build_ecn_edge_tbl2 <- function(edge_tbl){
    edge_tbl %>%
        dplyr::select(source = node_1_id, target = node_2_id, score) %>%
        dplyr::mutate(
            source = as.character(source),
            target = as.character(target),
            interaction = sample(c("C1", "C2"), dplyr::n(), replace = T),
        ) %>%
        as.data.frame()

}




#
# get_edge_table <- function(conc_edges, nodes_annot){
#     merge(conc_edges, nodes_annot, by.x = "source", by.y = "Obj") %>%
#         dplyr::select(
#             "From" = "source",
#             "From (Friendly Name)" = "FriendlyName",
#             target,
#             interaction,
#             score
#         ) %>%
#         merge(nodes_annot, by.x = "target", by.y = "Obj") %>%
#         dplyr::select(
#             From,
#             `From (Friendly Name)`,
#             "To" = "target",
#             "To (Friendly Name)" = "FriendlyName",
#             "Group" = "interaction",
#             "Concordance" = "score"
#         )
# }
#
# get_ab_nodes <- function(ab_nodes, conc_edges, nodes_annot, byImmune = FALSE){
#
#     all_nodes <- rbind(conc_edges %>% dplyr::select("Node" = "source" , "Group" = "interaction"),
#                        conc_edges %>% dplyr::select("Node" = "target", "Group" = "interaction")) %>%
#         dplyr::distinct()
#
#     if(byImmune == FALSE){
#         all_nodes <- merge(all_nodes, ab_nodes, by = c("Node", "Group"))
#     }else{
#         all_nodes <- merge(all_nodes, ab_nodes, by.x = c("Node", "Group"), by.y = c("Node","Immune"))
#     }
#
#     #including the annotation of Friendly Names and types
#     all_nodes <- merge(all_nodes, nodes_annot, by.x = "Node", by.y = "Obj") %>%
#         dplyr::select("Node" = "Node", "Friendly Name" = "FriendlyName", Type, Group, "Abundance" = "UpBinRatio")
#     all_nodes
# }
#
# compute_abundance <- function(subset_df, subset_col, cell_data, expr_data, cois, gois, byImmune = FALSE){
#
#     group_participant <- get_participants(subset_df, subset_col, byImmune)
#
#     dfclong.generic <- get_cell_long(cell_data, group_participant, cois, subset_df, byImmune)
#     dfelong.generic <- get_gene_long(expr_data, group_participant, gois, subset_df, byImmune)
#
#     dfn <- dplyr::bind_rows(dfelong.generic, dfclong.generic)
#
#     tertiles <- getNodeTertiles(dfn)
#     df_ternary_full_info <- dfn %>% dplyr::group_by(Node) %>% tidyr::nest() %>% ## split by nodes
#         dplyr::mutate(Bins=purrr::map2(.x = Node,.y = data, tertiles = tertiles, byImmune = byImmune, .f = multiBin)) %>% ## add Bins
#         dplyr::mutate(IncludeFeature=purrr::map(.x = Bins, byImmune = byImmune, .f = addPerGroupIncludes))
#
#     df_ternary_full_info
#
# }

# get_netdata <- function(sample_group, studyImmune = FALSE){
#     return(list("none"))
#     if (sample_group == "Immune Subtype") {
#         all_net_info$immune
#     } else if (sample_group == "TCGA Subtype") {
#         sample_data <- all_net_info$subtype
#     }else{
#         if (studyImmune == TRUE) return(all_net_info$studyImmune)
#         else sample_data <- all_net_info$study
#     }
# }


# get_ecn_groups <- function(group){
#     paste0(
#         "SELECT name FROM tags WHERE id IN ",
#         "(SELECT tag_id FROM tags_to_tags ",
#         "WHERE related_tag_id IN ",
#         "(SELECT id FROM tags where display = '", group, "'))"
#     ) %>%
#         perform_query() %>%
#         dplyr::pull(.data$name)
# }
