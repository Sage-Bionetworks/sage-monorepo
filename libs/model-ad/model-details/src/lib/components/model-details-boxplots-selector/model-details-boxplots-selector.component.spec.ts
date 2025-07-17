import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import {
  MockWikiComponent,
  SvgIconServiceStub,
  validWikiParams,
} from '@sagebionetworks/explorers/testing';
import { modelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen, waitFor } from '@testing-library/angular';
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

  it('should automatically select first tissue when data loads', async () => {
    await setup();
    const tissueSelect = screen.getByRole('combobox', { name: /tissue/i });

    const firstTissue = modelMock.pathology[0].tissue;
    await waitFor(() => {
      expect(tissueSelect).toHaveAccessibleName(firstTissue);
    });
  });

  it('should use default value for sex', async () => {
    await setup();
    const sexSelect = screen.getByRole('combobox', { name: /sex/i });

    await waitFor(() => {
      expect(sexSelect).toHaveAccessibleName('Male & Female');
    });
  });
});
