export interface SimilarGenesNetworkNode {
  ensembl_gene_id: string;
  hgnc_symbol: string;
  brain_regions: string[];
}

export interface SimilarGenesNetworkLink {
  source: string;
  target: string;
  source_hgnc_symbol: string;
  target_hgnc_symbol: string;
  brain_regions: string[];
}

export interface SimilarGenesNetwork {
  nodes: SimilarGenesNetworkNode[];
  links: SimilarGenesNetworkLink[];
  min: number;
  max: number;
}

export interface GeneNetworkLinks {
  geneA_ensembl_gene_id: string;
  geneB_ensembl_gene_id: string;
  geneA_external_gene_name: string;
  geneB_external_gene_name: string;
  brainRegion: string;
}

import { SimulationNodeDatum, SimulationLinkDatum } from 'd3';

export interface NetworkChartNode extends SimulationNodeDatum {
  id: string;
  label?: string;
  value?: number;
  class?: string;
}

export interface NetworkChartLink extends SimulationLinkDatum<SimulationNodeDatum> {
  source: NetworkChartNode;
  target: NetworkChartNode;
  value?: number;
  class?: string;
}

export interface NetworkChartData {
  nodes: NetworkChartNode[];
  links: NetworkChartLink[];
}
