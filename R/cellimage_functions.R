get_cellimage_genes <- function(){
  genes <-
    iatlas.api.client::query_genes(gene_types = "cellimage_network") %>%
    dplyr::pull("entrez")
}

get_cellimage_features <- function(){
  c(
    "Dendritic_cells_Aggregate2",
    "Macrophage_Aggregate2",
    "T_cells_CD8_Aggregate2",
    "Tumor_fraction"
  )
}

get_cellimage_gene_nodes <- function(dataset, tag, genes){

  nodes <-
    iatlas.api.client::query_nodes(
      datasets = dataset,
      network = "Cellimage Network",
      entrez = genes,
      tags = tag
    ) %>%
    dplyr::select(
      "tags" = "node_tags",
      "node_name",
      "node_feature_name" = "gene_entrez",
      "node_feature_display" = "gene_hgnc",
      "x" = "node_x",
      "y" = "node_y",
      "score" = "node_score"
    ) %>%
    tidyr::unnest("tags") %>%
    dplyr::select(
      "node_name",
      "node_feature_name",
      "node_feature_display",
      "tag" = "name",
      "x",
      "y",
      "score"
    ) %>%
    dplyr::mutate(
      "node_feature_name" = as.character(.data$node_feature_name),
      "Type" = "Gene"
    )
}

get_cellimage_feature_nodes <- function(dataset, tag, features){
  nodes <-
    iatlas.api.client::query_nodes(
      datasets = dataset,
      network = "Cellimage Network",
      features = features,
      tags = tag
    ) %>%
    dplyr::select(
      "tags" = "node_tags",
      "node_name",
      "node_feature_name" = "feature_name",
      "node_feature_display" = "feature_display",
      "x" = "node_x",
      "y" = "node_y",
      "score" = "node_score"
    ) %>%
    tidyr::unnest("tags") %>%
    dplyr::select(
      "node_name",
      "node_feature_name",
      "node_feature_display",
      "tag" = "name",
      "x",
      "y",
      "score"
    ) %>%
    dplyr::mutate("Type" = "Cell")
}

add_nonexistent_gene_nodes <- function(present_nodes, all_nodes){
  missing_nodes <- all_nodes[which(!all_nodes %in% present_nodes$node_feature_name)] #which node is missing
  missing_df <- get_cellimage_gene_nodes("TCGA", "C1", missing_nodes) #query for a group that is known to have all nodes
  missing_df$score <- NA_integer_

  rbind(present_nodes, missing_df)
}

add_nonexistent_feature_nodes <- function(present_nodes, all_nodes){
  missing_nodes <- all_nodes[which(!all_nodes %in% present_nodes$node_feature_name)] #which node is missing
  missing_df <- get_cellimage_feature_nodes("TCGA", "C1", missing_nodes) #query for a group that is known to have all nodes
  missing_df$score <- NA_integer_

  rbind(present_nodes, missing_df)
}

get_cellimage_edges <- function(){

  #Using TCGA/C1 as default for getting all cellimage edges
  nodes <- dplyr::bind_rows(
    get_cellimage_gene_nodes("TCGA", "C1", get_cellimage_genes()),
    get_cellimage_feature_nodes("TCGA", "C1", get_cellimage_features()))

  node_names <- nodes %>%
    dplyr::pull("node_name") %>%
    unique()

  edges <-
    iatlas.api.client::query_edges(
      node1 = node_names,
      node2 = node_names
    )

  if(nrow(edges) == 0) return(edges)

  edges %>%
    dplyr::select("node1", "node2") %>%
    dplyr::inner_join(nodes, by = c("node1" = "node_name")) %>%
    dplyr::select(
      "node_display1" = "node_feature_display",
      "node1",
      "node2"
    ) %>%
    dplyr::inner_join(nodes, by = c("node2" = "node_name")) %>%
    dplyr::select(
      "node_display1",
      "node_display2" = "node_feature_display",
      "node1",
      "node2",
      "tag"
    )
}

create_cellimage_json <- function(nodes, edges){
  edges <- edges %>%
    dplyr::select(
      "source" = "node_display1",
      "target" = "node_display2",
      "interaction" = "tag"
    ) %>%
    as.data.frame()

  #one of the edges needs to show that the interaction is mediated by a peptide
  edges[edges$source == "LAG3" & edges$target == "HLA-DPA1", "interaction"] <- "peptide"

  nodes <- dplyr::select(
    nodes,
    "id" = "node_feature_display",
    "FriendlyName" = "node_feature_display",
    "x",
    "y",
    "UpBinRatio" = "score"
  )

  cyjShiny::dataFramesToJSON(edges, nodes)
}

getVarColor <- function(feature, colormap, node_df, range_df, alpha = 1.){
  alpha.hex <- toupper(as.hexmode(0:255))[round(alpha*255)+1]

  display.val <- node_df %>%
    dplyr::filter(.data$node_feature_name == feature) %>%
    purrr::pluck("score")

  if ( is.null(display.val)) {
    usecolor <- paste("#e6e6e6",alpha.hex,sep = "")
  } else {
    vmin <- range_df %>%
      dplyr::filter(.data$node_feature_name == feature) %>%
      purrr::pluck("MinBound")
    vmax <- range_df %>%
      dplyr::filter(.data$node_feature_name == feature) %>%
      purrr::pluck("MaxBound")
    vnstep <- 51
    vstep <- (vmax-vmin)/(vnstep-1) ## size of step

    if(vstep == 0 || is.na(vstep)){
      stop("step size in getVarColor is 0 or NA")
    }
    if (length(display.val) == 0 || is.na(display.val)){
      stop("Display value: ", display.val, " is problematic")
    }
    breakList <- seq(vmin,vmax,vstep)
    cind <- min(which(!(display.val-breakList)>0)) ## right turnover point

    allcolors <- grDevices::colorRampPalette(RColorBrewer::brewer.pal(n = 7,name=colormap))(length(breakList))
    allcolors.with.alpha <- paste(allcolors,alpha.hex,sep = "")

    usecolor <- allcolors.with.alpha[cind]
  }
  usecolor


}

