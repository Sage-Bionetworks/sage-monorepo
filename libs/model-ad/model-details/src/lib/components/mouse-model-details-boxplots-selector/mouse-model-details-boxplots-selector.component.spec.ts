import { provideHttpClient } from '@angular/common/http';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import {
  MockWikiComponent,
  SvgIconServiceStub,
  validWikiParams,
} from '@sagebionetworks/explorers/testing';
import { ModelData, Sex } from '@sagebionetworks/model-ad/api-client';
import { mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen, waitFor } from '@testing-library/angular';
import { MouseModelDetailsBoxplotsSelectorComponent } from './mouse-model-details-boxplots-selector.component';

async function setup() {
  const { fixture } = await render(MouseModelDetailsBoxplotsSelectorComponent, {
    imports: [MockWikiComponent],
    componentInputs: {
      title: 'Pathology',
      modelName: mouseModelMock.name,
      modelControls: mouseModelMock.matched_controls,
      modelDataList: mouseModelMock.pathology,
      wikiParams: validWikiParams,
    },
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
  });
  const component = fixture.componentInstance;
  return { component, fixture };
}

describe('MouseModelDetailsBoxplotsSelectorComponent', () => {
  it('should render title', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 2, name: 'Pathology' })).toBeVisible();
  });

  it('should render description with model name', async () => {
    await setup();
    expect(
      screen.getByText(
        `The results shown on this page are derived from the analysis of post-mortem brains from ${mouseModelMock.name} mice and matched control animals.`,
      ),
    ).toBeVisible();
  });

  it('should render tissue dropdown', async () => {
    await setup();
    expect(screen.getByRole('combobox', { name: /tissue/i })).toBeVisible();
  });

  it('should render sex dropdown', async () => {
    await setup();
    expect(screen.getByRole('combobox', { name: /sex/i })).toBeVisible();
  });

  it('should compute genotype order placing controls before model name', async () => {
    const basePt = { sex: Sex.Male, individual_id: '1', value: 100 };
    const mockModelDataList: ModelData[] = [
      {
        name: 'ModelName',
        evidence_type: 'Test',
        tissue: 'Brain',
        age: '4 months',
        units: 'test',
        y_axis_max: 2000.0,
        data: [
          { ...basePt, genotype: 'ModelName' },
          { ...basePt, genotype: 'NewGenotype1' },
          { ...basePt, genotype: 'Control2' },
          { ...basePt, genotype: 'Control1' },
          { ...basePt, genotype: 'NewGenotype2' },
        ],
      },
      {
        name: 'ModelName',
        evidence_type: 'Test2',
        tissue: 'Brain',
        age: '4 months',
        units: 'test',
        y_axis_max: 2000.0,
        data: [
          { ...basePt, genotype: 'NewGenotype3' },
          { ...basePt, genotype: 'ModelName' },
        ],
      },
    ];

    const { fixture } = await render(MouseModelDetailsBoxplotsSelectorComponent, {
      imports: [MockWikiComponent],
      componentInputs: {
        title: 'Pathology',
        modelName: 'ModelName (Some Qualifier)',
        modelControls: ['Control1', 'Control2'],
        modelDataList: mockModelDataList,
        wikiParams: validWikiParams,
      },
      providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
    });
    const component = fixture.componentInstance;

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
});
