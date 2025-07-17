import { BaseComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tools';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { render } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { ModelOverviewComparisonToolComponent } from './model-overview-comparison-tool.component';
import { provideHttpClient } from '@angular/common/http';

async function setup() {
  const { fixture } = await render(ModelOverviewComparisonToolComponent, {
    imports: [BaseComparisonToolComponent],
    providers: [
      MessageService,
      provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
      provideHttpClient(),
    ],
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
