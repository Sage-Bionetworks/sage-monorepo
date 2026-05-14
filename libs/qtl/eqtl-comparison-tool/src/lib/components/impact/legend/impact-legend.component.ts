import { Component, inject, model, ViewEncapsulation } from '@angular/core';
import { SimpleTableCell, SimpleTableColumn } from '@sagebionetworks/explorers/models';
import { PlatformService } from '@sagebionetworks/explorers/services';
import { SimpleTableComponent } from '@sagebionetworks/explorers/ui';
import { CELL_CLASSES } from '@sagebionetworks/qtl/config';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';

@Component({
  selector: 'qtl-eqtl-comparison-tool-impact-legend',
  imports: [ButtonModule, DialogModule, SimpleTableComponent],
  templateUrl: './impact-legend.component.html',
  styleUrls: ['./impact-legend.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ImpactLegendComponent {
  protected readonly platformService = inject(PlatformService);

  visible = model<boolean>(false);

  protected readonly columns: SimpleTableColumn[] = [
    { name: 'Abbreviation', className: 'impact-legend-abbreviation-col' },
    { name: 'Description', className: 'impact-legend-description-col' },
  ];

  protected readonly rows: SimpleTableCell[][] = Object.entries(CELL_CLASSES).map(
    ([longName, { color, short }]) => [
      { type: 'swatch' as const, color, text: short },
      { type: 'text' as const, value: longName },
    ],
  );

  protected openInNewTab(): void {
    // TODO: navigate to the standalone legend page once it exists (QTL-42)
  }
}
