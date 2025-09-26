import {
  Gene,
  SimilarGenesNetwork,
  SimilarGenesNetworkLink,
  SimilarGenesNetworkNode,
} from '@sagebionetworks/agora/api-client';

export function getSimilarGenesNetwork(gene: Gene): SimilarGenesNetwork {
  const nodes: { [key: string]: SimilarGenesNetworkNode } = {};
  const links: { [key: string]: SimilarGenesNetworkLink } = {};
  const response: SimilarGenesNetwork = {
    nodes: [],
    links: [],
    min: 0,
    max: 0,
  };

  if (gene.links) {
    Object.values(gene.links).forEach((link) => {
      const a: string = link.geneA_ensembl_gene_id;
      const b: string = link.geneB_ensembl_gene_id;
      const key = a + b;
      const rKey = b + a;

      // Check if a reverse link already exists
      if (links[rKey] && !links[rKey].brain_regions.includes(link['brainRegion'])) {
        links[rKey].brain_regions.push(link['brainRegion']);
        return;
      }

      if (!links[key]) {
        links[key] = {
          source: a,
          target: b,
          source_hgnc_symbol: link?.geneA_external_gene_name,
          target_hgnc_symbol: link?.geneB_external_gene_name,
          brain_regions: [link.brainRegion],
        };
      } else if (!links[key].brain_regions.includes(link['brainRegion'])) {
        links[key].brain_regions.push(link['brainRegion']);
      }
    });
  }

  response.links = Object.values(links).sort((a: any, b: any) => {
    return a.brain_regions?.length - b.brain_regions?.length;
  });

  response.links.forEach((link: any) => {
    link.brain_regions.sort();

    ['source', 'target'].forEach((key: any) => {
      if (!nodes[link[key]]) {
        nodes[link[key]] = {
          ensembl_gene_id: link[key],
          hgnc_symbol: link[key + '_hgnc_symbol'],
          brain_regions: link.brain_regions,
        };
      } else {
        link.brain_regions.forEach((brainRegion: any) => {
          if (!nodes[link[key]].brain_regions.includes(brainRegion)) {
            nodes[link[key]].brain_regions.push(brainRegion);
          }
        });
      }
    });
  });

  response.nodes = Object.values(nodes)
    .sort((a: any, b: any) => {
      return a.brain_regions?.length - b.brain_regions?.length;
    })
    .reverse();

  response.nodes.forEach((node: any, i: number) => {
    node.brain_regions.sort();

    if (node.brain_regions.length < response.min) {
      response.min = node.brain_regions.length;
    }

    if (node.brain_regions.length > response.max) {
      response.max = node.brain_regions.length;
    }

    // Insert current node to the beginning of the array
    if (node.ensembl_gene_id === gene.ensembl_gene_id) {
      const currentNode = node;
      response.nodes.splice(i, 1);
      response.nodes.unshift(currentNode);
    }
  });

  return response;
}
