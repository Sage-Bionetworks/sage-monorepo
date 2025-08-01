import { Component, computed, inject } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';

@Component({
  selector: 'explorers-displayed-results',
  templateUrl: './displayed-results.component.html',
  styleUrls: ['./displayed-results.component.scss'],
})
export class DisplayedResultsComponent {
  service = inject(ComparisonToolService);
  totalResultsCount = this.service.totalResultsCount;
  pinnedResultsCount = this.service.pinnedResultsCount;
  displayedResultsCount = computed(() => this.totalResultsCount() + this.pinnedResultsCount());
}
