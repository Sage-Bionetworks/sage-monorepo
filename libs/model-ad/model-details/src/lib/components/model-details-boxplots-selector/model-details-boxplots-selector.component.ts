import { Location } from '@angular/common';
import {
  afterNextRender,
  Component,
  computed,
  effect,
  ElementRef,
  inject,
  input,
  signal,
  ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { HelperService } from '@sagebionetworks/explorers/services';
import { DecodeGreekEntityPipe, ModalLinkComponent } from '@sagebionetworks/explorers/util';
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
    DecodeGreekEntityPipe,
  ],
  templateUrl: './model-details-boxplots-selector.component.html',
  styleUrls: ['./model-details-boxplots-selector.component.scss'],
})
export class ModelDetailsBoxplotsSelectorComponent {
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

  private readonly SCROLL_PADDING = 15;
  isInitialScrollDone = false;
  hasInitializedOptions = false;

  constructor() {
    effect(() => {
      const sexOption = this.selectedSexOption();
      const tissueOption = this.selectedTissueOption();

      if (this.hasInitializedOptions) {
        this.updateQueryParams(sexOption.label, tissueOption);
      }
    });

    afterNextRender(() => {
      this.initializeOptionsFromUrlParams();

      setTimeout(() => {
        // provide time to render boxplots after initializing options before attempting to scroll
        this.initialScrollToSection();
      }, 10);
    });
  }

  selectedModelDataList = computed(() => {
    return this.modelDataList().filter(
      (modelData) => modelData.tissue === this.selectedTissueOption(),
    );
  });

  genotypeOrder = computed(() => {
    const baseGenotypes = new Set([...this.modelControls(), this.modelName()]);
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
    const sexParam = this.helperService.getUrlParam('sex');
    const tissueParam = this.helperService.getUrlParam('tissue');

    const matchingSexOption = this.sexOptions.find((option) => option.label === sexParam);
    if (matchingSexOption !== undefined) this.selectedSexOption.set(matchingSexOption);

    this.selectedTissueOption.set(tissueParam || this.getDefaultTissue());

    this.hasInitializedOptions = true;
  }

  initialScrollToSection() {
    if (typeof window !== 'undefined' && !this.isInitialScrollDone) {
      const hash = window.location.hash.slice(1);
      this.isInitialScrollDone = hash === '' ? true : this.scrollToSection(hash, false);
    }
  }

  generateAnchorId(evidenceType: string): string {
    return evidenceType
      .toLowerCase()
      .replace(/[^a-z0-9]/g, '-')
      .replace(/-+/g, '-');
  }

  updateUrlFragment(fragment: string): void {
    const newUrl = `${window.location.pathname}${window.location.search}#${fragment}`;
    this.location.replaceState(newUrl);
  }

  updateQueryParams(sex: string, tissue: string) {
    const params = new URLSearchParams(window.location.search);

    if (sex !== this.defaultSexOption.label) {
      params.set('sex', sex);
    } else {
      params.delete('sex');
    }

    if (tissue !== this.getDefaultTissue()) {
      params.set('tissue', tissue);
    } else {
      params.delete('tissue');
    }

    const queryString = params.toString();
    const queryStringFormatted = queryString ? `?${queryString}` : '';
    const newUrl = `${window.location.pathname}${queryStringFormatted}${window.location.hash}`;
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
}
