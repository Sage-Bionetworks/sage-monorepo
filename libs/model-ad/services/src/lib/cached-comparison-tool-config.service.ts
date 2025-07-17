import { Injectable, inject } from '@angular/core';
import { ComparisonToolConfigService } from '@sagebionetworks/model-ad/api-client-angular';
import { CachedApi } from '@sagebionetworks/explorers/services';

@Injectable({ providedIn: 'root' })
export class CachedComparisonToolConfigService {
  private readonly delegate = inject(ComparisonToolConfigService);
  private cache = new CachedApi((page) => this.delegate.getComparisonToolConfig(page));

  getComparisonToolConfig(page: string) {
    return this.cache.get(page);
  }
}
