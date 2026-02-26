import { Location } from '@angular/common';
import { AfterViewInit, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Panel, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { HelperService, PlatformService } from '@sagebionetworks/explorers/services';
import { PanelNavigationComponent } from '@sagebionetworks/explorers/ui';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { Model, ModelService } from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { ModelDetailsBoxplotsSelectorComponent } from './components/model-details-boxplots-selector/model-details-boxplots-selector.component';
import { ModelDetailsHeroComponent } from './components/model-details-hero/model-details-hero.component';
import { ModelDetailsOmicsComponent } from './components/model-details-omics/model-details-omics.component';
import { ModelDetailsResourcesComponent } from './components/model-details-resources/model-details-resources.component';

@Component({
  selector: 'model-ad-model-details',
  imports: [
    PanelNavigationComponent,
    LoadingIconComponent,
    ModelDetailsOmicsComponent,
    ModelDetailsResourcesComponent,
    ModelDetailsHeroComponent,
    ModelDetailsBoxplotsSelectorComponent,
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

  isLoading = true;

  model: Model | undefined;

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
        .getModelByName(modelName)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (model: Model) => {
            this.model = model;
            this.setActivePanelAndParentFromUrl(params);
            this.updatePanelDisabledState();
            this.changePanelAndUrlIfInitialActivePanelIsInvalid();
            this.scrollToPanelNavElementOnInitialLoad =
              this.maybeScrollToPanelNavElementOnInitialLoad;
            this.isLoading = false;
          },
          error: () => {
            this.isLoading = false;
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
        this.model?.gene_expression === null &&
        this.model?.disease_correlation === null
      ) {
        p.disabled = true;
      } else {
        p.disabled = false;
      }
    });
  }

  private setActivePanelAndParentFromUrl(params: ParamMap) {
    const noHashFragment = this.helperService.getHashFragment() === '';
    if (params.get('subtab')) {
      this.activePanel = params.get('subtab') as string;
      this.activeParent = params.get('tab') as string;
      this.maybeScrollToPanelNavElementOnInitialLoad = noHashFragment; // selector will handle to scroll if hash fragment is present
    } else if (params.get('tab')) {
      const panel = this.panels.find((p: Panel) => p.name === params.get('tab'));
      if (panel) {
        const { activePanel, activeParent } = this.helperService.getActivePanelAndParent(
          this.panels,
          panel,
        );
        this.activePanel = activePanel;
        this.activeParent = activeParent;
        this.maybeScrollToPanelNavElementOnInitialLoad = noHashFragment; // selector will handle scroll if hash fragment is present
      }
    }
  }

  // Initial active panel is invalid if it doesn't exist or is disabled
  private changePanelAndUrlIfInitialActivePanelIsInvalid() {
    const currentPanel = this.helperService.findPanelByName(this.panels, this.activePanel);
    if (!currentPanel || currentPanel.disabled) {
      const fallbackPanel = this.panels.find((panel) => panel.disabled === false);
      if (fallbackPanel) {
        this.updateActivePanel(fallbackPanel);
        this.location.replaceState(this.getUrlBasePath());
        this.maybeScrollToPanelNavElementOnInitialLoad = false; // don't scroll if we fell back to default panel
      }
    }
  }

  ngAfterViewInit() {
    if (!this.model?.name) {
      this.isLoading = true;
    }
  }

  updateActivePanel(panel: Panel) {
    const { activePanel, activeParent } = this.helperService.getActivePanelAndParent(
      this.panels,
      panel,
    );
    this.activePanel = activePanel;
    this.activeParent = activeParent;
  }

  getUrlBasePath() {
    const encodedModel = this.helperService.encodeParenthesesForwardSlashes(
      encodeURIComponent(this.model?.name || ''),
    );
    return `/${ROUTE_PATHS.MODELS}/${encodedModel}`;
  }

  onPanelChange(event: Panel) {
    const panel = event;

    if (panel.disabled) {
      return;
    }

    this.updateActivePanel(panel);

    const fullPath = this.helperService.getPanelUrl(
      this.getUrlBasePath(),
      this.activePanel,
      this.activeParent,
    );
    this.location.replaceState(fullPath);
  }
}
