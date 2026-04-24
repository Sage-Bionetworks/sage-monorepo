import { Location } from '@angular/common';
import { HttpContext } from '@angular/common/http';
import { AfterViewInit, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { Drug, DrugService } from '@sagebionetworks/agora/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/agora/config';
import { Panel } from '@sagebionetworks/explorers/models';
import {
  HelperService,
  LoggerService,
  PlatformService,
  SUPPRESS_ERROR_OVERLAY,
} from '@sagebionetworks/explorers/services';
import { PanelNavigationComponent } from '@sagebionetworks/explorers/ui';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import { DrugDetailsHeroComponent } from './components/drug-details-hero/drug-details-hero.component';
import { DrugDetailsResourcesComponent } from './components/drug-details-resources/drug-details-resources.component';

@Component({
  selector: 'agora-drug-details',
  imports: [
    PanelNavigationComponent,
    LoadingIconComponent,
    DrugDetailsResourcesComponent,
    DrugDetailsHeroComponent,
  ],
  templateUrl: './drug-details.component.html',
  styleUrls: ['./drug-details.component.scss'],
})
export class DrugDetailsComponent implements OnInit, AfterViewInit {
  route = inject(ActivatedRoute);
  router = inject(Router);
  location = inject(Location);
  helperService = inject(HelperService);
  drugService = inject(DrugService);
  destroyRef = inject(DestroyRef);
  platformService = inject(PlatformService);
  private readonly logger = inject(LoggerService);

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

  maybeScrollToPanelNavElementOnInitialLoad = false;
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
    const chemblId = params.get('chemblId');
    if (chemblId) {
      this.drugService
        .getDrug(chemblId, 'body', false, {
          context: new HttpContext().set(SUPPRESS_ERROR_OVERLAY, true),
        })
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (drug: Drug) => {
            this.drug = drug;
            this.setActivePanelAndParentFromUrl(params);
            this.changePanelAndUrlIfInitialActivePanelIsInvalid();
            this.scrollToPanelNavElementOnInitialLoad =
              this.maybeScrollToPanelNavElementOnInitialLoad;
            this.isLoading = false;
          },
          error: () => {
            this.isLoading = false;
            this.logger.log(
              `DrugDetailsComponent: loadPanelData: Drug ${chemblId} not found, redirecting`,
            );
            this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
          },
        });
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
    if (!this.drug?.chembl_id) {
      this.isLoading = true;
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
