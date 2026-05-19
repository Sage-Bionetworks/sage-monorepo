import { Component, signal } from '@angular/core';
import { CELL_CLASSES } from '@sagebionetworks/qtl/config';
import { MetadataChicletComponent } from '@sagebionetworks/qtl/ui';
import { getCellTypeMutedColor } from '@sagebionetworks/qtl/util';

type SidebarTab = 'impact' | 'traits' | 'resources';

@Component({
  selector: 'qtl-eqtl-comparison-tool-sidebar',
  imports: [MetadataChicletComponent],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss'],
})
export class SidebarComponent {
  // TODO: update with dynamically fetched sidebar data
  protected readonly data = {
    variant_id: 'rs29475839',
    hgnc_symbol: 'PAK1',
    cell_type: 'Astrocyte' as keyof typeof CELL_CLASSES,
  };

  protected readonly cellTypeBackgroundColor = getCellTypeMutedColor(this.data.cell_type);

  protected readonly tabs: { value: SidebarTab; label: string }[] = [
    { value: 'impact', label: 'Impact' },
    { value: 'traits', label: 'Traits' },
    { value: 'resources', label: 'Resources' },
  ];

  protected readonly activeTab = signal<SidebarTab>('impact');
}
