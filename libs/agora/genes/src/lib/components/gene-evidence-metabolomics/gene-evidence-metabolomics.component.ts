import { Component, inject, Input } from '@angular/core';
import { Gene } from '@sagebionetworks/agora/api-client-angular';
import { BoxPlotComponent } from '@sagebionetworks/agora/charts';
import { boxPlotChartItem } from '@sagebionetworks/agora/models';
import { HelperService } from '@sagebionetworks/agora/services';
import { ModalLinkComponent } from '@sagebionetworks/agora/shared';
import { DownloadDomImageComponent } from '../download-dom-image/download-dom-image.component';

@Component({
  selector: 'agora-gene-evidence-metabolomics',
  imports: [ModalLinkComponent, DownloadDomImageComponent, BoxPlotComponent],
  templateUrl: './gene-evidence-metabolomics.component.html',
  styleUrls: ['./gene-evidence-metabolomics.component.scss'],
})
export class GeneEvidenceMetabolomicsComponent {
  helperService = inject(HelperService);

  _gene: Gene | undefined;
  get gene(): Gene | undefined {
    return this._gene;
  }
  @Input() set gene(gene: Gene | undefined) {
    this._gene = gene;
    this.init();
  }

  boxPlotData: boxPlotChartItem[] = [];

  reset() {
    this.boxPlotData = [];
  }

  init() {
    this.reset();

    if (!this._gene?.metabolomics?.transposed_boxplot_stats) {
      this.boxPlotData = [];
      return;
    }

    const boxPlotData: boxPlotChartItem[] = [];

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
    return this.helperService.getSignificantFigures(n, b);
  }

  getSignificantText(pval: number): string {
    return pval <= 0.05 ? ' is ' : ' is not ';
  }
}
