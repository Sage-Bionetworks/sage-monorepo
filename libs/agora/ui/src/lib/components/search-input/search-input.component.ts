import { Component, DestroyRef, inject, input } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { GeneService, SearchResult } from '@sagebionetworks/agora/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/agora/config';
import { isEnsemblId, sanitizeSearchQuery } from '@sagebionetworks/agora/util';
import { SearchInputComponent as ExplorersSearchInputComponent } from '@sagebionetworks/explorers/ui';
import { Observable, tap } from 'rxjs';

@Component({
  selector: 'agora-search-input',
  imports: [ExplorersSearchInputComponent],
  templateUrl: './search-input.component.html',
  styleUrls: ['./search-input.component.scss'],
})
export class SearchInputComponent {
  router = inject(Router);
  geneService = inject(GeneService);
  destroyRef = inject(DestroyRef);

  searchPlaceholder = input<string>('Find Gene by Name...');
  searchImagePath = input<string | undefined>();
  searchImageAltText = input<string>('');
  hasThickBorder = input<boolean>(false);

  sanitizeSearchQuery = sanitizeSearchQuery;

  hgncSymbolCounts: { [key: string]: number } = {};

  getNoSearchResultsMessage(query: string): string {
    if (isEnsemblId(query) && query.length === 15) {
      return 'Unable to find a matching gene. Try searching by gene symbol.';
    }
    return 'No results found. Try searching by the Ensembl Gene ID.';
  }

  navigateToResult = (id: string): void => {
    this.router.navigate([ROUTE_PATHS.DETAILS, id]);
  };

  getHgncSymbolCounts(results: SearchResult[]): void {
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

  getSearchResults = (query: string): Observable<SearchResult[]> => {
    return this.geneService.searchGeneEnhanced(query).pipe(
      tap((results) => {
        this.getHgncSymbolCounts(results);
      }),
      takeUntilDestroyed(this.destroyRef),
    );
  };

  checkQueryForErrors(query: string): string {
    if (isEnsemblId(query) && query.length !== 15) {
      return 'You must enter a full 15-character value to search for a gene by Ensembl identifier.';
    }
    return ''; // empty string if no error
  }

  formatResultForDisplay(result: SearchResult): string {
    switch (result.match_field) {
      case 'ensembl_gene_id':
        return result.hgnc_symbol || result.id;
      case 'hgnc_symbol':
        return result.match_value;
      case 'alias':
        return `${result.hgnc_symbol} (Also known as ${result.match_value})`;
      default:
        return result.id;
    }
  }

  formatResultSubtextForDisplay = (result: SearchResult): string | undefined => {
    if (
      this.hgncSymbolCounts[result.hgnc_symbol || ''] > 1 ||
      result.match_field === 'ensembl_gene_id'
    ) {
      return result.id;
    }
    return undefined;
  };
}
