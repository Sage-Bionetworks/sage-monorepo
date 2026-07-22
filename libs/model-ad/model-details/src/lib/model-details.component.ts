import { Location } from '@angular/common';
import { HttpContext } from '@angular/common/http';
import { AfterViewInit, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Panel, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import {
  HelperService,
  LoggerService,
  PlatformService,
  SUPPRESS_ERROR_OVERLAY,
} from '@sagebionetworks/explorers/services';
import { PanelNavigationComponent } from '@sagebionetworks/explorers/ui';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { Model, ModelService, MouseModel, Organism } from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { ModelDetailsHeroComponent } from './components/model-details-hero/model-details-hero.component';
import { ModelDetailsOmicsComponent } from './components/model-details-omics/model-details-omics.component';
import { ModelDetailsResourcesComponent } from './components/model-details-resources/model-details-resources.component';
import { MouseModelDetailsBoxplotsSelectorComponent } from './components/mouse-model-details-boxplots-selector/mouse-model-details-boxplots-selector.component';

@Component({
  selector: 'model-ad-model-details',
  imports: [
    PanelNavigationComponent,
    LoadingIconComponent,
    ModelDetailsOmicsComponent,
    ModelDetailsResourcesComponent,
    ModelDetailsHeroComponent,
    MouseModelDetailsBoxplotsSelectorComponent,
  ],
  templateUrl: './model-details.component.html',
  styleUrls: ['./model-details.component.scss'],
})
export class ModelDetailsComponent implements OnInit, AfterViewInit {
  route = inject(ActivatedRoute);
  router = inject(Router);
  location = inject(Location);
  helperService = inject(HelperService);
  modelService = inject(ModelService);
  destroyRef = inject(DestroyRef);
  platformService = inject(PlatformService);
  private logger = inject(LoggerService);

  isLoading = true;

  model: MouseModel | undefined;

  biomarkersWikiParams: SynapseWikiParams = { ownerId: 'syn66271427', wikiId: '632871' };
  pathologyWikiParams: SynapseWikiParams = { ownerId: 'syn66271427', wikiId: '632872' };

  panels: Panel[] = [
    {
      name: 'omics',
      label: 'Omics',
      disabled: false,
    },
    {
      name: 'biomarkers',
      label: 'Biomarkers',
      disabled: false,
    },
    {
      name: 'pathology',
      label: 'Pathology',
      disabled: false,
    },
    {
      name: 'resources',
      label: 'Resources',
      disabled: false,
    },
  ];

  activePanel = '';
  activeParent = '';

  maybeScrollToPanelNavElementOnInitialLoad = false;
  scrollToPanelNavElementOnInitialLoad = false;

  reset() {
    this.model = undefined;
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
    const modelName = params.get('name');
    if (modelName) {
      this.modelService
        .getModelByName(Organism.Mouse, modelName, 'body', false, {
          context: new HttpContext().set(SUPPRESS_ERROR_OVERLAY, true),
        })
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          // TODO(MG-929): this page serves mouse models only, so the request hardcodes
          // Organism.Mouse and the response is always a MouseModel. When the marmoset details
          // page is built, replace this cast with a real model.type switch that delegates to
          // the mouse- or marmoset-specific component.
          next: (model: Model) => {
            this.model = model as MouseModel;
            this.setActivePanelAndParentFromUrl(params);
            this.updatePanelDisabledState();
            this.changePanelAndUrlIfInitialActivePanelIsInvalid();
            this.scrollToPanelNavElementOnInitialLoad =
              this.maybeScrollToPanelNavElementOnInitialLoad;
            this.isLoading = false;
          },
          error: () => {
            this.isLoading = false;
            this.logger.log(
              `ModelDetailsComponent: loadPanelData: Model ${modelName} not found, redirecting`,
            );
            this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
          },
        });
    }
  }

  private updatePanelDisabledState() {
    this.panels.forEach((p: Panel) => {
      if (p.name === 'biomarkers' && this.model?.biomarkers.length === 0) {
        p.disabled = true;
      } else if (p.name === 'pathology' && this.model?.pathology.length === 0) {
        p.disabled = true;
      } else if (
        p.name === 'omics' &&
        this.model?.transcriptomics === null &&
        this.model?.disease_correlation === null
      ) {
        p.disabled = true;
      } else {
        p.disabled = false;
      }
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

  private changePanelAndUrlIfInitialActivePanelIsInvalid() {
    const fallback = this.helperService.getFallbackPanelIfInvalid(this.panels, this.activePanel);
    if (fallback) {
      this.activePanel = fallback.activePanel;
      this.activeParent = fallback.activeParent;
      this.location.replaceState(this.getUrlBasePath());
      this.maybeScrollToPanelNavElementOnInitialLoad = false;
    }
  }

  ngAfterViewInit() {
    if (!this.model?.name) {
      this.isLoading = true;
    }
  }

  getUrlBasePath() {
    const encodedModel = this.helperService.encodeParenthesesForwardSlashes(
      encodeURIComponent(this.model?.name || ''),
    );
    return `/${ROUTE_PATHS.MODELS}/${encodedModel}`;
  }

  onPanelChange(event: Panel) {
    const result = this.helperService.handlePanelChange(this.panels, event, this.getUrlBasePath());
    if (result) {
      this.activePanel = result.activePanel;
      this.activeParent = result.activeParent;
      this.location.replaceState(result.url);
    }
  }
}
