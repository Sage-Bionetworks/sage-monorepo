import { Component, computed, effect, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
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
    if (typeof document !== 'undefined' && typeof window !== 'undefined') {
      const element = document.getElementById(anchorId);
      if (element) {
        const tocElement = document.querySelector('.table-of-contents-container');
        const tocHeight = tocElement ? tocElement.getBoundingClientRect().height : 0;

        const panelNavHeight = parseInt(
          getComputedStyle(document.documentElement).getPropertyValue('--panel-nav-height'),
        );

        const yOffset = -(tocHeight + panelNavHeight + 15); // provide padding

        const y = element.getBoundingClientRect().top + window.pageYOffset + yOffset;
        window.scrollTo({ top: y, behavior: 'smooth' });
      }
    }
  }
}
