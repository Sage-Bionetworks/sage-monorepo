import { Component, computed, inject } from '@angular/core';
import { ComparisonToolService } from '@sagebionetworks/explorers/services';

@Component({
  selector: 'explorers-help-links',
  templateUrl: './help-links.component.html',
  styleUrls: ['./help-links.component.scss'],
})
export class HelpLinksComponent {
  comparisonToolService = inject(ComparisonToolService);

  viewConfig = this.comparisonToolService.viewConfig;

  hasData = computed(() => {
    const unpinnedData = this.comparisonToolService.unpinnedData();
    return unpinnedData.length > 0;
  });
}
