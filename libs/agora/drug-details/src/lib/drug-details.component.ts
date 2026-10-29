import { Location } from '@angular/common';
import { Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { Drug, DrugService } from '@sagebionetworks/agora/api-client';
import { ROUTE_PARAMS, ROUTE_PATHS } from '@sagebionetworks/agora/config';
import { Panel } from '@sagebionetworks/explorers/models';
import {
  ErrorOverlayService,
  HelperService,
  HttpErrorReportLevelEnum,
  isNotFoundError,
  LoggerService,
  NotFoundService,
  PlatformService,
} from '@sagebionetworks/explorers/services';
import { PanelNavigationComponent } from '@sagebionetworks/explorers/ui';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { DrugDetailsHeroComponent } from './components/drug-details-hero/drug-details-hero.component';
import { DrugDetailsNominationDetailsComponent } from './components/drug-details-nomination-details/drug-details-nomination-details.component';
import { DrugDetailsResourcesComponent } from './components/drug-details-resources/drug-details-resources.component';
import { DrugDetailsSummaryComponent } from './components/drug-details-summary/drug-details-summary.component';

@Component({
  selector: 'agora-drug-details',
  imports: [
    PanelNavigationComponent,
    LoadingIconComponent,
    DrugDetailsResourcesComponent,
    DrugDetailsHeroComponent,
    DrugDetailsNominationDetailsComponent,
    DrugDetailsSummaryComponent,
  ],
  templateUrl: './drug-details.component.html',
  styleUrls: ['./drug-details.component.scss'],
})
export class DrugDetailsComponent implements OnInit {
  route = inject(ActivatedRoute);
  location = inject(Location);
  helperService = inject(HelperService);
  drugService = inject(DrugService);
  destroyRef = inject(DestroyRef);
  platformService = inject(PlatformService);
  private readonly errorOverlayService = inject(ErrorOverlayService);
  private readonly notFoundService = inject(NotFoundService);
  private readonly logger = inject(LoggerService).forSource('DrugDetailsComponent');

  isLoading = true;
  drug: Drug | undefined;

  panels: Panel[] = [
    {
      name: 'summary',
      label: 'Summary',
      disabled: false,
    },
    {
      name: 'resources',
      label: 'Resources',
      disabled: false,
    },
    {
      name: 'nominationDetails',
      label: 'Nomination Details',
      disabled: false,
    },
  ];

  activePanel = '';
  activeParent = '';

  scrollToPanelNavElementOnInitialLoad = false;

  reset() {
    this.drug = undefined;
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
    const chemblId = params.get(ROUTE_PARAMS.CHEMBL_ID);
    if (chemblId) {
      this.drugService
        .getDrug(chemblId)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (drug: Drug) => {
            this.drug = drug;
            this.setInitialPanel(params);
            this.isLoading = false;
          },
          error: (error) => {
            if (isNotFoundError(error)) {
              this.notFoundService.redirectToNotFound(this.logger, 'drug', { chemblId });
              return;
            }
            this.errorOverlayService.reportAndShow(this.logger, 'Error loading drug', {
              error,
              level: HttpErrorReportLevelEnum.Error,
            });
            this.isLoading = false;
          },
        });
    }
  }

  private setInitialPanel(params: ParamMap) {
    const initial = this.helperService.resolveInitialPanel(
      this.panels,
      params,
      this.getUrlBasePath(),
    );
    this.activePanel = initial.activePanel;
    this.activeParent = initial.activeParent;
    this.scrollToPanelNavElementOnInitialLoad = initial.shouldScrollToPanelNav;
    if (initial.replacementUrl) {
      this.location.replaceState(initial.replacementUrl);
    }
  }

  getUrlBasePath() {
    return `/${ROUTE_PATHS.DRUG_DETAILS}/${this.drug?.chembl_id}`;
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
