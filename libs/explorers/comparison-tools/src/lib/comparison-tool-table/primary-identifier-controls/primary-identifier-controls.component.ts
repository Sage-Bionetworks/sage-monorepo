import { Component, computed, inject, input, output } from '@angular/core';
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
  viewDetailsTooltip = input<string>('View detailed results');
  viewDetailsEvent = output<string>();

  maxPinnedItems = this.comparisonToolService.maxPinnedItems;
  hasMaxPinnedItems = this.comparisonToolService.hasMaxPinnedItems;

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
      return `You have already pinned the maximum number of items (${this.maxPinnedItems()}). You must unpin some items before you can pin more.`;
    }

    return isPinned ? 'Unpin this row' : 'Pin this row to the top of the list';
  });

  viewDetailsWasClicked() {
    this.viewDetailsEvent.emit(this.id());
  }

  pinToggle() {
    this.comparisonToolService.togglePin(this.id());
  }
}
