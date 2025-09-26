import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { SvgIconService, SynapseApiService } from '@sagebionetworks/agora/services';
import { mockSvgTestId, SvgIconServiceStub, synapseWikiMock } from '@sagebionetworks/agora/testing';
import { render, screen, waitFor } from '@testing-library/angular';
import userEvent from '@testing-library/user-event';
import { ButtonModule } from 'primeng/button';
import { PopoverModule } from 'primeng/popover';
import { of } from 'rxjs';
import { PopoverLinkComponent } from './popover-link.component';

const mockRenderedHtml = 'Some wiki content';
const mockSynapseApiService = { getWiki: jest.fn(), renderHtml: jest.fn() };
mockSynapseApiService.getWiki.mockReturnValue(of(synapseWikiMock));
mockSynapseApiService.renderHtml.mockReturnValue(mockRenderedHtml);

async function setup(wikiId = '618276') {
  const user = userEvent.setup();
  const component = await render(PopoverLinkComponent, {
    providers: [
      provideHttpClient(),
      { provide: SvgIconService, useClass: SvgIconServiceStub },
      provideNoopAnimations(),
    ],
    componentProperties: {
      wikiId: wikiId,
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
      expect(mockSynapseApiService.getWiki).toHaveBeenCalledTimes(1);
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
