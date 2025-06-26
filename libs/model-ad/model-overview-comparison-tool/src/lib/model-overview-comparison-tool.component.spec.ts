import { render } from '@testing-library/angular';
import { ModelOverviewComparisonToolComponent } from './model-overview-comparison-tool.component';
import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { MessageService } from 'primeng/api';
import { LOADING_ICON_COLORS_PROVIDER } from '@sagebionetworks/model-ad/testing';

async function setup() {
  const { fixture } = await render(ModelOverviewComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
    providers: [MessageService, LOADING_ICON_COLORS_PROVIDER],
  });

  const component = fixture.componentInstance;
  return { component };
}

describe('ModelOverviewComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });
});
