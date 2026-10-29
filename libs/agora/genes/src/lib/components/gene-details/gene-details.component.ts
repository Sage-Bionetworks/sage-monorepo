import { Location } from '@angular/common';
import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Gene, GeneService } from '@sagebionetworks/agora/api-client';
import { QUERY_PARAMS, ROUTE_PARAMS, ROUTE_PATHS } from '@sagebionetworks/agora/config';
import { Panel, QueryParams } from '@sagebionetworks/explorers/models';
import {
  ErrorOverlayService,
  HelperService,
  HttpErrorReportLevelEnum,
  LoggerService,
  NotFoundService,
  PlatformService,
  reportHttpError,
} from '@sagebionetworks/explorers/services';
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
  private readonly location = inject(Location);
  private readonly helperService = inject(HelperService);
  private readonly geneService = inject(GeneService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly platformService = inject(PlatformService);
  private readonly notFoundService = inject(NotFoundService);
  private readonly logger = inject(LoggerService).forSource('GeneDetailsComponent');
  private readonly errorOverlayService = inject(ErrorOverlayService);

  isLoading = true;
  gene: Gene | undefined;

  panels: Panel[] = getPanels();

  activePanel = '';
  activeParent = '';

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
            this.notFoundService.redirectToNotFound(this.logger, 'gene', { geneId });
            return;
          }
          this.gene = gene;
          this.panels = getPanelsWithDisabledState(gene, getPanels());
          this.setInitialPanel(params);
          this.isLoading = false;
        },
        error: (error) => {
          reportHttpError(this.logger, 'Error loading gene', {
            error,
            level: HttpErrorReportLevelEnum.Error,
          });
          this.errorOverlayService.showRequestError(error);
          this.isLoading = false;
        },
      });
  }

  private setInitialPanel(params: ParamMap) {
    const initial = this.helperService.resolveInitialPanel(
      this.panels,
      params,
      this.getUrlBasePath(),
      this.getModelQueryParams(),
    );
    this.activePanel = initial.activePanel;
    this.activeParent = initial.activeParent;
    this.scrollToPanelNavElementOnInitialLoad = initial.shouldScrollToPanelNav;
    if (initial.replacementUrl) {
      this.location.replaceState(initial.replacementUrl);
    }
  }

  getUrlBasePath() {
    return `/${ROUTE_PATHS.DETAILS}/${this.gene?.ensembl_gene_id}`;
  }

  private getModelQueryParams(): QueryParams {
    return { [QUERY_PARAMS.MODEL]: this.helperService.getUrlParam(QUERY_PARAMS.MODEL) };
  }

  onPanelChange(event: Panel) {
    const result = this.helperService.handlePanelChange(
      this.panels,
      event,
      this.getUrlBasePath(),
      this.getModelQueryParams(),
    );
    if (result) {
      this.activePanel = result.activePanel;
      this.activeParent = result.activeParent;
      this.location.replaceState(result.url);
    }
  }
}
