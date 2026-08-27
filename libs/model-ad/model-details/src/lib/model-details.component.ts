import { Location } from '@angular/common';
import { HttpContext } from '@angular/common/http';
import { AfterViewInit, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Panel } from '@sagebionetworks/explorers/models';
import {
  HelperService,
  LoggerService,
  PlatformService,
  SUPPRESS_ERROR_OVERLAY,
} from '@sagebionetworks/explorers/services';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import {
  MarmosetModel,
  Model,
  ModelOrganism,
  ModelService,
  MouseModel,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { resolveModelOrganism } from '@sagebionetworks/model-ad/util';
import { combineLatest, distinctUntilChanged, map } from 'rxjs';
import { MarmosetModelDetailsContentComponent } from './components/marmoset-model-details-content/marmoset-model-details-content.component';
import {
  getPanels as getMarmosetPanels,
  getPanelsWithDisabledState as getMarmosetPanelsWithDisabledState,
} from './components/marmoset-model-details-content/marmoset-model-details-panels';
import { MouseModelDetailsContentComponent } from './components/mouse-model-details-content/mouse-model-details-content.component';
import {
  getPanels as getMousePanels,
  getPanelsWithDisabledState as getMousePanelsWithDisabledState,
} from './components/mouse-model-details-content/mouse-model-details-panels';

const MODEL_ORGANISM_QUERY_KEY = 'modelOrganism';

@Component({
  selector: 'model-ad-model-details',
  imports: [
    LoadingIconComponent,
    MouseModelDetailsContentComponent,
    MarmosetModelDetailsContentComponent,
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
  private readonly logger = inject(LoggerService);

  isLoading = true;

  model: Model | undefined;
  modelOrganism: ModelOrganism = ModelOrganism.Mouse;

  get mouseModel(): MouseModel | undefined {
    return this.model?.type === 'mouse' ? (this.model as MouseModel) : undefined;
  }

  get marmosetModel(): MarmosetModel | undefined {
    return this.model?.type === 'marmoset' ? (this.model as MarmosetModel) : undefined;
  }

  panels: Panel[] = getMousePanels();

  activePanel = '';
  activeParent = '';

  maybeScrollToPanelNavElementOnInitialLoad = false;
  scrollToPanelNavElementOnInitialLoad = false;

  reset() {
    this.model = undefined;
    this.modelOrganism = ModelOrganism.Mouse;
    this.activePanel = '';
    this.activeParent = '';
    this.isLoading = true;
  }

  ngOnInit() {
    combineLatest([this.route.paramMap, this.route.queryParamMap])
      .pipe(
        map(([params, queryParams]): [ParamMap, ModelOrganism] => [
          params,
          resolveModelOrganism(queryParams.get(MODEL_ORGANISM_QUERY_KEY)),
        ]),
        distinctUntilChanged(
          ([prevParams, prevModelOrganism], [params, modelOrganism]) =>
            prevParams.get('name') === params.get('name') && prevModelOrganism === modelOrganism,
        ),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(([params, modelOrganism]) => {
        this.reset();

        // only fetch data during client hydration
        if (this.platformService.isBrowser) {
          this.loadPanelData(params, modelOrganism);
        }
      });
  }

  private loadPanelData(params: ParamMap, modelOrganism: ModelOrganism) {
    this.modelOrganism = modelOrganism;
    const modelName = params.get('name');
    if (modelName) {
      this.modelService
        .getModelByName(modelOrganism, modelName, 'body', false, {
          context: new HttpContext().set(SUPPRESS_ERROR_OVERLAY, true),
        })
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (model: Model) => {
            this.model = model;
            this.panels = this.buildPanels(model);
            this.setActivePanelAndParentFromUrl(params);
            this.applyFallbackPanelIfNeeded(params);
            this.scrollToPanelNavElementOnInitialLoad =
              this.maybeScrollToPanelNavElementOnInitialLoad;
            this.isLoading = false;
          },
          error: () => {
            this.isLoading = false;
            this.logger.log(
              `ModelDetailsComponent: loadPanelData: Model ${modelName} (modelOrganism: ${modelOrganism}) not found, redirecting`,
            );
            this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
          },
        });
    }
  }

  private buildPanels(model: Model): Panel[] {
    switch (model.type) {
      case 'marmoset':
        return getMarmosetPanelsWithDisabledState(model as MarmosetModel, getMarmosetPanels());
      case 'mouse':
      default:
        return getMousePanelsWithDisabledState(model as MouseModel, getMousePanels());
    }
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
   * If no panel is specified, then use the default panel and retain all query parameters and hash
   * fragments.
   * If a disabled or invalid panel is specified, then drop the query parameters and hash fragment
   * (other than the required modelOrganism query parameter).
   */
  private applyFallbackPanelIfNeeded(params: ParamMap) {
    const fallback = this.helperService.getFallbackPanelIfInvalid(this.panels, this.activePanel);
    if (!fallback) return;

    this.activePanel = fallback.activePanel;
    this.activeParent = fallback.activeParent;

    if (params.get('tab') ?? params.get('subtab')) {
      this.location.replaceState(this.appendModelOrganism(this.getUrlBasePath()));
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

  private appendModelOrganism(url: string) {
    const separator = url.includes('?') ? '&' : '?';
    return `${url}${separator}${MODEL_ORGANISM_QUERY_KEY}=${this.modelOrganism}`;
  }

  onPanelChange(event: Panel) {
    const result = this.helperService.handlePanelChange(this.panels, event, this.getUrlBasePath());
    if (result) {
      this.activePanel = result.activePanel;
      this.activeParent = result.activeParent;
      this.location.replaceState(this.appendModelOrganism(result.url));
    }
  }
}
