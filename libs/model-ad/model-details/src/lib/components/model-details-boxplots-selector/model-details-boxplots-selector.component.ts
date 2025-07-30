import {
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

  @ViewChild('boxplotsContainer', { static: false }) boxplotsContainer!: ElementRef<HTMLElement>;

  title = input.required<string>();
  modelName = input.required<string>();
  modelControls = input.required<string[]>();
  modelDataList = input.required<ModelData[]>();
  wikiParams = input.required<SynapseWikiParams>();

  sexOptions: { label: string; value: IndividualData.SexEnum[] }[] = [
    { label: 'Male & Female', value: ['Male', 'Female'] },
    { label: 'Female', value: ['Female'] },
    { label: 'Male', value: ['Male'] },
  ];
  selectedSexOption = signal(this.sexOptions[0]);

  tissueOptions = computed(() => {
    return Array.from(new Set(this.modelDataList().map((item) => item.tissue)));
  });
  selectedTissueOption = signal('');

  private readonly SCROLL_PADDING = 15;

  constructor() {
    effect(() => {
      const options = this.tissueOptions();
      if (options.length > 0 && !this.selectedTissueOption()) {
        this.selectedTissueOption.set(options[0]);
      }
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

  generateAnchorId(evidenceType: string): string {
    return evidenceType
      .toLowerCase()
      .replace(/[^a-z0-9]/g, '-')
      .replace(/-+/g, '-');
  }

  scrollToSection(anchorId: string): void {
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
      }
    }
  }
}
