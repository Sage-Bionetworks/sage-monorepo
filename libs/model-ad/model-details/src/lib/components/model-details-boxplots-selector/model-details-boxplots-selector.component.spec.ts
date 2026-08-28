import { provideHttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { ComponentFixture } from '@angular/core/testing';
import { SvgIconService } from '@sagebionetworks/explorers/services';
import {
  MockWikiComponent,
  SvgIconServiceStub,
  validWikiParams,
} from '@sagebionetworks/explorers/testing';
import { ModelData, Sex } from '@sagebionetworks/model-ad/api-client';
import { mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { BoxplotsGridComponent } from '@sagebionetworks/model-ad/ui';
import { fireEvent, render, screen, waitFor } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import {
  ANCHOR_HIGHLIGHT_HOLD_MS,
  FilterConfig,
  ModelDetailsBoxplotsSelectorComponent,
} from './model-details-boxplots-selector.component';

// Longer default timeout for all tests; PrimeNG rendering can be slow in CI
const TEST_TIMEOUT_MS = 15000;
jest.setTimeout(TEST_TIMEOUT_MS);

const mockTitle = 'Pathology';
const mockDescription = 'Test description';
const mouseFilterConfig: FilterConfig = {
  label: 'Tissue',
  queryParamKey: 'tissue',
  dataField: 'tissue',
};

// Host component that provides a sectionTemplate, required by the base component
@Component({
  standalone: true,
  imports: [ModelDetailsBoxplotsSelectorComponent, BoxplotsGridComponent],
  template: `
    <ng-template #section let-data="data" let-sexFilter="sexFilter">
      <model-ad-boxplots-grid [boxplotDataList]="data" [sexFilter]="sexFilter" />
    </ng-template>
    <model-ad-model-details-boxplots-selector
      [title]="title"
      [description]="description"
      [modelName]="modelName"
      [modelDataList]="modelDataList"
      [wikiParams]="wikiParams"
      [filterConfig]="filterConfig"
      [anchorDataField]="anchorDataField"
      [sectionTemplate]="section"
    />
  `,
})
class TestHostComponent {
  title = mockTitle;
  description = mockDescription;
  modelName = mouseModelMock.name;
  modelDataList: ModelData[] = mouseModelMock.pathology;
  wikiParams = validWikiParams[0];
  filterConfig: FilterConfig = mouseFilterConfig;
  anchorDataField: keyof ModelData = 'evidence_type';
}

async function setupHost(overrides: Partial<TestHostComponent> = {}) {
  const user = userEvent.setup();
  const { fixture } = await render(TestHostComponent, {
    imports: [MockWikiComponent],
    componentInputs: overrides,
    providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
  });
  return { fixture, host: fixture.componentInstance, user };
}

function getBaseComponent(fixture: ComponentFixture<TestHostComponent>) {
  return fixture.debugElement.children[0]
    .componentInstance as ModelDetailsBoxplotsSelectorComponent;
}

async function setupWithExpandedToc(overrides: Partial<TestHostComponent> = {}) {
  const { fixture, user } = await setupHost(overrides);
  const base = getBaseComponent(fixture);
  base.isTocCollapsed.set(false);
  fixture.detectChanges();

  const scrollToSpy = jest.spyOn(window, 'scrollTo').mockImplementation(() => undefined);

  return { fixture, base, user, scrollToSpy };
}

function getTocLink(name: string) {
  return screen.getByRole('link', { name });
}

function getSectionHeading(name: string) {
  return screen.getByRole('heading', { level: 2, name });
}

describe('ModelDetailsBoxplotsSelectorComponent', () => {
  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('should render title', async () => {
    await setupHost();
    expect(screen.getByRole('heading', { level: 2, name: mockTitle })).toBeVisible();
  });

  it('should render description', async () => {
    await setupHost();
    expect(screen.getByText(mockDescription)).toBeVisible();
  });

  it('should render filters', async () => {
    await setupHost();
    expect(screen.getByRole('combobox', { name: /tissue/i })).toBeVisible();
    expect(screen.getByRole('combobox', { name: /sex/i })).toBeVisible();
  });

  it('should use default values for sex and filter', async () => {
    await setupHost();

    // Use longer timeout for CI environments where PrimeNG may take longer to render
    const sexFilter = await screen.findByRole(
      'combobox',
      { name: 'Female & Male' },
      { timeout: 10000 },
    );
    expect(sexFilter).toBeVisible();

    const tissueFilter = await screen.findByRole(
      'combobox',
      { name: mouseModelMock.pathology[0].tissue as string },
      { timeout: 10000 },
    );
    expect(tissueFilter).toBeVisible();
  });

  it('should convert label to anchor id', async () => {
    const { fixture } = await setupHost();
    const base = getBaseComponent(fixture);
    expect(base.generateAnchorId('Amyloid Beta')).toBe('amyloid-beta');
    expect(base.generateAnchorId('Insoluble A&beta;42')).toBe('insoluble-abeta42');
    expect(base.generateAnchorId('Tau-pS396')).toBe('tau-ps396');
    expect(base.generateAnchorId('Astrocyte Cell Density (GFAP)')).toBe(
      'astrocyte-cell-density-gfap',
    );
  });

  it('should decode HTML entities correctly', async () => {
    const { fixture } = await setupHost();
    const base = getBaseComponent(fixture);
    expect(base.decodeHtmlEntities('A&beta;42')).toBe('Abeta42');
    expect(base.decodeHtmlEntities('&alpha;-&gamma; test')).toBe('alpha-gamma test');
    expect(base.decodeHtmlEntities('no entities here')).toBe('no entities here');
    expect(base.decodeHtmlEntities('Insoluble A&beta;40')).toBe('Insoluble Abeta40');
  });

  it('should generate boxplots filename correctly', async () => {
    const { fixture } = await setupHost();
    const base = getBaseComponent(fixture);
    expect(
      base.generateBoxplotsFilename(
        'Insoluble A&beta;40',
        'Cerebral Cortex',
        ['Female', 'Male'],
        '3xTg-AD',
      ),
    ).toBe('3xTg-AD_Insoluble_Abeta40_Cerebral_Cortex_Female_Male');
    expect(
      base.generateBoxplotsFilename(
        'Dystrophic Neurites (LAMP1)',
        'Hippocampus',
        ['Male'],
        '5xFAD (UCI)',
      ),
    ).toBe('5xFAD_(UCI)_Dystrophic_Neurites_(LAMP1)_Hippocampus_Male');
    expect(
      base.generateBoxplotsFilename(
        'Plaque Size (Thio-S)',
        'Hippocampus',
        ['Female'],
        'Abca7*V1599M',
      ),
    ).toBe('Abca7_V1599M_Plaque_Size_(Thio-S)_Hippocampus_Female');
  });

  it('should not duplicate evidenceType in filename when it matches filterValue', async () => {
    const { fixture } = await setupHost();
    const base = getBaseComponent(fixture);
    expect(
      base.generateBoxplotsFilename(
        'Soluble A&beta;40',
        'Soluble A&beta;40',
        ['Female', 'Male'],
        'APOE4-KI',
      ),
    ).toBe('APOE4-KI_Soluble_Abeta40_Female_Male');
  });

  it('should generate CSV data with tissue column when tissue is present', async () => {
    const mockModelDataList: ModelData[] = [
      {
        name: 'TestModel',
        evidence_type: 'NfL',
        tissue: 'Brain',
        age: '4 months',
        units: 'pg/mg',
        y_axis_max: 1000,
        data: [
          { sex: Sex.Female, individual_id: '100', value: 42.5, genotype: 'TestModel' },
          { sex: Sex.Male, individual_id: '101', value: 55.1, genotype: 'Control' },
        ],
      },
    ];

    const { fixture } = await render(TestHostComponent, {
      imports: [MockWikiComponent],
      componentProperties: { modelDataList: mockModelDataList },
      providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
    });
    const base = getBaseComponent(fixture);

    await waitFor(() => expect(base.selectedFilterOption()).toBe('Brain'));

    const csv = base.generateBoxplotsCsvData('NfL');
    expect(csv[0]).toEqual([
      'name',
      'evidence_type',
      'tissue',
      'age',
      'sex',
      'genotype',
      'individual_id',
      'value',
      'units',
    ]);
    expect(csv[1]).toEqual([
      'TestModel',
      'NfL',
      'Brain',
      '4 months',
      'Female',
      'TestModel',
      '100',
      '42.5',
      'pg/mg',
    ]);
    expect(csv[2]).toEqual([
      'TestModel',
      'NfL',
      'Brain',
      '4 months',
      'Male',
      'Control',
      '101',
      '55.1',
      'pg/mg',
    ]);
  });

  it('should generate CSV data without tissue column when tissue is absent', async () => {
    const mockModelDataList: ModelData[] = [
      {
        name: 'MarmosetModel',
        evidence_type: 'Soluble A40',
        age: '0-1 year',
        units: 'pg/ml',
        y_axis_max: 1000,
        data: [{ sex: Sex.Female, individual_id: '1', value: 10.0, genotype: 'PSEN1' }],
      },
    ];

    const { fixture } = await render(TestHostComponent, {
      imports: [MockWikiComponent],
      componentProperties: {
        modelDataList: mockModelDataList,
        filterConfig: {
          label: 'Measurement',
          queryParamKey: 'measurement',
          dataField: 'evidence_type',
        } as FilterConfig,
        anchorDataField: 'age' as keyof ModelData,
      },
      providers: [provideHttpClient(), { provide: SvgIconService, useClass: SvgIconServiceStub }],
    });
    const base = getBaseComponent(fixture);

    await waitFor(() => expect(base.selectedFilterOption()).toBe('Soluble A40'));

    const csv = base.generateBoxplotsCsvData('Soluble A40');
    expect(csv[0]).toEqual([
      'name',
      'evidence_type',
      'age',
      'sex',
      'genotype',
      'individual_id',
      'value',
      'units',
    ]);
    expect(csv[0]).not.toContain('tissue');
  });

  it('should generate boxplots zip filename correctly', async () => {
    const { fixture } = await setupHost();
    const base = getBaseComponent(fixture);
    expect(
      base.generateBoxplotsZipFilename(
        'Cerebral Cortex',
        ['Female', 'Male'],
        'Abca7*V1599M',
        'Pathology',
      ),
    ).toBe('Abca7_V1599M_Cerebral_Cortex_Female_Male_Pathology');
    expect(
      base.generateBoxplotsZipFilename(
        'Cerebral Cortex',
        ['Female', 'Male'],
        'Abca7*V1599M',
        'Biomarkers',
      ),
    ).toBe('Abca7_V1599M_Cerebral_Cortex_Female_Male_Biomarkers');
  });

  it('should populate TOC from anchorDataField', async () => {
    const { user } = await setupHost();

    const expandButton = screen.getByRole('button', { name: 'Expand' });
    await user.click(expandButton);

    await waitFor(() => {
      expect(screen.getAllByRole('listitem').length).toBeGreaterThan(0);
    });
  });

  describe('collapsible TOC', () => {
    it('should start collapsed with expand button and no TOC items visible', async () => {
      await setupHost();

      const expandButton = screen.getByRole('button', { name: 'Expand' });
      expect(expandButton).toBeVisible();

      const tocList = screen.queryByRole('list');
      expect(tocList).not.toBeInTheDocument();
    });

    it('should show TOC items when expand button is clicked', async () => {
      const { user } = await setupHost();

      const expandButton = screen.getByRole('button', { name: 'Expand' });
      await user.click(expandButton);

      await waitFor(() => {
        const collapseButton = screen.getByRole('button', { name: 'Collapse' });
        expect(collapseButton).toBeVisible();

        const tocList = screen.getByRole('list');
        expect(tocList).toBeVisible();
      });
    });

    it('should hide TOC items when collapse button is clicked', async () => {
      const { user } = await setupHost();

      const expandButton = screen.getByRole('button', { name: 'Expand' });
      await user.click(expandButton);

      await waitFor(() => {
        expect(screen.getByRole('list')).toBeVisible();
      });

      const collapseButton = screen.getByRole('button', { name: 'Collapse' });
      await user.click(collapseButton);

      await waitFor(() => {
        const expandButtonAgain = screen.getByRole('button', { name: 'Expand' });
        expect(expandButtonAgain).toBeVisible();

        const tocList = screen.queryByRole('list');
        expect(tocList).not.toBeInTheDocument();
      });
    });
  });

  describe('TOC link navigation', () => {
    it('should render TOC items as links to the current page plus their section anchor', async () => {
      const { base } = await setupWithExpandedToc();
      const evidenceType = base.evidenceTypes()[0];

      expect(getTocLink(evidenceType)).toHaveAttribute(
        'href',
        `${window.location.pathname}#${base.generateAnchorId(evidenceType)}`,
      );
    });

    it('should move focus to the section heading when a TOC link is clicked', async () => {
      const { base, user } = await setupWithExpandedToc();
      const evidenceType = base.evidenceTypes()[0];

      await user.click(getTocLink(evidenceType));

      expect(getSectionHeading(evidenceType)).toHaveFocus();
    });

    it('should leave a modified click to the browser so the link can open in a new tab', async () => {
      const { base, scrollToSpy } = await setupWithExpandedToc();
      const evidenceType = base.evidenceTypes()[0];
      scrollToSpy.mockClear();

      fireEvent.click(getTocLink(evidenceType), { metaKey: true });

      expect(scrollToSpy).not.toHaveBeenCalled();
      expect(getSectionHeading(evidenceType)).not.toHaveFocus();
    });
  });

  describe('anchor highlight', () => {
    async function setupWithFakeTimers() {
      const setup = await setupWithExpandedToc();
      // PrimeNG rendering needs real timers, so switch to fake timers only after render()
      jest.useFakeTimers();
      return setup;
    }

    afterEach(() => {
      jest.useRealTimers();
    });

    it('should highlight the TOC link and section heading of the scrolled-to anchor', async () => {
      const { fixture, base } = await setupWithFakeTimers();
      const evidenceType = base.evidenceTypes()[0];

      base.scrollToSection(base.generateAnchorId(evidenceType));
      fixture.detectChanges();

      expect(base.highlightedAnchorId()).toBe(base.generateAnchorId(evidenceType));
      expect(getTocLink(evidenceType)).toHaveClass('highlighted');
      expect(getSectionHeading(evidenceType)).toHaveClass('highlighted');
    });

    it('should clear the highlight after the hold duration', async () => {
      const { fixture, base } = await setupWithFakeTimers();
      const evidenceType = base.evidenceTypes()[0];

      base.scrollToSection(base.generateAnchorId(evidenceType));
      fixture.detectChanges();

      jest.advanceTimersByTime(ANCHOR_HIGHLIGHT_HOLD_MS);
      fixture.detectChanges();

      expect(base.highlightedAnchorId()).toBe('');
      expect(getTocLink(evidenceType)).not.toHaveClass('highlighted');
      expect(getSectionHeading(evidenceType)).not.toHaveClass('highlighted');
    });

    it('should move the highlight to the newly scrolled-to anchor', async () => {
      const { fixture, base } = await setupWithFakeTimers();
      const [firstEvidenceType, secondEvidenceType] = base.evidenceTypes();

      base.scrollToSection(base.generateAnchorId(firstEvidenceType));
      fixture.detectChanges();

      base.scrollToSection(base.generateAnchorId(secondEvidenceType));
      fixture.detectChanges();

      expect(getSectionHeading(firstEvidenceType)).not.toHaveClass('highlighted');
      expect(getSectionHeading(secondEvidenceType)).toHaveClass('highlighted');
    });

    it('should restart the hold on the newly scrolled-to anchor rather than inheriting the previous deadline', async () => {
      const partialHoldMs = ANCHOR_HIGHLIGHT_HOLD_MS / 2;
      const { fixture, base } = await setupWithFakeTimers();
      const [firstEvidenceType, secondEvidenceType] = base.evidenceTypes();

      base.scrollToSection(base.generateAnchorId(firstEvidenceType));
      fixture.detectChanges();

      jest.advanceTimersByTime(partialHoldMs);
      base.scrollToSection(base.generateAnchorId(secondEvidenceType));
      fixture.detectChanges();

      jest.advanceTimersByTime(partialHoldMs);
      fixture.detectChanges();
      expect(getSectionHeading(secondEvidenceType)).toHaveClass('highlighted');

      jest.advanceTimersByTime(partialHoldMs);
      fixture.detectChanges();
      expect(getSectionHeading(secondEvidenceType)).not.toHaveClass('highlighted');
    });
  });
});
