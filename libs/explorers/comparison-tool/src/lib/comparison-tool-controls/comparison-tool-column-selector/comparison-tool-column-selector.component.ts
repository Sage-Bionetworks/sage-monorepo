import { Component, inject, ViewEncapsulation } from '@angular/core';
import { ComparisonToolColumn } from '@sagebionetworks/explorers/models';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { OverlayBadgeModule } from 'primeng/overlaybadge';
import { PopoverModule } from 'primeng/popover';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'explorers-comparison-tool-column-selector',
  imports: [PopoverModule, SvgIconComponent, TooltipModule, OverlayBadgeModule],
  templateUrl: './comparison-tool-column-selector.component.html',
  styleUrls: ['./comparison-tool-column-selector.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolColumnSelectorComponent {
  private readonly comparisonToolService = inject(ComparisonToolService);

  get columns() {
    return this.comparisonToolService.columns();
  }

  toggleColumn(column: ComparisonToolColumn) {
    this.comparisonToolService.toggleColumn(column);
  }

  hasUnselectedColumns() {
    return this.comparisonToolService.hasUnselectedColumns();
  }
}
