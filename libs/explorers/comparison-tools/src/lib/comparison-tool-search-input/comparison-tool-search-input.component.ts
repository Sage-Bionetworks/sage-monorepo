import { Component, inject, signal, ViewEncapsulation } from '@angular/core';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { ComparisonToolFilterService } from '../services/comparison-tool-filter.service';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-comparison-tool-search-input',
  imports: [TableModule, TooltipModule, SvgIconComponent],
  templateUrl: './comparison-tool-search-input.component.html',
  styleUrls: ['./comparison-tool-search-input.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolSearchInputComponent {
  private comparisonToolFilterService = inject(ComparisonToolFilterService);

  get searchTerm() {
    return this.comparisonToolFilterService.searchTerm();
  }

  onSearchInput(event: Event) {
    const input = event?.target as HTMLInputElement | HTMLTextAreaElement;
    if (input) {
      this.setSearchTerm(input.value);
    }
  }

  setSearchTerm(term: string) {
    this.comparisonToolFilterService.updateSearchTerm(term);
  }

  clearSearch() {
    this.setSearchTerm('');
  }
}
