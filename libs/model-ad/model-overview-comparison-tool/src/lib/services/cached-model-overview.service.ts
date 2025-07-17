import { Injectable, inject } from '@angular/core';
import { ModelOverviewService } from '@sagebionetworks/model-ad/api-client-angular';
import { CachedApi } from '@sagebionetworks/explorers/services';

@Injectable({ providedIn: 'root' })
export class CachedModelOverviewService {
  private readonly delegate = inject(ModelOverviewService);
  private cache = new CachedApi(() => this.delegate.getModelOverview());

  getModelOverview() {
    return this.cache.get('singleton'); // there is no key, so we use a singleton pattern
  }
}
