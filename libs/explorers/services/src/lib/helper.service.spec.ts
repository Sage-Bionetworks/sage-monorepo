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
});
