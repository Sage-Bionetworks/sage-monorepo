build_ecn_gene_choice_list <- function(){
    genes <- iatlas.api.client::query_genes("extra_cellular_network") %>%
        dplyr::select("hgnc", "entrez") %>%
        dplyr::mutate("entrez" = stringr::str_c("gene:", .data$entrez)) %>%
        tibble::deframe(.)

    list(
        "Genesets" = c(
            "Extracellular Network Genes" = "geneset:extra_cellular_network",
            "Immunomodulator Genes" = "geneset:immunomodulator"
        ),
        "Genes" = genes
    )
}

build_ecn_celltype_choice_list <- function(){
    features <-
        iatlas.api.client::query_features(
            feature_classes = "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class"
        ) %>%
        dplyr::select("display", "name") %>%
        tibble::deframe(.) %>%
        c("All" = "All", .)
}

get_selected_gene_ids <- function(gene_input_list){
    gene_inputs <- unlist(gene_input_list)
    if(is.null(gene_inputs)) return(NULL)
    genesets <- gene_inputs %>%
        purrr::keep(., stringr::str_detect(., "^geneset:")) %>%
        stringr::str_remove_all(., "^geneset:")
    if(length(genesets) != 0){
        geneset_genes <- genesets %>%
            iatlas.api.client::query_genes(gene_types = .) %>%
            dplyr::pull("entrez")
    } else {
        geneset_genes <- c()
    }
    genes <- gene_inputs %>%
        purrr::keep(., stringr::str_detect(., "^gene:")) %>%
        stringr::str_remove_all(., "^gene:") %>%
        c(geneset_genes) %>%
        unique() %>%
        as.integer() %>%
        sort()
}

get_selected_celltypes <- function(celltype_input_list){
    celltype_input = unlist(celltype_input_list)
    if("All" %in% celltype_input) {
        celltypes <- iatlas.api.client::query_features(
            feature_classes = "Immune Cell Proportion - Common Lymphoid and Myeloid Cell Derivative Class"
        ) %>%
            dplyr::pull("name")
    } else {
        celltypes <- celltype_input
    }
    return(celltypes)
}

get_gene_nodes <- function(
  stratify,
  genes,
  tags,
  stratified_tags,
  min_abundance
  ){
  n_tags <- find_n_tags(stratify)
  nodes <-
    iatlas.api.client::query_gene_nodes(
      datasets = "TCGA",
      network = "extracellular_network",
      entrez = genes,
      tags = tags,
      min_score = min_abundance / 100
    ) %>%
    filter_nodes_for_n_tags(n_tags) %>%
    dplyr::select(
      "Type" = "label",
      "tags",
      "node_name" = "name",
      "node_display" = "hgnc"
    )

  if(stratify) nodes <- filter_nodes_with_tag_name(nodes, stratified_tags)
  unnest_nodes(nodes, stratify)
}

get_feature_nodes <- function(
  stratify,
  features,
  tags,
  stratified_tags,
  min_abundance
  ){
  n_tags <- find_n_tags(stratify)
  nodes <-
    iatlas.api.client::query_feature_nodes(
      datasets = "TCGA",
      network = "extracellular_network",
      features = features,
      tags = tags,
      min_score = min_abundance / 100
    ) %>%
    filter_nodes_for_n_tags(n_tags) %>%
    dplyr::select(
      "Type" = "label",
      "tags",
      "node_name" ="name",
      "node_display" = "feature_display"
    )
  if(stratify) nodes <- filter_nodes_with_tag_name(nodes, stratified_tags)
  unnest_nodes(nodes, stratify)
}

find_n_tags <- function(stratify){
  if(stratify) return(2)
  else return(1)
}

filter_nodes_for_n_tags <- function(data, n_tags){
  dplyr::filter(
    data,
    purrr::map_lgl(purrr::map_int(.data$tags, nrow), ~ .x == n_tags)
  )
}

filter_nodes_with_tag_name <- function(data, names){
  dplyr::filter(
    data,
    purrr::map_lgl(
      purrr::map(.data$tags, dplyr::pull, "name"), ~ any(names %in% .x)
    )
  )
}

