import {
  Component,
  computed,
  effect,
  ElementRef,
  inject,
  linkedSignal,
  signal,
  viewChildren,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  HttpErrorReportLevelEnum,
  LoggerService,
  PlatformService,
  reportHttpError,
} from '@sagebionetworks/explorers/services';
import {
  Eqtl,
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
  formatGenomicLocation,
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
import { EqtlComparisonToolService } from '../../services/eqtl-comparison-tool.service';
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
export const SELECTED_EQTL_NOT_FOUND_MESSAGE = 'Selected eQTL not found in the loaded rows';
export const SIDEBAR_NO_DATA_MESSAGE = 'No data available';

// TODO(EX-281): take the eQTL from the selected row
export const SIDEBAR_PLACEHOLDER_PHENOTYPE_QUERY: EqtlPhenotypeQuery = {
  variant: 'rs2120461',
  ensemblGeneId: 'ENSG00000142599',
  cellTypeId: 'astro',
};

interface SelectedEqtlView {
  variant: string;
  geneSymbol: string;
  cellClass: CellClass;
  cellClassBackgroundColor: string;
  cellSubclass: ResolvedCellSubclass;
  impactDetails: ImpactDetails;
}

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
  private readonly comparisonToolService = inject(EqtlComparisonToolService);

  protected readonly chicletListAriaLabel = SIDEBAR_CHICLET_LIST_ARIA_LABEL;
  protected readonly noDataMessage = SIDEBAR_NO_DATA_MESSAGE;

  // Keeps the last matching row, since the selection survives a sort or page change that moves its
  // row off the rows on screen.
  private readonly selectedEqtl = linkedSignal<
    { selectedRowId: string | null; rows: Eqtl[] },
    Eqtl | undefined
  >({
    source: () => ({
      selectedRowId: this.comparisonToolService.selectedRowId(),
      rows: [
        ...this.comparisonToolService.pinnedData(),
        ...this.comparisonToolService.unpinnedData(),
      ],
    }),
    computation: ({ selectedRowId, rows }, previous) => {
      if (selectedRowId === null) return undefined;
      const isSelected = (row: Eqtl) => this.comparisonToolService.rowId(row) === selectedRowId;
      return (
        rows.find(isSelected) ??
        (previous?.value && isSelected(previous.value) ? previous.value : undefined)
      );
    },
  });

  protected readonly selectedEqtlView = computed(() => {
    const eqtl = this.selectedEqtl();
    return eqtl && this.toSelectedEqtlView(eqtl);
  });

  protected readonly impactPlotData = toSignal(this.loadImpactPlotData());

  private readonly reportMissingSelectedEqtl = effect(() => {
    const selectedRowId = this.comparisonToolService.selectedRowId();
    if (selectedRowId === null || this.selectedEqtl()) return;
    this.logger.error(SELECTED_EQTL_NOT_FOUND_MESSAGE, { data: { selectedRowId } });
  });

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

  private toSelectedEqtlView(eqtl: Eqtl): SelectedEqtlView {
    const cellSubclass = this.lookUpCellSubclass(eqtl.cell_type_id, eqtl.composite_id);
    const { cellClass } = cellSubclass;
    return {
      variant: eqtl.variant,
      geneSymbol: eqtl.gene_symbol,
      cellClass,
      cellClassBackgroundColor: getCellClassMutedColor(cellClass),
      cellSubclass,
      impactDetails: {
        beta: String(eqtl.beta),
        pValue: eqtl.adj_p_val,
        variantLocation: formatGenomicLocation(eqtl.variant_location),
        geneLocation: formatGenomicLocation(eqtl.gene_location),
        cellClass: cellClass.name,
      },
    };
  }

  private toImpactPlotData(phenotype: EqtlPhenotype): ImpactPlotData {
    return {
      expressionSummaries: phenotype.expression_summaries,
      effectSizes: phenotype.effect_size_cell_class_data.map((effectSize) => ({
        ...effectSize,
        color: this.lookUpCellClass(effectSize.cell_type_id, phenotype.variant).color,
      })),
    };
  }

  private lookUpCellClass(cellClassId: string, variant: string): CellClass {
    const cellClass = findCellClass(cellClassId);
    if (cellClass) return cellClass;
    this.logger.error(UNKNOWN_CELL_CLASS_ID_MESSAGE, { data: { cellClassId, variant } });
    return unknownCellClass(cellClassId);
  }

  private lookUpCellSubclass(cellSubclassId: string, eqtlId: string): ResolvedCellSubclass {
    const cellSubclass = findCellSubclass(cellSubclassId);
    if (cellSubclass) return cellSubclass;
    this.logger.error(UNKNOWN_CELL_SUBCLASS_ID_MESSAGE, { data: { cellSubclassId, eqtlId } });
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
