import { Component, computed, effect, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { ModalLinkComponent } from '@sagebionetworks/explorers/util';
import { IndividualData, ModelData } from '@sagebionetworks/model-ad/api-client-angular';
import { SelectModule } from 'primeng/select';
import { ModelDetailsBoxplotsGridComponent } from '../model-details-boxplots-grid/model-details-boxplots-grid.component';

@Component({
  selector: 'model-ad-model-details-boxplots-selector',
  imports: [FormsModule, SelectModule, ModelDetailsBoxplotsGridComponent, ModalLinkComponent],
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
}
