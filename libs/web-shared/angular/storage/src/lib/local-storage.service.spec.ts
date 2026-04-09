import { TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';

import { LocalStorageService } from './local-storage.service';
import { Logger, LOGGER } from './logger';

function setup(platformId: string): { service: LocalStorageService; logger: Logger } {
  const logger: Logger = { error: jest.fn() };
  TestBed.configureTestingModule({
    providers: [
      { provide: PLATFORM_ID, useValue: platformId },
      { provide: LOGGER, useValue: logger },
    ],
  });
  return { service: TestBed.inject(LocalStorageService), logger };
}

describe('LocalStorageService', () => {
  let getItemSpy: jest.SpyInstance;
  let setItemSpy: jest.SpyInstance;
  let removeItemSpy: jest.SpyInstance;

  beforeEach(() => {
    localStorage.clear();
    getItemSpy = jest.spyOn(Storage.prototype, 'getItem');
    setItemSpy = jest.spyOn(Storage.prototype, 'setItem');
    removeItemSpy = jest.spyOn(Storage.prototype, 'removeItem');
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  describe('in browser', () => {
    let service: LocalStorageService;
    let logger: Logger;

    beforeEach(() => {
      ({ service, logger } = setup('browser'));
    });

    describe('getItem', () => {
      it('should return value from localStorage', () => {
        localStorage.setItem('key', 'value');
        getItemSpy.mockClear();

        expect(service.getItem('key')).toBe('value');
        expect(getItemSpy).toHaveBeenCalledWith('key');
      });

      it('should return null when key does not exist', () => {
        expect(service.getItem('missing')).toBeNull();
      });

      it('should return null and log when localStorage throws', () => {
        getItemSpy.mockImplementation(() => {
          throw new Error('access denied');
        });

        expect(service.getItem('key')).toBeNull();
        expect(logger.error).toHaveBeenCalled();
      });
    });

    describe('setItem', () => {
      it('should write value to localStorage', () => {
        service.setItem('key', 'value');

        expect(setItemSpy).toHaveBeenCalledWith('key', 'value');
        expect(localStorage.getItem('key')).toBe('value');
      });

      it('should not throw and should log when localStorage throws', () => {
        setItemSpy.mockImplementation(() => {
          throw new Error('quota exceeded');
        });

        expect(() => service.setItem('key', 'value')).not.toThrow();
        expect(logger.error).toHaveBeenCalled();
      });
    });

    describe('removeItem', () => {
      it('should remove key from localStorage', () => {
        localStorage.setItem('key', 'value');
        removeItemSpy.mockClear();

        service.removeItem('key');

        expect(removeItemSpy).toHaveBeenCalledWith('key');
        expect(localStorage.getItem('key')).toBeNull();
      });

      it('should not throw and should log when localStorage throws', () => {
        removeItemSpy.mockImplementation(() => {
          throw new Error('access denied');
        });

        expect(() => service.removeItem('key')).not.toThrow();
        expect(logger.error).toHaveBeenCalled();
      });
    });
  });

  describe('on server', () => {
    let service: LocalStorageService;

    beforeEach(() => {
      ({ service } = setup('server'));
    });

    it('getItem should return null without accessing localStorage', () => {
      expect(service.getItem('key')).toBeNull();
      expect(getItemSpy).not.toHaveBeenCalled();
    });

    it('setItem should not access localStorage', () => {
      service.setItem('key', 'value');
      expect(setItemSpy).not.toHaveBeenCalled();
    });

    it('removeItem should not access localStorage', () => {
      service.removeItem('key');
      expect(removeItemSpy).not.toHaveBeenCalled();
    });
  });
});
