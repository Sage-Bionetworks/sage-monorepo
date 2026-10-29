import { Location } from '@angular/common';
import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Panel, QueryParams } from '@sagebionetworks/explorers/models';
import {
  ErrorOverlayService,
  HelperService,
  HttpErrorReportLevelEnum,
  isNotFoundError,
  LoggerService,
  NotFoundService,
  PlatformService,
  reportHttpError,
} from '@sagebionetworks/explorers/services';
import { LoadingIconComponent, routeSnapshotChanges } from '@sagebionetworks/explorers/util';
import {
  MarmosetModel,
  Model,
  ModelOrganism,
  ModelService,
  MouseModel,
} from '@sagebionetworks/model-ad/api-client';
import { QUERY_PARAMS, ROUTE_PARAMS, ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { resolveModelOrganism } from '@sagebionetworks/model-ad/util';
import { catchError, distinctUntilChanged, EMPTY, map, Observable, switchMap, tap } from 'rxjs';
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
export class ModelDetailsComponent implements OnInit {
  route = inject(ActivatedRoute);
  location = inject(Location);
  helperService = inject(HelperService);
  modelService = inject(ModelService);
  destroyRef = inject(DestroyRef);
  platformService = inject(PlatformService);
  private readonly errorOverlayService = inject(ErrorOverlayService);
  private readonly notFoundService = inject(NotFoundService);
  private readonly logger = inject(LoggerService).forSource('ModelDetailsComponent');

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

  scrollToPanelNavElementOnInitialLoad = false;

  reset() {
    this.model = undefined;
    this.modelOrganism = ModelOrganism.Mouse;
    this.activePanel = '';
    this.activeParent = '';
    this.isLoading = true;
  }

  ngOnInit() {
    routeSnapshotChanges(this.route)
      .pipe(
        map((snapshot): [ParamMap, ModelOrganism] => [
          snapshot.paramMap,
          resolveModelOrganism(snapshot.queryParamMap.get(QUERY_PARAMS.MODEL_ORGANISM)),
        ]),
        distinctUntilChanged(
          ([prevParams, prevModelOrganism], [params, modelOrganism]) =>
            prevParams.get(ROUTE_PARAMS.MODEL_NAME) === params.get(ROUTE_PARAMS.MODEL_NAME) &&
            prevModelOrganism === modelOrganism,
        ),
        tap(() => this.reset()),
        switchMap(([params, modelOrganism]) =>
          // only fetch data during client hydration
          this.platformService.isBrowser
            ? this.loadPanelData(params, modelOrganism).pipe(map((model) => ({ model, params })))
            : EMPTY,
        ),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe(({ model, params }) => this.setModel(model, params));
  }

  private loadPanelData(params: ParamMap, modelOrganism: ModelOrganism): Observable<Model> {
    this.modelOrganism = modelOrganism;
    const modelName = params.get(ROUTE_PARAMS.MODEL_NAME);
    if (!modelName) return EMPTY;

    return this.modelService.getModelByName(modelOrganism, modelName).pipe(
      catchError((error) => {
        if (isNotFoundError(error)) {
          this.notFoundService.redirectToNotFound(this.logger, 'model', {
            modelName,
            modelOrganism,
          });
          return EMPTY;
        }
        reportHttpError(this.logger, 'Error loading model', {
          error,
          level: HttpErrorReportLevelEnum.Error,
        });
        this.errorOverlayService.showRequestError(error);
        this.isLoading = false;
        return EMPTY;
      }),
    );
  }

  private setModel(model: Model, params: ParamMap) {
    this.model = model;
    this.panels = this.buildPanels(model);
    this.setInitialPanel(params);
    this.isLoading = false;
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

  // A URL with no panel is left untouched; modelOrganismUrlGuard has already added modelOrganism to it
  private setInitialPanel(params: ParamMap) {
    const initial = this.helperService.resolveInitialPanel(
      this.panels,
      params,
      this.getUrlBasePath(),
      this.getModelOrganismQueryParams(),
    );
    this.activePanel = initial.activePanel;
    this.activeParent = initial.activeParent;
    this.scrollToPanelNavElementOnInitialLoad = initial.shouldScrollToPanelNav;
    if (initial.replacementUrl) {
      this.location.replaceState(initial.replacementUrl);
    }
  }

  getUrlBasePath() {
    const encodedModel = this.helperService.encodeParenthesesForwardSlashes(
      encodeURIComponent(this.model?.name || ''),
    );
    return `/${ROUTE_PATHS.MODELS}/${encodedModel}`;
  }

  private getModelOrganismQueryParams(): QueryParams {
    return { [QUERY_PARAMS.MODEL_ORGANISM]: this.modelOrganism };
  }

  onPanelChange(event: Panel) {
    const result = this.helperService.handlePanelChange(
      this.panels,
      event,
      this.getUrlBasePath(),
      this.getModelOrganismQueryParams(),
    );
    if (result) {
      this.activePanel = result.activePanel;
      this.activeParent = result.activeParent;
      this.location.replaceState(result.url);
    }
  }
}
