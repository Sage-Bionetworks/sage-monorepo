// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { HelperService } from './helper.service';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Service: Helper', () => {
  let helperService: HelperService;

  beforeEach(async () => {
    helperService = new HelperService();
  });

  it('should create', () => {
    expect(helperService).toBeDefined();
  });

  it('should set loading', () => {
    helperService.setLoading(true);
    expect(helperService.loading).toEqual(true);
  });

  it('should get loading', () => {
    const res = helperService.getLoading();
    expect(res).toEqual(false);
  });

  it('should get GCT column tooltip text for Risk Score', () => {
    const res = helperService.getGCTColumnTooltipText('RISK SCORE');
    expect(res).toEqual('Target Risk Score');
  });

  it('should get GCT column tooltip text for ACC brain region', () => {
    const res = helperService.getGCTColumnTooltipText('ACC');
    expect(res).toEqual('Anterior Cingulate Cortex');
  });

  it('should round 0.123 to 0.12', () => {
    const res = helperService.roundNumber(0.123, 2);
    expect(res).toEqual('0.12');
  });

  it('should round 1.96897662132087 to 1.97', () => {
    const res = helperService.roundNumber(1.96897662132087, 2);
    expect(res).toEqual('1.97');
  });

  it('should round 0.1 to 0.10', () => {
    const res = helperService.roundNumber(0.1, 2);
    expect(res).toEqual('0.10');
  });

  it('should round 1.015 to 1.02', () => {
    const res = helperService.roundNumber(1.015, 2);
    expect(res).toEqual('1.02');
  });

  it('should round 4.015 to 4.02', () => {
    const res = helperService.roundNumber(4.015, 2);
    expect(res).toEqual('4.02');
  });

  it('should round 5.015 to 5.02', () => {
    const res = helperService.roundNumber(5.015, 2);
    expect(res).toEqual('5.02');
  });

  it('should round 6.015 to 6.02', () => {
    const res = helperService.roundNumber(6.015, 2);
    expect(res).toEqual('6.02');
  });

  it('should round 7.015 to 7.02', () => {
    const res = helperService.roundNumber(7.015, 2);
    expect(res).toEqual('7.02');
  });

  it('should round 128.015 to 128.02', () => {
    const res = helperService.roundNumber(128.015, 2);
    expect(res).toEqual('128.02');
  });

  it('should set GCT selection', () => {
    const mock = ['A'];
    helperService.setGCTSelection(mock);
    expect(helperService.gctSelection).toEqual(mock);
  });

  it('should get GCT selection', () => {
    const mock = ['A'];
    helperService.setGCTSelection(mock);
    const res = helperService.getGCTSelection();
    expect(res).toEqual(mock);
  });

  it('should delete GCT selection', () => {
    const mock = ['A'];
    helperService.setGCTSelection(mock);
    helperService.deleteGCTSelection();
    expect(helperService.gctSelection).toEqual([]);
  });

  it('should get color', () => {
    const res = helperService.getColor('primary');
    expect(res).toEqual('#3c4a63');
  });

  it('should capitalize first letter of string', () => {
    const testString = 'nike just do it';
    const result = helperService.capitalizeFirstLetterOfString(testString);
    expect(result).toEqual('Nike just do it');
  });

  it('should capitalize the only letter of a string having a length of 1', () => {
    const testString = 'n';
    const result = helperService.capitalizeFirstLetterOfString(testString);
    expect(result).toEqual('N');
  });

  it('should return an empty string if attempting to capitalize an empty string', () => {
    const testString = '';
    const result = helperService.capitalizeFirstLetterOfString(testString);
    expect(result).toEqual('');
  });

  it('should return a url with parameters', () => {
    const url = '/genes/ENSG123';
    const expected = '/genes/ENSG123?model=abc';
    const result = helperService.addSingleUrlParam(url, 'model', 'abc');
    expect(result).toEqual(expected);
  });

  it('should replace existing url parameters if the same one exists', () => {
    const url = '/genes/ENSG123?model=abc';
    const expected = '/genes/ENSG123?model=fff';
    const result = helperService.addSingleUrlParam(url, 'model', 'fff');
    expect(result).toEqual(expected);
  });

  it('should replace existing url parameters if a different one exists', () => {
    const url = '/genes/ENSG123?model=abc';
    const expected = '/genes/ENSG123?test=fff';
    const result = helperService.addSingleUrlParam(url, 'test', 'fff');
    expect(result).toEqual(expected);
  });
});
