import { Component, computed, inject } from '@angular/core';
import {
  getViewNounLabel,
  TABLE_DATA_LOADING_MESSAGE_SHORT,
} from '@sagebionetworks/explorers/constants';
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
  readonly loadingMessage = TABLE_DATA_LOADING_MESSAGE_SHORT;

  // Unknown while any fetch is in flight, since the unpinned and pinned fetches land separately
  // and their sum would otherwise mix the previous view's rows with the new view's
  displayedResultsCount = computed(() =>
    this.service.isLoadingTableData() ? null : this.unpinnedRowCount() + this.pinnedRowCount(),
  );
  displayedResultsLabel = computed(() => getViewNounLabel('Displayed', this.service.nouns()));
}
