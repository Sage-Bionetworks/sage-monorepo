import { Component, signal } from '@angular/core';
import {
  CandlestickChart,
  CandlestickItem,
  candlestickItems,
} from '@sagebionetworks/explorers/charts';
import { render, screen } from '@testing-library/angular';
import { CandlestickDirective } from './candlestick.directive';

const renderTestComponent = async (items: CandlestickItem[]) => {
  @Component({
    imports: [CandlestickDirective],
    template: `<div sageCandlestick [items]="items"></div>`,
  })
  class TestComponent {
    items = items;
  }

  return await render(TestComponent);
};

describe('CandlestickDirective', () => {
  it('should render no data placeholder when items is empty', async () => {
    await renderTestComponent([]);
    expect(screen.getByLabelText('No data is currently available.')).toBeVisible();
  });

  it('should render chart container with aria label when items are provided', async () => {
    await renderTestComponent(candlestickItems);
    const chart = document.querySelector('[aria-label]');
    expect(chart).not.toBeNull();
    expect(chart?.getAttribute('aria-label')).not.toBe('');
  });

  it('should call setOptions with updated items when items input changes', async () => {
    @Component({
      imports: [CandlestickDirective],
      template: `<div sageCandlestick [items]="items()"></div>`,
    })
    class UpdatingTestComponent {
      items = signal<CandlestickItem[]>(candlestickItems);
    }

    const setOptionsSpy = jest.spyOn(CandlestickChart.prototype, 'setOptions');
    const { fixture } = await render(UpdatingTestComponent);
    setOptionsSpy.mockClear();

    fixture.componentInstance.items.set([]);
    fixture.detectChanges();

    expect(setOptionsSpy).toHaveBeenCalledWith(expect.objectContaining({ items: [] }));

    setOptionsSpy.mockRestore();
  });
});
