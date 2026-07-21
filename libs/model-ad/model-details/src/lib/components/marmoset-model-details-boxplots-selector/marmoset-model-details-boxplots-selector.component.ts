import { Component, computed, inject, input } from '@angular/core';
import { LegendDirective } from '@sagebionetworks/explorers/charts-angular';
import { SynapseWikiParams } from '@sagebionetworks/explorers/models';
import { LoggerService } from '@sagebionetworks/explorers/services';
import { CopyLinkButtonComponent } from '@sagebionetworks/explorers/ui';
import { ModelData } from '@sagebionetworks/model-ad/api-client';
import { BoxplotComponent, getPointStylesBySex } from '@sagebionetworks/model-ad/ui';
import { generateAnchorId } from '../../utils';
import { ModelDetailsBoxplotsSelectorComponent } from '../model-details-boxplots-selector/model-details-boxplots-selector.component';

@Component({
  selector: 'model-ad-marmoset-model-details-boxplots-selector',
  imports: [
    ModelDetailsBoxplotsSelectorComponent,
    BoxplotComponent,
    CopyLinkButtonComponent,
    LegendDirective,
  ],
  templateUrl: './marmoset-model-details-boxplots-selector.component.html',
  styleUrls: ['./marmoset-model-details-boxplots-selector.component.scss'],
})
export class MarmosetModelDetailsBoxplotsSelectorComponent {
  private readonly logger = inject(LoggerService);

  readonly getPointStyles = getPointStylesBySex;
  readonly generateAnchorId = generateAnchorId;

  title = input.required<string>();
  modelName = input.required<string>();
  modelDataList = input.required<ModelData[]>();
  wikiParams = input.required<SynapseWikiParams>();

  readonly filterConfig = {
    label: 'Measurement',
    queryParamKey: 'measurement',
    dataField: 'evidence_type' as keyof ModelData,
  };

  description = computed(
    () =>
      `The results shown on this page are derived from the analysis of ${this.modelName()} marmosets and age- and sex-matched contemporaneous non-carrier control animals.`,
  );

  transformSectionData = (data: ModelData[]) => {
    const ages = Array.from(new Set(data.map((item) => item.age)));
    return ages.map((age) => {
      const items = data.filter((item) => item.age === age);
      if (items.length > 1) {
        this.logger.warn(
          `MarmosetModelDetailsBoxplotsSelectorComponent: expected 1 ModelData per age group but got ${items.length} for age "${age}". Only the first item will be rendered.`,
        );
      }
      return { age, data: items };
    });
  };
}
