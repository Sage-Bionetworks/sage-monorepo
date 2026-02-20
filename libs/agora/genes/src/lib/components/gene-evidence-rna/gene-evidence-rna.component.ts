import {
  AfterViewChecked,
  Component,
  DestroyRef,
  inject,
  Input,
  PLATFORM_ID,
  ViewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

import { isPlatformBrowser } from '@angular/common';
import {
  DistributionService,
  Gene,
  MedianExpression,
  RnaDifferentialExpression,
} from '@sagebionetworks/agora/api-client';
import {
  BoxPlotComponent,
  CandlestickChartComponent,
  MedianBarChartComponent,
  RowChartComponent,
} from '@sagebionetworks/agora/charts';
import { BoxPlotChartItem, RowChartItem } from '@sagebionetworks/agora/models';
import { HelperService } from '@sagebionetworks/agora/services';
import { LoggerService } from '@sagebionetworks/explorers/services';
import { ModalLinkComponent } from '@sagebionetworks/explorers/util';
import { getStatisticalModels } from '../../helpers';
import { DownloadDomImageComponent } from '@sagebionetworks/explorers/ui';
import { GeneModelSelectorComponent } from '../gene-model-selector/gene-model-selector.component';
import { GeneNetworkComponent } from '../gene-network/gene-network.component';

@Component({
  selector: 'agora-gene-evidence-rna',
  standalone: true,
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
  templateUrl: './gene-evidence-rna.component.html',
  styleUrls: ['./gene-evidence-rna.component.scss'],
})
export class GeneEvidenceRnaComponent implements AfterViewChecked {
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformId: Record<string, any> = inject(PLATFORM_ID);
  private readonly logger = inject(LoggerService);

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

  differentialExpressionChartData: BoxPlotChartItem[] = [];
  differentialExpressionYAxisMin: number | undefined;
  differentialExpressionYAxisMax: number | undefined;

  consistencyOfChangeChartData: RowChartItem[] = [];

  @ViewChild(BoxPlotComponent) boxPlotComponent: BoxPlotComponent | null = null;
  hasScrolled = false;

  reset() {
    this.statisticalModels = [];
    this.selectedStatisticalModel = '';

    this.medianExpression = [];

    this.resetDifferentialExpression();

    this.consistencyOfChangeChartData = [];

    this.hasScrolled = false;
  }

  resetDifferentialExpression() {
    this.differentialExpression = [];
    this.differentialExpressionChartData = [];
    this.differentialExpressionYAxisMin = undefined;
    this.differentialExpressionYAxisMax = undefined;
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
    if (isPlatformBrowser(this.platformId)) {
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
  }

  initMedianExpression() {
    if (!this._gene?.median_expression?.length) {
      this.medianExpression = [];
      return;
    }

    this.medianExpression = this._gene.median_expression.filter((d) => d.median && d.median > 0);
  }

  initDifferentialExpression() {
    this.resetDifferentialExpression();

    if (!this._gene?.rna_differential_expression?.length) {
      this.differentialExpression = [];
      return;
    }

    this.differentialExpression = this._gene.rna_differential_expression.filter((g) => {
      return g.model === this.selectedStatisticalModel;
    });

    this.logger.log(
      `GeneEvidenceRnaComponent: Loading distribution for model ${this.selectedStatisticalModel}`,
    );

    this.distributionService
      .getDistribution()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((data) => {
        const distribution = data.rna_differential_expression.filter((data) => {
          return data.model === this.selectedStatisticalModel;
        });

        const differentialExpressionChartData: BoxPlotChartItem[] = [];

        this.differentialExpression.forEach((item) => {
          const data = distribution.find((d) => {
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
    this.consistencyOfChangeChartData = this.differentialExpression.map((item) => {
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
