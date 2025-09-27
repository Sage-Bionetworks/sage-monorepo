import { CommonModule } from '@angular/common';
import { Component, Input, ViewEncapsulation, inject } from '@angular/core';
import { Router } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faAngleRight } from '@fortawesome/free-solid-svg-icons';
import {
  Gene,
  GeneService,
  SimilarGenesNetworkLink,
  SimilarGenesNetworkNode,
} from '@sagebionetworks/agora/api-client';
import { NetworkChartComponent } from '@sagebionetworks/agora/charts';
import {
  NetworkChartData,
  NetworkChartLink,
  NetworkChartNode,
} from '@sagebionetworks/agora/models';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'agora-gene-network',
  imports: [CommonModule, FontAwesomeModule, NetworkChartComponent, TooltipModule],
  templateUrl: './gene-network.component.html',
  styleUrls: ['./gene-network.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class GeneNetworkComponent {
  router = inject(Router);
  geneService = inject(GeneService);

  _gene: Gene | undefined;
  get gene(): Gene | undefined {
    return this._gene;
  }
  @Input() set gene(gene: Gene | undefined) {
    this._gene = gene;
    this.selectedGene = this._gene;
    this.init();
  }

  data: NetworkChartData | undefined;
  selectedGene: Gene | undefined;

  filters: number[] = [];
  selectedFilter = 1;

  faAngleRight = faAngleRight;

  init() {
    if (!this._gene?.similar_genes_network?.nodes?.length) {
      this.data = undefined;
      return;
    }

    const nodes: NetworkChartNode[] = this._gene.similar_genes_network.nodes.map(
      (node: SimilarGenesNetworkNode) => {
        return {
          id: node.ensembl_gene_id,
          label: node.hgnc_symbol || node.ensembl_gene_id,
          value: node.brain_regions?.length || 0,
          class: 'edges-' + (node.brain_regions?.length || 0),
        };
      },
    );

    const links: NetworkChartLink[] = this._gene.similar_genes_network.links.map(
      (link: SimilarGenesNetworkLink) => {
        return {
          source: nodes.find(
            (node: NetworkChartNode) => node.id === link.source,
          ) as NetworkChartNode,
          target: nodes.find(
            (node: NetworkChartNode) => node.id === link.target,
          ) as NetworkChartNode,
          value: link.brain_regions?.length,
          class: 'edges-' + (link.brain_regions?.length || 0),
        };
      },
    );

    this.data = {
      nodes: nodes,
      links: links,
    };

    this.filters = [...Array(this._gene.similar_genes_network.max).keys()].map((n) => {
      return ++n;
    });
  }

  onNodeClick(node: NetworkChartNode) {
    this.geneService.getGene(node.id).subscribe((gene) => {
      this.selectedGene = gene;
    });
  }

  navigateToSimilarGenes() {
    // https://github.com/angular/angular/issues/45202
    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    this.router.navigate(['/genes/' + this._gene?.ensembl_gene_id + '/similar']);
  }

  // If the 'state' value can be modified by another boolean value, pass the modifying value as 'isStateApplicable'
  // Example: rna_brain_change_studied: false indicates that is_any_rna_changed_in_ad_brain is
  // undefined, so calling:
  //     getText(is_any_rna_changed_in_ad_brain, rna_brain_change_studied)
  // will return the desired 'No data' text, regardless of the is_any_rna_changed_in_ad_brain value
  getText(state?: boolean, isStateApplicable = true): string {
    let text = '';

    if (!isStateApplicable) {
      text = 'No data';
    } else {
      if (state) {
        text = 'True';
      } else {
        if (state === undefined) {
          text = 'No data';
        } else {
          text = 'False';
        }
      }
    }
    return text;
  }

  getNominationText(nominations: number | null): string {
    return this.getText(nominations === null ? false : nominations > 0);
  }

  // Use black text if 'isStateApplicable' is false ('No data')
  // Otherwise, use green text when 'state' is true, use red text when 'state' is false
  // getTextColorClass(state: boolean, isStateApplicable = true): any {
  //   const colorClassObj = {} as any;
  //   if (state && isStateApplicable) {
  //     colorClassObj['text-success'] = true;
  //   } else if (!state && isStateApplicable) {
  //     colorClassObj['text-danger'] = true;
  //   }
  //   return colorClassObj;
  // }
}
