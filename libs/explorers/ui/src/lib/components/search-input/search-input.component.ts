import {
  AfterViewInit,
  Component,
  DestroyRef,
  ElementRef,
  HostListener,
  inject,
  input,
  output,
  viewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faMagnifyingGlass, faSpinner, faXmark } from '@fortawesome/free-solid-svg-icons';
import { SearchResult } from '@sagebionetworks/explorers/models';
import { SanitizeHtmlPipe } from '@sagebionetworks/explorers/util';
import {
  catchError,
  debounceTime,
  EMPTY,
  filter,
  fromEvent,
  Observable,
  of,
  switchMap,
} from 'rxjs';
import sanitizeHtml from 'sanitize-html';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'explorers-search-input',
  imports: [SvgImageComponent, FormsModule, FontAwesomeModule, SanitizeHtmlPipe],
  templateUrl: './search-input.component.html',
  styleUrls: ['./search-input.component.scss'],
  standalone: true,
})
export class SearchInputComponent implements AfterViewInit {
  router = inject(Router);
  elementRef = inject(ElementRef);
  destroyRef = inject(DestroyRef);

  searchNavigated = output();

  searchPlaceholder = input.required<string>();
  searchImagePath = input<string | undefined>();
  searchImageAltText = input<string>('');
  hasThickBorder = input<boolean>(false);

  private readonly MINIMUM_SEARCH_LENGTH_DEFAULT = 3;
  minimumSearchLength = input<number, number>(this.MINIMUM_SEARCH_LENGTH_DEFAULT, {
    transform: (value: number) => {
      if (value >= 10) {
        console.warn(
          `minimumSearchLength must be less than 10, received ${value}. Using default value of ${this.MINIMUM_SEARCH_LENGTH_DEFAULT}.`,
        );
        return this.MINIMUM_SEARCH_LENGTH_DEFAULT;
      }
      return Math.max(1, Math.floor(value)); // Ensure it's at least 1 and is an integer
    },
  });

  navigateToResult = input.required<(id: string) => void>();
  getSearchResults = input.required<(query: string) => Observable<SearchResult[]>>();
  getNoSearchResultsMessage = input<(query: string) => string>(
    (query: string) => 'No results match your search term.',
  );
  checkQueryForErrors = input.required<(query: string) => string>(); // empty string if no error
  sanitizeQuery = input<(query: string) => string>((query: string) => query); // default is no-op
  formatResultForDisplay = input<(result: SearchResult) => string>(
    (result: SearchResult) => result.id,
  );
  formatResultSubtextForDisplay = input<(result: SearchResult) => string | undefined>(
    (result: SearchResult) => undefined,
  );

  faMagnifyingGlass = faMagnifyingGlass;
  faSpinner = faSpinner;
  faXmark = faXmark;

  isLoading = false;
  query = '';
  error = '';
  results: SearchResult[] = [];
  selectedResultIndex = -1; // -1 means no result is selected

  showResults = false;

  root = viewChild.required<ElementRef>('root');
  input = viewChild.required<ElementRef<HTMLInputElement>>('input');

  @HostListener('document:click', ['$event'])
  onClick(event: Event) {
    this.checkClickIsInsideComponent(event);
  }

  ngAfterViewInit() {
    fromEvent(this.input().nativeElement, 'keyup')
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        // Filter out navigation keys to prevent triggering search
        filter((event: Event) => {
          const keyEvent = event as KeyboardEvent;
          return !['ArrowDown', 'ArrowUp', 'Enter', 'Escape'].includes(keyEvent.key);
        }),
        debounceTime(500),
        switchMap((event: Event) => {
          const target = event.target as HTMLInputElement;
          return this.search(target.value).pipe(
            catchError(() => {
              this.error = 'An unknown error occurred, please try again.';
              this.isLoading = false;
              this.showResults = true;
              return of([]);
            }),
          );
        }),
      )
      .subscribe((response: SearchResult[]) => {
        this.showResults = true;
        this.setResults(response);
      });

