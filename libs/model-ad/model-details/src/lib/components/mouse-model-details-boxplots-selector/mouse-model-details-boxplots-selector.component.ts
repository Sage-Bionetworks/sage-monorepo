import { Component, computed, input } from '@angular/core';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { ModelData } from '@sagebionetworks/model-ad/api-client';
import { BoxplotsGridComponent } from '@sagebionetworks/model-ad/ui';
import { ModelDetailsBoxplotsSelectorComponent } from '../model-details-boxplots-selector/model-details-boxplots-selector.component';

@Component({
  selector: 'model-ad-mouse-model-details-boxplots-selector',
  imports: [ModelDetailsBoxplotsSelectorComponent, BoxplotsGridComponent],
  templateUrl: './mouse-model-details-boxplots-selector.component.html',
  styleUrls: ['./mouse-model-details-boxplots-selector.component.scss'],
})
export class MouseModelDetailsBoxplotsSelectorComponent {
  title = input.required<string>();
  modelName = input.required<string>();
  modelControls = input.required<string[]>();
  modelDataList = input.required<ModelData[]>();
  wikiParams = input.required<SynapseWikiParams>();

  readonly filterConfig = {
    label: 'Tissue',
    queryParamKey: 'tissue',
    dataField: 'tissue' as keyof ModelData,
  };

  description = computed(
    () =>
      `The results shown on this page are derived from the analysis of post-mortem brains from ${this.modelName()} mice and matched control animals.`,
  );

  genotypeOrder = computed(() => {
    // MG-331: ensure that model name aligns with genotype values
    // to prevent adding additional boxplot x-axis labels
    // For example, model name "5xFAD (UCI)" should match genotype values "5xFAD",
    // so boxplot only shows "5xFAD" x-axis label rather than "5xFAD" and "5xFAD (UCI)" labels
    const modelNameWithoutParentheticalQualifier = this.modelName().replace(/\s\([^)]*\)$/, '');
    const baseGenotypes = new Set([
      ...this.modelControls(),
      modelNameWithoutParentheticalQualifier,
    ]);
    const extraGenotypes = [
      ...new Set(
        this.modelDataList().flatMap((modelData) => modelData.data.map((item) => item.genotype)),
      ),
    ].filter((genotype) => !baseGenotypes.has(genotype));
    return [...baseGenotypes, ...extraGenotypes];
  });
}
