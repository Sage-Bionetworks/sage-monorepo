import { Component, ElementRef, signal, viewChildren } from '@angular/core';
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
