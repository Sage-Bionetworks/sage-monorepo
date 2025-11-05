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

    const instance = component.fixture.componentInstance;
    return { component, instance };
  }

  it('should create', async () => {
    const { instance } = await setup();
    expect(instance).toBeTruthy();
  });

  it('arrayToCSVString should double quote and escape values', async () => {
    const { instance } = await setup();
    expect(instance.arrayToCSVString(['a', 'b', 'c'])).toBe('"a","b","c"\n');
  });

  it('arrayToCSVString should handle empty array', async () => {
    const { instance } = await setup();
    expect(instance.arrayToCSVString([])).toBe('\n');
  });

  it('arrayToCSVString should handle embedded quotes', async () => {
    const { instance } = await setup();
    expect(instance.arrayToCSVString(['a"b', 'c'])).toBe('"a""b","c"\n');
  });

  it('arrayToCSVString should handle commas and newlines', async () => {
    const { instance } = await setup();
    expect(instance.arrayToCSVString(['a,b', 'c\nd'])).toBe('"a,b","c\nd"\n');
  });

  it('arrayToCSVString should handle empty string', async () => {
    const { instance } = await setup();
    expect(instance.arrayToCSVString([''])).toBe('""\n');
  });
});
