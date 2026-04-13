import { TestBed } from '@angular/core/testing';
import { LocalStorageService } from '@sagebionetworks/web-shared/angular/storage';
import {
  HIDE_VISUALIZATION_OVERVIEW_KEY,
  PAGE_SIZE_KEY,
  VALID_PAGE_SIZES,
  DEFAULT_PAGE_SIZE,
} from './app-storage.constants';
import { AppStorageService } from './app-storage.service';

describe('AppStorageService', () => {
  let storageServiceMock: {
    getItem: jest.Mock;
    setItem: jest.Mock;
    removeItem: jest.Mock;
  };

  function setup() {
    storageServiceMock = {
      getItem: jest.fn().mockReturnValue(null),
      setItem: jest.fn(),
      removeItem: jest.fn(),
    };

    TestBed.configureTestingModule({
      providers: [{ provide: LocalStorageService, useValue: storageServiceMock }],
    });

    return TestBed.inject(AppStorageService);
  }

  describe('isVisualizationOverviewHidden', () => {
    it('returns false when nothing stored', () => {
      const service = setup();
      expect(service.isVisualizationOverviewHidden()).toBe(false);
      expect(storageServiceMock.getItem).toHaveBeenCalledWith(HIDE_VISUALIZATION_OVERVIEW_KEY);
    });

    it('returns true when value is "true"', () => {
      const service = setup();
      storageServiceMock.getItem.mockReturnValue('true');
      expect(service.isVisualizationOverviewHidden()).toBe(true);
    });

    it('returns false and cleans up legacy "1" value', () => {
      const service = setup();
      storageServiceMock.getItem.mockReturnValue('1');
      expect(service.isVisualizationOverviewHidden()).toBe(false);
      expect(storageServiceMock.removeItem).toHaveBeenCalledWith(HIDE_VISUALIZATION_OVERVIEW_KEY);
    });

    it('returns false and cleans up any other invalid value', () => {
      const service = setup();
      storageServiceMock.getItem.mockReturnValue('abc');
      expect(service.isVisualizationOverviewHidden()).toBe(false);
      expect(storageServiceMock.removeItem).toHaveBeenCalledWith(HIDE_VISUALIZATION_OVERVIEW_KEY);
    });
  });

  describe('setVisualizationOverviewHidden', () => {
    it('stores "true" when hiding', () => {
      const service = setup();
      service.setVisualizationOverviewHidden(true);
      expect(storageServiceMock.setItem).toHaveBeenCalledWith(
        HIDE_VISUALIZATION_OVERVIEW_KEY,
        'true',
      );
    });

    it('removes key when showing', () => {
      const service = setup();
      service.setVisualizationOverviewHidden(false);
      expect(storageServiceMock.removeItem).toHaveBeenCalledWith(HIDE_VISUALIZATION_OVERVIEW_KEY);
      expect(storageServiceMock.setItem).not.toHaveBeenCalled();
    });
  });

  describe('getPageSize', () => {
    it('returns default when nothing stored', () => {
      const service = setup();
      expect(service.getPageSize()).toBe(DEFAULT_PAGE_SIZE);
    });

    it.each(VALID_PAGE_SIZES)('returns stored value %i when valid', (size) => {
      const service = setup();
      storageServiceMock.getItem.mockReturnValue(String(size));
      expect(service.getPageSize()).toBe(size);
    });

    it('returns default and cleans up for invalid stored value', () => {
      const service = setup();
      const invalidPageSize = '42';
      storageServiceMock.getItem.mockReturnValue(invalidPageSize);
      expect(service.getPageSize()).toBe(DEFAULT_PAGE_SIZE);
      expect(storageServiceMock.removeItem).toHaveBeenCalledWith(PAGE_SIZE_KEY);
    });

    it('returns default and cleans up for non-numeric stored value', () => {
      const service = setup();
      const nonNumericValue = 'abc';
      storageServiceMock.getItem.mockReturnValue(nonNumericValue);
      expect(service.getPageSize()).toBe(DEFAULT_PAGE_SIZE);
      expect(storageServiceMock.removeItem).toHaveBeenCalledWith(PAGE_SIZE_KEY);
    });
  });

  describe('setPageSize', () => {
    it('writes non-default size to storage', () => {
      const service = setup();
      const nonDefaultPageSize = VALID_PAGE_SIZES[1];
      service.setPageSize(nonDefaultPageSize);
      expect(storageServiceMock.setItem).toHaveBeenCalledWith(
        PAGE_SIZE_KEY,
        String(nonDefaultPageSize),
      );
    });

    it('removes key when setting default page size', () => {
      const service = setup();
      service.setPageSize(DEFAULT_PAGE_SIZE);
      expect(storageServiceMock.removeItem).toHaveBeenCalledWith(PAGE_SIZE_KEY);
      expect(storageServiceMock.setItem).not.toHaveBeenCalled();
    });

    it('does not write invalid page size', () => {
      const service = setup();
      const invalidPageSize = 42;
      service.setPageSize(invalidPageSize);
      expect(storageServiceMock.setItem).not.toHaveBeenCalled();
      expect(storageServiceMock.removeItem).not.toHaveBeenCalled();
    });
  });
});
