import { render } from '@testing-library/angular';
import { DownloadDomImageComponent } from './download-dom-image.component';

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
    return { component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component.fixture.componentInstance).toBeTruthy();
  });
});
