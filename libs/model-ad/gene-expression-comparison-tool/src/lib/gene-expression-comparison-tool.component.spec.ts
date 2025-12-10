import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { provideRouter } from '@angular/router';
import { ComparisonToolComponent } from '@sagebionetworks/explorers/comparison-tool';
import {
  PlatformService,
  provideComparisonToolFilterService,
  provideComparisonToolService,
} from '@sagebionetworks/explorers/services';
import { provideLoadingIconColors } from '@sagebionetworks/explorers/testing';
import { ComparisonToolConfigService } from '@sagebionetworks/model-ad/api-client';
import { MODEL_AD_LOADING_ICON_COLORS } from '@sagebionetworks/model-ad/config';
import { render } from '@testing-library/angular';
import { MessageService } from 'primeng/api';
import { of } from 'rxjs';
import { GeneExpressionComparisonToolComponent } from './gene-expression-comparison-tool.component';
import { GeneExpressionComparisonToolService } from './services/gene-expression-comparison-tool.service';

async function setup() {
  const { fixture } = await render(GeneExpressionComparisonToolComponent, {
    imports: [ComparisonToolComponent],
    providers: [
      MessageService,
      provideLoadingIconColors(MODEL_AD_LOADING_ICON_COLORS),
      provideHttpClient(),
      provideNoopAnimations(),
      provideRouter([]),
      {
        provide: PlatformService,
        useValue: { isBrowser: true },
      },
      {
        provide: ComparisonToolConfigService,
        useValue: {
          getComparisonToolConfig: jest.fn().mockReturnValue(of([])),
        },
      },
      ...provideComparisonToolService(),
      ...provideComparisonToolFilterService(),
      GeneExpressionComparisonToolService,
    ],
  });

  const component = fixture.componentInstance;
  return { component };
}

describe('GeneExpressionComparisonToolComponent', () => {
  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should cleanup effects on destroy', async () => {
    const { component } = await setup();

    const pinnedEffectDestroySpy = jest.spyOn(
      (component as any).onPinnedDataUpdateEffectRef,
      'destroy',
    );
    const unpinnedEffectDestroySpy = jest.spyOn(
      (component as any).onUnpinnedDataUpdateEffectRef,
      'destroy',
    );

    component.ngOnDestroy();

    expect(pinnedEffectDestroySpy).toHaveBeenCalled();
    expect(unpinnedEffectDestroySpy).toHaveBeenCalled();
  });
});
