import { PlatformService } from '@sagebionetworks/explorers/services';
import { MouseModel } from '@sagebionetworks/model-ad/api-client';
import { mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { MouseModelDetailsContentComponent } from './mouse-model-details-content.component';
import { getPanels, getPanelsWithDisabledState } from './mouse-model-details-panels';

async function setup(model: MouseModel = mouseModelMock, activePanel = 'omics') {
  return render(MouseModelDetailsContentComponent, {
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

describe('MouseModelDetailsContentComponent', () => {
  it('should display the model name', async () => {
    await setup();
    expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent(mouseModelMock.name);
  });

  it('should display the hero', async () => {
    await setup();
    expect(screen.getByText(`Mouse model of ${mouseModelMock.model_type}`)).toBeInTheDocument();
  });

  it('should display all tabs for which the model has data', async () => {
    await setup();
    const tabs = ['Omics', 'Biomarkers', 'Pathology', 'Resources'];
    for (const tab of tabs) {
      expect(screen.getByText(tab)).toBeInTheDocument();
    }
  });

  it('should hide tabs for which the model does not have data', async () => {
    await setup({ ...mouseModelMock, biomarkers: [], pathology: [] });
    expect(screen.getByText('Omics')).toBeInTheDocument();
    expect(screen.queryByText('Biomarkers')).not.toBeInTheDocument();
    expect(screen.queryByText('Pathology')).not.toBeInTheDocument();
    expect(screen.getByText('Resources')).toBeInTheDocument();
  });

  it('should display all model-specific resource cards on the resources panel', async () => {
    await setup(mouseModelMock, 'resources');

    expect(screen.getByText('Model-Specific Resources')).toBeInTheDocument();
    expect(screen.getByText(/available for this model in the ad knowledge portal/i)).toBeVisible();
    expect(screen.getByText(/visit alzforum to find more information/i)).toBeVisible();
    expect(screen.getByText(/detailed information about this ad model on jax/i)).toBeVisible();
  });

  it('should display all additional resource cards on the resources panel', async () => {
    await setup(mouseModelMock, 'resources');

    expect(screen.getByText('Additional Resources')).toBeInTheDocument();
    expect(screen.getByText(/human genes in ad/i)).toBeInTheDocument();
    expect(screen.getByText(/allen brain atlas/i)).toBeInTheDocument();
    expect(screen.getByText(/model-ad program/i)).toBeInTheDocument();
    expect(screen.getByText(/mouse genome informatics/i)).toBeInTheDocument();
    expect(screen.getByText(/model-ad preclinical testing core/i)).toBeInTheDocument();
  });

  it('should not display alzforum card when alzforum_id is missing', async () => {
    await setup({ ...mouseModelMock, alzforum_id: '' }, 'resources');
    expect(screen.queryByText(/visit alzforum to find more information/i)).not.toBeInTheDocument();
  });
});
