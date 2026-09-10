import { Component, inject, input } from '@angular/core';
import { Router } from '@angular/router';
import { SearchResultIcon } from '@sagebionetworks/explorers/models';
import { SearchInputComponent as ExplorersSearchInputComponent } from '@sagebionetworks/explorers/ui';
import { ModelOrganism, SearchResult, SearchService } from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { Observable } from 'rxjs';

@Component({
  selector: 'model-ad-search-input',
  imports: [ExplorersSearchInputComponent],
  templateUrl: './search-input.component.html',
  styleUrls: ['./search-input.component.scss'],
})
export class SearchInputComponent {
  router = inject(Router);
  searchService = inject(SearchService);

  searchPlaceholder = input<string>('Find model by name or ID...');
  searchImagePath = input<string | undefined>();
  searchImageAltText = input<string>('');
  hasThickBorder = input<boolean>(false);
  modelOrganisms = input<ModelOrganism[] | undefined>();

  private readonly resultIconByOrganism: Record<ModelOrganism, SearchResultIcon> = {
    mouse: { imagePath: 'model-ad-assets/images/mouse-head.svg', label: 'mouse' },
    marmoset: { imagePath: 'model-ad-assets/images/marmoset-head.svg', label: 'marmoset' },
  };

  getResultIcon = (result: SearchResult): SearchResultIcon => {
    return this.resultIconByOrganism[result.model_organism];
  };

  navigateToResult = (result: SearchResult): void => {
    this.router.navigate([ROUTE_PATHS.MODELS, result.id], {
      queryParams: { modelOrganism: result.model_organism },
    });
  };

  getSearchResults = (query: string): Observable<SearchResult[]> => {
    return this.searchService.searchModels(query, this.modelOrganisms());
  };

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  checkQueryForErrors = (query: string): string => {
    return ''; // empty string if no error
  };

  formatResultForDisplay = (result: SearchResult): string => {
    switch (result.match_field) {
      case 'name':
        return result.id;
      case 'aliases':
        return `${result.id} (Alias ${result.match_value})`;
      case 'jax_id':
        return `${result.id} (Jax ID: ${result.match_value})`;
      case 'rrid':
        return `${result.id} (RRID: ${result.match_value})`;
      default:
        return result.id;
    }
  };
}
