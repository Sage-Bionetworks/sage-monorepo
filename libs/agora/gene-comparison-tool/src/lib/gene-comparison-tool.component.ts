import { CommonModule } from '@angular/common';
import {
  AfterViewInit,
  Component,
  DestroyRef,
  inject,
  OnDestroy,
  OnInit,
  ViewChild,
  ViewEncapsulation,
} from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import {
  DistributionService,
  GCTGene,
  GCTGeneTissue,
  GeneService,
  OverallScoresDistribution,
} from '@sagebionetworks/agora/api-client';
import {
  GCTColumn,
  GCTDetailsPanelData,
  GCTFilter,
  GCTSelectOption,
  GCTSortEvent,
} from '@sagebionetworks/agora/models';
import { DEFAULT_SYNAPSE_WIKI_OWNER_ID } from '@sagebionetworks/agora/config';
import { HelperService } from '@sagebionetworks/agora/services';
import { LoggerService } from '@sagebionetworks/explorers/services';
import { cloneDeep } from 'lodash';
import { FilterService, MessageService, SortEvent } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { Table, TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import { combineLatest, Subscription } from 'rxjs';

import * as helpers from './gene-comparison-tool.helpers';
import * as variables from './gene-comparison-tool.variables';

import { GeneComparisonToolDetailsPanelComponent } from './components/gene-comparison-tool-details-panel/gene-comparison-tool-details-panel.component';
import { GeneComparisonToolFilterPanelComponent } from './components/gene-comparison-tool-filter-panel/gene-comparison-tool-filter-panel.component';
import { GeneComparisonToolPinnedGenesModalComponent } from './components/gene-comparison-tool-pinned-genes-modal/gene-comparison-tool-pinned-genes-modal.component';
import { GeneComparisonToolScorePanelComponent } from './components/gene-comparison-tool-score-panel/gene-comparison-tool-score-panel.component';

import { FormsModule } from '@angular/forms';
import {
  LoadingIconComponent,
  PopoverLinkComponent,
  SvgIconComponent,
} from '@sagebionetworks/explorers/util';
import { PopoverModule } from 'primeng/popover';
import { SelectModule } from 'primeng/select';
import { ToggleSwitchModule } from 'primeng/toggleswitch';
import { GeneComparisonToolFilterListComponent } from './components/gene-comparison-tool-filter-list/gene-comparison-tool-filter-list.component';
import { GeneComparisonToolHowToPanelComponent } from './components/gene-comparison-tool-how-to-panel/gene-comparison-tool-how-to-panel.component';
import { GeneComparisonToolLegendPanelComponent } from './components/gene-comparison-tool-legend-panel/gene-comparison-tool-legend-panel.component';

@Component({
  selector: 'agora-gene-comparison-tool',
  imports: [
    ButtonModule,
    CommonModule,
    ButtonModule,
    FormsModule,
    RouterModule,
    TableModule,
    TooltipModule,
    SelectModule,
    ToggleSwitchModule,
    PopoverLinkComponent,
    PopoverModule,
    SvgIconComponent,
    GeneComparisonToolHowToPanelComponent,
    GeneComparisonToolLegendPanelComponent,
    GeneComparisonToolFilterListComponent,
    GeneComparisonToolScorePanelComponent,
    GeneComparisonToolDetailsPanelComponent,
    GeneComparisonToolFilterPanelComponent,
    GeneComparisonToolPinnedGenesModalComponent,
    LoadingIconComponent,
  ],
  templateUrl: './gene-comparison-tool.component.html',
  styleUrls: ['./gene-comparison-tool.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class GeneComparisonToolComponent implements OnInit, AfterViewInit, OnDestroy {
  private readonly destroyRef = inject(DestroyRef);
  private readonly logger = inject(LoggerService);

  router = inject(Router);
  route = inject(ActivatedRoute);
  geneService = inject(GeneService);
  distributionService = inject(DistributionService);
  helperService = inject(HelperService);
  messageService = inject(MessageService);
  filterService = inject(FilterService);

  readonly defaultSynapseWikiOwnerId = DEFAULT_SYNAPSE_WIKI_OWNER_ID;

  isLoading = true;

  /* Genes ----------------------------------------------------------------- */
  genes: GCTGene[] = [];

  /* Categories ------------------------------------------------------------ */
  categories: GCTSelectOption[] = cloneDeep(variables.categories);
  category: 'RNA - Differential Expression' | 'Protein - Differential Expression';
  subCategories: GCTSelectOption[] = [];
  subCategory = '';
  subCategoryLabel = '';

  /* Columns --------------------------------------------------------------- */
  columns: string[] = [];
  columnWidth = 'auto';

  scoresColumns: GCTColumn[] = [
    { field: 'RISK SCORE', header: 'AD Risk Score', selected: true, visible: true },
    { field: 'GENETIC', header: 'Genetic Risk Score', selected: true, visible: true },
    { field: 'MULTI-OMIC', header: 'Multi-omic Risk Score', selected: true, visible: true },
  ];

  brainRegionsColumns: GCTColumn[] = [
    { field: 'ACC', header: 'ACC - Anterior Cingulate Cortex', selected: true, visible: true },
    { field: 'CBE', header: 'CBE - Cerebellum', selected: true, visible: true },
    {
      field: 'DLPFC',
      header: 'DLPFC - Dorsolateral Prefrontal Cortex',
      selected: true,
      visible: true,
    },
    { field: 'FP', header: 'FP - Frontal Pole', selected: true, visible: true },
    { field: 'IFG', header: 'IFG - Inferior Frontal Gyrus', selected: true, visible: true },
    { field: 'PCC', header: 'PCC - Posterior Cingulate Cortex', selected: true, visible: true },
    { field: 'PHG', header: 'PHG - Parahippocampal Gyrus', selected: true, visible: true },
    { field: 'STG', header: 'STG - Superior Temporal Gyrus', selected: true, visible: true },
    { field: 'TCX', header: 'TCX - Temporal Cortex', selected: true, visible: true },
  ];

  scoresDistribution: OverallScoresDistribution[] = [];

  /* Sort ------------------------------------------------------------------ */
  sortField = '';
  sortOrder = -1;

  /* Filters --------------------------------------------------------------- */
  filters: GCTFilter[] = cloneDeep(variables.filters);
  searchTerm = '';

  // /* URL ------------------------------------------------------------------- */
  urlParams: { [key: string]: any } | undefined;
  urlParamsSubscription: Subscription | undefined;

  // /* Pinned ---------------------------------------------------------------- */
  initialLoad = true;

  lastPinnedCategory = '';
  lastPinnedSubCategory = '';

  pinnedItems: GCTGene[] = [];
  uniquePinnedGenesCount = 0;
  pinnedItemsCache: GCTGene[] = [];
  pendingPinnedItems: GCTGene[] = [];
  maxPinnedGenes = 50;

  /* ----------------------------------------------------------------------- */
  private DEFAULT_SIGNIFICANCE_THRESHOLD = 0.05;
  significanceThreshold = this.DEFAULT_SIGNIFICANCE_THRESHOLD;
  significanceThresholdActive = false;

  /* Components ------------------------------------------------------------ */
  @ViewChild('headerTable', { static: true }) headerTable!: Table;
  @ViewChild('pinnedTable', { static: true }) pinnedTable!: Table;
  @ViewChild('genesTable', { static: true }) genesTable!: Table;

  @ViewChild('filterPanel') filterPanel!: GeneComparisonToolFilterPanelComponent;
  @ViewChild('detailsPanel') detailsPanel!: GeneComparisonToolDetailsPanelComponent;
  @ViewChild('scorePanel') scorePanel!: GeneComparisonToolScorePanelComponent;
  @ViewChild('pinnedGenesModal') pinnedGenesModal!: GeneComparisonToolPinnedGenesModalComponent;

  constructor() {
    this.category = 'RNA - Differential Expression';
  }

  ngOnInit() {
    this.urlParamsSubscription = this.route.queryParams.subscribe((params) => {
      this.urlParams = params || {};

      this.category = this.urlParams['category'] || this.categories[0].value;
      this.subCategory = this.urlParams['subCategory'] || '';
      this.updateSubCategories();

      this.sortField = this.urlParams['sortField'] || '';
      this.sortOrder = '1' === this.urlParams['sortOrder'] ? 1 : -1;

      this.significanceThreshold =
        this.urlParams['significance'] || this.DEFAULT_SIGNIFICANCE_THRESHOLD;
      this.significanceThresholdActive = !!this.urlParams['significance'];

      this.loadGenes();
    });

    this.filterService.register('intersect', helpers.intersectFilterCallback);
    this.filterService.register(
      'exclude_ensembl_gene_id',
      helpers.excludeEnsemblGeneIdFilterCallback,
    );
    this.filterService.register('exclude_uniprotid', helpers.excludeUniprotIdCallback);
  }

  ngAfterViewInit() {
    setTimeout(() => {
      this.updateColumnWidth();
    }, 50);
  }

  ngOnDestroy() {
    this.urlParamsSubscription?.unsubscribe();
  }

  isScoresColumn(column: string) {
    const isScore = this.scoresColumns.find((c) => c.field === column);
    return isScore !== undefined;
  }

  toggleGCTColumn(column: GCTColumn) {
    column.selected = !column.selected;
    this.updateVisibleColumns();
    this.onResize();
  }

  updateVisibleColumns() {
    const visibleScoresColumns: string[] = this.scoresColumns
      .filter((c) => c.visible && c.selected)
      .map((c) => c.field);
    const visibleBrainRegionColumns: string[] = this.brainRegionsColumns
      .filter((c) => c.visible && c.selected)
      .map((c) => c.field);
    this.columns = visibleScoresColumns.concat(visibleBrainRegionColumns);
  }

  public isNumber(value: string | number): boolean {
    return value != null && value !== '' && !isNaN(Number(value.toString()));
  }

  /* ----------------------------------------------------------------------- */
  /* Genes
  /* ----------------------------------------------------------------------- */

  getScoreForNumericColumn(columnName: string, gene: GCTGene) {
    if (columnName === this.scoresColumns[0].field) {
      return gene.target_risk_score;
    }
    if (columnName === this.scoresColumns[1].field) {
      return gene.genetics_score;
    }
    if (columnName === this.scoresColumns[2].field) {
      return gene.multi_omics_score;
    }
    return null;
  }

  loadGenes() {
    // this.helperService.setLoading(true);
    this.isLoading = true;
    // this.genesTable.showLoader = true;
    this.genes = [];
    this.pinnedItems = [];

    this.logger.log(
      `GeneComparisonToolComponent: Loading genes for ${this.category} / ${this.subCategory}`,
    );

    const genesApi$ = this.geneService.getComparisonGenes(this.category, this.subCategory);
    const distributionApi$ = this.distributionService.getDistribution();

    combineLatest([genesApi$, distributionApi$])
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: ([genesResult, distributionResult]) => {
          if (genesResult.items) {
            this.initData(genesResult.items);
            this.sortTable(this.headerTable);
            this.refresh();

            this.scoresDistribution = distributionResult.overall_scores;

            this.isLoading = false;
          }
        },
        error: (error) => {
          this.logger.error('Failed to load gene comparison data', error);
          this.isLoading = false;
        },
      });
  }

  getGeneProperty(gene: GCTGene, property: string) {
    return property.split('.').reduce((o: any, i: any) => o[i], gene);
  }

  getUid(item: GCTGene) {
    // rna is just the ensembl gene id
    // protein is a combination of ensembl gene id and uniprotid
    if (this.category === 'RNA - Differential Expression') return item.ensembl_gene_id;
    else return item.ensembl_gene_id + item.uniprotid;
  }

  // when current category is RNA, this method will always return a list of ensembl gene ids, regardless of previous category
  // when current category is Protein, this method will return either ensg (if previous category was RNA) or ensg + uniprot (if previous category was Protein)
  getPreviousPins() {
    // if the last pinned category is blank, then this means this is the initial load
    // note: we only need to check the category for blank but subcategory will also be blank
    // In this scenario, we check the url to see if this was a shared url with pinned genes/proteins
    if (this.lastPinnedCategory === '') {
      // check the url for pinned genes/proteins
      this.setLastPinnedCategories();
      return this.getUrlParam('pinned', true);
    }

    if (this.currentCategoriesMatchLastPinnedCategories()) {
      // load from cache since it has been previously cached
      // uid works for both proten and rna cases
      return this.pinnedItemsCache.map((g: GCTGene) => g.uid);
    } else {
      // categories don't match, so grab it from the cache and format it
      if (this.category === 'RNA - Differential Expression') {
        // if the current category is RNA, we only need the previous ensgs
        // instead of getting the uid, we need to get the ensg
        return this.pinnedItemsCache.map((g: GCTGene) => g.ensembl_gene_id);
      } else {
        // if the current category is Protein, we need the uid
        // because the previous category and subcategory may or may not match
        // e.g. same category and different subcategory OR different category
        return this.pinnedItemsCache.map((g: GCTGene) => g.uid);
      }
    }
  }

  initData(items: GCTGene[]) {
    // hide brain region columns initially
    this.brainRegionsColumns.forEach((c) => (c.visible = false));

    const itemsToPin: GCTGene[] = [];

    // load the previous pins and format previousPins
    const previousPins = this.getPreviousPins();

    items.forEach((item: GCTGene) => {
      item.uid = this.getUid(item);
      item.search_array = [item.ensembl_gene_id.toLowerCase(), item.hgnc_symbol.toLowerCase()];

      if (this.category === 'Protein - Differential Expression') {
        item.search_array.push(item.uniprotid?.toLowerCase() || '');

        // if there is a match on uid or ensembl_gene_id, add it to pinnedGenes
        // if it wasn't added already
        if (this.lastPinnedCategory === 'RNA - Differential Expression') {
          // previousPins would be a list of ensg
          if (previousPins.includes(item.ensembl_gene_id)) itemsToPin.push(item);
        } else {
          // previousPins would be a list of ensg+uniprotids
          if (previousPins.includes(item.uid)) itemsToPin.push(item);
        }
      } else {
        if (previousPins.includes(item.uid)) {
          itemsToPin.push(item);
        }
      }

      item.search_string = item.search_array.join();

      // apply filters
      this.filters.forEach((filter: GCTFilter) => {
        if (!filter.field) {
          return;
        }

        const value = this.getGeneProperty(item, filter.field);

        if (value) {
          if (Array.isArray(value)) {
            value.forEach((v: any) => {
              this.setFilterOption(filter.name, v);
            });
          } else {
            this.setFilterOption(filter.name, value);
          }
        }
      });

      // add tissue columns
      item.tissues?.forEach((tissue: GCTGeneTissue) => {
        const column = this.brainRegionsColumns.find((c) => c.field === tissue.name);
        if (column) column.visible = true;
      });
    });

    // on initial load, we want to cache any items
    if (this.initialLoad) {
      this.initialLoad = false;
      this.setPinnedItemsCache(itemsToPin);
    }

    this.uniquePinnedGenesCount = this.getCountOfUniqueGenes();

    this.updateVisibleColumns();

    if (!this.sortField || !this.columns.includes(this.sortField)) {
      this.sortField = this.columns[0];
      this.sortOrder = -1;
    }

    const preSelection = this.helperService.getGCTSelection();
    this.helperService.deleteGCTSelection();
    if (preSelection?.length) {
      this.searchTerm = preSelection.join(',');
    }

    if (itemsToPin.length) {
      itemsToPin.sort((a, b) => (a.ensembl_gene_id > b.ensembl_gene_id ? 1 : -1));

      if (
        'Protein - Differential Expression' === this.category &&
        this.uniquePinnedGenesCount > this.maxPinnedGenes
      ) {
        this.pendingPinnedItems = itemsToPin;
        this.pinnedGenesModal.show();
      } else {
        this.pinnedItems = [];
        this.pendingPinnedItems = [];
        this.uniquePinnedGenesCount = this.getCountOfUniqueGenes();
        this.pinGenes(itemsToPin);
      }
    } else {
      this.pinnedItems = [];
    }

    this.genes = items;
  }

  /* ----------------------------------------------------------------------- */
  /* Categories
  /* ----------------------------------------------------------------------- */

  updateSubCategories() {
    // update subcategory label text
    if ('Protein - Differential Expression' === this.category) {
      this.subCategoryLabel = 'Profiling Method';
    } else {
      this.subCategoryLabel = 'Models';
    }

    this.subCategories = cloneDeep(variables.subCategories)[this.category];

    // default to first option if subcategory not defined/found
    if (
      !this.subCategory ||
      !this.subCategories.find((c: GCTSelectOption) => c.value === this.subCategory)
    ) {
      this.subCategory = this.subCategories[0]?.value;
    }
  }

  onCategoryChange() {
    this.updateSubCategories();
    this.loadGenes();
  }

  onSubCategoryChange() {
    this.loadGenes();
  }

  /* ----------------------------------------------------------------------- */
  /* Significance Threshold
  /* ----------------------------------------------------------------------- */

  setSignificanceThresholdActive(significanceThresholdActive: boolean) {
    this.significanceThresholdActive = significanceThresholdActive;
    this.filter();
    this.updateUrl();
  }

  /* ----------------------------------------------------------------------- */
  /* Filters
  /* ----------------------------------------------------------------------- */

  setFilters(filters: any) {
    this.filters = filters;
    this.filter();
    this.updateUrl();
  }

  getFilterValues() {
    const values: { [key: string]: string | number | string[] | number[] } = {};

    for (const filter of this.filters) {
      const value: string[] = [];
      for (const option of filter.options.filter((o) => o.selected)) {
        value.push(option.value);
      }
      if (value.length) {
        values[filter.name] = value;
      }
    }

    return values;
  }

  setFilterOption(name: string, value: string | number | string[] | number[]) {
    const filter = this.filters.find((f) => f.name === name);

    if (!filter || !value) {
      return;
    }

    const option = filter?.options.find((option) => value === option.value);
    const urlParam = filter ? this.getUrlParam(filter.name, true) : [];
    const isSelected =
      urlParam && urlParam.indexOf(typeof value === 'string' ? value : String(value)) !== -1;

    if (!option) {
      filter.options.push({
        label: helpers.filterOptionLabel(value),
        value,
        selected: isSelected,
      });

      filter.options.sort((a, b) => {
        if (a.label < b.label) {
          return -1;
        } else if (a.label > b.label) {
          return 1;
        }
        return 0;
      });

      if (filter.order === 'DESC') {
        filter.options.reverse();
      }
    } else if (isSelected) {
      option.selected = isSelected;
    }
  }

  hasSelectedFilters() {
    for (const filter of this.filters) {
      if (filter.options.find((option) => option.selected)) {
        return true;
      }
    }
    return false;
  }

  setSearchTerm(term: string) {
    this.searchTerm = term;
    this.filter();
  }

  clearSearch() {
    this.searchTerm = '';
    this.filter();
  }

  filter() {
    let filters: { [key: string]: any };

    if (this.category === 'RNA - Differential Expression') {
      filters = {
        ensembl_gene_id: {
          value: this.getPinnedEnsemblGeneIds(),
          matchMode: 'exclude_ensembl_gene_id',
        },
      };
    } else {
      filters = {
        uniprotid: {
          value: this.getPinnedUniProtIds(),
          matchMode: 'exclude_uniprotid',
        },
      };
    }

    if (this.searchTerm) {
      if (this.searchTerm.indexOf(',') !== -1) {
        const terms = this.searchTerm
          .toLowerCase()
          .split(',')
          .map((t: string) => t.trim())
          .filter((t: string) => t !== '');
        filters['search_array'] = {
          value: terms,
          matchMode: 'intersect',
        };
      } else {
        filters['search_string'] = {
          value: this.searchTerm.toLowerCase(),
          matchMode: 'contains',
        };
      }
    }

    this.filters.forEach((filter) => {
      if (!filter.field) {
        return;
      }

      const values = filter.options
        .filter((option) => option.selected)
        .map((selected) => selected.value);

      if (values.length) {
        filters[filter.field] = {
          value: values,
          matchMode: filter.matchMode || 'equals',
        };
      }
    });

    const filterChanged =
      JSON.stringify({ ...filters, ...{ ensembl_gene_id: '' } }) !==
      JSON.stringify({
        ...this.genesTable.filters,
        ...{ ensembl_gene_id: '' },
      });

    const currentPage = this.genesTable._first;

    this.genesTable.filters = filters;
    this.genesTable._filter();

    // Restoring current pagination if filters didn't change
    if (!filterChanged) {
      this.genesTable._first = currentPage;
    }
  }

  /* ----------------------------------------------------------------------- */
  /* Sort
  /* ----------------------------------------------------------------------- */

  setSort(event: GCTSortEvent) {
    this.sortField = event.field;
    this.sortOrder = event.order;
    this.sort();
    this.updateUrl();
  }

  sortCallback(event: SortEvent) {
    const order = event.order || 1;
    if (!event.field || !event.data) {
      return;
    }
    const isScoresColumnSort = this.scoresColumns.find((c) => c.field === event.field);
    if (isScoresColumnSort) {
      // if it is one of the numeric scores
      event.data.sort((a, b) => {
        const value1 = this.getScoreForNumericColumn(event.field as string, a);
        const value2 = this.getScoreForNumericColumn(event.field as string, b);

        if (value1 === value2) return 0; // equal so don't do anything
        if (value1 === null) return 1; // sort null after everything
        if (value2 === null) return -1; // sort null after everything

        const result = value1 < value2 ? -1 : 1;
        return order * result;
      });
    } else {
      //it's one of the tissues
      event.data.sort((a, b) => {
        let result = null;

        a = a.tissues.find((tissue: GCTGeneTissue) => tissue.name === event.field)?.logfc;

        b = b.tissues.find((tissue: GCTGeneTissue) => tissue.name === event.field)?.logfc;

        if (a == null && b != null) result = 1 * order;
        else if (a != null && b == null) result = -1 * order;
        else if (a == null && b == null) result = 0;
        else result = a < b ? -1 : a > b ? 1 : 0;

        return order * result;
      });
    }
  }

  sortTable(table: Table) {
    table.sortField = '';
    table.defaultSortOrder = this.sortOrder;
    table.sort({ field: this.sortField });
  }

  sort() {
    this.sortTable(this.pinnedTable || null);
    this.sortTable(this.genesTable || null);
  }

  /* ----------------------------------------------------------------------- */
  /* Pin/Unpin
  /* ----------------------------------------------------------------------- */

  currentCategoriesMatchLastPinnedCategories() {
    // returns if the current categories match the last pinned categories
    return (
      this.lastPinnedCategory === this.category && this.lastPinnedSubCategory === this.subCategory
    );
  }

  setLastPinnedCategories() {
    this.lastPinnedCategory = this.category;
    this.lastPinnedSubCategory = this.subCategory;
  }

  getPinnedGenesCacheKey(category: string, subCategory?: string) {
    return (category + (subCategory ? '-' + subCategory : ''))
      .replace(/[^a-z0-9]/gi, '')
      .toLowerCase();
  }

  setPinnedItemsCache(genes: GCTGene[]) {
    this.pinnedItemsCache = genes;
  }

  clearPinnedItemsCache() {
    this.pinnedItemsCache = [];
    this.uniquePinnedGenesCount = this.getCountOfUniqueGenes();
  }

  refreshPinnedGenes() {
    this.setPinnedItemsCache(this.pinnedItems);
    this.filter();
    this.updateUrl();
  }

  onPinGeneClick(gene: GCTGene) {
    // user-initiated gene pin means we set the last pinned categories
    this.setLastPinnedCategories();

    this.pinGene(gene);

    if (this.category === 'Protein - Differential Expression')
      this.uniquePinnedGenesCount = this.getCountOfUniqueGenes();
  }

  pinGene(gene: GCTGene, refresh = true) {
    const index = this.pinnedItems.findIndex((g: GCTGene) => g.uid === gene.uid);
    if (this.category === 'RNA - Differential Expression') {
      if (index > -1 || this.pinnedItems.length >= this.maxPinnedGenes) return;
    } else {
      // the same unique id exists, so don't allow it to be added
      if (index > -1) return;

      if (this.uniquePinnedGenesCount >= this.maxPinnedGenes) {
        // border condition: if we are at the max allowable pinned genes
        // check if the pinned genes list has a gene with the ensembl id,
        // in which case the protein can be added
        const ensemblIndex = this.pinnedItems.findIndex(
          (g: GCTGene) => g.ensembl_gene_id === gene.ensembl_gene_id,
        );
        if (ensemblIndex < 0) {
          this.showUnableToAddItemErrorToast();
          return;
        }
      }
    }

    this.pinnedItems.push(gene);
    this.uniquePinnedGenesCount = this.getCountOfUniqueGenes();

    if (refresh) {
      this.clearPinnedItemsCache();
      this.refreshPinnedGenes();
    }
  }

  getCountOfUniqueGenes() {
    // this method is used for protein views since there can be multiple pinned proteins
    // that have the same ensg value but different uniprotids
    // so this will return the count of genes with unique ensgs
    const uids = this.pinnedItems.map((g) => g.ensembl_gene_id);
    const uniqueUids = new Set(uids);
    return uniqueUids.size;
  }

  showMaxPinnedRowsErrorToast(rows: number) {
    let message = '';
    if (rows === 0) {
      message =
        'No rows were pinned because you reached the maximum of ' +
        this.maxPinnedGenes +
        ' pinned genes.';
    } else if (rows === 1) {
      message =
        'Only ' +
        rows +
        ' row were pinned, because you reached the maximum of ' +
        this.maxPinnedGenes +
        ' pinned genes.';
    } else {
      message =
        'Only ' +
        rows +
        ' rows were pinned, because you reached the maximum of ' +
        this.maxPinnedGenes +
        ' pinned genes.';
    }

    this.messageService.clear();
    this.messageService.add({
      severity: 'warn',
      sticky: true,
      summary: '',
      detail: message,
    });
    setTimeout(() => {
      this.messageService.clear();
    }, 5000);
  }

  pinGenes(genes: GCTGene[]) {
    const remaining = this.maxPinnedGenes - this.pinnedItems.length;

    if (remaining < 1) {
      return;
    } else {
      if (this.category === 'RNA - Differential Expression') {
        if (remaining < genes?.length) {
          this.showMaxPinnedRowsErrorToast(remaining);
        }
        genes.slice(0, remaining).forEach((g: GCTGene) => {
          this.pinGene(g, false);
        });
      } else {
        genes.slice(0, genes.length).forEach((g: GCTGene) => {
          this.pinGene(g, false);
        });
      }
    }

    if (this.category === 'Protein - Differential Expression')
      this.uniquePinnedGenesCount = this.getCountOfUniqueGenes();
  }

  showUnableToAddItemErrorToast() {
    this.messageService.clear();
    this.messageService.add({
      severity: 'warn',
      sticky: true,
      summary: '',
      detail:
        'The row was not pinned because you reached the maximum of ' +
        this.maxPinnedGenes +
        ' pinned genes.',
    });
    setTimeout(() => {
      this.messageService.clear();
    }, 5000);
  }

  ensgExistsInProteins(ensemblGeneId: string) {
    const ensemblIndex = this.pinnedItems.findIndex(
      (g: GCTGene) => g.ensembl_gene_id === ensemblGeneId,
    );
    if (ensemblIndex < 0) {
      return false;
    }
    return true;
  }

  pinProteins(proteins: GCTGene[]) {
    let remaining = this.maxPinnedGenes - this.uniquePinnedGenesCount;

    let proteinsAdded = 0;

    let showToast = false;
    for (let i = 0; i < proteins.length; i++) {
      // if remaining count is zero, show alert toast
      if (remaining <= 0) {
        // check border condition: when there are no remaining ensg slots, it is still possible there
        // are proteins that could be added
        showToast = true;
        if (this.ensgExistsInProteins(proteins[i].ensembl_gene_id)) {
          // if the gene exists, we can still add the protein
          this.pinGene(proteins[i], false);
          proteinsAdded++;
          remaining = this.maxPinnedGenes - this.getCountOfUniqueGenes();
        }
      } else {
        // add protein to pinned collection
        this.pinGene(proteins[i], false);
        proteinsAdded++;
        // have to call method below since we need to recompute the count of unique genes
        remaining = this.maxPinnedGenes - this.getCountOfUniqueGenes();
      }
    }
    if (showToast) {
      this.showMaxPinnedRowsErrorToast(proteinsAdded);
    }
    this.uniquePinnedGenesCount = this.getCountOfUniqueGenes();
  }

  onUnPinGeneClick(gene: GCTGene, refresh = true) {
    this.setLastPinnedCategories();

    const index = this.pinnedItems.findIndex((g: GCTGene) => g.uid === gene.uid);

    if (index === -1) {
      return;
    }

    this.pinnedItems.splice(index, 1);

    if (refresh) {
      this.clearPinnedItemsCache();
      this.refreshPinnedGenes();
    }

    if (this.category === 'Protein - Differential Expression')
      this.uniquePinnedGenesCount = this.getCountOfUniqueGenes();
  }

  onClearAllClick() {
    this.setLastPinnedCategories();
    this.clearPinnedGenes();
  }

  clearPinnedGenes() {
    this.pinnedItems = [];
    this.clearPinnedItemsCache();
    this.refreshPinnedGenes();
  }

  getPinnedEnsemblGeneIds() {
    return this.pinnedItems.map((g: GCTGene) => g.ensembl_gene_id);
  }

  getPinnedUniProtIds() {
    return this.pinnedItems.map((g: GCTGene) => g.uniprotid);
  }

  getPinDisabledStatus() {
    if (this.category === 'RNA - Differential Expression')
      return this.pinnedItems.length >= this.maxPinnedGenes;
    else {
      // default to showing pin/pin all button for protein view
      return false;
    }
  }

  onPinAllClick() {
    this.setLastPinnedCategories();
    if (this.category === 'RNA - Differential Expression') this.pinFilteredGenes();
    else this.pinFilteredProteins();
  }

  pinFilteredGenes() {
    this.pinGenes(this.genesTable.filteredValue as GCTGene[]);
    this.refreshPinnedGenes();
  }

  pinFilteredProteins() {
    this.pinProteins(this.genesTable.filteredValue as GCTGene[]);
    this.refreshPinnedGenes();
  }

  onPinnedGenesModalChange(response: boolean) {
    if (response) {
      this.pinnedItems = [];
      this.pinGenes(this.pendingPinnedItems);
    } else {
      this.category = this.categories[0].value;
      this.onCategoryChange();
    }
    this.pendingPinnedItems = [];
  }

  /* ----------------------------------------------------------------------- */
  /* URL
  /* ----------------------------------------------------------------------- */

  getUrlParam(name: string, returnArray = false) {
    if (this.urlParams && this.urlParams[name]) {
      return returnArray && typeof this.urlParams[name] === 'string'
        ? this.urlParams[name].split(',')
        : this.urlParams[name];
    }
    return returnArray ? [] : null;
  }

  updateUrl() {
    const params: { [key: string]: any } = this.getFilterValues();

    if (this.category !== this.categories[0].value) {
      params['category'] = this.category;
    }

    if (this.subCategory !== this.subCategories[0]?.value) {
      params['subCategory'] = this.subCategory;
    }

    if (this.sortField && this.sortField !== this.columns[0]) {
      params['sortField'] = this.sortField;
    }

    if (this.sortOrder != -1) {
      params['sortOrder'] = this.sortOrder;
    }

    if (this.pinnedItems.length > 0) {
      params['pinned'] = this.pinnedItems.map((g: GCTGene) => g.uid);
      params['pinned'].sort();
    }

    if (this.significanceThresholdActive) {
      params['significance'] = this.significanceThreshold;
    }

    this.urlParams = params;

    let url = this.router.serializeUrl(this.router.createUrlTree(['/genes/comparison']));

    if (Object.keys(params).length > 0) {
      url += '?' + new URLSearchParams(params);
    }

    window.history.pushState(null, '', url);
  }

  copyUrl() {
    navigator.clipboard.writeText(window.location.href);
    this.messageService.clear();
    this.messageService.add({
      severity: 'info',
      sticky: true,
      summary: '',
      detail:
        'URL copied to clipboard! Use this URL to share or bookmark the current table configuration.',
    });
    setTimeout(() => {
      this.messageService.clear();
    }, 5000);
  }

  /* ----------------------------------------------------------------------- */
  /* Details Panel
  /* ----------------------------------------------------------------------- */

  getDetailsPanelData(tissueName: string, gene: GCTGene) {
    const tissue: any = gene.tissues.find((t) => t.name === tissueName);
    if (tissue) {
      return helpers.getDetailsPanelData(this.category, this.subCategory, gene, tissue);
    }
    return;
  }

  /* ----------------------------------------------------------------------- */
  /* Score Panel
  /* ----------------------------------------------------------------------- */

  getScorePanelData(
    columnName: string,
    gene: GCTGene,
    scoresDistributions: OverallScoresDistribution[] | undefined,
  ) {
    // get the scores distribution for the column and row clicked
    if (!scoresDistributions) {
      return;
    }
    return helpers.getScorePanelData(columnName, gene, scoresDistributions);
  }

  /* ----------------------------------------------------------------------- */
  /* Circles
  /* ----------------------------------------------------------------------- */

  nRoot(x: number, n: number) {
    try {
      const negate = n % 2 === 1 && x < 0;
      if (negate) {
        x = -x;
      }
      const possible = Math.pow(x, 1 / n);
      n = Math.pow(possible, n);
      if (Math.abs(x - n) < 1 && x > 0 === n > 0) {
        return negate ? -possible : possible;
      }
      return;
    } catch (e) {
      return;
    }
  }

  getCircleColor(logfc: number | undefined) {
    if (logfc === undefined) return '#F0F0F0';

    const rounded = this.helperService.getSignificantFigures(logfc, 3);
    if (rounded > 0) {
      if (rounded < 0.1) {
        return '#B5CBEF';
      } else if (rounded < 0.2) {
        return '#84A5DB';
      } else if (rounded < 0.3) {
        return '#5E84C3';
      } else if (rounded < 0.4) {
        return '#3E68AA';
      } else {
        return '#245299';
      }
    } else {
      if (rounded > -0.1) {
        return '#FBB8C5';
      } else if (rounded > -0.2) {
        return '#F78BA0';
      } else if (rounded > -0.3) {
        return '#F16681';
      } else if (rounded > -0.4) {
        return '#EC4769';
      } else {
        return '#D72247';
      }
    }
  }

  getCircleSize(pval: number | null | undefined) {
    // define min and max size of possible circles in pixels
    const MIN_SIZE = 6;
    const MAX_SIZE = 50;

    // pval shouldn't be undefined but if it is, don't show a circle
    // null means there is no data in which case, also don't show a circle
    if (pval === null || pval === undefined) return 0;

    // if significance cutoff radio button selected and
    // p-Value > significance threshhold, don't show
    if (this.significanceThresholdActive && pval > this.significanceThreshold) {
      return 0;
    }

    const pValue = 1 - (this.nRoot(pval, 3) || 0);
    const size = Math.round(pValue * MAX_SIZE);

    // ensure the smallest circles have a min size to be easily hoverable/clickable
    return size < MIN_SIZE ? MIN_SIZE : size;
  }

  getCircleStyle(tissueName: string, gene: GCTGene) {
    const tissue = gene.tissues.find((t) => t.name === tissueName);
    const size = this.getCircleSize(tissue?.adj_p_val);
    const color = this.getCircleColor(tissue?.logfc);

    return {
      display: size > 0 ? 'block' : 'none',
      width: size + 'px',
      height: size + 'px',
      backgroundColor: color,
    };
  }

  getCircleClass(tissueName: string, gene: GCTGene) {
    let classes = 'gene-indicator';
    const tissue = gene.tissues.find((t) => t.name === tissueName);

    if (tissue) {
      if (tissue.logfc) {
        if (tissue.logfc >= 0) {
          classes += ' plus';
        } else {
          classes += ' minus';
        }
      }
    }

    return classes;
  }

  getCircleTooltip(tissueName: string, gene: GCTGene) {
    const tissue = gene.tissues.find((t) => t.name === tissueName);

    if (tissue) {
      return (
        'L2FC: ' +
        this.helperService.getSignificantFigures(tissue.logfc, 3) +
        '\n' +
        'P-value: ' +
        this.helperService.getSignificantFigures(tissue.adj_p_val, 3) +
        '\n\n' +
        'Click for more details'
      );
    }

    return '';
  }

  isCircleTooltipDisabled() {
    return (
      this.detailsPanel?.panels?.first?.overlayVisible ||
      this.detailsPanel?.panels?.last?.overlayVisible ||
      false
    );
  }

  /* ----------------------------------------------------------------------- */
  /* Download pinned genes as CSV
  /* ----------------------------------------------------------------------- */

  downloadPinnedCsv() {
    const columnHeaders = [
      'ensembl_gene_id',
      'hgnc_symbol',
      'target_risk_score',
      'multi_omic_risk_score',
      'genetic_risk_score',
      'Protein - Differential Expression' === this.category ? 'uniprotid' : 'model',
      'tissue',
      'log2_fc',
      'ci_upr',
      'ci_lwr',
      'adj_p_val',
      'biodomains',
    ];
    const data: any[][] = [];

    this.pinnedItems.forEach((g: GCTGene) => {
      const baseRow = [
        g.ensembl_gene_id,
        g.hgnc_symbol,
        this.returnEmptyStringIfNull(g.target_risk_score),
        this.returnEmptyStringIfNull(g.multi_omics_score),
        this.returnEmptyStringIfNull(g.genetics_score),
      ];

      if ('Protein - Differential Expression' === this.category) {
        baseRow.push(g.uniprotid || '');
      } else {
        baseRow.push(this.subCategory);
      }

      this.columns.forEach((tissueName: string) => {
        if (this.isScoresColumn(tissueName)) {
          return;
        }
        const tissue: GCTGeneTissue | undefined = g.tissues.find((t) => t.name === tissueName);
        data.push([
          ...baseRow,
          ...[
            tissueName,
            tissue ? tissue.logfc : '',
            tissue ? tissue.ci_r : '',
            tissue ? tissue.ci_l : '',
            tissue ? tissue.adj_p_val : '',
            g.biodomains?.join(',') || '',
          ],
        ]);
      });
    });

    let csv = '';
    csv = this.arrayToCSVString(columnHeaders);

    data.forEach((row) => {
      csv += this.arrayToCSVString(row);
    });

    const blob = new Blob([csv], { type: 'text/csv' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    const filename = (this.category + '-' + this.subCategory)
      .toLowerCase()
      .replace(/( -)|[()]/gi, '')
      .replace(/ /gi, '-');
    a.setAttribute('href', url);
    a.setAttribute('download', filename + '.csv');
    a.click();
  }

  returnEmptyStringIfNull(val: number | null) {
    return val === null ? '' : val;
  }

  arrayToCSVString(values: string[]): string {
    return values.map((value) => `"${value}"`).join(',') + '\n';
  }

  /* ----------------------------------------------------------------------- */
  /* Utils
  /* ----------------------------------------------------------------------- */

  refresh() {
    this.sort();
    this.filter();
    this.updateColumnWidth();
  }

  navigateToGene(gene: GCTGene) {
    const url = this.router.serializeUrl(
      this.router.createUrlTree(['/genes/' + gene.ensembl_gene_id]),
    );

    window.open(url, '_blank');
  }

  getGCTColumnTooltipText(columnName: string) {
    return this.helperService.getGCTColumnTooltipText(columnName);
  }

  getGCTColumnSortIconTooltipText(columnName: string) {
    return this.helperService.getGCTColumnSortIconTooltipText(columnName);
  }

  onSearchInput(event: Event) {
    const el = event?.target as HTMLTextAreaElement;
    this.setSearchTerm(el.value);
  }

  updateColumnWidth() {
    const count = this.columns.length < 5 ? 5 : this.columns.length;
    const width = this.headerTable?.containerViewChild?.nativeElement?.offsetWidth || 0;
    this.columnWidth = Math.ceil((width - 300) / count) + 'px';
  }

  onResize() {
    this.updateColumnWidth();
  }

  getRoundedGeneData(gene: GCTDetailsPanelData) {
    return {
      l2fc: this.helperService.getSignificantFigures(gene.value || 0, 3),
      pValue: this.helperService.getSignificantFigures(gene.pValue || 0, 3),
    };
  }

  navigateToConsistencyOfChange(data: any) {
    const baseURL = this.router.createUrlTree([
      '/genes/' + data.gene.ensembl_gene_id + '/evidence/rna',
    ]);

    const url = `${baseURL.toString()}/?model=${this.subCategory}#consistency-of-change`;

    window.open(url, '_blank');
  }
}
