import { Component, computed, inject } from '@angular/core';
import { getViewNounLabel } from '@sagebionetworks/explorers/constants';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';

@Component({
  selector: 'explorers-displayed-results',
  templateUrl: './displayed-results.component.html',
  styleUrls: ['./displayed-results.component.scss'],
})
export class DisplayedResultsComponent {
  service = inject(ComparisonToolService);
  unpinnedRowCount = this.service.unpinnedRowCount;
  pinnedRowCount = this.service.pinnedRowCount;
  displayedResultsCount = computed(() => this.unpinnedRowCount() + this.pinnedRowCount());
  displayedResultsLabel = computed(() => getViewNounLabel('Displayed', this.service.nouns()));
}
