import {
  AfterViewInit,
  Component,
  DestroyRef,
  ElementRef,
  EventEmitter,
  HostListener,
  inject,
  input,
  Output,
  ViewChild,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faMagnifyingGlass, faSpinner, faXmark } from '@fortawesome/free-solid-svg-icons';
import { SearchResult } from '@sagebionetworks/explorers/models';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  EMPTY,
  fromEvent,
  Observable,
  switchMap,
  throwError,
} from 'rxjs';
import { SvgImageComponent } from '../svg-image/svg-image.component';

@Component({
  selector: 'explorers-search-input',
  imports: [SvgImageComponent, FormsModule, FontAwesomeModule],
  templateUrl: './search-input.component.html',
  styleUrls: ['./search-input.component.scss'],
  standalone: true,
})
export class SearchInputComponent implements AfterViewInit {
  router = inject(Router);
  destroyRef = inject(DestroyRef);

  @Output() searchNavigated = new EventEmitter();

  searchPlaceholder = input.required<string>();
  searchImagePath = input<string | undefined>();
  searchImageAltText = input<string>('');
  hasThickBorder = input<boolean>(false);

  navigateToResult = input.required<(id: string) => void>();
  getSearchResults = input.required<(query: string) => Observable<SearchResult[]>>();
  checkQueryForErrors = input.required<(query: string) => string>(); // empty string if no error
  formatResultForDisplay = input<(result: SearchResult) => string>(
    (result: SearchResult) => result.id,
  );

  faMagnifyingGlass = faMagnifyingGlass;
  faSpinner = faSpinner;
  faXmark = faXmark;

  isLoading = false;
  query = '';
  error = '';
  results: SearchResult[] = [];

  showResults = false;
  errorMessages: { [key: string]: string } = {
    notFound: 'No results found. Try searching again.',
    notValidSearch: 'Please enter at least three characters.',
    unknown: 'An unknown error occurred, please try again.',
  };

  @ViewChild('root') root!: ElementRef;
  @ViewChild('input') input!: ElementRef<HTMLInputElement>;

  @HostListener('document:click', ['$event'])
  onClick(event: Event) {
    this.checkClickIsInsideComponent(event);
  }

  ngAfterViewInit() {
    fromEvent(this.input.nativeElement, 'keyup')
      .pipe(
        takeUntilDestroyed(this.destroyRef),
        debounceTime(500),
        distinctUntilChanged(),
        switchMap((event: any) => {
          return this.search(event.target.value);
        }),
        catchError((err) => {
          this.error = this.errorMessages['unknown'];
          this.isLoading = false;
          return throwError(() => new Error(err));
        }),
      )
      .subscribe((response: SearchResult[]) => {
        this.showResults = true;
        this.setResults(response);
      });
  }

  search(query: string): Observable<SearchResult[]> {
    this.results = [];
    this.error = '';
    this.query = query = query.trim().replace(/[^a-z0-9-_]/gi, '');

    if (query.length > 0 && query.length < 3) {
      this.error = this.errorMessages['notValidSearch'];
    } else {
      this.error = this.checkQueryForErrors()(query);
    }

    if (this.error) {
      this.showResults = true;
    }

    this.isLoading = query && !this.error ? true : false;
    return this.isLoading ? this.getSearchResults()(query) : EMPTY;
  }

  setResults(results: SearchResult[]) {
    if (results.length < 1) {
      this.error = this.errorMessages['notFound'];
    }
    this.results = results;
    this.isLoading = false;
  }

  goToResult(id: string) {
    this.input.nativeElement.blur();
    this.query = '';
    this.results = [];
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
    this.input.nativeElement.focus();
    this.query = '';
    this.error = '';
    this.results = [];
  }

  checkClickIsInsideComponent(event: Event) {
    // if clicked element is not part of this component, hide results
    if (!this.root.nativeElement.contains(event.target)) {
      this.showResults = false;
    }
  }
}
