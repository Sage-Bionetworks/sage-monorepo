import { HelperService } from './helper.service';

describe('Service: Helper', () => {
  let helperService: HelperService;

  beforeEach(async () => {
    helperService = new HelperService();
  });

  it('should create', () => {
    expect(helperService).toBeDefined();
  });

  it('should get scroll top', () => {
    const res = helperService.getScrollTop();
    expect(res).toEqual({ x: 0, y: 0 });
  });

  it('should get offset', () => {
    const res = helperService.getOffset('');
    expect(res).toEqual({ top: 0, left: 0 });
  });

  it('should construct url for panel with parent', () => {
    const url = '/genes/ENSG123';
    const expected = '/genes/ENSG123/evidence/rna';
    const result = helperService.getPanelUrl(url, 'rna', 'evidence');
    expect(result).toEqual(expected);
  });

  it('should construct url for panel without parent', () => {
    const url = '/genes/ENSG123';
    const expected = '/genes/ENSG123/summary';
    const result = helperService.getPanelUrl(url, 'summary', '');
    expect(result).toEqual(expected);
  });

  it('should encode urls', () => {
    expect(helperService.encodeParenthesesForwardSlashes('/models/5xFAD (UCI)')).toBe(
      '/models/5xFAD %28UCI%29',
    );
    expect(helperService.encodeParenthesesForwardSlashes('/models/5xFAD (IU/Jax/Pitt)')).toBe(
      '/models/5xFAD %28IU%2FJax%2FPitt%29',
    );
  });

  it('should get number from CSS value', () => {
    expect(helperService.getNumberFromCSSValue('12px')).toBe(12);
    expect(helperService.getNumberFromCSSValue('0.4px')).toBe(0.4);
    expect(helperService.getNumberFromCSSValue('-120px')).toBe(-120);
  });

  it('should get significant figures', () => {
    const res = helperService.getSignificantFigures(0.123, 2);
    expect(res).toEqual(0.12);
  });
});
