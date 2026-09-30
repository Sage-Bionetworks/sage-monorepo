import { captureDomToBlob } from '@sagebionetworks/explorers/util';
import { render } from '@testing-library/angular';
import { DownloadDomImageComponent } from './download-dom-image.component';
import { MessageService } from 'primeng/api';

jest.mock('@sagebionetworks/explorers/util', () => ({
  ...jest.requireActual('@sagebionetworks/explorers/util'),
  captureDomToBlob: jest.fn(),
}));

describe('DownloadDomImageComponent', () => {
  async function setup(inputs?: Partial<DownloadDomImageComponent>) {
    const mockElement = {
      offsetWidth: 100,
      offsetHeight: 100,
    } as HTMLElement;

    const component = await render(DownloadDomImageComponent, {
      componentInputs: {
        target: mockElement,
        filename: 'test-file',
        ...inputs,
      },
      providers: [MessageService],
    });

    const instance = component.fixture.componentInstance;
    return { component, instance };
  }

  it('should create', async () => {
    const { instance } = await setup();
    expect(instance).toBeTruthy();
  });

  it('should throw when the plot capture fails, so the popover stays open', async () => {
    (captureDomToBlob as jest.Mock).mockResolvedValue(null);
    const mockElement = { offsetWidth: 100, offsetHeight: 100 } as HTMLElement;
    const { fixture } = await render(DownloadDomImageComponent, {
      componentInputs: { target: mockElement, filename: 'test-file' },
      providers: [MessageService],
    });

    await expect(fixture.componentInstance.performDownload('.png')).rejects.toThrow(
      'Failed to capture the plot image for download: test-file',
    );
  });
});
