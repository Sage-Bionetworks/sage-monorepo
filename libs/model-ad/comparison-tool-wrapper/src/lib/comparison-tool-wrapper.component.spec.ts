import { render } from '@testing-library/angular';
import { ComparisonToolWrapperComponent } from './comparison-tool-wrapper.component';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';

async function setup() {
  const { fixture } = await render(ComparisonToolWrapperComponent, {
    imports: [BaseComparisonToolComponent],
  });

  const component = fixture.componentInstance;
  return { component };
}

describe('ComparisonToolWrapperComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });
});
