import { Component, inject, Input } from '@angular/core';
import { Gene } from '@sagebionetworks/agora/api-client';
import { BoxPlotComponent } from '@sagebionetworks/agora/charts';
import { DEFAULT_SYNAPSE_WIKI_OWNER_ID } from '@sagebionetworks/agora/config';
import { BoxPlotChartItem } from '@sagebionetworks/agora/models';
import { HelperService as ExplorersHelperService } from '@sagebionetworks/explorers/services';
import { DownloadDomImageComponent } from '@sagebionetworks/explorers/ui';
import { ModalLinkComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'agora-gene-evidence-metabolomics',
  imports: [ModalLinkComponent, DownloadDomImageComponent, BoxPlotComponent],
  templateUrl: './gene-evidence-metabolomics.component.html',
  styleUrls: ['./gene-evidence-metabolomics.component.scss'],
})
export class GeneEvidenceMetabolomicsComponent {
  explorersHelperService = inject(ExplorersHelperService);

  readonly defaultSynapseWikiOwnerId = DEFAULT_SYNAPSE_WIKI_OWNER_ID;

  _gene: Gene | undefined;
  get gene(): Gene | undefined {
    return this._gene;
  }
  @Input() set gene(gene: Gene | undefined) {
    this._gene = gene;
    this.init();
  }

  boxPlotData: BoxPlotChartItem[] = [];

  reset() {
    this.boxPlotData = [];
  }

  init() {
    this.reset();

    if (!this._gene?.metabolomics?.transposed_boxplot_stats) {
      this.boxPlotData = [];
      return;
    }

    const boxPlotData: BoxPlotChartItem[] = [];

    this._gene.metabolomics.transposed_boxplot_stats.forEach((item: number[], index: number) => {
      boxPlotData.push({
        key: this._gene?.metabolomics?.boxplot_group_names[index] ?? '',
        value: [item[0], item[2], item[4]],
        quartiles: [item[1], item[2], item[3]],
      });
    });

    this.boxPlotData = boxPlotData;
  }

  getSignificantFigures(n: any, b: any) {
    return this.explorersHelperService.getSignificantFigures(n, b);
  }

  getSignificantText(pval: number): string {
    return pval <= 0.05 ? ' is ' : ' is not ';
  }
}
