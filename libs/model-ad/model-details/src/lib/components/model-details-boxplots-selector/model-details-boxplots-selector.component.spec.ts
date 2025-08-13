import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import {
  MockWikiComponent,
  SvgIconServiceStub,
  validWikiParams,
} from '@sagebionetworks/explorers/testing';
import { IndividualData } from '@sagebionetworks/model-ad/api-client-angular';
import { modelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen, waitFor } from '@testing-library/angular';
import { ModelDetailsBoxplotsSelectorComponent } from './model-details-boxplots-selector.component';

const mockTitle = 'Pathology';

async function setup(model = modelMock, title = mockTitle, wikiParams = validWikiParams) {
  const { fixture } = await render(ModelDetailsBoxplotsSelectorComponent, {
    imports: [MockWikiComponent],
    componentInputs: {
      title: title,
      modelName: model.model,
      modelControls: model.matched_controls,
      modelDataList: model.pathology,
      wikiParams: wikiParams,
    },
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
  });
  const component = fixture.componentInstance;
  return { component };
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

  it('should use default values for sex and tissue', async () => {
    await setup();

    const sexFilter = await screen.findByRole('combobox', { name: 'Female & Male' });
    expect(sexFilter).toBeVisible();

    const tissueFilter = await screen.findByRole('combobox', {
      name: modelMock.pathology[0].tissue,
    });
    expect(tissueFilter).toBeVisible();
  });

  it('should return the expected genotype order', async () => {
    const basePt = { sex: IndividualData.SexEnum.Male, individual_id: '1', value: 100 };
    const mockModelDataList = [
      {
        model: 'ModelName',
        evidence_type: 'Test',
        tissue: 'Brain',
        age: '4 months',
        units: 'test',
        data: [
          { ...basePt, genotype: 'ModelName' },
          { ...basePt, genotype: 'NewGenotype1' },
          { ...basePt, genotype: 'Control2' },
          { ...basePt, genotype: 'Control1' },
          { ...basePt, genotype: 'NewGenotype2' },
        ],
      },
      {
        model: 'ModelName',
        evidence_type: 'Test2',
        tissue: 'Brain',
        age: '4 months',
        units: 'test',
        data: [
          { ...basePt, genotype: 'NewGenotype3' },
          { ...basePt, genotype: 'ModelName' },
        ],
      },
    ];

    const { component } = await setup(
      {
        ...modelMock,
        model: 'ModelName',
        matched_controls: ['Control1', 'Control2'],
        pathology: mockModelDataList,
      },
      mockTitle,
      validWikiParams,
    );

    await waitFor(() => {
      expect(component.genotypeOrder()).toEqual([
        'Control1',
        'Control2',
        'ModelName',
        'NewGenotype1',
        'NewGenotype2',
        'NewGenotype3',
      ]);
    });
  });

  it('should convert evidence type to anchor id', async () => {
    const { component } = await setup();
    expect(component.generateAnchorId('Amyloid Beta')).toBe('amyloid-beta');
    expect(component.generateAnchorId('A&beta;42')).toBe('a-beta-42');
    expect(component.generateAnchorId('Tau-pS396')).toBe('tau-ps396');
  });
});
