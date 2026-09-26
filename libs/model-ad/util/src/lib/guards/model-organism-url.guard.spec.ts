import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { SourceLogger } from '@sagebionetworks/explorers/services';
import { ModelOrganism } from '@sagebionetworks/model-ad/api-client';
import { modelOrganismUrlGuard, UNKNOWN_MODEL_ORGANISM_MESSAGE } from './model-organism-url.guard';

function runGuard(url: string, queryParams: Record<string, string | string[]>) {
  const route = { queryParams } as unknown as ActivatedRouteSnapshot;
  const state = { url } as RouterStateSnapshot;
  return TestBed.runInInjectionContext(() => modelOrganismUrlGuard(route, state));
}

describe('modelOrganismUrlGuard', () => {
  let router: Router;
  let warn: jest.SpyInstance;

  beforeEach(() => {
    router = TestBed.inject(Router);
    warn = jest.spyOn(SourceLogger.prototype, 'warn').mockImplementation();
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });

  it('should allow activation when modelOrganism is a valid mouse value', () => {
    expect(runGuard('/models/APOE4?modelOrganism=mouse', { modelOrganism: 'mouse' })).toBe(true);
  });

  it('should allow activation when modelOrganism is a valid marmoset value', () => {
    expect(
      runGuard('/models/Presenilin%201?modelOrganism=marmoset', { modelOrganism: 'marmoset' }),
    ).toBe(true);
  });

  it('should redirect and add mouse when modelOrganism is missing', () => {
    const result = runGuard('/models/APOE4', {});
    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe(
      `/models/APOE4?modelOrganism=${ModelOrganism.Mouse}`,
    );
  });

  it('should redirect and normalize an unknown organism to mouse', () => {
    const result = runGuard('/models/APOE4?modelOrganism=rat', { modelOrganism: 'rat' });
    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe(
      `/models/APOE4?modelOrganism=${ModelOrganism.Mouse}`,
    );
  });

  it('should redirect and lowercase a wrong-case organism', () => {
    const result = runGuard('/models/APOE4?modelOrganism=Marmoset', { modelOrganism: 'Marmoset' });
    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe(
      `/models/APOE4?modelOrganism=${ModelOrganism.Marmoset}`,
    );
  });

  it('should redirect and lowercase a wrong-case mouse value', () => {
    const result = runGuard('/models/APOE4?modelOrganism=MOUSE', { modelOrganism: 'MOUSE' });
    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe(
      `/models/APOE4?modelOrganism=${ModelOrganism.Mouse}`,
    );
  });

  it('should preserve a hash fragment when redirecting', () => {
    const result = runGuard('/models/APOE4/biomarkers#nfl', {});
    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe(
      `/models/APOE4/biomarkers?modelOrganism=${ModelOrganism.Mouse}#nfl`,
    );
  });

  it('should preserve other query params and a hash fragment together when redirecting', () => {
    const result = runGuard('/models/3xTg-AD/biomarkers?tissue=Hippocampus#insoluble-abeta42', {
      tissue: 'Hippocampus',
    });
    expect(result).toBeInstanceOf(UrlTree);
    expect(router.serializeUrl(result as UrlTree)).toBe(
      `/models/3xTg-AD/biomarkers?tissue=Hippocampus&modelOrganism=${ModelOrganism.Mouse}#insoluble-abeta42`,
    );
  });

  it('should preserve other query params and the tab path when redirecting', () => {
    const result = runGuard('/models/APOE4/biomarkers?tissue=Hippocampus&sex=Male', {
      tissue: 'Hippocampus',
      sex: 'Male',
    });
    expect(result).toBeInstanceOf(UrlTree);
    const serialized = router.serializeUrl(result as UrlTree);
    expect(serialized).toContain('/models/APOE4/biomarkers');
    expect(serialized).toContain('tissue=Hippocampus');
    expect(serialized).toContain('sex=Male');
    expect(serialized).toContain(`modelOrganism=${ModelOrganism.Mouse}`);
  });

  describe('logging', () => {
    it('should warn when modelOrganism is an unknown value', () => {
      const url = '/models/APOE4?modelOrganism=rat';

      runGuard(url, { modelOrganism: 'rat' });

      expect(warn).toHaveBeenCalledWith(UNKNOWN_MODEL_ORGANISM_MESSAGE, {
        rawModelOrganism: 'rat',
        fallback: ModelOrganism.Mouse,
        url,
      });
    });

    it('should warn when modelOrganism is repeated', () => {
      runGuard('/models/APOE4?modelOrganism=mouse&modelOrganism=marmoset', {
        modelOrganism: ['mouse', 'marmoset'],
      });

      expect(warn).toHaveBeenCalledWith(
        UNKNOWN_MODEL_ORGANISM_MESSAGE,
        expect.objectContaining({ rawModelOrganism: ['mouse', 'marmoset'] }),
      );
    });

    it('should not warn when modelOrganism is missing', () => {
      runGuard('/models/APOE4', {});

      expect(warn).not.toHaveBeenCalled();
    });

    it('should not warn when modelOrganism is empty', () => {
      runGuard('/models/APOE4?modelOrganism=', { modelOrganism: '' });

      expect(warn).not.toHaveBeenCalled();
    });

    it('should not warn when modelOrganism is a wrong-case valid value', () => {
      runGuard('/models/APOE4?modelOrganism=MOUSE', { modelOrganism: 'MOUSE' });

      expect(warn).not.toHaveBeenCalled();
    });

    it('should not warn when modelOrganism is valid', () => {
      runGuard('/models/APOE4?modelOrganism=marmoset', { modelOrganism: 'marmoset' });

      expect(warn).not.toHaveBeenCalled();
    });
  });
});
