import { inject, Injectable } from '@angular/core';
import {
  BioDomain,
  BioDomainInfo,
  BioDomainsService,
  Distribution,
  DistributionService,
  Gene,
  GenesService,
  SimilarGenesNetwork,
  SimilarGenesNetworkLink,
  SimilarGenesNetworkNode,
} from '@sagebionetworks/agora/api-client-angular';
import { of, Observable } from 'rxjs';
import { map, tap, share, finalize } from 'rxjs/operators';

@Injectable()
export class GeneService {
  genes: { [key: string]: Gene } = {};
  distribution: Distribution | undefined = undefined;
  distributionObservable: Observable<Distribution> | undefined;
  comparisonData: any = {};

  bioDomains: { [key: string]: BioDomain[] } = {};
  allBioDomains: BioDomainInfo[] = [];

  genesService = inject(GenesService);
  bioDomainsService = inject(BioDomainsService);
  distributionService = inject(DistributionService);

  getGene(id: string): Observable<Gene | null> {
    if (this.genes[id]) {
      return of(this.genes[id]);
    }

    return this.genesService.getGene(id).pipe(
      map((gene: Gene | null) => {
        if (!gene) return null;
        gene.similar_genes_network = this.getSimilarGenesNetwork(gene);
        return (this.genes[id] = gene);
      }),
    );
  }

  // ------------------------------------------------------------------------ //

  getGenes(ids: string | string[]): Observable<Gene[]> {
    if (typeof ids === 'object') {
      ids = ids.join(',');
    }

    return this.genesService.getGenes(ids);
  }

  getStatisticalModels(gene: Gene) {
    const models: string[] = [];

    gene.rna_differential_expression?.forEach((item: any) => {
      if (!models.includes(item.model)) {
        models.push(item.model);
      }
    });

    return models;
  }

  getSimilarGenesNetwork(gene: Gene): SimilarGenesNetwork {
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

  // ------------------------------------------------------------------------ //

  getBiodomains(ensg: string): Observable<BioDomain[]> {
    if (this.bioDomains[ensg]) {
      return of(this.bioDomains[ensg]);
    }

    return this.bioDomainsService.getBioDomain(ensg).pipe(
      map((data: BioDomain[]) => {
        return (this.bioDomains[ensg] = data);
      }),
    );
  }

  // ------------------------------------------------------------------------ //

  getAllBiodomains(): Observable<BioDomainInfo[]> {
    if (this.allBioDomains.length > 0) {
      return of(this.allBioDomains);
    }

    return this.bioDomainsService.listBioDomains();
  }

  // ------------------------------------------------------------------------ //

  getDistribution(): Observable<Distribution> {
    if (this.distribution) {
      return of(this.distribution);
    } else if (this.distributionObservable) {
      return this.distributionObservable;
    } else {
      this.distributionObservable = this.distributionService.getDistribution().pipe(
        tap((data: any) => (this.distribution = data)),
        share(),
        finalize(() => (this.distributionObservable = undefined)),
      );
      return this.distributionObservable;
    }
  }

  // ------------------------------------------------------------------------ //

  getComparisonGenes(
    category: 'RNA - Differential Expression' | 'Protein - Differential Expression',
    subCategory: string,
  ) {
    const key = category + subCategory;
    if (this.comparisonData[key]) {
      return of(this.comparisonData[key]);
    } else {
      return this.genesService.getComparisonGenes(category, subCategory).pipe(
        tap((data) => {
          this.comparisonData[key] = data;
        }),
      );
    }
  }
}
