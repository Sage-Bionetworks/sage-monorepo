import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import { ComparisonToolViewConfig } from '@sagebionetworks/explorers/models';
import { ComparisonToolUrlService, PlatformService } from '@sagebionetworks/explorers/services';
import { ComparisonToolConfigService, ComparisonToolPage } from '@sagebionetworks/qtl/api-client';
import { DEFAULT_SYNAPSE_WIKI_OWNER_ID } from '@sagebionetworks/qtl/config';
import { of, shareReplay } from 'rxjs';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { EqtlComparisonToolService } from './services/eqtl-comparison-tool.service';

@Component({
  selector: 'qtl-eqtl-comparison-tool',
  imports: [ComparisonToolComponent, SidebarComponent],
  templateUrl: './eqtl-comparison-tool.component.html',
  styleUrls: ['./eqtl-comparison-tool.component.scss'],
})
export class EqtlComparisonToolComponent implements OnInit, OnDestroy {
  private readonly platformService = inject(PlatformService);
  private readonly comparisonToolConfigService = inject(ComparisonToolConfigService);
  private readonly comparisonToolService = inject(EqtlComparisonToolService);
  private readonly comparisonToolUrlService = inject(ComparisonToolUrlService);

  isInitialized = this.comparisonToolService.isInitialized;

  readonly config$ = this.comparisonToolConfigService
    .getComparisonToolConfig(ComparisonToolPage.EQtls)
    .pipe(shareReplay({ bufferSize: 1, refCount: true }));

  viewConfig: Partial<ComparisonToolViewConfig> = {
    headerTitle: ComparisonToolPage.EQtls,
    headerTitleWikiParams: { ownerId: DEFAULT_SYNAPSE_WIKI_OWNER_ID, wikiId: '641079' },
    filterResultsButtonTooltip: 'Filter results by variant, gene, cell type and more',
    showSignificanceControls: false,
    showTableSearch: false,
    legendEnabled: false,
    // TODO(EX-251): set the row id, default sort and pinned download
    // TODO(EX-252): turn on row selection and hover
  };

  constructor() {
    this.comparisonToolService.setViewConfig(this.viewConfig);
  }

  ngOnInit() {
    if (this.platformService.isServer) {
      return;
    }

    this.comparisonToolService.connect({
      page: ComparisonToolPage.EQtls,
      config$: this.config$,
      queryParams$: this.comparisonToolUrlService.params$,
      // TODO(EX-251): fetch the matching rows for Pin All
      pinAllFetch: () => of({ rows: [], totalElements: 0 }),
    });
  }

  ngOnDestroy() {
    this.comparisonToolService.disconnect();
  }

  // TODO(EX-251): fetch the unpinned and pinned rows; the URL isn't updated until the first pinned fetch lands
}
