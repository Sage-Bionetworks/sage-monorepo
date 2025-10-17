import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { SvgIconService, SynapseApiService } from '@sagebionetworks/explorers/services';
import {
  mockSvgTestId,
  provideLoadingIconColors,
  SvgIconServiceStub,
  synapseWikiMock,
} from '@sagebionetworks/explorers/testing';
import { render, screen, waitFor } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { ButtonModule } from 'primeng/button';
import { PopoverModule } from 'primeng/popover';
import { of } from 'rxjs';
import { PopoverLinkComponent } from './popover-link.component';

const defaultWikiParams = { ownerId: 'syn618276', wikiId: '618276' };
const mockRenderedHtml = 'Some wiki content';
const mockSynapseApiService = { getWikiMarkdown: jest.fn(), renderHtml: jest.fn() };
mockSynapseApiService.getWikiMarkdown.mockReturnValue(of(synapseWikiMock));
mockSynapseApiService.renderHtml.mockReturnValue(mockRenderedHtml);

async function setup(wikiParams = defaultWikiParams) {
  const user = userEvent.setup();
  const component = await render(PopoverLinkComponent, {
    providers: [
      provideHttpClient(),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
      provideNoopAnimations(),
      ...provideLoadingIconColors(),
    ],
    componentInputs: {
      wikiParams: wikiParams,
    },
    imports: [ButtonModule, PopoverLinkComponent, PopoverModule],
    componentProviders: [{ provide: SynapseApiService, useValue: mockSynapseApiService }],
  });
  return { user, component };
}

describe('Component: Popover Link', () => {
  it('should create', async () => {
    await setup();
    const icon = screen.getByTestId(mockSvgTestId);
    expect(icon).toBeVisible();
  });

  it('should open and close popover on click', async () => {
    const { user } = await setup();

    const button = screen.getByRole('button');
    await user.click(button);

    await waitFor(() => {
      expect(mockSynapseApiService.getWikiMarkdown).toHaveBeenCalledTimes(1);
      expect(mockSynapseApiService.renderHtml).toHaveBeenCalledTimes(1);
    });

    const dialogContent = await screen.findByText(mockRenderedHtml);
    expect(dialogContent).toBeInTheDocument();

    await user.click(button);
    await waitFor(() => {
      expect(dialogContent).not.toBeInTheDocument();
    });
  });
});
