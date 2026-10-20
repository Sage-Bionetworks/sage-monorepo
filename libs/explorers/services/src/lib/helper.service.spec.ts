import { convertToParamMap } from '@angular/router';
import { PANEL_ROUTE_PARAMS } from '@sagebionetworks/explorers/constants';
import { Panel } from '@sagebionetworks/explorers/models';
import { HelperService } from './helper.service';

const panelsMock: Panel[] = [
  { name: 'summary', label: 'Summary', disabled: false },
  {
    name: 'evidence',
    label: 'Evidence',
    disabled: false,
    children: [
      { name: 'rna', label: 'RNA', disabled: false },
      { name: 'protein', label: 'Protein', disabled: true },
    ],
  },
  { name: 'resources', label: 'Resources', disabled: false },
  { name: 'nominations', label: 'Nominations', disabled: true },
];

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

  it('should construct url for panel with query params', () => {
    const result = helperService.getPanelUrl('/models/APOE4', 'omics', '', {
      modelOrganism: 'mouse',
    });
    expect(result).toEqual('/models/APOE4/omics?modelOrganism=mouse');
  });

  describe('appendQueryParams', () => {
    it('should append query params', () => {
      const result = helperService.appendQueryParams('/genes/ENSG123', { a: '1', b: '2' });
      expect(result).toEqual('/genes/ENSG123?a=1&b=2');
    });

    it('should append to existing query params', () => {
      const result = helperService.appendQueryParams('/genes/ENSG123?a=1', { b: '2' });
      expect(result).toEqual('/genes/ENSG123?a=1&b=2');
    });

    it('should insert query params before a hash fragment', () => {
      expect(helperService.appendQueryParams('/genes/ENSG123#section', { b: '2' })).toEqual(
        '/genes/ENSG123?b=2#section',
      );
      expect(helperService.appendQueryParams('/genes/ENSG123?a=1#section', { b: '2' })).toEqual(
        '/genes/ENSG123?a=1&b=2#section',
      );
    });

    it('should drop null, undefined, and empty values', () => {
      const result = helperService.appendQueryParams('/genes/ENSG123', {
        a: null,
        b: undefined,
        c: '',
        d: '4',
      });
      expect(result).toEqual('/genes/ENSG123?d=4');
    });

    it('should leave the url unchanged when there are no query params', () => {
      expect(helperService.appendQueryParams('/genes/ENSG123', {})).toEqual('/genes/ENSG123');
      expect(helperService.appendQueryParams('/genes/ENSG123', { a: null })).toEqual(
        '/genes/ENSG123',
      );
    });

    it('should encode spaces as %20 and leave parentheses as-is', () => {
      const result = helperService.appendQueryParams('/genes/ENSG123', {
        model: 'AD Diagnosis x Sex (females only)',
      });
      expect(result).toEqual('/genes/ENSG123?model=AD%20Diagnosis%20x%20Sex%20(females%20only)');
    });
  });

  describe('handlePanelChange', () => {
    const basePath = '/genes/ENSG123';

    it('should activate a top-level panel', () => {
      const result = helperService.handlePanelChange(panelsMock, panelsMock[2], basePath);
      expect(result).toEqual({
        activePanel: 'resources',
        activeParent: '',
        url: '/genes/ENSG123/resources',
      });
    });

    it('should activate the first child of a parent panel', () => {
      const result = helperService.handlePanelChange(panelsMock, panelsMock[1], basePath);
      expect(result).toEqual({
        activePanel: 'rna',
        activeParent: 'evidence',
        url: '/genes/ENSG123/evidence/rna',
      });
    });

    it('should set the parent of a child panel', () => {
      const childPanel = { name: 'rna', label: 'RNA', disabled: false };
      const result = helperService.handlePanelChange(panelsMock, childPanel, basePath);
      expect(result).toEqual({
        activePanel: 'rna',
        activeParent: 'evidence',
        url: '/genes/ENSG123/evidence/rna',
      });
    });

    it('should ignore a disabled panel', () => {
      expect(helperService.handlePanelChange(panelsMock, panelsMock[3], basePath)).toBeUndefined();
    });

    it('should carry query params into the url', () => {
      const result = helperService.handlePanelChange(panelsMock, panelsMock[2], basePath, {
        model: 'AD Diagnosis (males and females)',
      });
      expect(result?.url).toEqual(
        '/genes/ENSG123/resources?model=AD%20Diagnosis%20(males%20and%20females)',
      );
    });
  });

  describe('getActivePanelAndParentFromUrl', () => {
    it('should resolve a top-level tab', () => {
      const params = convertToParamMap({ [PANEL_ROUTE_PARAMS.TAB]: 'resources' });
      expect(helperService.getActivePanelAndParentFromUrl(panelsMock, params)).toEqual({
        activePanel: 'resources',
        activeParent: '',
        shouldScrollToPanelNav: true,
      });
    });

    it('should resolve a parent tab to its first child', () => {
      const params = convertToParamMap({ [PANEL_ROUTE_PARAMS.TAB]: 'evidence' });
      expect(helperService.getActivePanelAndParentFromUrl(panelsMock, params)).toEqual({
        activePanel: 'rna',
        activeParent: 'evidence',
        shouldScrollToPanelNav: true,
      });
    });

    it('should resolve a tab and subtab', () => {
      const params = convertToParamMap({
        [PANEL_ROUTE_PARAMS.TAB]: 'evidence',
        [PANEL_ROUTE_PARAMS.SUBTAB]: 'rna',
      });
      expect(helperService.getActivePanelAndParentFromUrl(panelsMock, params)).toEqual({
        activePanel: 'rna',
        activeParent: 'evidence',
        shouldScrollToPanelNav: true,
      });
    });

    it('should return undefined for a subtab that belongs to a different parent', () => {
      const params = convertToParamMap({
        [PANEL_ROUTE_PARAMS.TAB]: 'resources',
        [PANEL_ROUTE_PARAMS.SUBTAB]: 'rna',
      });
      expect(helperService.getActivePanelAndParentFromUrl(panelsMock, params)).toBeUndefined();
    });

    it('should return undefined for a top-level panel used as a subtab', () => {
      const params = convertToParamMap({
        [PANEL_ROUTE_PARAMS.TAB]: 'evidence',
        [PANEL_ROUTE_PARAMS.SUBTAB]: 'summary',
      });
      expect(helperService.getActivePanelAndParentFromUrl(panelsMock, params)).toBeUndefined();
    });

    it('should return undefined for a subtab under an unknown parent', () => {
      const params = convertToParamMap({
        [PANEL_ROUTE_PARAMS.TAB]: 'does-not-exist',
        [PANEL_ROUTE_PARAMS.SUBTAB]: 'rna',
      });
      expect(helperService.getActivePanelAndParentFromUrl(panelsMock, params)).toBeUndefined();
    });

    it('should return undefined for an unknown tab', () => {
      const params = convertToParamMap({ [PANEL_ROUTE_PARAMS.TAB]: 'does-not-exist' });
      expect(helperService.getActivePanelAndParentFromUrl(panelsMock, params)).toBeUndefined();
    });

    it('should return undefined when no tab is specified', () => {
      expect(
        helperService.getActivePanelAndParentFromUrl(panelsMock, convertToParamMap({})),
      ).toBeUndefined();
    });
  });

  describe('findPanelByName', () => {
    it('should find a top-level panel', () => {
      expect(helperService.findPanelByName(panelsMock, 'resources')).toBe(panelsMock[2]);
    });

    it('should find a child panel', () => {
      expect(helperService.findPanelByName(panelsMock, 'rna')).toBe(panelsMock[1].children?.[0]);
    });

    it('should return undefined for an unknown panel', () => {
      expect(helperService.findPanelByName(panelsMock, 'does-not-exist')).toBeUndefined();
    });
  });

  describe('getFallbackPanelIfInvalid', () => {
    const firstAvailablePanel = { activePanel: 'summary', activeParent: '' };

    it('should not fall back for an enabled child panel', () => {
      expect(helperService.getFallbackPanelIfInvalid(panelsMock, 'rna')).toBeUndefined();
    });

    it('should fall back for a disabled child panel', () => {
      expect(helperService.getFallbackPanelIfInvalid(panelsMock, 'protein')).toEqual(
        firstAvailablePanel,
      );
    });

    it('should fall back for a disabled top-level panel', () => {
      expect(helperService.getFallbackPanelIfInvalid(panelsMock, 'nominations')).toEqual(
        firstAvailablePanel,
      );
    });

    it('should fall back for an unknown panel', () => {
      expect(helperService.getFallbackPanelIfInvalid(panelsMock, 'does-not-exist')).toEqual(
        firstAvailablePanel,
      );
    });
  });

  describe('resolveInitialPanel', () => {
    const basePath = '/models/APOE4';
    const queryParams = { modelOrganism: 'mouse' };
    const defaultPanel = {
      activePanel: 'summary',
      activeParent: '',
      shouldScrollToPanelNav: false,
    };

    afterEach(() => jest.restoreAllMocks());

    it('should use the panel from the url and scroll to the nav', () => {
      const params = convertToParamMap({ [PANEL_ROUTE_PARAMS.TAB]: 'resources' });
      expect(helperService.resolveInitialPanel(panelsMock, params, basePath, queryParams)).toEqual({
        activePanel: 'resources',
        activeParent: '',
        shouldScrollToPanelNav: true,
      });
    });

    it('should open the first child of a parent panel from the url', () => {
      const params = convertToParamMap({ [PANEL_ROUTE_PARAMS.TAB]: 'evidence' });
      expect(helperService.resolveInitialPanel(panelsMock, params, basePath, queryParams)).toEqual({
        activePanel: 'rna',
        activeParent: 'evidence',
        shouldScrollToPanelNav: true,
      });
    });

    it('should not scroll to the nav when the url has a hash fragment', () => {
      jest.spyOn(helperService, 'getHashFragment').mockReturnValue('section');
      const params = convertToParamMap({ [PANEL_ROUTE_PARAMS.TAB]: 'resources' });
      expect(
        helperService.resolveInitialPanel(panelsMock, params, basePath, queryParams)
          .shouldScrollToPanelNav,
      ).toBe(false);
    });

    it('should use the default panel without replacing the url when no panel is specified', () => {
      expect(
        helperService.resolveInitialPanel(panelsMock, convertToParamMap({}), basePath, queryParams),
      ).toEqual(defaultPanel);
    });

    it('should replace the url with the base path and query params for an unknown panel', () => {
      const params = convertToParamMap({ [PANEL_ROUTE_PARAMS.TAB]: 'does-not-exist' });
      expect(helperService.resolveInitialPanel(panelsMock, params, basePath, queryParams)).toEqual({
        ...defaultPanel,
        replacementUrl: '/models/APOE4?modelOrganism=mouse',
      });
    });

    it('should replace the url for a disabled panel', () => {
      const params = convertToParamMap({ [PANEL_ROUTE_PARAMS.TAB]: 'nominations' });
      expect(helperService.resolveInitialPanel(panelsMock, params, basePath)).toEqual({
        ...defaultPanel,
        replacementUrl: basePath,
      });
    });

    it('should replace the url for a subtab that belongs to a different parent', () => {
      const params = convertToParamMap({
        [PANEL_ROUTE_PARAMS.TAB]: 'resources',
        [PANEL_ROUTE_PARAMS.SUBTAB]: 'rna',
      });
      expect(helperService.resolveInitialPanel(panelsMock, params, basePath)).toEqual({
        ...defaultPanel,
        replacementUrl: basePath,
      });
    });

    it('should replace the url for a disabled child panel', () => {
      const params = convertToParamMap({
        [PANEL_ROUTE_PARAMS.TAB]: 'evidence',
        [PANEL_ROUTE_PARAMS.SUBTAB]: 'protein',
      });
      expect(helperService.resolveInitialPanel(panelsMock, params, basePath)).toEqual({
        ...defaultPanel,
        replacementUrl: basePath,
      });
    });
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

  it('should get url param', () => {
    // window.location.search = '?test=42';
    const res = helperService.getUrlParam('test');
    expect(res).toEqual(null);
  });
});
