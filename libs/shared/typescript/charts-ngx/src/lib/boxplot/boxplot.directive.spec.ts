import { Component } from '@angular/core';
import { BoxplotProps } from '@sagebionetworks/shared/charts';
import { render, screen } from '@testing-library/angular';
import { BoxplotDirective } from './boxplot.directive';

const renderTestComponent = async (props: BoxplotProps) => {
  @Component({
    template: `<div
      sageBoxplot
      [points]="points"
      [summaries]="summaries"
      [title]="title"
      [xAxisTitle]="xAxisTitle"
      [yAxisTitle]="yAxisTitle"
      [yAxisMin]="yAxisMin"
      [yAxisMax]="yAxisMax"
      [xAxisCategoryToTooltipText]="xAxisCategoryToTooltipText"
      [pointTooltipFormatter]="pointTooltipFormatter"
    ></div>`,
  })
  class TestComponent {
    points = props.points;
    summaries = props.summaries;
    title = props.title;
    xAxisTitle = props.xAxisTitle;
    yAxisTitle = props.yAxisTitle;
    yAxisMin = props.yAxisMin;
    yAxisMax = props.yAxisMax;
    xAxisCategoryToTooltipText = props.xAxisCategoryToTooltipText;
    pointTooltipFormatter = props.pointTooltipFormatter;
  }

  await render(TestComponent, {
    imports: [BoxplotDirective],
  });
};

describe('BoxplotDirective', () => {
  it('should render chart when no data is passed', async () => {
    await renderTestComponent({ points: [] });

    // TODO - handle testing a canvas element
    expect(screen.getByText('No data is currently available.')).toBeVisible();
  });
});