unnest_nodes <- function(data, stratify){
  result <- data %>%
    tidyr::unnest("tags") %>%
    dplyr::select("Type", "node_name", "node_display", "tag" = "name")
  if(stratify){
    result <- dplyr::filter(
      result, .data$tag %in% c("C1", "C2", "C3", "C4", "C5", "C6")
    )
  }
  return(result)
}

get_edges <- function(nodes, min_concordance){
  node_names <- nodes %>%
    dplyr::pull("node_name") %>%
    unique()

  edges <- iatlas.api.client::query_edges(
    min_score = min_concordance,
    node1 = node_names,
    node2 = node_names
  ) %>%
    dplyr::select("edge_name" = "name", "node1", "node2", "score") %>%
    tidyr::pivot_longer(
      cols = c("node1", "node2"),
      names_to = "node_num",
      values_to = "node_name"
    ) %>%
    dplyr::inner_join(nodes, by = "node_name") %>%
    dplyr::select(
      "edge_name", "score", "node_num", "node_display", "tag"
    ) %>%
    tidyr::pivot_wider(
      names_from = "node_num", values_from = "node_display"
    )
}

create_graph_json <- function(edges, nodes){
  edges_nodes <-
    c(edges$node1, edges$node2) %>%
    unique()

  nodes <- nodes %>%
    dplyr::filter(.data$node_display %in% edges_nodes) %>%
    dplyr::arrange("node_display") %>%
    dplyr::select(
      "id" = "node_display",
      "Type",
      "FriendlyName" = "node_display"
    ) %>%
    dplyr::distinct() %>%
    as.data.frame()

  edges <- edges %>%
    dplyr::select(
      "source" = "node1",
      "target" = "node2",
      "score",
      "interaction" = "tag"
    ) %>%
    as.data.frame()

  cyjShiny::dataFramesToJSON(edges, nodes)
}




# build_ecn_scaffold_tbl <- function(){
#     paste0(
#         "SELECT DISTINCT node_1_id, node_2_id FROM edges WHERE node_1_id IN ",
#         "(SELECT node_id FROM nodes_to_tags WHERE tag_id = ",
#         "(SELECT id FROM tags where name = 'C1')) ",
#         "ORDER BY node_1_id, node_2_id"
#     ) %>%
#         perform_query()
# }

