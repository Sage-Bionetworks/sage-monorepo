import { TitleCasePipe } from '@angular/common';
import { Component, computed, inject, input } from '@angular/core';
import { DecodeGreekEntityPipe } from '@sagebionetworks/explorers/util';
import { IndividualData, ModelData } from '@sagebionetworks/model-ad/api-client-angular';
import { CategoryPoint, getTextWidth } from '@sagebionetworks/shared/charts';
import { BoxplotDirective } from '@sagebionetworks/shared/charts-angular';
import { CallbackDataParams } from 'echarts/types/dist/shared';

@Component({
  selector: 'model-ad-model-details-boxplot',
  imports: [BoxplotDirective],
  providers: [DecodeGreekEntityPipe, TitleCasePipe],
  templateUrl: './model-details-boxplot.component.html',
  styleUrls: ['./model-details-boxplot.component.scss'],
})
export class ModelDetailsBoxplotComponent {
  titleCasePipe = inject(TitleCasePipe);
  decodeGreekEntityPipe = inject(DecodeGreekEntityPipe);

  private readonly X_AXIS_LABEL_MAX_WIDTH = 100;
  private readonly X_AXIS_LABEL_FONT = "bold 14px 'DM Sans Variable', sans-serif";

  pointCategoryColors = {
    Male: '#1B00B3',
    Female: '#DB00FF',
  };

  pointCategoryShapes = {
    Male: 'circle',
    Female: 'triangle',
  };

  modelData = input.required<ModelData>();
  sexes = input.required<IndividualData.SexEnum[]>();
  showLegend = input<boolean>(false);
  genotypeOrder = input<string[] | undefined>();

  points = computed<CategoryPoint[]>(() => {
    return this.modelData()
      .data.filter((individual) => this.sexes().includes(individual.sex))
      .map((individual) => ({
        xAxisCategory: individual.genotype,
        value: individual.value,
        gridCategory: individual.genotype,
        pointCategory: individual.sex,
        text: this.createTooltipText(individual, this.modelData().units),
      }))
      .sort((a, b) => a.pointCategory.localeCompare(b.pointCategory));
  });

  yAxisTitle = computed(() => this.modelData().units);

  createTooltipText(individual: IndividualData, units: string): string {
    return `${individual.sex}\n${individual.value} ${units}\nIndividual ID: ${individual.individual_id}`;
  }

  pointTooltipFormatter = (pt: CategoryPoint, params: CallbackDataParams) => {
    return `${params.marker} ${pt.text ?? pt.value.toString()}`;
  };

  xAxisLabelFormatter = (value: string) => {
    const textWidth = getTextWidth(value, this.X_AXIS_LABEL_FONT);
    if (textWidth > this.X_AXIS_LABEL_MAX_WIDTH) {
      // replace first occurrence of - or * with a newline character
      return value.replace(/[-*]/, (match) => match + '\n');
    }
    return value;
  };
}
