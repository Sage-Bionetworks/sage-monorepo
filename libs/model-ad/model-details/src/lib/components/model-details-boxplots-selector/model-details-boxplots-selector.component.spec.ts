import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import {
  MockWikiComponent,
  SvgIconServiceStub,
  validWikiParams,
} from '@sagebionetworks/explorers/testing';
import { modelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { ModelDetailsBoxplotsSelectorComponent } from './model-details-boxplots-selector.component';

const mockTitle = 'Pathology';

async function setup(model = modelMock, title = mockTitle, wikiParams = validWikiParams) {
  return render(ModelDetailsBoxplotsSelectorComponent, {
    imports: [MockWikiComponent],
    componentInputs: {
      title: title,
      modelName: model.model,
      modelDataList: model.pathology,
      wikiParams: wikiParams,
    },
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
  });
}

describe('ModelDetailsBoxplotsSelectorComponent', () => {
  it('should render title', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 2, name: mockTitle })).toBeVisible();
  });

  it('should render description with model name', async () => {
    await setup();
    expect(screen.getByText(modelMock.model, { exact: false })).toBeVisible();
  });

  it('should render filters', async () => {
    await setup();
    expect(screen.getByRole('combobox', { name: /tissue/i })).toBeVisible();
    expect(screen.getByRole('combobox', { name: /sex/i })).toBeVisible();
  });
});
