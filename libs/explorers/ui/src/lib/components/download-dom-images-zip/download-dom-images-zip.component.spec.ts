import { LoggerService } from '@sagebionetworks/explorers/services';
import { captureDomToBlob } from '@sagebionetworks/explorers/util';
import { render } from '@testing-library/angular';
import { saveAs } from 'file-saver';
import JSZip from 'jszip';
import { DownloadDomImagesZipComponent } from './download-dom-images-zip.component';

jest.mock('file-saver', () => ({ saveAs: jest.fn() }));

jest.mock('@sagebionetworks/explorers/util', () => ({
  ...jest.requireActual('@sagebionetworks/explorers/util'),
  captureDomToBlob: jest.fn(),
}));

describe('DownloadDomImagesZipComponent', () => {
  beforeEach(() => jest.clearAllMocks());
  afterEach(() => jest.restoreAllMocks());

  async function setup(inputs?: Partial<DownloadDomImagesZipComponent>) {
    const mockElement1 = {
      offsetWidth: 100,
      offsetHeight: 100,
    } as HTMLElement;
    const mockElement2 = {
      offsetWidth: 100,
      offsetHeight: 100,
    } as HTMLElement;

    const component = await render(DownloadDomImagesZipComponent, {
      componentInputs: {
        domFiles: [
          { target: mockElement1, filename: 'test-file-1' },
          { target: mockElement2, filename: 'test-file-2' },
        ],
        filename: 'test-file',
        ...inputs,
      },
    });
    return { component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component.fixture.componentInstance).toBeTruthy();
  });

  it('should create a zip of CSV files when fileType is .csv', async () => {
    const mockElement = { offsetWidth: 100, offsetHeight: 100 } as HTMLElement;
    const { fixture } = await render(DownloadDomImagesZipComponent, {
      componentInputs: {
        domFiles: [{ target: mockElement, filename: 'img-1' }],
        filename: 'test-file',
        csvFiles: [
          {
            filename: 'data-1',
            data: [
              ['age', 'sex', 'value'],
              ['4 months', 'Female', '42.5'],
            ],
          },
          {
            filename: 'data-2',
            data: [
              ['age', 'sex', 'value'],
              ['6 months', 'Male', '55.1'],
            ],
          },
        ],
        hasCsvDownload: true,
      },
    });

    await fixture.componentInstance.performDownload('.csv');

    expect(saveAs).toHaveBeenCalledWith(expect.any(Blob), 'test-file.zip');
  });

  it('should save a single CSV file under its own name instead of zipping it', async () => {
    const mockElement = { offsetWidth: 100, offsetHeight: 100 } as HTMLElement;
    const { fixture } = await render(DownloadDomImagesZipComponent, {
      componentInputs: {
        domFiles: [{ target: mockElement, filename: 'img-1' }],
        filename: 'test-file',
        csvFiles: [
          {
            filename: 'data-1',
            data: [
              ['age', 'sex', 'value'],
              ['4 months', 'Female', '42.5'],
            ],
          },
        ],
        hasCsvDownload: true,
      },
    });

    await fixture.componentInstance.performDownload('.csv');

    expect(saveAs).toHaveBeenCalledWith(expect.any(Blob), 'data-1.csv');
  });

  it('should create a zip of images when there are multiple images', async () => {
    (captureDomToBlob as jest.Mock).mockResolvedValue(new Blob(['image'], { type: 'image/png' }));
    const fileSpy = jest.spyOn(JSZip.prototype, 'file');
    // jest-fixed-jsdom's global Blob is not jsdom's, so real zip generation cannot read it
    const generateSpy = jest
      .spyOn(JSZip.prototype, 'generateAsync')
      .mockResolvedValue(new Blob(['zip']) as never);
    const { component } = await setup();

    await component.fixture.componentInstance.performDownload('.png');

    expect(fileSpy).toHaveBeenCalledTimes(2);
    expect(saveAs).toHaveBeenCalledWith(expect.any(Blob), 'test-file.zip');
  });

  it('should warn when a plot capture fails and when nothing is left to download', async () => {
    (captureDomToBlob as jest.Mock).mockResolvedValue(null);
    const warn = jest.fn();
    const mockElement = { offsetWidth: 100, offsetHeight: 100 } as HTMLElement;
    const { fixture } = await render(DownloadDomImagesZipComponent, {
      componentInputs: {
        domFiles: [{ target: mockElement, filename: 'img-1' }],
        filename: 'test-file',
      },
      providers: [{ provide: LoggerService, useValue: { warn } }],
    });

    await fixture.componentInstance.performDownload('.png');

    expect(warn).toHaveBeenCalledWith(
      'Failed to capture a plot image; omitting it from the download',
      {
        filename: 'img-1',
      },
    );
    expect(warn).toHaveBeenCalledWith('No files were available for download', {
      fileType: '.png',
    });
  });

  it('should save a single image under its own name instead of zipping it', async () => {
    const mockElement = { offsetWidth: 100, offsetHeight: 100 } as HTMLElement;
    const blob = new Blob(['image'], { type: 'image/png' });
    (captureDomToBlob as jest.Mock).mockResolvedValue(blob);

    const { fixture } = await render(DownloadDomImagesZipComponent, {
      componentInputs: {
        domFiles: [{ target: mockElement, filename: 'img-1' }],
        filename: 'test-file',
      },
    });

    await fixture.componentInstance.performDownload('.png');

    expect(saveAs).toHaveBeenCalledWith(blob, 'img-1.png');
  });
});