    fromEvent(this.input().nativeElement, 'keydown')
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((event: Event) => {
        this.handleKeyboardNavigation(event as KeyboardEvent);
      });
  }

  getNotValidSearchMessage(minimumSearchLength: number): string {
    const numWords = ['one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine'];
    return `Please enter at least ${numWords[minimumSearchLength - 1]} character${minimumSearchLength > 1 ? 's' : ''}.`;
  }

  search(query: string): Observable<SearchResult[]> {
    this.results = [];
    this.error = '';
    this.selectedResultIndex = -1;

    // If query is empty, hide results and return
    if (query.length === 0) {
      this.showResults = false;
      this.isLoading = false;
      return EMPTY;
    }

    // Optional frontend sanitization - expect backend to handle input escaping and security validation
    const sanitizedQuery = this.sanitizeQuery()(query);
    this.query = sanitizedQuery;
    if (sanitizedQuery.length > 0 && sanitizedQuery.length < this.minimumSearchLength()) {
      this.error = this.getNotValidSearchMessage(this.minimumSearchLength());
    } else {
      this.error = this.checkQueryForErrors()(sanitizedQuery);
    }

    if (this.error) {
      this.showResults = true;
    }

    this.isLoading = !!(sanitizedQuery && !this.error);
    return this.isLoading ? this.getSearchResults()(sanitizedQuery) : EMPTY;
  }

  setResults(results: SearchResult[]) {
    if (results.length < 1 && !this.error) {
      this.error = this.getNoSearchResultsMessage()(this.query);
    }
    this.results = results;
    this.selectedResultIndex = -1;
    this.isLoading = false;
  }

  handleKeyboardNavigation(event: KeyboardEvent) {
    if (!this.showResults || this.results.length === 0) {
      return;
    }

    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault();
        this.selectedResultIndex = Math.min(this.selectedResultIndex + 1, this.results.length - 1);
        this.scrollSelectedIntoView();
        break;

      case 'ArrowUp':
        event.preventDefault();
        this.selectedResultIndex = Math.max(this.selectedResultIndex - 1, -1);
        this.scrollSelectedIntoView();
        break;

      case 'Enter':
        event.preventDefault();
        if (this.selectedResultIndex >= 0 && this.selectedResultIndex < this.results.length) {
          const selectedResult = this.results[this.selectedResultIndex];
          this.goToResult(selectedResult.id);
        }
        break;

      case 'Escape':
        event.preventDefault();
        this.clearInput();
        break;
    }
  }

  goToResult(id: string) {
    this.input().nativeElement.blur();
    this.query = '';
    this.results = [];
    this.selectedResultIndex = -1;
    this.showResults = false;
    this.searchNavigated.emit();
    this.navigateToResult()(id);
  }

  onFocus() {
    if (this.results.length > 0 || this.error) {
      this.showResults = true;
    }
  }

  clearInput() {
    this.input().nativeElement.focus();
    this.query = '';
    this.error = '';
    this.results = [];
    this.selectedResultIndex = -1;
    this.showResults = false;
  }

  checkClickIsInsideComponent(event: Event) {
    // if clicked element is not part of this component, hide results
    if (!this.root().nativeElement.contains(event.target)) {
      this.showResults = false;
    }
  }

  highlightMatches(text: string, query: string): string {
    if (!text || !query || query.length < 1) {
      return sanitizeHtml(text);
    }

    // Escape special regex characters in the query
    const escapedQuery = query.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const regex = new RegExp(`(${escapedQuery})`, 'gi');

    return sanitizeHtml(text).replace(regex, '<mark>$1</mark>');
  }

  formatAndHighlightResultsForDisplay(result: SearchResult): string {
    const formattedText = this.formatResultForDisplay()(result);
    return this.highlightMatches(formattedText, this.query);
  }

  formatAndHighlightResultsSubtextForDisplay(result: SearchResult): string | undefined {
    const subtext = this.formatResultSubtextForDisplay()(result);
    return subtext ? this.highlightMatches(subtext, this.query) : undefined;
  }

  onResultHover(index: number) {
    this.selectedResultIndex = index;
  }

  onResultMouseLeave() {
    this.selectedResultIndex = -1;
  }

  // Ensure selected item is visible when navigating long search result list with keyboard
  private scrollSelectedIntoView(): void {
    // Defer to next tick to ensure DOM updates from selectedResultIndex change are applied
    setTimeout(() => {
      const selectedElement = this.elementRef.nativeElement.querySelector(
        'ul li.selected',
      ) as HTMLElement;

      if (selectedElement) {
        selectedElement.scrollIntoView({
          behavior: 'smooth',
          block: 'nearest',
        });
      }
    }, 0);
  }
}
