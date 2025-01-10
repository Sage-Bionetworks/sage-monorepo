import { AfterViewChecked, Component, inject, Input, ViewChild } from '@angular/core';

import { HelperService } from '@sagebionetworks/agora/services';
import {
  BoxPlotComponent,
  CandlestickChartComponent,
  MedianBarChartComponent,
  RowChartComponent,
} from '@sagebionetworks/agora/charts';
import { GeneNetworkComponent } from '../gene-network/gene-network.component';
import { GeneModelSelectorComponent } from '../gene-model-selector/gene-model-selector.component';
import {
  DistributionService,
  Gene,
  MedianExpression,
  RnaDifferentialExpression,
} from '@sagebionetworks/agora/api-client-angular';
import { getStatisticalModels } from '../../helpers';
import { DownloadDomImageComponent } from '../download-dom-image/download-dom-image.component';
import { ModalLinkComponent } from '@sagebionetworks/agora/shared';

@Component({
  selector: 'agora-gene-evidence-rna',
  imports: [
    GeneNetworkComponent,
    GeneModelSelectorComponent,
    CandlestickChartComponent,
    RowChartComponent,
    MedianBarChartComponent,
    ModalLinkComponent,
    DownloadDomImageComponent,
    BoxPlotComponent,
  ],
  providers: [HelperService],
  templateUrl: './gene-evidence-rna.component.html',
  styleUrls: ['./gene-evidence-rna.component.scss'],
})
export class GeneEvidenceRnaComponent implements AfterViewChecked {
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

  statisticalModels: string[] = [];
  selectedStatisticalModel = '';

  medianExpression: MedianExpression[] = [];
  differentialExpression: RnaDifferentialExpression[] = [];

  differentialExpressionChartData: any | undefined;
  differentialExpressionYAxisMin: number | undefined;
  differentialExpressionYAxisMax: number | undefined;

  consistencyOfChangeChartData: any | undefined;

  @ViewChild(BoxPlotComponent) boxPlotComponent: BoxPlotComponent | null = null;
  hasScrolled = false;

  reset() {
    this.statisticalModels = [];
    this.selectedStatisticalModel = '';

    this.medianExpression = [];
    this.differentialExpression = [];

    this.differentialExpressionChartData = undefined;
    this.differentialExpressionYAxisMin = undefined;
    this.differentialExpressionYAxisMax = undefined;

    this.consistencyOfChangeChartData = undefined;

    this.hasScrolled = false;
  }

  init() {
    this.reset();

    if (!this._gene?.rna_differential_expression) {
      return;
    }

    this.statisticalModels = getStatisticalModels(this._gene);

    const urlModelParam = this.helperService.getUrlParam('model');
    this.selectedStatisticalModel = urlModelParam || this.statisticalModels[0];

    this.initMedianExpression();
    this.initDifferentialExpression();
    this.initConsistencyOfChange();
  }

  ngAfterViewChecked() {
    this.scrollToAnchorLink();
  }

  scrollToAnchorLink() {
    // AG-1408 - wait for differential expression box plot to finish loading before scrolling
    if (this.boxPlotComponent?.isInitialized && !this.hasScrolled) {
      const hash = window.location.hash.slice(1);
      if (hash) {
        const target = document.getElementById(hash);
        if (target) {
          window.scrollTo(0, this.helperService.getOffset(target).top - 150);
          this.hasScrolled = true;
        }
      }
    }
  }

  initMedianExpression() {
    if (!this._gene?.median_expression?.length) {
      this.medianExpression = [];
      return;
    }

    this.medianExpression = this._gene.median_expression.filter((d) => d.median && d.median > 0);
  }

  initDifferentialExpression() {
    if (!this._gene?.rna_differential_expression?.length) {
      this.differentialExpression = [];
      return;
    }

    this.differentialExpression = this._gene.rna_differential_expression.filter((g: any) => {
      return g.model === this.selectedStatisticalModel;
    });

    this.distributionService.getDistribution().subscribe((data: any) => {
      const distribution = data.rna_differential_expression.filter((data: any) => {
        return data.model === this.selectedStatisticalModel;
      });

      const differentialExpressionChartData: any = [];

      this.differentialExpression.forEach((item: any) => {
        const data: any = distribution.find((d: any) => {
          return d.tissue === item.tissue;
        });

        if (data) {
          const yAxisMin = item.logfc < data.min ? item.logfc : data.min;
          const yAxisMax = item.logfc > data.max ? item.logfc : data.max;

          if (
            this.differentialExpressionYAxisMin === undefined ||
            yAxisMin < this.differentialExpressionYAxisMin
          ) {
            this.differentialExpressionYAxisMin = yAxisMin;
          }

          if (
            this.differentialExpressionYAxisMax === undefined ||
            yAxisMax > this.differentialExpressionYAxisMax
          ) {
            this.differentialExpressionYAxisMax = yAxisMax;
          }

          differentialExpressionChartData.push({
            key: data.tissue,
            value: [data.min, data.median, data.max],
            circle: {
              value: item.logfc,
              tooltip:
                (item.hgnc_symbol || item.ensembl_gene_id) +
                ' is ' +
                (item.adj_p_val <= 0.05 ? ' ' : 'not ') +
                'significantly differentially expressed in ' +
                item.tissue +
                ' with a log fold change value of ' +
                this.helperService.getSignificantFigures(item.logfc, 3) +
                ' and an adjusted p-value of ' +
                this.helperService.getSignificantFigures(item.adj_p_val, 3) +
                '.',
            },
            quartiles:
              data.first_quartile > data.third_quartile
                ? [data.third_quartile, data.median, data.first_quartile]
                : [data.first_quartile, data.median, data.third_quartile],
          });
        }
      });

      if (this.differentialExpressionYAxisMin) {
        this.differentialExpressionYAxisMin -= 0.2;
      }

      if (this.differentialExpressionYAxisMax) {
        this.differentialExpressionYAxisMax += 0.2;
      }

      this.differentialExpressionChartData = differentialExpressionChartData;
    });
  }

  initConsistencyOfChange() {
    this.consistencyOfChangeChartData = this.differentialExpression.map((item: any) => {
      return {
        key: [item.tissue, item.ensembl_gene_id, item.model],
        value: {
          adj_p_val: item.adj_p_val,
          fc: item.fc,
          logfc: item.logfc,
        },
        tissue: item.tissue,
        ci_l: item.ci_l,
        ci_r: item.ci_r,
      };
    });
  }

  onStatisticalModelChange(event: any) {
    if (!event) {
      return;
    }
    if (!this._gene?.rna_differential_expression) {
      return;
    }
    this.selectedStatisticalModel = event.name;
    this.initDifferentialExpression();
    this.initConsistencyOfChange();
  }
}
