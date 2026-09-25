import {
  MOCK_CLIENT_HEIGHT_PX,
  MOCK_CLIENT_WIDTH_PX,
  mockElementClientSize,
  mockResizeObserver,
  mockSvgGetBBox,
} from './jsdom-mocks';

const SVG_NAMESPACE = 'http://www.w3.org/2000/svg';

describe('jsdom mocks', () => {
  describe('with mockElementClientSize', () => {
    mockElementClientSize();

    it('should report the mocked client size as numbers', () => {
      const element = document.createElement('div');

      expect(element.clientHeight).toBe(MOCK_CLIENT_HEIGHT_PX);
      expect(element.clientWidth).toBe(MOCK_CLIENT_WIDTH_PX);
    });
  });

  describe('with mockSvgGetBBox', () => {
    mockSvgGetBBox();

    it('should stub getBBox on SVG elements', () => {
      const svgElement = document.createElementNS(SVG_NAMESPACE, 'rect') as SVGGraphicsElement;

      expect(svgElement.getBBox()).toEqual({ x: 0, y: 0 });
    });
  });

  describe('with mockResizeObserver', () => {
    mockResizeObserver();

    it('should provide a ResizeObserver', () => {
      const observer = new ResizeObserver(() => undefined);

      expect(() => observer.observe(document.body)).not.toThrow();
    });
  });

  describe('after the mocked tests', () => {
    it('should restore jsdom client size and remove the stubs', () => {
      const element = document.createElement('div');

      expect(element.clientHeight).toBe(0);
      expect(element.clientWidth).toBe(0);
      expect('getBBox' in document.createElementNS(SVG_NAMESPACE, 'rect')).toBe(false);
      expect(typeof ResizeObserver).toBe('undefined');
    });
  });
});
