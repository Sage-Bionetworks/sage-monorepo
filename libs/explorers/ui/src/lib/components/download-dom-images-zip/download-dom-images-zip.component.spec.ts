import { render } from '@testing-library/angular';
import { saveAs } from 'file-saver';
import { DownloadDomImagesZipComponent } from './download-dom-images-zip.component';

jest.mock('file-saver', () => ({ saveAs: jest.fn() }));

describe('DownloadDomImagesZipComponent', () => {
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
});