# build_ecn_group_tbl <- function(group){
#     paste0(
#         "SELECT name, id FROM tags WHERE id IN ",
#         "(SELECT tag_id FROM nodes_to_tags) ",
#         "AND id IN ",
#         "(SELECT tag_id FROM tags_to_tags WHERE related_tag_id = ",
#         "(SELECT id FROM tags WHERE display = '",
#         # "TCGA Study",
#         group,
#         "'))"
#     ) %>%
#         perform_query()
# }
#
# build_ecn_node_tag_tbl <- function(tag_ids){
#     paste0(
#         "SELECT node_id, tag_id, COUNT(*) OVER(PARTITION BY node_id) AS count ",
#         "FROM nodes_to_tags ",
#         "WHERE node_id IN (",
#         "SELECT node_id FROM nodes_to_tags WHERE tag_id IN (",
#         numeric_values_to_query_list(tag_ids),
#         ")",
#         ")"
#     ) %>%
#         perform_query()
# }
#
#
# get_ecn_celltypes <- function(){
#     paste0(
#         "SELECT display, id FROM features WHERE id IN ",
#         "(SELECT feature_id FROM nodes)"
#     ) %>%
#         perform_query() %>%
#         deframe() %>%
#         c("Default: all cells" = 0)
# }
#
# get_ecn_genes <- function(){
#     paste0(
#         "SELECT hgnc, id FROM genes WHERE id IN ",
#         "(SELECT gene_id FROM nodes)"
#     ) %>%
#         perform_query() %>%
#         deframe() %>%
#         c("Default: immunomodulator genes" = 0)
# }
#
# build_node_group_tbl <- function(tag_list){
#     paste0(
#         "SELECT * FROM nodes_to_tags ",
#         "WHERE tag_id IN (",
#         numeric_values_to_query_list(tag_list[[1]]),
#         ") ",
#         "AND node_id IN (",
#         "SELECT node_id FROM nodes_to_tags ",
#         "WHERE node_id IN (",
#         create_get_nodes_with_n_tags_query(length(tag_list)),
#         ") ",
#         "AND ",
#         create_get_nodes_with_tag_id_lists_query(tag_list),
#         ") ",
#         "ORDER BY node_id, tag_id"
#     ) %>%
#         perform_query()
# }
#
# create_get_nodes_with_tag_id_lists_query <- function(lst){
#     lst %>%
#         purrr::map_chr(create_get_nodes_with_tag_ids_query) %>%
#         paste0("node_id IN (", ., ")", collapse = " AND ")
# }
#
# create_get_nodes_with_tag_ids_query <- function(ids){
#     paste0(
#         "SELECT node_id FROM nodes_to_tags ",
#         "WHERE tag_id IN(",
#         numeric_values_to_query_list(ids),
#         ")"
#     )
# }
#
# create_get_nodes_with_n_tags_query <- function(n){
#     paste0(
#         "SELECT node_id FROM nodes_to_tags ",
#         "GROUP BY node_id ",
#         "HAVING count(*) = ",
#         n
#     )
# }
#
# build_ecn_node_tbl <- function(score_threshold, node_ids, gene_ids, cell_ids){
#     paste0(
#         "SELECT id, score, display AS node FROM (",
#         "SELECT id, score, ",
#         create_id_to_feature_display_subquery(),
#         " FROM nodes a ",
#         "WHERE score > ",
#         score_threshold,
#         # ".05 ",
#         "AND id IN (",
#         numeric_values_to_query_list(node_ids),
#         # numeric_values_to_query_list(1:1000000),
#         ") ",
#         "AND feature_id IN(",
#         numeric_values_to_query_list(cell_ids),
#         ")",
#         ") a UNION ALL ",
#         "SELECT id, score, gene AS node FROM (",
#         "SELECT id, gene_id, score, ",
#         create_id_to_hgnc_subquery(),
#         " FROM nodes a ",
#         "WHERE score > ",
#         score_threshold,
#         # ".05 ",
#         "AND id IN (",
#         numeric_values_to_query_list(node_ids),
#         # numeric_values_to_query_list(1:100000),
#         ") ",
#         "AND gene_id IN(",
#         numeric_values_to_query_list(gene_ids),
#         ")",
#         ") b"
#     ) %>%
#         perform_query()
# }
#
# build_ecn_edge_tbl <- function(node_ids, score_threshold){
#     node_ids %>%
#         numeric_values_to_query_list() %>%
#         paste0(
#             "SELECT node_1_id, node_2_id, score ",
#             "FROM edges ",
#             "WHERE score > ",
#             score_threshold,
#             " AND node_1_id in (",
#             .,
#             ")",
#             "AND node_2_id in (",
#             .,
#             ")"
#         ) %>%
#         perform_query()
# }
#
# build_ecn_node_tbl2 <- function(node_tbl, edge_tbl){
#     node_tbl %>%
#         dplyr::filter(
#             .data$id %in% c(edge_tbl$node_1_id, edge_tbl$node_2_id)
#         )
# }
#
# format_ecn_node_tbl <- function(tbl){
#     tbl %>%
#         dplyr::select(.data$id, Gene = .data$node) %>%
#         dplyr::mutate(
#             name = .data$Gene,
#             Type = "Gene",
#             FriendlyName = .data$Gene
#         ) %>%
#         as.data.frame() %>%
#         print()
# }
#
# format_ecn_edge_tbl <- function(tbl){
#     tbl %>%
#         dplyr::select(
#             source = .data$node_1_id,
#             target = .data$node_2_id,
#             .data$score
#         ) %>%
#         dplyr::mutate(
#             source = as.character(.data$source),
#             target = as.character(.data$target),
#             interaction = sample(c("C1", "C2"), dplyr::n(), replace = T),
#         ) %>%
#         as.data.frame()
# }




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
