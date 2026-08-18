import { Component, inject, input } from '@angular/core';
import { Router } from '@angular/router';
import { SearchResult } from '@sagebionetworks/explorers/models';
import { SearchInputComponent as ExplorersSearchInputComponent } from '@sagebionetworks/explorers/ui';
import { ModelOrganism, SearchService } from '@sagebionetworks/model-ad/api-client';
import { SearchResult as ModelAdSearchResult } from '@sagebionetworks/model-ad/api-client';
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

  // TODO(MG-938): define getResultIcon and bind it in the template once search
  // results identify the model's organism

  navigateToResult = (result: SearchResult): void => {
    const modelOrganism =
      (result as ModelAdSearchResult).model_organism ?? ModelOrganism.Mouse;
    this.router.navigate([ROUTE_PATHS.MODELS, result.id], {
      queryParams: { modelOrganism },
    });
  };

  getSearchResults = (query: string): Observable<SearchResult[]> => {
    return this.searchService.searchModels(query, this.modelOrganisms());
  };

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  checkQueryForErrors = (query: string): string => {
    return '';
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
