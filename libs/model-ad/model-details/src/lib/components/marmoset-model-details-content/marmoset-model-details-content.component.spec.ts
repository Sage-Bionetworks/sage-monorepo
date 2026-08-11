import { PlatformService } from '@sagebionetworks/explorers/services';
import { MockWikiComponent } from '@sagebionetworks/explorers/testing';
import { MarmosetModel } from '@sagebionetworks/model-ad/api-client';
import { marmosetModelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { MarmosetModelDetailsContentComponent } from './marmoset-model-details-content.component';
import { getPanels, getPanelsWithDisabledState } from './marmoset-model-details-panels';

async function setup(model: MarmosetModel = marmosetModelMock, activePanel = 'biomarkers') {
  return render(MarmosetModelDetailsContentComponent, {
    imports: [MockWikiComponent],
    componentInputs: {
      model,
      panels: getPanelsWithDisabledState(model, getPanels()),
      activePanel,
      activeParent: '',
      scrollToPanelNavElementOnInitialLoad: false,
    },
    providers: [{ provide: PlatformService, useValue: { isBrowser: true, isServer: false } }],
  });
}

describe('MarmosetModelDetailsContentComponent', () => {
  it('should display the model name', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(marmosetModelMock.name);
  });

  it('should display the Plasma Biomarkers tab', async () => {
    await setup();
    expect(screen.getByRole('button', { name: 'Plasma Biomarkers' })).toBeInTheDocument();
  });

  it('should render the Plasma Biomarkers selector for the marmoset model', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 2, name: 'Plasma Biomarkers' })).toBeVisible();
    expect(screen.getByText(`${marmosetModelMock.name} marmosets`, { exact: false })).toBeVisible();
  });

  it('should hide the Plasma Biomarkers tab when there is no biomarker data', async () => {
    await setup({ ...marmosetModelMock, biomarkers: [] });
    expect(screen.queryByRole('button', { name: 'Plasma Biomarkers' })).not.toBeInTheDocument();
  });

  it('should display the Resources tab', async () => {
    await setup();
    expect(screen.getByRole('button', { name: 'Resources' })).toBeInTheDocument();
  });

  it('should display all additional resource cards on the resources panel', async () => {
    await setup(marmosetModelMock, 'resources');

    expect(screen.getByText('Additional Resources')).toBeInTheDocument();
    expect(screen.getByText(/available for this model in the ad knowledge portal/i)).toBeVisible();
    expect(screen.getByText(/human genes in ad/i)).toBeVisible();
    expect(screen.getByText(/visit alzforum to find more information/i)).toBeVisible();
    expect(screen.getByText(/marmo-ad program/i)).toBeVisible();
    expect(screen.getByText(/marmo-ad tissue biobank/i)).toBeVisible();
    expect(screen.getByText(/marmoset resources and events/i)).toBeVisible();
    expect(screen.getByText(/marmosets in neuroscience research/i)).toBeVisible();
    expect(screen.getByText(/primate aging database/i)).toBeVisible();
  });

  it('should not display a model-specific resources section on the resources panel', async () => {
    await setup(marmosetModelMock, 'resources');
    expect(screen.queryByText('Model-Specific Resources')).not.toBeInTheDocument();
  });
});
