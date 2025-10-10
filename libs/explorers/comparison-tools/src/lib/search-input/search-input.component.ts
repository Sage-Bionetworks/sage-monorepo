import { Component, inject, signal, ViewEncapsulation } from '@angular/core';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { FilterPanelService } from '../filter-panel/filter-panel.service';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';

@Component({
  selector: 'explorers-search-input',
  imports: [TableModule, TooltipModule, SvgIconComponent],
  templateUrl: './search-input.component.html',
  styleUrls: ['./search-input.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class SearchInputComponent {
  private filterPanelService = inject(FilterPanelService);
  searchTerm = signal('');

  onSearchInput(event: Event) {
    const el = event?.target as HTMLTextAreaElement;
    this.setSearchTerm(el.value);
  }

  setSearchTerm(term: string) {
    this.searchTerm.set(term);
    this.filter();
  }

  filter() {
    const filters: { [key: string]: any } = {};

    if (this.searchTerm()) {
      if (this.searchTerm().indexOf(',') !== -1) {
        const terms = this.searchTerm()
          .toLowerCase()
          .split(',')
          .map((t: string) => t.trim())
          .filter((t: string) => t !== '');
        filters['search_array'] = {
          value: terms,
          matchMode: 'intersect',
        };
      } else {
        filters['search_string'] = {
          value: this.searchTerm().toLowerCase(),
          matchMode: 'contains',
        };
      }
    }

    this.filterPanelService.updateSearchFilters(filters);
  }

  clearSearch() {
    this.searchTerm.set('');
    this.filter();
  }
}
