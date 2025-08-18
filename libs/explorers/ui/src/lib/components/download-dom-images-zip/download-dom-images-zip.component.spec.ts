import { render } from '@testing-library/angular';
import { DownloadDomImagesZipComponent } from './download-dom-images-zip.component';

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
});
