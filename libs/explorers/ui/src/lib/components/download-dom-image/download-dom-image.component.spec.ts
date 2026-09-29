import { LoggerService } from '@sagebionetworks/explorers/services';
import { captureDomToBlob } from '@sagebionetworks/explorers/util';
import { render } from '@testing-library/angular';
import { DownloadDomImageComponent } from './download-dom-image.component';

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
    });

    const instance = component.fixture.componentInstance;
    return { component, instance };
  }

  it('should create', async () => {
    const { instance } = await setup();
    expect(instance).toBeTruthy();
  });

  it('should log a warning when the plot capture fails', async () => {
    (captureDomToBlob as jest.Mock).mockResolvedValue(null);
    const warn = jest.fn();
    const mockElement = { offsetWidth: 100, offsetHeight: 100 } as HTMLElement;
    const { fixture } = await render(DownloadDomImageComponent, {
      componentInputs: { target: mockElement, filename: 'test-file' },
      providers: [{ provide: LoggerService, useValue: { warn } }],
    });

    await fixture.componentInstance.performDownload('.png');

    expect(warn).toHaveBeenCalledWith('Failed to capture a plot image; no image was downloaded', {
      filename: 'test-file',
    });
  });
});
