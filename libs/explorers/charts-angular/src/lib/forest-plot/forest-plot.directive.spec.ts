import { Component, signal } from '@angular/core';
import {
  ForestPlotChart,
  ForestPlotItem,
  forestPlotItems,
} from '@sagebionetworks/explorers/charts';
import { render, screen } from '@testing-library/angular';
import { ForestPlotDirective } from './forest-plot.directive';

const renderTestComponent = async (items: ForestPlotItem[]) => {
  @Component({
    imports: [ForestPlotDirective],
    template: `<div sageForestPlot [items]="items"></div>`,
  })
  class TestComponent {
    items = items;
  }

  return await render(TestComponent);
};

describe('ForestPlotDirective', () => {
  it('should render no data placeholder when items is empty', async () => {
    await renderTestComponent([]);
    expect(screen.getByLabelText('No data is currently available.')).toBeVisible();
  });

  it('should render chart container with aria label when items are provided', async () => {
    await renderTestComponent(forestPlotItems);
    const chart = document.querySelector('[aria-label]');
    expect(chart).not.toBeNull();
    expect(chart?.getAttribute('aria-label')).not.toBe('');
  });

  it('should call setOptions with updated items when items input changes', async () => {
    @Component({
      imports: [ForestPlotDirective],
      template: `<div sageForestPlot [items]="items()"></div>`,
    })
    class UpdatingTestComponent {
      items = signal<ForestPlotItem[]>(forestPlotItems);
    }

    const setOptionsSpy = jest.spyOn(ForestPlotChart.prototype, 'setOptions');
    const { fixture } = await render(UpdatingTestComponent);
    setOptionsSpy.mockClear();

    fixture.componentInstance.items.set([]);
    fixture.detectChanges();

    expect(setOptionsSpy).toHaveBeenCalledWith(expect.objectContaining({ items: [] }));

    setOptionsSpy.mockRestore();
  });
});
