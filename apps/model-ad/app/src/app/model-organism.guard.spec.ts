import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { ModelOrganism } from '@sagebionetworks/model-ad/api-client';
import { modelOrganismGuard } from './model-organism.guard';

function runGuard(url: string, queryParams: Record<string, string>) {
  const route = { queryParams } as unknown as ActivatedRouteSnapshot;
  const state = { url } as RouterStateSnapshot;
  return TestBed.runInInjectionContext(() => modelOrganismGuard(route, state));
}

describe('modelOrganismGuard', () => {
  let router: Router;

  beforeEach(() => {
    router = TestBed.inject(Router);
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

  it('should redirect and normalize a wrong-case organism to mouse', () => {
    const result = runGuard('/models/APOE4?modelOrganism=Marmoset', { modelOrganism: 'Marmoset' });
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
});
