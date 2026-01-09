import { Location } from '@angular/common';
import { AfterViewInit, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { HelperService, PlatformService } from '@sagebionetworks/explorers/services';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import {
  GeneExpressionDetail,
  GeneExpressionDetailFilterQuery,
  GeneExpressionService,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { geneExpressionDetailMocks } from '@sagebionetworks/model-ad/testing';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'model-ad-gene-details',
  imports: [LoadingIconComponent],
  templateUrl: './gene-details.component.html',
  styleUrls: ['./gene-details.component.scss'],
})
export class GeneDetailsComponent implements OnInit, AfterViewInit {
  route = inject(ActivatedRoute);
  router = inject(Router);
  location = inject(Location);
  helperService = inject(HelperService);
  geneExpressionService = inject(GeneExpressionService);
  destroyRef = inject(DestroyRef);
  platformService = inject(PlatformService);

  isLoading = true;

  geneDetails: GeneExpressionDetail[] | undefined;

  reset() {
    this.geneDetails = undefined;
    this.isLoading = true;
  }

  ngOnInit() {
    combineLatest([this.route.paramMap, this.route.queryParamMap])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(([params, queryParams]) => {
        this.reset();

        // only fetch data during client hydration
        if (this.platformService.isBrowser) {
          this.loadgeneDetails(params, queryParams);
        }
      });
  }

  private loadgeneDetails(params: ParamMap, queryParams: ParamMap) {
    const ensemblGeneId = params.get('ensemblGeneId');
    const modelName = queryParams.get('model');
    const modelGroup = queryParams.get('modelGroup');
    const tissue = queryParams.get('tissue');

    const modelIdentifierType = modelGroup
      ? GeneExpressionDetailFilterQuery.ModelIdentifierTypeEnum.ModelGroup
      : GeneExpressionDetailFilterQuery.ModelIdentifierTypeEnum.Name;
    const modelIdentifier = modelGroup || modelName;

    console.log('ensemblGeneId', ensemblGeneId);
    console.log('tissue', tissue);
    console.log('modelIdentifierType', modelIdentifierType);
    console.log('modelIdentifier', modelIdentifier);

    this.geneDetails = geneExpressionDetailMocks;
    this.isLoading = false;

    // TODO: re-enable real data fetching
    /* if (ensemblGeneId && tissue && modelIdentifierType && modelIdentifier) {
      const query: GeneExpressionDetailFilterQuery = {
        ensemblGeneId,
        tissue,
        modelIdentifierType,
        modelIdentifier,
      };

      this.geneExpressionService
        .getGeneExpressionDetails(query)
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (geneDetails: GeneExpressionDetail[]) => {
            this.geneDetails = geneDetails;
            this.isLoading = false;
          },
          error: (error) => {
            console.error('Error retrieving gene details: ', error);
            this.isLoading = false;
            this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
          },
        });
    } */
  }

  ngAfterViewInit() {
    if (this.geneDetails?.length === 0) {
      this.isLoading = true;
    }
  }
}
