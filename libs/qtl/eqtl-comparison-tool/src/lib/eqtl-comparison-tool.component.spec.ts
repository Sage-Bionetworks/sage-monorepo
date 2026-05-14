import { render } from '@testing-library/angular';
import { EqtlComparisonToolComponent } from './eqtl-comparison-tool.component';

async function setup() {
  const { fixture } = await render(EqtlComparisonToolComponent);

  const component = fixture.componentInstance;
  return { component, fixture };
}

describe('EqtlComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });
});
