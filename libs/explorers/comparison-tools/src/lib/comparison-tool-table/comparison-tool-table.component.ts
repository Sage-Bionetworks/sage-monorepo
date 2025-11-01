import {
  AfterViewInit,
  Component,
  effect,
  ElementRef,
  HostListener,
  inject,
  viewChild,
  ViewEncapsulation,
} from '@angular/core';
import {
  ComparisonToolFilterService,
  ComparisonToolService,
  HelperService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { TooltipModule } from 'primeng/tooltip';
import { ComparisonToolColumnsComponent } from '../comparison-tool-columns/comparison-tool-columns.component';
import { BaseTableComponent } from './base-table/base-table.component';

@Component({
  selector: 'explorers-comparison-tool-table',
  imports: [TooltipModule, ComparisonToolColumnsComponent, SvgIconComponent, BaseTableComponent],
  templateUrl: './comparison-tool-table.component.html',
  styleUrls: ['./comparison-tool-table.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolTableComponent implements AfterViewInit {
  comparisonToolService = inject(ComparisonToolService);
  comparisonToolFilterService = inject(ComparisonToolFilterService);
  helperService = inject(HelperService);
  platformService = inject(PlatformService);

  tableElement = viewChild<ElementRef>('table');

  pinnedResultsCount = this.comparisonToolService.pinnedResultsCount;
  maxPinnedItems = this.comparisonToolService.maxPinnedItems;
  hasMaxPinnedItems = this.comparisonToolService.hasMaxPinnedItems;
  disabledPinTooltip = this.comparisonToolService.disabledPinTooltip;

  searchTerm = this.comparisonToolFilterService.searchTerm;
  hasSelectedFilters = this.comparisonToolFilterService.hasSelectedFilters;

  selectedColumns = this.comparisonToolService.selectedColumns;

  pinnedData = this.comparisonToolService.pinnedData;
  unpinnedData = this.comparisonToolService.unpinnedData;

  columnWidth = 'auto';
  primaryColumnWidth = 300;

  constructor() {
    this.primaryColumnWidth = this.helperService.getNumberFromCSSValue(
      getComputedStyle(document.documentElement).getPropertyValue(
        '--comparison-tool-primary-column-width',
      ),
    );

    // Recalculate column widths whenever selected columns change
    effect(() => {
      // Access selectedColumns to track changes
      this.selectedColumns();
      // Recalculate widths on next tick to ensure DOM is updated
      setTimeout(() => {
        this.onWindowResize();
      }, 0);
    });
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.onWindowResize();
    }, 100);
  }

  @HostListener('window:resize', ['$event'])
  onWindowResize() {
    if (this.platformService.isBrowser) {
      const tableElementWidth = this.tableElement()?.nativeElement?.offsetWidth || 0;
      this.columnWidth = this.calculateNonprimaryColumnWidth(
        this.selectedColumns().length - 1,
        tableElementWidth,
      );
    }
  }

  downloadPinned() {
    // TODO: MG-451
  }

  pinAll() {
    // TODO: handle pagination (i.e. unpinnedData only contains the first page of data, rather than all unpinned data)
    this.comparisonToolService.pinList(this.unpinnedData().map((item) => item._id));
  }

  clearAllPinned() {
    this.comparisonToolService.resetPinnedItems();
  }

  calculateNonprimaryColumnWidth(nCols: number, tableWidth: number) {
    const count = Math.max(nCols, 5);
    return Math.ceil((tableWidth - this.primaryColumnWidth) / count) + 'px';
  }
}
