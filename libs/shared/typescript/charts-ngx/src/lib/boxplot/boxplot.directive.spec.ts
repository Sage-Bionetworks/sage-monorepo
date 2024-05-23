import { Component } from '@angular/core';
import { render, screen } from '@testing-library/angular';
import { BoxplotDirective } from './boxplot.directive';

const renderTestComponent = async (testText: string) => {
  @Component({
    template: `<p sageBoxplot [text]="'${testText}'">{{ text }}</p>`,
  })
  class TestComponent {}

  await render(TestComponent, {
    imports: [BoxplotDirective],
  });
};

describe('BoxplotDirective', () => {
  it('should render text', async () => {
    const testText = 'Hello, World!';
    await renderTestComponent(testText);

    expect(screen.getByText(testText)).toBeVisible();
  });
});
