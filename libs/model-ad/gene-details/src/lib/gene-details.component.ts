import { Location } from '@angular/common';
import { AfterViewInit, Component, computed, DestroyRef, inject, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpContext } from '@angular/common/http';
import { ActivatedRoute, ParamMap, Router } from '@angular/router';
import { SUPPRESS_ERROR_OVERLAY } from '@sagebionetworks/explorers/constants';
import { HelperService, LoggerService, PlatformService } from '@sagebionetworks/explorers/services';
import { DownloadDomImageComponent } from '@sagebionetworks/explorers/ui';
import { LoadingIconComponent } from '@sagebionetworks/explorers/util';
import {
  GeneExpressionIndividual,
  GeneExpressionIndividualFilterQuery,
  GeneExpressionIndividualService,
  IndividualData,
  ModelIdentifierType,
} from '@sagebionetworks/model-ad/api-client';
import { ROUTE_PATHS } from '@sagebionetworks/model-ad/config';
import { BoxplotsGridComponent } from '@sagebionetworks/model-ad/ui';
import { combineLatest } from 'rxjs';

@Component({
  selector: 'model-ad-gene-details',
  imports: [LoadingIconComponent, BoxplotsGridComponent, DownloadDomImageComponent],
  templateUrl: './gene-details.component.html',
  styleUrls: ['./gene-details.component.scss'],
})
export class GeneDetailsComponent implements OnInit, AfterViewInit {
  route = inject(ActivatedRoute);
  router = inject(Router);
  location = inject(Location);
  helperService = inject(HelperService);
  geneExpressionIndividualService = inject(GeneExpressionIndividualService);
  destroyRef = inject(DestroyRef);
  platformService = inject(PlatformService);
  private readonly logger = inject(LoggerService);

  isLoading = true;

  geneExpressionIndividualData: GeneExpressionIndividual[] | undefined;
  tissue: string | null = null;
  modelIdentifier: string | null = null;
  modelIdentifierType: ModelIdentifierType | null = null;

  primaryGene = computed(() => this.geneExpressionIndividualData?.[0]);

  label = computed(() => {
    const gene = this.primaryGene();
    if (!gene) return { left: '', right: '' };

    const geneSymbol = gene.gene_symbol;
    const ensemblGeneId = gene.ensembl_gene_id;
    return {
      left: geneSymbol || ensemblGeneId,
      right: ensemblGeneId && geneSymbol ? ensemblGeneId : '',
    };
  });

  subtitle = computed(() => {
    return this.modelIdentifier ? `${this.modelIdentifier} (Females & Males)` : '';
  });

  xAxisOrder = computed(() => this.primaryGene()?.result_order);

  csvData = computed(() => {
    if (!this.geneExpressionIndividualData) return [];
    return this.convertToCsvData(this.geneExpressionIndividualData);
  });

  filename = computed(() => {
    const gene = this.primaryGene();
    if (!gene) return '';

    const geneSymbol = gene.gene_symbol;
    const ensemblGeneId = gene.ensembl_gene_id;
    const filename = `gene-expression-individual-${geneSymbol || ensemblGeneId}-${this.modelIdentifier}-${(this.tissue || '').toLowerCase()}`;
    return this.helperService.cleanFilename(filename);
  });

  reset() {
    this.geneExpressionIndividualData = undefined;
    this.tissue = null;
    this.modelIdentifier = null;
    this.modelIdentifierType = null;
    this.isLoading = true;
  }

  ngOnInit() {
    combineLatest([this.route.paramMap, this.route.queryParamMap])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(([params, queryParams]) => {
        this.reset();

        // only fetch data during client hydration
        if (this.platformService.isBrowser) {
          this.loadGeneExpressionIndividualData(params, queryParams);
        }
      });
  }

  private loadGeneExpressionIndividualData(params: ParamMap, queryParams: ParamMap) {
    const ensemblGeneId = params.get('ensemblGeneId');
    const modelName = queryParams.get('model');
    const modelGroup = queryParams.get('modelGroup');
    const tissue = queryParams.get('tissue');
    this.tissue = tissue;

    this.modelIdentifierType = modelGroup
      ? ModelIdentifierType.ModelGroup
      : ModelIdentifierType.Name;
    this.modelIdentifier = modelGroup || modelName;

    if (ensemblGeneId && tissue && this.modelIdentifierType && this.modelIdentifier) {
      const query: GeneExpressionIndividualFilterQuery = {
        ensemblGeneId,
        tissue,
        modelIdentifierType: this.modelIdentifierType,
        modelIdentifier: this.modelIdentifier,
      };

      this.geneExpressionIndividualService
        .getGeneExpressionIndividual(query, 'body', false, {
          context: new HttpContext().set(SUPPRESS_ERROR_OVERLAY, true),
        })
        .pipe(takeUntilDestroyed(this.destroyRef))
        .subscribe({
          next: (geneExpressionIndividualData: GeneExpressionIndividual[]) => {
            this.geneExpressionIndividualData = geneExpressionIndividualData;
            this.isLoading = false;
          },
          error: () => {
            this.isLoading = false;
            this.logger.log(
              `GeneDetailsComponent: loadGeneExpressionIndividualData: query: ${JSON.stringify(query)}, redirecting`,
            );
            this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
          },
        });
    } else {
      this.isLoading = false;
      this.logger.log(
        `GeneDetailsComponent: loadGeneExpressionIndividualData: ensemblGeneId: ${ensemblGeneId} modelIdentifierType: ${this.modelIdentifierType} modelIdentifier: ${this.modelIdentifier}, redirecting`,
      );
      this.router.navigateByUrl(ROUTE_PATHS.NOT_FOUND, { skipLocationChange: true });
    }
  }

  ngAfterViewInit() {
    if (this.geneExpressionIndividualData?.length === 0) {
      this.isLoading = true;
    }
  }

  convertToCsvData(data: GeneExpressionIndividual[]): string[][] {
    const columnHeaders = [
      'ensembl_gene_id',
      'gene_symbol',
      'age',
      'genotype',
      'sex',
      'individual_id',
      'log2_cpm',
    ];
    const csvRows: string[][] = [];
    csvRows.push(columnHeaders);

    data.forEach((g: GeneExpressionIndividual) => {
      const baseRow = [g.ensembl_gene_id, g.gene_symbol, g.age];
      g.data.forEach((point: IndividualData) => {
        csvRows.push([
          ...baseRow,
          point.genotype,
          point.sex,
          point.individual_id,
          String(point.value || ''),
        ]);
      });
    });

    return csvRows;
  }
}
