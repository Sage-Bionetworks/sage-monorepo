import { TitleCasePipe } from '@angular/common';
import { Component, computed, inject, input } from '@angular/core';
import { CategoryPoint, getTextWidth } from '@sagebionetworks/explorers/charts';
import { BoxplotDirective } from '@sagebionetworks/explorers/charts-angular';
import { DecodeGreekEntityPipe } from '@sagebionetworks/explorers/util';
import { IndividualData, Sex } from '@sagebionetworks/model-ad/api-client';
import { MODEL_DETAILS_BOXPLOT_POINT_STYLES } from '@sagebionetworks/model-ad/config';
import { CallbackDataParams } from 'echarts/types/dist/shared';

/**
 * Common interface for boxplot data.
 * Both GeneExpressionDetail and ModelData satisfy this interface.
 */
export interface BoxplotData {
  age: string;
  units: string;
  data: IndividualData[];
  y_axis_max?: number;
}

@Component({
  selector: 'model-ad-boxplot',
  imports: [BoxplotDirective],
  providers: [DecodeGreekEntityPipe, TitleCasePipe],
  templateUrl: './boxplot.component.html',
  styleUrls: ['./boxplot.component.scss'],
})
export class BoxplotComponent {
  titleCasePipe = inject(TitleCasePipe);
  decodeGreekEntityPipe = inject(DecodeGreekEntityPipe);

  private readonly X_AXIS_LABEL_MAX_WIDTH = 100;
  private readonly X_AXIS_LABEL_FONT = "bold 14px 'DM Sans Variable', sans-serif";

  pointCategoryColors = Object.fromEntries(
    MODEL_DETAILS_BOXPLOT_POINT_STYLES.map((style) => [style.label, style.color]),
  );

  pointCategoryShapes = Object.fromEntries(
    MODEL_DETAILS_BOXPLOT_POINT_STYLES.map((style) => [style.label, style.shape]),
  );

  boxplotData = input.required<BoxplotData>();
  sexFilter = input<Sex[] | undefined>();
  xAxisOrder = input<string[] | undefined>();
  showLegend = input<boolean>(false);

  points = computed<CategoryPoint[]>(() => {
    let data = this.boxplotData().data;

    const sexes = this.sexFilter();
    if (sexes) {
      data = data.filter((individual) => sexes.includes(individual.sex));
    }

    return data
      .map((individual) => ({
        xAxisCategory: individual.genotype,
        value: individual.value,
        gridCategory: individual.genotype,
        pointCategory: individual.sex,
        text: this.createTooltipText(individual, this.boxplotData().units),
      }))
      .sort((a, b) => a.pointCategory.localeCompare(b.pointCategory));
  });

  yAxisTitle = computed(() => this.boxplotData().units);

  yAxisMax = computed(() => this.boxplotData().y_axis_max);

  createTooltipText(individual: IndividualData, units: string): string {
    const color = this.pointCategoryColors[individual.sex] || 'black';
    const tooltipMarker = `<span style="display: inline-block; width: 10px; height: 10px; background-color: ${color}; margin-right: 4px;"></span>`;
    return `${tooltipMarker}${individual.sex}\n${individual.value} ${units}\nIndividual ID: ${individual.individual_id}`;
  }

  pointTooltipFormatter = (pt: CategoryPoint, params: CallbackDataParams) => {
    return `<div style="text-align: left;">${pt.text ?? pt.value.toString()}</div>`;
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
