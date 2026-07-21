import { Clipboard } from '@angular/cdk/clipboard';
import { Location, NgTemplateOutlet } from '@angular/common';
import {
  afterNextRender,
  Component,
  computed,
  effect,
  ElementRef,
  inject,
  input,
  OnDestroy,
  OnInit,
  signal,
  TemplateRef,
  viewChild,
  viewChildren,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { HelperService, PlatformService } from '@sagebionetworks/explorers/services';
import {
  CopyLinkButtonComponent,
  DownloadDomImageComponent,
  DownloadDomImagesZipComponent,
} from '@sagebionetworks/explorers/ui';
import { DecodeGreekEntityPipe, ModalLinkComponent } from '@sagebionetworks/explorers/util';
import { ModelData, Sex } from '@sagebionetworks/model-ad/api-client';
import { ButtonModule } from 'primeng/button';
import { SelectModule } from 'primeng/select';
import { generateAnchorId } from '../../utils';

export interface FilterConfig {
  label: string;
  queryParamKey: string;
  dataField: keyof ModelData;
}

export interface SectionContext<T = ModelData[]> {
  data: T;
  sexFilter: Sex[];
  copyLinkFn: (anchorId: string) => () => void;
  tooltipTextFn: (anchorId: string) => string;
  setActiveShareLink: (id: string) => void;
  clearActiveShareLink: () => void;
  isShareLinkActive: (anchorId: string) => boolean;
}

@Component({
  selector: 'model-ad-model-details-boxplots-selector',
  imports: [
    FormsModule,
    SelectModule,
    ButtonModule,
    NgTemplateOutlet,
    ModalLinkComponent,
    DecodeGreekEntityPipe,
    DownloadDomImageComponent,
    DownloadDomImagesZipComponent,
    CopyLinkButtonComponent,
  ],
  templateUrl: './model-details-boxplots-selector.component.html',
  styleUrls: ['./model-details-boxplots-selector.component.scss'],
})
export class ModelDetailsBoxplotsSelectorComponent implements OnInit, OnDestroy {
  private readonly helperService = inject(HelperService);
  private readonly location = inject(Location);
  private readonly clipboard = inject(Clipboard);
  private readonly platformService = inject(PlatformService);

  readonly BOXPLOT_DOWNLOAD_IMAGE_PADDING_PX = 20;

  readonly generateAnchorId = generateAnchorId;

  tableOfContentsContainer = viewChild('tableOfContentsContainer', { read: ElementRef });
  boxplotsContainer = viewChild('boxplotsContainer', { read: ElementRef });
  sectionBodies = viewChildren('sectionBody', { read: ElementRef });

  title = input.required<string>();
  modelName = input.required<string>();
  modelDataList = input.required<ModelData[]>();
  wikiParams = input.required<SynapseWikiParams>();
  description = input.required<string>();
  filterConfig = input.required<FilterConfig>();
  anchorDataField = input.required<keyof ModelData>();
  sectionTemplate = input.required<TemplateRef<SectionContext>>();
  /**
   * Optional function to transform section data before passing to the template.
   * Called once per evidence type when data changes.
   * Must return an array that can be iterated in the template.
   */
  transformSectionData = input<(data: ModelData[]) => unknown[]>();
  showSectionShareLink = input<boolean>(true);
  showRowDownloadButton = input<boolean>(true);

  sexOptions: { label: string; value: Sex[] }[] = [
    { label: 'Female & Male', value: ['Female', 'Male'] },
    { label: 'Female', value: ['Female'] },
    { label: 'Male', value: ['Male'] },
  ];
  defaultSexOption = this.sexOptions[0];
  selectedSexOption = signal(this.defaultSexOption);

  filterOptions = computed(() => {
    return Array.from(
      new Set(
        this.modelDataList()
          .map((item) => item[this.filterConfig().dataField] as string)
          .filter((v) => v != null),
      ),
    );
  });
  selectedFilterOption = signal('');

  private readonly SEX_QUERY_KEY = 'sex';

  private readonly SCROLL_PADDING = 15;
  isInitialScrollDone = false;
  hasInitializedOptions = false;

  activeShareLink = signal('');
  lastShareLinkCopied = signal('');

  isTocCollapsed = signal(true);

  constructor() {
    effect(() => {
      const sexOption = this.selectedSexOption();
      const filterOption = this.selectedFilterOption();

      // Keep URL query parameters in sync with filter selections, but avoid updating
      // during initialization to prevent circular updates when reading from URL params
      if (this.hasInitializedOptions) {
        this.updateQueryParams(sexOption.label, filterOption);
        this.lastShareLinkCopied.set('');
      }
    });

    afterNextRender(() => {
      this.scrollToSectionOnFirstRender();
    });
  }

  // Handle scrolling after same-document navigation
  // Use popstate because NavigationEnd does not fire on
  // same-document navigation with the same fragment
  onPopState = () => {
    const hashFragment = this.helperService.getHashFragment();
    if (this.isValidHashFragment(hashFragment)) {
      // Wait for the DOM to be settled
      setTimeout(() => {
        this.scrollToSection(hashFragment, false);
      }, 100);
    } else {
      // Defer URL update until after popstate event completes
      setTimeout(() => {
        this.updateUrlFragment(undefined);
      }, 0);
    }
  };

  ngOnInit(): void {
    this.initializeOptionsFromUrlParams();
    if (this.platformService.isBrowser) {
      window.addEventListener('popstate', this.onPopState);
    }
  }

  ngOnDestroy(): void {
    if (this.platformService.isBrowser) {
      window.removeEventListener('popstate', this.onPopState);
    }
  }

  selectedModelDataList = computed(() => {
    return this.modelDataList().filter(
      (modelData) =>
        (modelData[this.filterConfig().dataField] as string) === this.selectedFilterOption(),
    );
  });

  evidenceTypes = computed(() => {
    return Array.from(new Set(this.selectedModelDataList().map((item) => item.evidence_type)));
  });

  tocItems = computed(() => {
    return Array.from(
      new Set(this.selectedModelDataList().map((item) => item[this.anchorDataField()] as string)),
    );
  });

  boxplotSections = computed(() => {
    return this.evidenceTypes().map((evidenceType: string) => ({
      evidenceType,
      filename: this.generateBoxplotsFilename(
        evidenceType,
        this.selectedFilterOption(),
        this.selectedSexOption().value,
        this.modelName(),
      ),
      data: this.generateBoxplotsCsvData(evidenceType),
    }));
  });

  sectionDataByEvidenceType = computed(() => {
    const transform = this.transformSectionData();
    return new Map(
      this.evidenceTypes().map((evidenceType) => {
        const rawData = this.selectedModelDataList().filter(
          (modelData) => modelData.evidence_type === evidenceType,
        );
        const transformedData = transform ? transform(rawData) : rawData;
        return [evidenceType, { rawData, transformedData }];
      }),
    );
  });

  domFiles = computed(() => {
    if (
      this.sectionBodies().length === 0 ||
      this.sectionBodies().length !== this.evidenceTypes().length
    ) {
      return [];
    }

    return this.boxplotSections().map((section, index) => ({
      target: this.sectionBodies()[index].nativeElement,
      filename: section.filename,
    }));
  });

  getSectionBodyTargetByEvidenceType(evidenceType: string) {
    const index = this.evidenceTypes().indexOf(evidenceType);
    return this.domFiles()[index]?.target;
  }

  generateBoxplotsCsvData(evidenceType: string): string[][] {
    const sectionData = this.sectionDataByEvidenceType().get(evidenceType)?.rawData ?? [];
    const hasTissue = sectionData.some((item) => item.tissue != null);
    const header = [
      'name',
      'evidence_type',
      ...(hasTissue ? ['tissue'] : []),
      'age',
      'sex',
      'genotype',
      'individual_id',
      'value',
      'units',
    ];
    const sexes = this.selectedSexOption().value;
    const rows: string[][] = [header];
    for (const modelData of sectionData) {
      for (const item of modelData.data) {
        if (!sexes.includes(item.sex as Sex)) continue;
        rows.push([
          modelData.name,
          modelData.evidence_type,
          ...(hasTissue ? [modelData.tissue ?? ''] : []),
          modelData.age,
          item.sex,
          item.genotype,
          item.individual_id,
          String(item.value),
          modelData.units,
        ]);
      }
    }
    return rows;
  }

  getDefaultFilterOption() {
    return this.filterOptions()[0] || '';
  }

  initializeOptionsFromUrlParams() {
    const sexParam = this.helperService.getUrlParam(this.SEX_QUERY_KEY);
    const filterParam = this.helperService.getUrlParam(this.filterConfig().queryParamKey);

    const matchingSexOption = this.sexOptions.find((option) => option.label === sexParam);
    if (matchingSexOption !== undefined) this.selectedSexOption.set(matchingSexOption);

    const matchingFilterOption = this.filterOptions().find((option) => option === filterParam);
    this.selectedFilterOption.set(matchingFilterOption || this.getDefaultFilterOption());

    this.hasInitializedOptions = true;
  }

  isValidHashFragment(hashFragment: string): boolean {
    return this.tocItems().some((item) => this.generateAnchorId(item) === hashFragment);
  }

  scrollToSectionOnFirstRender() {
    if (typeof window !== 'undefined' && !this.isInitialScrollDone) {
      const hashFragment = this.helperService.getHashFragment();
      if (this.isValidHashFragment(hashFragment)) {
        this.isInitialScrollDone = this.scrollToSection(hashFragment, false);
      } else {
        this.isInitialScrollDone = true;
        this.updateUrlFragment(hashFragment);
      }
    }
  }

  getUpdatedUrlFragment(fragment: string | undefined): string {
    const fragmentPart = fragment ? `#${fragment}` : '';
    return `${window.location.pathname}${window.location.search}${fragmentPart}`;
  }

  updateUrlFragment(fragment: string | undefined): void {
    this.location.replaceState(this.getUpdatedUrlFragment(fragment));
  }

  updateQueryParams(sex: string, filterValue: string) {
    const params = new URLSearchParams(window.location.search);

    if (sex !== this.defaultSexOption.label) {
      params.set(this.SEX_QUERY_KEY, sex);
    } else {
      // Don't set query param for default value
      params.delete(this.SEX_QUERY_KEY);
    }

    const filterQueryKey = this.filterConfig().queryParamKey;
    if (filterValue !== this.getDefaultFilterOption()) {
      params.set(filterQueryKey, filterValue);
    } else {
      // Don't set query param for default value
      params.delete(filterQueryKey);
    }

    const queryString = params.toString();
    const queryStringFormatted = queryString ? `?${queryString}` : '';

    const hashFragment = this.helperService.getHashFragment();
    const hash = this.isValidHashFragment(hashFragment) ? `#${hashFragment}` : '';

    const newUrl = `${window.location.pathname}${queryStringFormatted}${hash}`;
    this.location.replaceState(newUrl);
  }

  scrollToSection(anchorId: string, updateUrl = true): boolean {
    const container = this.boxplotsContainer();
    if (typeof document !== 'undefined' && typeof window !== 'undefined' && container) {
      const element = container.nativeElement.querySelector(`[id="${anchorId}"]`) as HTMLElement;

      if (element) {
        const tocElement = this.tableOfContentsContainer()?.nativeElement;
        const tocHeight = tocElement ? tocElement.getBoundingClientRect().height : 0;

        const panelNavHeight = this.helperService.getNumberFromCSSValue(
          getComputedStyle(document.documentElement).getPropertyValue('--panel-nav-height'),
        );

        const yOffset = -(tocHeight + panelNavHeight + this.SCROLL_PADDING);
        const elementOffset = this.helperService.getOffset(element);
        const y = elementOffset.top + yOffset;
        window.scrollTo({ top: y, behavior: 'smooth' });

        if (updateUrl) this.updateUrlFragment(anchorId);

        return true;
      }
    }
    return false;
  }

  getShareLinkTooltipText = (label: string): string => {
    return this.lastShareLinkCopied() === label
      ? 'URL copied to clipboard'
      : 'Copy the URL to this section';
  };

  copyShareLink(label: string): void {
    const urlFragment = this.getUpdatedUrlFragment(this.generateAnchorId(label));
    this.clipboard.copy(`${window.location.origin}${urlFragment}`);
    this.lastShareLinkCopied.set(label);
  }

  copyShareLinkCallbackFn = (label: string): (() => void) => {
    return () => this.copyShareLink(label);
  };

  setActiveShareLinkFn = (id: string): void => {
    this.activeShareLink.set(id);
  };

  clearActiveShareLinkFn = (): void => {
    this.activeShareLink.set('');
  };

  isShareLinkActiveFn = (anchorId: string): boolean => {
    return this.activeShareLink() === anchorId;
  };

  decodeHtmlEntities(text: string): string {
    const htmlEntityRegex = /&([^;]+);/g;
    return text.replaceAll(htmlEntityRegex, '$1');
  }

  generateBoxplotsZipFilename(
    filterValue: string,
    sex: string[],
    modelName: string,
    title: string,
  ) {
    const filename = `${modelName}_${filterValue}_${sex.join('_')}_${title}`;
    return this.helperService.cleanFilename(filename);
  }

  generateBoxplotsFilename(
    evidenceType: string,
    filterValue: string,
    sex: string[],
    modelName: string,
  ) {
    const decodedEvidenceType = this.decodeHtmlEntities(evidenceType);
    // Only include filterValue if it differs from evidenceType to avoid duplication
    const filterPart = filterValue !== evidenceType ? `_${filterValue}` : '';
    const filename = `${modelName}_${decodedEvidenceType}${filterPart}_${sex.join('_')}`;
    return this.helperService.cleanFilename(filename);
  }

  toggleToc(): void {
    this.isTocCollapsed.set(!this.isTocCollapsed());
  }
}
