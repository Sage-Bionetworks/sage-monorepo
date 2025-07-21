import { Location } from '@angular/common';
import { AfterViewInit, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Panel, SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { HelperService } from '@sagebionetworks/explorers/services';
import { PanelNavigationComponent } from '@sagebionetworks/explorers/ui';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { Model, ModelsService } from '@sagebionetworks/model-ad/api-client-angular';
import { ConfigService, ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
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
  modelsService = inject(ModelsService);
  destroyRef = inject(DestroyRef);
  configService = inject(ConfigService);

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

  activePanel = 'omics';
  activeParent = '';

  ngOnInit() {
    this.route.paramMap.pipe(takeUntilDestroyed(this.destroyRef)).subscribe((params: ParamMap) => {
      this.isLoading = true;

      // only fetch data during client hydration
      if (!this.configService.config.isPlatformServer) {
        this.loadPanelData(params);
        this.setActivePanelAndParentFromUrl(params);
      }
    });
  }

  private loadPanelData(params: ParamMap) {
    const modelName = params.get('model');
    if (modelName) {
      this.modelsService
        .getModelByName(modelName)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (model: Model) => {
            this.model = model;
            this.updatePanelDisabledState();
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error retrieving model: ', error);
            this.isLoading = false;
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
      } else {
        p.disabled = false;
      }
    });
  }

  private setActivePanelAndParentFromUrl(params: ParamMap) {
    if (params.get('subtab')) {
      this.activePanel = params.get('subtab') as string;
      this.activeParent = params.get('tab') as string;
    } else if (params.get('tab')) {
      const panel = this.panels.find((p: Panel) => p.name === params.get('tab'));
      if (panel) {
        const { activePanel, activeParent } = this.helperService.getActivePanelAndParent(
          this.panels,
          panel,
        );
        this.activePanel = activePanel;
        this.activeParent = activeParent;
      }
    }
  }

  ngAfterViewInit() {
    if (!this.model?.model) {
      this.isLoading = true;
    }
  }

  onPanelChange(event: Panel) {
    const panel = event;

    if (panel.disabled) {
      return;
    }

    const { activePanel, activeParent } = this.helperService.getActivePanelAndParent(
      this.panels,
      panel,
    );
    this.activePanel = activePanel;
    this.activeParent = activeParent;

    const basePath = `/${ROUTE_PATHS.MODELS}/${this.model?.model}`;
    const fullPath = this.helperService.getPanelUrl(basePath, this.activePanel, this.activeParent);
    this.location.replaceState(fullPath);
  }
}
