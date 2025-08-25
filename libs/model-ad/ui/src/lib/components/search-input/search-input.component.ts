import { Component, inject, input } from '@angular/core';
import { Router } from '@angular/router';
import { SearchResult } from '@sagebionetworks/explorers/models';
import { SearchInputComponent as ExplorersSearchInputComponent } from '@sagebionetworks/explorers/ui';
import { ModelsService } from '@sagebionetworks/model-ad/api-client-angular';
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
  modelsService = inject(ModelsService);

  searchPlaceholder = input<string>('Find model by name...');
  searchImagePath = input<string | undefined>();
  searchImageAltText = input<string>('');
  hasThickBorder = input<boolean>(false);

  navigateToResult = (id: string): void => {
    this.router.navigate([ROUTE_PATHS.MODELS, id]);
  };

  getSearchResults = (query: string): Observable<SearchResult[]> => {
    return this.modelsService.searchModels(query);
  };

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
