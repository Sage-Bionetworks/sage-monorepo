import { provideHttpClient } from '@angular/common/http';
import { By } from '@angular/platform-browser';
import { LoggerService, SvgIconService } from '@sagebionetworks/explorers/services';
import {
  MockWikiComponent,
  SvgIconServiceStub,
  validWikiParams,
} from '@sagebionetworks/explorers/testing';
import { ModelData, Sex } from '@sagebionetworks/model-ad/api-client';
import { marmosetModelDataMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import {
  ANCHOR_HIGHLIGHT_HOLD_MS,
  ModelDetailsBoxplotsSelectorComponent,
} from '../model-details-boxplots-selector/model-details-boxplots-selector.component';
import { MarmosetModelDetailsBoxplotsSelectorComponent } from './marmoset-model-details-boxplots-selector.component';

const ageGroup = '0-1 year';

async function setup() {
  const { fixture } = await render(MarmosetModelDetailsBoxplotsSelectorComponent, {
    imports: [MockWikiComponent],
    componentInputs: {
      title: 'Plasma Biomarkers',
      modelName: 'Presenilin 1',
      modelDataList: marmosetModelDataMock,
      wikiParams: validWikiParams[0],
    },
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
  });
  return { fixture, component: fixture.componentInstance };
}

describe('MarmosetModelDetailsBoxplotsSelectorComponent', () => {
  it('should render title', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 2, name: 'Plasma Biomarkers' })).toBeVisible();
  });

  it('should render description with model name', async () => {
    await setup();
    expect(screen.getByText(/Presenilin 1 marmosets/, { exact: false })).toBeVisible();
  });

  it('should render measurement dropdown', async () => {
    await setup();
    expect(screen.getByRole('combobox', { name: /measurement/i })).toBeVisible();
  });

  it('should render sex dropdown', async () => {
    await setup();
    expect(screen.getByRole('combobox', { name: /sex/i })).toBeVisible();
  });

  it('should warn via LoggerService when an age group has more than one ModelData item', async () => {
    const duplicateAgeData: ModelData[] = [
      {
        name: 'PSEN1',
        evidence_type: 'Soluble Aβ40',
        age: '0-1 year',
        units: 'pg/ml',
        y_axis_max: 1000,
        data: [{ sex: Sex.Female, individual_id: '1', value: 350, genotype: 'Control' }],
      },
      {
        name: 'PSEN1',
        evidence_type: 'Soluble Aβ40',
        age: '0-1 year',
        units: 'pg/ml',
        y_axis_max: 1000,
        data: [{ sex: Sex.Male, individual_id: '2', value: 420, genotype: 'PSEN1' }],
      },
    ];

    const warnSpy = jest.fn();
    const { fixture } = await render(MarmosetModelDetailsBoxplotsSelectorComponent, {
      imports: [MockWikiComponent],
      componentInputs: {
        title: 'Plasma Biomarkers',
        modelName: 'Presenilin 1',
        modelDataList: duplicateAgeData,
        wikiParams: validWikiParams[0],
      },
      providers: [
        provideHttpClient(),
        { provide: SvgIconService, useClass: SvgIconServiceStub },
        { provide: LoggerService, useValue: { warn: warnSpy, log: jest.fn() } },
      ],
    });
    const component = fixture.componentInstance;

    component.transformSectionData(duplicateAgeData);

    expect(warnSpy).toHaveBeenCalledWith(
      expect.stringContaining('expected 1 ModelData per age group but got 2'),
    );
  });

  it('should highlight the age group heading of the highlighted anchor until the hold elapses', async () => {
    const { fixture, component } = await setup();
    const base = fixture.debugElement.query(By.directive(ModelDetailsBoxplotsSelectorComponent))
      .componentInstance as ModelDetailsBoxplotsSelectorComponent;

    const heading = screen.getByRole('heading', { level: 3, name: ageGroup });
    const anchorId = component.generateAnchorId(ageGroup);

    jest.useFakeTimers();

    base.highlightAnchor(anchorId);
    fixture.detectChanges();
    expect(heading).toHaveClass('highlighted');

    jest.advanceTimersByTime(ANCHOR_HIGHLIGHT_HOLD_MS);
    fixture.detectChanges();
    expect(heading).not.toHaveClass('highlighted');

    jest.useRealTimers();
  });
});
