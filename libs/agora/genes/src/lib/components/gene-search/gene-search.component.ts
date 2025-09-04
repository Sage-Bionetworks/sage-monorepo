import {
  AfterViewInit,
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  inject,
  Input,
  OnDestroy,
  Output,
  ViewChild,
} from '@angular/core';

import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faMagnifyingGlass, faSpinner } from '@fortawesome/free-solid-svg-icons';
import { Gene, GeneService, GenesList } from '@sagebionetworks/agora/api-client-angular';
import {
  catchError,
  debounceTime,
  distinctUntilChanged,
  EMPTY,
  fromEvent,
  Observable,
  Subject,
  switchMap,
  takeUntil,
  throwError,
} from 'rxjs';
import { CloseIconComponent } from './assets/close-icon/close-icon.component';
import { GeneIconComponent } from './assets/gene-icon/gene-icon.component';

@Component({
  selector: 'agora-gene-search',
  imports: [FormsModule, FontAwesomeModule, GeneIconComponent, CloseIconComponent],
  templateUrl: './gene-search.component.html',
  styleUrls: ['./gene-search.component.scss'],
})
export class GeneSearchComponent implements AfterViewInit, OnDestroy {
  @Input() location: 'header' | 'home' = 'header';
  @Output() searchNavigated = new EventEmitter();

  router = inject(Router);
  apiService = inject(GeneService);

  protected unsubscribe$ = new Subject<void>();

  searchIcon = faMagnifyingGlass;
  searchLoadingIcon = faSpinner;

  results: Gene[] = [];
  isLoading = false;
  isEnsemblId = false;
  query = '';
  error = '';
  hgncSymbolCounts: { [key: string]: number } = {};

  showGeneResults = false; // controls display of gene results list items

  errorMessages: { [key: string]: string } = {
    hgncSymbolNotFound: 'No results found. Try searching by the Ensembl Gene ID.',
    ensemblIdNotFound: 'Unable to find a matching gene. Try searching by gene symbol.',
    notValidSearch: 'Please enter at least two characters.',
    notValidEnsemblId:
      'You must enter a full 15-character value to search for a gene by Ensembl identifier.',
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
        takeUntil(this.unsubscribe$),
        debounceTime(500),
        distinctUntilChanged(),
        switchMap((event: any) => {
          return this.search(event.target.value);
        }),
        catchError((err) => {
          this.error = this.errorMessages['unknown'];
          this.isLoading = false;
          return throwError(err);
        }),
      )
      .subscribe((response: any) => {
        this.showGeneResults = true;
        this.setResults(response.items);
      });
  }

  search(query: string): Observable<GenesList> {
    this.results = [];
    this.error = '';
    this.query = query = query.trim().replace(/[^a-z0-9-_]/gi, '');
    this.isEnsemblId = 'ensg' === query.toLowerCase().substring(0, 4);

    if (query.length > 0 && query.length < 2) {
      this.showGeneResults = true;
      this.error = this.errorMessages['notValidSearch'];
    } else if (this.isEnsemblId) {
      const digits = query.toLowerCase().substring(4, query.length);
      if (digits.length !== 11 || !/^\d+$/.test(digits)) {
        this.showGeneResults = true;
        this.error = this.errorMessages['notValidEnsemblId'];
      }
    }

    this.isLoading = query && !this.error ? true : false;
    return this.isLoading ? this.apiService.searchGene(query) : EMPTY;
  }

  setResults(results: Gene[]) {
    // If we got an empty array as response, or no genes found
    if (results.length < 1) {
      this.error = this.isEnsemblId
        ? this.errorMessages['ensemblIdNotFound']
        : this.errorMessages['hgncSymbolNotFound'];
    } else {
      if (this.isEnsemblId) {
        // Multiple matching genes: This should never happen…but if it does, log an error
        if (results.length > 1) {
          console.log(
            'Unexpected duplicate gene_info objects for ensembl ID "' + this.query + '" found.',
          );
          this.error = this.errorMessages['ensemblIdNotFound'];
        } else {
          this.goToGene(results[0].ensembl_gene_id);
          this.isLoading = false;
          return;
        }
      } else {
        this.hgncSymbolCounts = {};
        for (const item of results) {
          if (item.hgnc_symbol) {
            if (!this.hgncSymbolCounts[item.hgnc_symbol]) {
              this.hgncSymbolCounts[item.hgnc_symbol] = 1;
            } else {
              this.hgncSymbolCounts[item.hgnc_symbol]++;
            }
          }
        }
      }
    }

    this.results = results;
    this.isLoading = false;
  }

  goToGene(id: string) {
    this.input.nativeElement.blur();
    this.query = '';
    this.results = [];
    this.showGeneResults = false;
    this.searchNavigated.emit();
    // https://github.com/angular/angular/issues/45202
    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    this.router.navigate(['/genes/' + id]);
  }

  hasAlias(gene: Gene): boolean {
    return (
      !gene.hgnc_symbol.toLowerCase().includes(this.query.toLowerCase()) &&
      gene.alias?.map((s: string) => s.toLowerCase()).includes(this.query.toLowerCase())
    );
  }

  onFocus() {
    if (this.results.length > 0 || this.error) {
      this.showGeneResults = true;
    }
  }

  clearInput() {
    this.input.nativeElement.focus();
    this.query = '';
    this.error = '';
    this.results = [];
  }

  checkClickIsInsideComponent(event: Event) {
    // if clicked element is not part of this component, hide gene results
    if (!this.root?.nativeElement?.contains(event.target)) {
      this.showGeneResults = false;
    }
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }
}
