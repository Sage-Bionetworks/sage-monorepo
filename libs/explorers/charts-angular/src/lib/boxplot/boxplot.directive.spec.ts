import { Component } from '@angular/core';
import {
  BoxplotProps,
  dynamicBoxplotPoints,
  staticBoxplotPoints,
  staticBoxplotSummaries,
} from '@sagebionetworks/explorers/charts';
import { render, screen } from '@testing-library/angular';
import { BoxplotDirective } from './boxplot.directive';

const renderTestComponent = async (props: BoxplotProps) => {
  @Component({
    imports: [BoxplotDirective],
    template: `<div
      sageBoxplot
      [points]="points"
      [summaries]="summaries"
      [title]="title"
      [xAxisTitle]="xAxisTitle"
      [xAxisLabelFormatter]="xAxisLabelFormatter"
      [xAxisCategories]="xAxisCategories"
      [yAxisTitle]="yAxisTitle"
      [yAxisMin]="yAxisMin"
      [yAxisMax]="yAxisMax"
      [xAxisLabelTooltipFormatter]="xAxisLabelTooltipFormatter"
      [pointTooltipFormatter]="pointTooltipFormatter"
      [pointCategoryColors]="pointCategoryColors"
      [pointCategoryShapes]="pointCategoryShapes"
      [showLegend]="showLegend"
      [pointOpacity]="pointOpacity"
      [noDataStyle]="noDataStyle"
      [chartStyle]="chartStyle"
    ></div>`,
  })
  class TestComponent {
    points = props.points;
    summaries = props.summaries;
    title = props.title;
    xAxisTitle = props.xAxisTitle;
    xAxisLabelFormatter = props.xAxisLabelFormatter;
    xAxisCategories = props.xAxisCategories;
    yAxisTitle = props.yAxisTitle;
    yAxisMin = props.yAxisMin;
    yAxisMax = props.yAxisMax;
    xAxisLabelTooltipFormatter = props.xAxisLabelTooltipFormatter;
    pointTooltipFormatter = props.pointTooltipFormatter;
    pointCategoryColors = props.pointCategoryColors;
    pointCategoryShapes = props.pointCategoryShapes;
    showLegend = props.showLegend;
    pointOpacity = props.pointOpacity;
    noDataStyle = props.noDataStyle;
    chartStyle = props.chartStyle;
  }

  return await render(TestComponent, {
    imports: [BoxplotDirective],
  });
};

describe('BoxplotDirective', () => {
  it('should render no data placeholder when no data is passed', async () => {
    await renderTestComponent({ points: [] });
    expect(screen.getByLabelText('No data is currently available.')).toBeVisible();
  });

  it('should render boxplot with static summaries', async () => {
    await renderTestComponent({
      points: staticBoxplotPoints,
      summaries: staticBoxplotSummaries,
    });
    expect(
      screen.getByLabelText(
        /The 0 series is a Boxplot.The data is as follows: the data for 1 is CAT1, 10, 20, 30, 40, 50, 1, the data for 2 is CAT2, 15, 25, 35, 45, 55, 2, the data for 3 is CAT3, 20, 30, 40, 50, 60, 3, the data for 4 is CAT4, 20, 30, 40, 50, 60, 4./i,
      ),
    ).toBeVisible();
  });

  it('should render boxplot with dynamic summaries', async () => {
    await renderTestComponent({
      points: dynamicBoxplotPoints,
    });
    expect(
      screen.getByLabelText(
        /The 0 series is a Boxplot.The data is as follows: the data for 0 is 0, NaN, NaN, NaN, NaN, NaN, the data for 1 is 1, 317, 604.5, 729, 830.25, 877, the data for 2 is 2, 407, 540.25, 570, 754.25, 919, the data for 3 is 3, 336, 461, 611, 798.5, 982./i,
      ),
    ).toBeVisible();
  });
});
