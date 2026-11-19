import { Component, ElementRef, signal, viewChildren } from '@angular/core';
import {
  CELL_CLASSES,
  CELL_SUBCLASSES,
  CellClassId,
  CellSubclassId,
} from '@sagebionetworks/qtl/config';
import { MetadataChicletComponent } from '@sagebionetworks/qtl/ui';
import { getCellClassMutedColor } from '@sagebionetworks/qtl/util';
import { ImpactBoxplotComponent } from '../impact/boxplot/impact-boxplot.component';
import { IMPACT_BOXPLOT_EXAMPLE_SUMMARIES } from '../impact/boxplot/impact-boxplot.constants';
import { ImpactDetails, ImpactDetailsComponent } from '../impact/details/impact-details.component';
import { ImpactForestPlotComponent } from '../impact/forest-plot/impact-forest-plot.component';
import { IMPACT_FOREST_PLOT_EXAMPLE_EFFECT_SIZES } from '../impact/forest-plot/impact-forest-plot.constants';

type SidebarTab = 'impact' | 'traits' | 'resources';

export const SIDEBAR_CHICLET_LIST_ARIA_LABEL = 'Selected eQTL';

@Component({
  selector: 'qtl-eqtl-comparison-tool-sidebar',
  imports: [
    ImpactBoxplotComponent,
    ImpactDetailsComponent,
    ImpactForestPlotComponent,
    MetadataChicletComponent,
  ],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent {
  // TODO: update with dynamically fetched sidebar data
  protected readonly data = {
    variant_id: 'rs29475839',
    hgnc_symbol: 'PAK1',
    cell_class_id: 'class_astro' as CellClassId,
    cell_subclass_id: 'astro' as CellSubclassId,
    beta: '.98',
    p_value: '1.25e-08',
    variant_location: 'chr:11:54455—252455',
    gene_location: 'chr:11:54455—252455',
    // TODO: fetch from the eQTL phenotype endpoint (EX-283)
    expression_summaries: IMPACT_BOXPLOT_EXAMPLE_SUMMARIES,
    effect_size_cell_class_data: IMPACT_FOREST_PLOT_EXAMPLE_EFFECT_SIZES,
  };

  protected readonly chicletListAriaLabel = SIDEBAR_CHICLET_LIST_ARIA_LABEL;

  protected readonly cellClass = CELL_CLASSES[this.data.cell_class_id];
  protected readonly cellSubclass = CELL_SUBCLASSES[this.data.cell_subclass_id];

  protected readonly cellClassBackgroundColor = getCellClassMutedColor(this.data.cell_class_id);

  protected readonly impactDetails: ImpactDetails = {
    beta: this.data.beta,
    pValue: this.data.p_value,
    variantLocation: this.data.variant_location,
    geneLocation: this.data.gene_location,
    cellClass: this.cellClass.name,
  };

  protected readonly tabs: { value: SidebarTab; label: string }[] = [
    { value: 'impact', label: 'Impact' },
    { value: 'traits', label: 'Traits' },
    { value: 'resources', label: 'Resources' },
  ];

  protected readonly activeTab = signal<SidebarTab>('impact');

  private readonly tabButtons = viewChildren<ElementRef<HTMLButtonElement>>('tabButton');

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
