import { Component, inject, Input } from '@angular/core';
import { Gene } from '@sagebionetworks/agora/api-client-angular';
import { BoxPlotComponent } from '@sagebionetworks/agora/charts';
import { HelperService } from '@sagebionetworks/agora/services';
import { DownloadDomImageComponent } from '../download-dom-image/download-dom-image.component';
import { ModalLinkComponent } from '@sagebionetworks/agora/shared';

@Component({
  selector: 'agora-gene-evidence-metabolomics',
  imports: [ModalLinkComponent, DownloadDomImageComponent, BoxPlotComponent],
  providers: [HelperService],
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

  boxPlotData: any = [];

  reset() {
    this.boxPlotData = [];
  }

  init() {
    this.reset();

    if (!this._gene?.metabolomics?.['transposed_boxplot_stats']) {
      this.boxPlotData = [];
      return;
    }

    const boxPlotData: any = [];

    this._gene.metabolomics['transposed_boxplot_stats'].forEach((item: string, index: number) => {
      boxPlotData.push({
        key: this._gene?.metabolomics?.['boxplot_group_names'][index],
        value: item,
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
