import { Component } from '@angular/core';
import { LegendProps, mockPointStyles } from '@sagebionetworks/explorers/charts';
import { render } from '@testing-library/angular';
import { LegendDirective } from './legend.directive';

const renderTestComponent = async (props: LegendProps) => {
  @Component({
    imports: [LegendDirective],
    template: `<div sageLegend [pointStyles]="pointStyles"></div>`,
  })
  class TestComponent {
    pointStyles = props.pointStyles;
  }

  return await render(TestComponent, {
    imports: [LegendDirective],
  });
};

describe('LegendDirective', () => {
  it('should create with empty array', async () => {
    const { fixture } = await renderTestComponent({ pointStyles: [] });
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should create with data', async () => {
    const { fixture } = await renderTestComponent({ pointStyles: mockPointStyles });
    expect(fixture.componentInstance).toBeTruthy();
  });
});
