import { PlatformService } from '@sagebionetworks/explorers/services';
import { MouseModel } from '@sagebionetworks/model-ad/api-client';
import { mouseModelMock } from '@sagebionetworks/model-ad/testing';
import { render, screen } from '@testing-library/angular';
import { MouseModelDetailsContentComponent } from './mouse-model-details-content.component';
import { getPanels, getPanelsWithDisabledState } from './mouse-model-details-panels';

async function setup(model: MouseModel = mouseModelMock) {
  return render(MouseModelDetailsContentComponent, {
    componentInputs: {
      model,
      panels: getPanelsWithDisabledState(model, getPanels()),
      activePanel: 'omics',
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
});
