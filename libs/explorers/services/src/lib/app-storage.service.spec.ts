import { TestBed } from '@angular/core/testing';
import { AppStorageService } from './app-storage.service';
import { PlatformService } from './platform.service';

describe('AppStorageService', () => {
  const createPlatformServiceMock = (isServer: boolean) => ({
    isBrowser: !isServer,
    isServer,
  });

  let getItemSpy: jest.SpyInstance;
  let setItemSpy: jest.SpyInstance;
  let removeItemSpy: jest.SpyInstance;

  beforeEach(() => {
    getItemSpy = jest.spyOn(Storage.prototype, 'getItem');
    setItemSpy = jest.spyOn(Storage.prototype, 'setItem');
    removeItemSpy = jest.spyOn(Storage.prototype, 'removeItem');
  });

  afterEach(() => {
    jest.restoreAllMocks();
    localStorage.clear();
  });

  function setup({ isServer = false }: { isServer?: boolean } = {}) {
    const platformServiceMock = createPlatformServiceMock(isServer);

    TestBed.configureTestingModule({
      providers: [{ provide: PlatformService, useValue: platformServiceMock }],
    });

    const service = TestBed.inject(AppStorageService);

    return { service, platformServiceMock };
  }

  describe('isVisualizationOverviewHidden', () => {
    it('returns false on server', () => {
      const { service } = setup({ isServer: true });
      localStorage.setItem('hide_visualization_overview', 'true');
      expect(service.isVisualizationOverviewHidden()).toBe(false);
      expect(getItemSpy).not.toHaveBeenCalled();
    });

    it('returns false when nothing stored', () => {
      const { service } = setup();
      expect(service.isVisualizationOverviewHidden()).toBe(false);
    });

    it('returns true when value is "true"', () => {
      localStorage.setItem('hide_visualization_overview', 'true');
      const { service } = setup();
      expect(service.isVisualizationOverviewHidden()).toBe(true);
    });

    it('returns false and cleans up legacy "1" value', () => {
      localStorage.setItem('hide_visualization_overview', '1');
      const { service } = setup();
      expect(service.isVisualizationOverviewHidden()).toBe(false);
      expect(removeItemSpy).toHaveBeenCalledWith('hide_visualization_overview');
    });

    it('returns false and cleans up any other invalid value', () => {
      localStorage.setItem('hide_visualization_overview', 'abc');
      const { service } = setup();
      expect(service.isVisualizationOverviewHidden()).toBe(false);
      expect(removeItemSpy).toHaveBeenCalledWith('hide_visualization_overview');
    });
  });

  describe('setVisualizationOverviewHidden', () => {
    it('stores "true" when hiding', () => {
      const { service } = setup();
      service.setVisualizationOverviewHidden(true);
      expect(setItemSpy).toHaveBeenCalledWith('hide_visualization_overview', 'true');
    });

    it('stores "false" when showing', () => {
      const { service } = setup();
      service.setVisualizationOverviewHidden(false);
      expect(setItemSpy).toHaveBeenCalledWith('hide_visualization_overview', 'false');
    });

    it('does nothing on server', () => {
      const { service } = setup({ isServer: true });
      service.setVisualizationOverviewHidden(true);
      expect(setItemSpy).not.toHaveBeenCalled();
      expect(removeItemSpy).not.toHaveBeenCalled();
    });
  });

  describe('getPageSize', () => {
    it('returns default on server', () => {
      const { service } = setup({ isServer: true });
      expect(service.getPageSize()).toBe(10);
      expect(getItemSpy).not.toHaveBeenCalled();
    });

    it('returns default when nothing stored', () => {
      const { service } = setup();
      expect(service.getPageSize()).toBe(10);
    });

    it.each([10, 25, 50])('returns stored value %i when valid', (size) => {
      localStorage.setItem('comparison_tool_page_size', String(size));
      const { service } = setup();
      expect(service.getPageSize()).toBe(size);
    });

    it('returns default and cleans up for invalid stored value', () => {
      localStorage.setItem('comparison_tool_page_size', '42');
      const { service } = setup();
      expect(service.getPageSize()).toBe(10);
      expect(removeItemSpy).toHaveBeenCalledWith('comparison_tool_page_size');
    });

    it('returns default for non-numeric stored value', () => {
      localStorage.setItem('comparison_tool_page_size', 'abc');
      const { service } = setup();
      expect(service.getPageSize()).toBe(10);
    });

    it('returns default when localStorage throws', () => {
      getItemSpy.mockImplementation(() => {
        throw new Error('localStorage disabled');
      });
      const { service } = setup();
      expect(service.getPageSize()).toBe(10);
    });
  });

  describe('setPageSize', () => {
    it('writes to localStorage', () => {
      const { service } = setup();
      service.setPageSize(25);
      expect(setItemSpy).toHaveBeenCalledWith('comparison_tool_page_size', '25');
    });

    it('does nothing on server', () => {
      const { service } = setup({ isServer: true });
      service.setPageSize(25);
      expect(setItemSpy).not.toHaveBeenCalled();
    });

    it('does not write invalid page size', () => {
      const { service } = setup();
      service.setPageSize(42);
      expect(setItemSpy).not.toHaveBeenCalled();
    });

    it('silently fails when localStorage throws', () => {
      setItemSpy.mockImplementation(() => {
        throw new Error('quota exceeded');
      });
      const { service } = setup();
      expect(() => service.setPageSize(25)).not.toThrow();
    });
  });
});
