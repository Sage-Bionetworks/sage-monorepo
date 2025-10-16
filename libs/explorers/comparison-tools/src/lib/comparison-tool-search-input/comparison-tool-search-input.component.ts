import { Component, inject, ViewEncapsulation } from '@angular/core';
import { ComparisonToolFilterService } from '@sagebionetworks/explorers/services';
import { SvgIconComponent } from '@sagebionetworks/explorers/util';
import { TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { FormsModule } from '@angular/forms';
import { debounceTime, Subject } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { DEBOUNCE_TIME_MS } from '@sagebionetworks/explorers/constants';

@Component({
  selector: 'explorers-comparison-tool-search-input',
  imports: [FormsModule, TableModule, TooltipModule, SvgIconComponent],
  templateUrl: './comparison-tool-search-input.component.html',
  styleUrls: ['./comparison-tool-search-input.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class ComparisonToolSearchInputComponent {
  comparisonToolFilterService = inject(ComparisonToolFilterService);

  private searchSubject = new Subject<string>();

  constructor() {
    this.searchSubject
      .pipe(debounceTime(DEBOUNCE_TIME_MS), takeUntilDestroyed())
      .subscribe((term) => this.comparisonToolFilterService.updateSearchTerm(term));
  }

  get searchTerm() {
    return this.comparisonToolFilterService.searchTerm();
  }

  updateSearchTerm(term: string) {
    this.searchSubject.next(term);
  }

  clearSearch() {
    this.comparisonToolFilterService.updateSearchTerm('');
  }
}
