import { Component, inject, Input } from '@angular/core';

import {
  DistributionService,
  Gene,
  ProteinDifferentialExpression,
  ProteomicsDistribution,
} from '@sagebionetworks/agora/api-client';
import { BoxPlotComponent } from '@sagebionetworks/agora/charts';
import { BoxPlotChartItem, ChartRange } from '@sagebionetworks/agora/models';
import { HelperService } from '@sagebionetworks/agora/services';
import { ModalLinkComponent } from '@sagebionetworks/agora/shared';
import { DownloadDomImageComponent } from '@sagebionetworks/explorers/ui';
import { GeneProteinSelectorComponent } from '../gene-protein-selector/gene-protein-selector.component';

@Component({
  selector: 'agora-gene-evidence-proteomics',
  imports: [
    ModalLinkComponent,
    DownloadDomImageComponent,
    GeneProteinSelectorComponent,
    BoxPlotComponent,
  ],
  templateUrl: './gene-evidence-proteomics.component.html',
  styleUrls: ['./gene-evidence-proteomics.component.scss'],
})
export class GeneEvidenceProteomicsComponent {
  helperService = inject(HelperService);
  distributionService = inject(DistributionService);

  _gene: Gene | undefined;
  get gene(): Gene | undefined {
    return this._gene;
  }
  @Input() set gene(gene: Gene | undefined) {
    this._gene = gene;
    this.init();
  }

  uniProtIds: string[] = [];
  selectedUniProtId = '';

  LFQData: BoxPlotChartItem[] = [];
  LFQRange: ChartRange | undefined;

  SRMData: BoxPlotChartItem[] = [];
  SRMRange: ChartRange | undefined;

  TMTData: BoxPlotChartItem[] = [];
  TMTRange: ChartRange | undefined;

  reset() {
    this.uniProtIds = [];
    this.selectedUniProtId = '';

    this.resetSRM();
    this.resetLFQ();
    this.resetTMT();
  }

  resetSRM() {
    this.SRMData = [];
    this.SRMRange = undefined;
  }

  resetLFQ() {
    this.LFQData = [];
    this.LFQRange = undefined;
  }

  resetTMT() {
    this.TMTData = [];
    this.TMTRange = undefined;
  }

  init() {
    this.reset();

    this.uniProtIds = [];

    this._gene?.proteomics_LFQ?.forEach((item: ProteinDifferentialExpression) => {
      if (!this.uniProtIds.includes(item.uniprotid)) {
        this.uniProtIds.push(item.uniprotid);
      }
    });

    this._gene?.proteomics_TMT?.forEach((item: ProteinDifferentialExpression) => {
      if (!this.uniProtIds.includes(item.uniprotid)) {
        this.uniProtIds.push(item.uniprotid);
      }
    });

    this.uniProtIds.sort();
    if (!this.selectedUniProtId) {
      this.selectedUniProtId = this.uniProtIds[0];
    }

    this.initSRM();
    this.initLFQ();
    this.initTMT();
  }

  processDifferentialExpressionData(
    item: ProteinDifferentialExpression,
    data: ProteomicsDistribution,
    range: ChartRange,
    proteomicData: BoxPlotChartItem[],
  ) {
    const yAxisMin = item.log2_fc < data.min ? item.log2_fc : data.min;
    const yAxisMax = item.log2_fc > data.max ? item.log2_fc : data.max;

    if (yAxisMin < range.Min) {
      range.Min = yAxisMin;
    }

    if (yAxisMax > range.Max) {
      range.Max = yAxisMax;
    }

    proteomicData.push({
      key: data.tissue,
      value: [data.min, data.median, data.max],
      circle: {
        value: item.log2_fc,
        tooltip: this.getTooltipText(item),
      },
      quartiles:
        data.first_quartile > data.third_quartile
          ? [data.third_quartile, data.median, data.first_quartile]
          : [data.first_quartile, data.median, data.third_quartile],
    });
  }

  initSRM() {
    this.resetSRM();
    this.distributionService.getDistribution().subscribe((data) => {
      const distribution = data.proteomics_SRM;
      const differentialExpression = this._gene?.proteomics_SRM || [];
      const proteomicData: BoxPlotChartItem[] = [];

      differentialExpression.forEach((item) => {
        const data = distribution.find((d) => {
          return d.tissue === item.tissue;
        });

        if (data) {
          if (!this.SRMRange) this.SRMRange = new ChartRange(data.min, data.max);
          this.processDifferentialExpressionData(item, data, this.SRMRange, proteomicData);
        }
      });

      this.SRMData = proteomicData;
    });
  }

  initLFQ() {
    this.resetLFQ();
    this.distributionService.getDistribution().subscribe((data) => {
      const distribution = data.proteomics_LFQ;
      const differentialExpression =
        this._gene?.proteomics_LFQ?.filter((item) => {
          return item.uniprotid === this.selectedUniProtId;
        }) || [];
      const proteomicData: BoxPlotChartItem[] = [];

      differentialExpression.forEach((item) => {
        const data = distribution.find((d) => {
          return d.tissue === item.tissue;
        });

        if (data) {
          if (!this.LFQRange) this.LFQRange = new ChartRange(data.min, data.max);
          this.processDifferentialExpressionData(item, data, this.LFQRange, proteomicData);
        }
      });

      this.LFQData = proteomicData;
    });
  }

  initTMT() {
    this.resetTMT();
    this.distributionService.getDistribution().subscribe((data) => {
      const distribution = data.proteomics_TMT;
      const differentialExpression =
        this._gene?.proteomics_TMT?.filter((item) => {
          return item.uniprotid === this.selectedUniProtId;
        }) || [];
      const proteomicData: BoxPlotChartItem[] = [];

      differentialExpression.forEach((item) => {
        const data = distribution.find((d) => {
          return d.tissue === item.tissue;
        });

        if (data) {
          if (!this.TMTRange) this.TMTRange = new ChartRange(data.min, data.max);
          this.processDifferentialExpressionData(item, data, this.TMTRange, proteomicData);
        }
      });

      this.TMTData = proteomicData;
    });
  }

  onProteinChange(event: any) {
    if (!this._gene?.proteomics_LFQ) {
      return;
    }
    this.selectedUniProtId = event.value;
    this.initLFQ();
    this.initTMT();
  }

  getTooltipText(item: ProteinDifferentialExpression) {
    const tooltipText = `${item.hgnc_symbol || item.ensembl_gene_id} is${item.cor_pval <= 0.05 ? '' : ' not'} significantly differentially expressed in ${item.tissue} with a log fold change value of ${this.helperService.getSignificantFigures(item.log2_fc, 3)} and an adjusted p-value of ${this.helperService.getSignificantFigures(item.cor_pval, 3)}.`;
    return tooltipText;
  }
}
