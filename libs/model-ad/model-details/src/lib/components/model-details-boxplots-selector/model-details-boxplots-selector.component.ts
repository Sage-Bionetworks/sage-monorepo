import { Location } from '@angular/common';
import {
  afterNextRender,
  Component,
  computed,
  effect,
  ElementRef,
  inject,
  input,
  OnInit,
  signal,
  ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { HelperService } from '@sagebionetworks/explorers/services';
import { DownloadDomImageComponent } from '@sagebionetworks/explorers/ui';
import {
  DecodeGreekEntityPipe,
  ModalLinkComponent,
  SvgIconComponent,
} from '@sagebionetworks/explorers/util';
import { IndividualData, ModelData } from '@sagebionetworks/model-ad/api-client-angular';
import { SelectModule } from 'primeng/select';
import { ModelDetailsBoxplotsGridComponent } from '../model-details-boxplots-grid/model-details-boxplots-grid.component';

@Component({
  selector: 'model-ad-model-details-boxplots-selector',
  imports: [
    FormsModule,
    SelectModule,
    ModelDetailsBoxplotsGridComponent,
    ModalLinkComponent,
    SvgIconComponent,
    DecodeGreekEntityPipe,
    DownloadDomImageComponent,
  ],
  templateUrl: './model-details-boxplots-selector.component.html',
  styleUrls: ['./model-details-boxplots-selector.component.scss'],
})
export class ModelDetailsBoxplotsSelectorComponent implements OnInit {
  private readonly helperService = inject(HelperService);
  private readonly location = inject(Location);

  @ViewChild('boxplotsContainer', { static: false }) boxplotsContainer!: ElementRef<HTMLElement>;

  title = input.required<string>();
  modelName = input.required<string>();
  modelControls = input.required<string[]>();
  modelDataList = input.required<ModelData[]>();
  wikiParams = input.required<SynapseWikiParams>();

  sexOptions: { label: string; value: IndividualData.SexEnum[] }[] = [
    { label: 'Female & Male', value: ['Female', 'Male'] },
    { label: 'Female', value: ['Female'] },
    { label: 'Male', value: ['Male'] },
  ];
  defaultSexOption = this.sexOptions[0];
  selectedSexOption = signal(this.defaultSexOption);

  tissueOptions = computed(() => {
    return Array.from(new Set(this.modelDataList().map((item) => item.tissue)));
  });
  selectedTissueOption = signal('');

  private readonly TISSUE_QUERY_KEY = 'tissue';
  private readonly SEX_QUERY_KEY = 'sex';

  private readonly SCROLL_PADDING = 15;
  isInitialScrollDone = false;
  hasInitializedOptions = false;

  constructor() {
    effect(() => {
      const sexOption = this.selectedSexOption();
      const tissueOption = this.selectedTissueOption();

      // Keep URL query parameters in sync with filter selections, but avoid updating
      // during initialization to prevent circular updates when reading from URL params
      if (this.hasInitializedOptions) {
        this.updateQueryParams(sexOption.label, tissueOption);
      }
    });

    afterNextRender(() => {
      this.scrollToSectionOnFirstRender();
    });
  }

  ngOnInit(): void {
    this.initializeOptionsFromUrlParams();
  }

  selectedModelDataList = computed(() => {
    return this.modelDataList().filter(
      (modelData) => modelData.tissue === this.selectedTissueOption(),
    );
  });

  genotypeOrder = computed(() => {
    // MG-331: ensure that model name aligns with genotype values
    // to prevent adding additional boxplot x-axis labels
    // For example, model name "5xFAD (UCI)" should match genotype values "5xFAD",
    // so boxplot only shows "5xFAD" x-axis label rather than "FxFAD" and "FxFAD (UCI)" labels
    const modelNameWithoutParentheticalQualifier = this.modelName().replace(/\s\([^)]*\)$/, '');
    const baseGenotypes = new Set([
      ...this.modelControls(),
      modelNameWithoutParentheticalQualifier,
    ]);
    const extraGenotypes = [
      ...new Set(
        this.selectedModelDataList().flatMap((modelData) =>
          modelData.data.map((item) => item.genotype),
        ),
      ),
    ].filter((genotype) => !baseGenotypes.has(genotype));
    return [...baseGenotypes, ...extraGenotypes];
  });

  evidenceTypes = computed(() => {
    return Array.from(new Set(this.selectedModelDataList().map((item) => item.evidence_type)));
  });

  getSelectedModelDataForEvidenceType(evidenceType: string) {
    return this.selectedModelDataList().filter(
      (modelData) => modelData.evidence_type === evidenceType,
    );
  }

  getDefaultTissue() {
    return this.tissueOptions()[0] || '';
  }

  initializeOptionsFromUrlParams() {
    const sexParam = this.helperService.getUrlParam(this.SEX_QUERY_KEY);
    const tissueParam = this.helperService.getUrlParam(this.TISSUE_QUERY_KEY);

    const matchingSexOption = this.sexOptions.find((option) => option.label === sexParam);
    if (matchingSexOption !== undefined) this.selectedSexOption.set(matchingSexOption);

    const matchingTissueOption = this.tissueOptions().find((option) => option === tissueParam);
    this.selectedTissueOption.set(matchingTissueOption || this.getDefaultTissue());

    this.hasInitializedOptions = true;
  }

  getHashFragment() {
    // Extract hash fragment from URL (e.g. "nfl" from "#nfl")
    return window.location.hash.slice(1);
  }

  isValidHashFragment(hashFragment: string): boolean {
    return this.evidenceTypes().some(
      (evidenceType) => this.generateAnchorId(evidenceType) === hashFragment,
    );
  }

  scrollToSectionOnFirstRender() {
    if (typeof window !== 'undefined' && !this.isInitialScrollDone) {
      const hashFragment = this.getHashFragment();
      if (this.isValidHashFragment(hashFragment)) {
        this.isInitialScrollDone = this.scrollToSection(hashFragment, false);
      } else {
        this.isInitialScrollDone = true;
        this.updateUrlFragment(hashFragment);
      }
    }
  }

  generateAnchorId(evidenceType: string): string {
    return evidenceType
      .toLowerCase()
      .replace(/[^a-z0-9]/g, '-')
      .replace(/-+/g, '-');
  }

  updateUrlFragment(fragment: string | undefined): void {
    const fragmentPart = fragment ? `#${fragment}` : '';
    const newUrl = `${window.location.pathname}${window.location.search}${fragmentPart}`;
    this.location.replaceState(newUrl);
  }

  updateQueryParams(sex: string, tissue: string) {
    const params = new URLSearchParams(window.location.search);

    if (sex !== this.defaultSexOption.label) {
      params.set(this.SEX_QUERY_KEY, sex);
    } else {
      // Don't set query param for default value
      params.delete(this.SEX_QUERY_KEY);
    }

    if (tissue !== this.getDefaultTissue()) {
      params.set(this.TISSUE_QUERY_KEY, tissue);
    } else {
      // Don't set query param for default value
      params.delete(this.TISSUE_QUERY_KEY);
    }

    const queryString = params.toString();
    const queryStringFormatted = queryString ? `?${queryString}` : '';

    const hashFragment = this.getHashFragment();
    const hash = this.isValidHashFragment(hashFragment) ? `#${hashFragment}` : '';

    const newUrl = `${window.location.pathname}${queryStringFormatted}${hash}`;
    this.location.replaceState(newUrl);
  }

  scrollToSection(anchorId: string, updateUrl = true): boolean {
    if (
      typeof document !== 'undefined' &&
      typeof window !== 'undefined' &&
      this.boxplotsContainer
    ) {
      const element = this.boxplotsContainer.nativeElement.querySelector(
        `#${anchorId}`,
      ) as HTMLElement;

      if (element) {
        const tocElement = document.querySelector('.table-of-contents-container');
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

  decodeHtmlEntities(text: string): string {
    const htmlEntityRegex = /&([^;]+);/g;
    return text.replace(htmlEntityRegex, '$1');
  }

  generateBoxplotsFilename(evidenceType: string, tissue: string, sex: string[], modelName: string) {
    const invalidFilenameCharsRegex = /[<>:"\\/|?*]/g;
    const filename = `${modelName}_${this.decodeHtmlEntities(evidenceType)}_${tissue}_${sex.join('_')}`;
    const cleanFilename = filename.replace(invalidFilenameCharsRegex, '_').replace(/ /g, '_');
    return cleanFilename;
  }

  generateBoxplotsHtmlElementId(evidenceType: string): string {
    return `${this.generateAnchorId(evidenceType)}-boxplots-grid`;
  }

  getBoxplotsHTMLElement(evidenceType: string) {
    const id = this.generateBoxplotsHtmlElementId(evidenceType);
    return document.getElementById(id) as HTMLElement;
  }
}
