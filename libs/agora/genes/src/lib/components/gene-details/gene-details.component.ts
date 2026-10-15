import { Location } from '@angular/common';
import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Gene, GeneService } from '@sagebionetworks/agora/api-client';
import { QUERY_PARAMS, ROUTE_PARAMS, ROUTE_PATHS } from '@sagebionetworks/agora/config';
import { PANEL_ROUTE_PARAMS } from '@sagebionetworks/explorers/constants';
import { Panel } from '@sagebionetworks/explorers/models';
import { HelperService, LoggerService, PlatformService } from '@sagebionetworks/explorers/services';
import { PanelNavigationComponent } from '@sagebionetworks/explorers/ui';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { GeneEvidenceMetabolomicsComponent } from '../gene-evidence-metabolomics/gene-evidence-metabolomics.component';
import { GeneEvidenceProteomicsComponent } from '../gene-evidence-proteomics/gene-evidence-proteomics.component';
import { GeneEvidenceRnaComponent } from '../gene-evidence-rna/gene-evidence-rna.component';
import { ExperimentalValidationComponent } from '../gene-experimental-validation/gene-experimental-validation.component';
import { GeneHeroComponent } from '../gene-hero/gene-hero.component';
import { GeneNominationsComponent } from '../gene-nominations/gene-nominations.component';
import { GeneResourcesComponent } from '../gene-resources/gene-resources.component';
import { GeneSoeComponent } from '../gene-soe/gene-soe.component';
import { getPanels, getPanelsWithDisabledState } from './gene-details-panels';

@Component({
  selector: 'agora-gene-details',
  imports: [
    PanelNavigationComponent,
    LoadingIconComponent,
    GeneHeroComponent,
    GeneSoeComponent,
    GeneEvidenceMetabolomicsComponent,
    GeneEvidenceProteomicsComponent,
    GeneEvidenceRnaComponent,
    ExperimentalValidationComponent,
    GeneNominationsComponent,
    GeneResourcesComponent,
  ],
  templateUrl: './gene-details.component.html',
  styleUrls: ['./gene-details.component.scss'],
})
export class GeneDetailsComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly location = inject(Location);
  private readonly helperService = inject(HelperService);
  private readonly geneService = inject(GeneService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformService = inject(PlatformService);
  private readonly logger = inject(LoggerService).forSource('GeneDetailsComponent');

  isLoading = true;
  gene: Gene | undefined;

  panels: Panel[] = getPanels();

  activePanel = '';
  activeParent = '';

  maybeScrollToPanelNavElementOnInitialLoad = false;
  scrollToPanelNavElementOnInitialLoad = false;

  reset() {
    this.gene = undefined;
    this.activePanel = '';
    this.activeParent = '';
    this.isLoading = true;
  }

  ngOnInit() {
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params: ParamMap) => {
      this.reset();

      // only fetch data during client hydration
      if (this.platformService.isBrowser) {
        this.loadPanelData(params);
      }
    });
  }

  private loadPanelData(params: ParamMap) {
    const geneId = params.get(ROUTE_PARAMS.GENE_ID);
    if (!geneId) return;

    this.logger.log('Loading gene', { geneId });
    this.geneService
      .getGene(geneId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (gene) => {
          // the API answers an unknown gene with an empty body rather than a 404
          if (!gene) {
            this.logger.log('Gene not found, redirecting', { geneId });
            this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
            return;
          }
          this.gene = gene;
          this.panels = getPanelsWithDisabledState(gene, getPanels());
          this.setActivePanelAndParentFromUrl(params);
          this.applyFallbackPanelIfNeeded(params);
          this.scrollToPanelNavElementOnInitialLoad =
            this.maybeScrollToPanelNavElementOnInitialLoad;
          this.isLoading = false;
        },
        error: () => {
          this.isLoading = false;
        },
      });
  }

  private setActivePanelAndParentFromUrl(params: ParamMap) {
    const result = this.helperService.getActivePanelAndParentFromUrl(this.panels, params);
    if (result) {
      this.activePanel = result.activePanel;
      this.activeParent = result.activeParent;
      this.maybeScrollToPanelNavElementOnInitialLoad = result.shouldScrollToPanelNav;
    }
  }

  /**
   * Specifies the active panel when either no panel is specified in the path or when the specified
   * panel is disabled or doesn't exist.
   * If no panel is specified, then use the default panel and leave the URL untouched, retaining all
   * query parameters and hash fragments.
   * If a disabled or invalid panel is specified, then reset the URL to the gene's base path.
   */
  private applyFallbackPanelIfNeeded(params: ParamMap) {
    const fallback = this.helperService.getFallbackPanelIfInvalid(this.panels, this.activePanel);
    if (!fallback) return;

    this.activePanel = fallback.activePanel;
    this.activeParent = fallback.activeParent;

    if (params.has(PANEL_ROUTE_PARAMS.TAB)) {
      this.location.replaceState(this.getUrlBasePath());
      this.maybeScrollToPanelNavElementOnInitialLoad = false;
    }
  }

  getUrlBasePath() {
    return `/${ROUTE_PATHS.DETAILS}/${this.gene?.ensembl_gene_id}`;
  }

  onPanelChange(event: Panel) {
    const result = this.helperService.handlePanelChange(this.panels, event, this.getUrlBasePath(), {
      [QUERY_PARAMS.MODEL]: this.helperService.getUrlParam(QUERY_PARAMS.MODEL),
    });
    if (result) {
      this.activePanel = result.activePanel;
      this.activeParent = result.activeParent;
      this.location.replaceState(result.url);
    }
  }
}
