import { Component, ElementRef, inject, signal, viewChildren } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  HttpErrorReportLevelEnum,
  LoggerService,
  PlatformService,
  reportHttpError,
} from '@sagebionetworks/explorers/services';
import {
  EqtlPhenotype,
  EqtlPhenotypeQuery,
  EqtlPhenotypeService,
  GenotypeExpressionSummary,
} from '@sagebionetworks/qtl/api-client';
import { CellClass } from '@sagebionetworks/qtl/config';
import { MetadataChicletComponent } from '@sagebionetworks/qtl/ui';
import {
  findCellClass,
  findCellSubclass,
  getCellClassMutedColor,
  ResolvedCellSubclass,
  unknownCellClass,
  unknownCellSubclass,
} from '@sagebionetworks/qtl/util';
import { ImpactBoxplotComponent } from '../impact/boxplot/impact-boxplot.component';
import { ImpactDetails, ImpactDetailsComponent } from '../impact/details/impact-details.component';
import {
  ColoredCellClassEffectSize,
  ImpactForestPlotComponent,
} from '../impact/forest-plot/impact-forest-plot.component';
import { LegendComponent } from '../legend/legend.component';
import { catchError, EMPTY, map, Observable } from 'rxjs';

export const SIDEBAR_TABS = [
  { value: 'impact', label: 'Impact' },
  { value: 'legend', label: 'Legend' },
] as const;

type SidebarTab = (typeof SIDEBAR_TABS)[number]['value'];

export const SIDEBAR_CHICLET_LIST_ARIA_LABEL = 'Selected eQTL';
const UNKNOWN_CELL_CLASS_ID_MESSAGE = 'Unknown cell class ID';
const UNKNOWN_CELL_SUBCLASS_ID_MESSAGE = 'Unknown cell subclass ID';
export const EQTL_PHENOTYPE_ERROR_MESSAGE = 'Error loading eQTL phenotype data';

// TODO(EX-281): take the eQTL from the selected row
export const SIDEBAR_PLACEHOLDER_PHENOTYPE_QUERY: EqtlPhenotypeQuery = {
  variant: 'rs2120461',
  ensemblGeneId: 'ENSG00000142599',
  cellTypeId: 'astro',
};

interface ImpactPlotData {
  expressionSummaries: GenotypeExpressionSummary[];
  effectSizes: ColoredCellClassEffectSize[];
}

@Component({
  selector: 'qtl-eqtl-comparison-tool-sidebar',
  imports: [
    ImpactBoxplotComponent,
    ImpactDetailsComponent,
    ImpactForestPlotComponent,
    LegendComponent,
    MetadataChicletComponent,
  ],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent {
  private readonly logger = inject(LoggerService).forSource('SidebarComponent');
  private readonly platformService = inject(PlatformService);
  private readonly eqtlPhenotypeService = inject(EqtlPhenotypeService);

  // TODO: update with dynamically fetched sidebar data
  protected readonly data = {
    variant_id: 'rs29475839',
    hgnc_symbol: 'PAK1',
    cell_class_id: 'class_astro',
    cell_subclass_id: 'astro',
    beta: '.98',
    p_value: '1.25e-08',
    variant_location: 'chr:11:54455—252455',
    gene_location: 'chr:11:54455—252455',
  };

  protected readonly chicletListAriaLabel = SIDEBAR_CHICLET_LIST_ARIA_LABEL;

  protected readonly cellClass = this.lookUpCellClass(this.data.cell_class_id);
  protected readonly cellSubclass = this.lookUpCellSubclass(this.data.cell_subclass_id);
  protected readonly impactPlotData = toSignal(this.loadImpactPlotData());

  protected readonly cellClassBackgroundColor = getCellClassMutedColor(this.cellClass);

  protected readonly impactDetails: ImpactDetails = {
    beta: this.data.beta,
    pValue: this.data.p_value,
    variantLocation: this.data.variant_location,
    geneLocation: this.data.gene_location,
    cellClass: this.cellClass.name,
  };

  protected readonly tabs = SIDEBAR_TABS;

  protected readonly activeTab = signal<SidebarTab>('impact');

  private readonly tabButtons = viewChildren<ElementRef<HTMLButtonElement>>('tabButton');

  private loadImpactPlotData(): Observable<ImpactPlotData> {
    if (!this.platformService.isBrowser) return EMPTY;

    return this.eqtlPhenotypeService.getEqtlPhenotype(SIDEBAR_PLACEHOLDER_PHENOTYPE_QUERY).pipe(
      map((phenotype) => this.toImpactPlotData(phenotype)),
      catchError((error) => {
        reportHttpError(this.logger, EQTL_PHENOTYPE_ERROR_MESSAGE, {
          error,
          level: HttpErrorReportLevelEnum.Error,
        });
        return EMPTY;
      }),
    );
  }

  private toImpactPlotData(phenotype: EqtlPhenotype): ImpactPlotData {
    return {
      expressionSummaries: phenotype.expression_summaries,
      effectSizes: phenotype.effect_size_cell_class_data.map((effectSize) => ({
        ...effectSize,
        color: this.lookUpCellClass(effectSize.cell_type_id).color,
      })),
    };
  }

  private lookUpCellClass(cellClassId: string): CellClass {
    const cellClass = findCellClass(cellClassId);
    if (cellClass) return cellClass;
    this.logger.error(UNKNOWN_CELL_CLASS_ID_MESSAGE, {
      data: { cellClassId, variantId: this.data.variant_id },
    });
    return unknownCellClass(cellClassId);
  }

  private lookUpCellSubclass(cellSubclassId: string): ResolvedCellSubclass {
    const cellSubclass = findCellSubclass(cellSubclassId);
    if (cellSubclass) return cellSubclass;
    this.logger.error(UNKNOWN_CELL_SUBCLASS_ID_MESSAGE, {
      data: { cellSubclassId, variantId: this.data.variant_id },
    });
    return unknownCellSubclass(cellSubclassId);
  }

  protected onTabKeydown(event: KeyboardEvent, index: number): void {
    const last = this.tabs.length - 1;
    let nextIndex: number | null = null;

    switch (event.key) {
      case 'ArrowRight':
        nextIndex = index === last ? 0 : index + 1;
        break;
      case 'ArrowLeft':
        nextIndex = index === 0 ? last : index - 1;
        break;
      case 'Home':
        nextIndex = 0;
        break;
      case 'End':
        nextIndex = last;
        break;
    }

    if (nextIndex === null) return;

    event.preventDefault();
    this.activeTab.set(this.tabs[nextIndex].value);
    this.tabButtons()[nextIndex]?.nativeElement.focus();
  }
}
