import { Component, HostBinding, computed, inject, input } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { TooltipModule } from 'primeng/tooltip';

@Component({
  selector: 'explorers-primary-identifier-controls',
  imports: [TooltipModule, SvgIconComponent],
  templateUrl: './primary-identifier-controls.component.html',
  styleUrls: ['./primary-identifier-controls.component.scss'],
})
export class PrimaryIdentifierControlsComponent {
  comparisonToolService = inject(ComparisonToolService);

  id = input.required<string>();
  label = input.required<string>();

  @HostBinding('attr.role')
  protected readonly hostRole = 'group';

  @HostBinding('attr.aria-label')
  protected get hostAriaLabel(): string {
    return this.id();
  }

  maxPinnedItems = this.comparisonToolService.maxPinnedItems;
  hasMaxPinnedItems = this.comparisonToolService.hasMaxPinnedItems;
  viewConfig = this.comparisonToolService.viewConfig;

  isPinned = computed(() => {
    return this.comparisonToolService.isPinned(this.id());
  });

  isPinDisabled = computed(() => {
    return !this.isPinned() && this.hasMaxPinnedItems();
  });

  pinTooltip = computed(() => {
    const isPinDisabled = this.isPinDisabled();
    const isPinned = this.isPinned();

    if (!isPinned && isPinDisabled) {
      return this.comparisonToolService.disabledPinTooltip();
    }

    return isPinned ? 'Unpin this row' : 'Pin this row to the top of the list';
  });

  viewDetailsWasClicked(event: MouseEvent) {
    this.viewConfig().viewDetailsClick(this.id(), this.label());

    // MG-629: Remove focus from button and its container to prevent focus-within state from persisting
    const button = event.currentTarget as HTMLElement;
    button.blur();
  }

  pinToggle() {
    this.comparisonToolService.togglePin(this.id());
  }
}
