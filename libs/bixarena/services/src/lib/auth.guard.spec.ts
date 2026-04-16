import { TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { AuthService } from './auth.service';
import { BattleGateService } from './battle-gate.service';
import { authGuard } from './auth.guard';
import { ConfigService } from '@sagebionetworks/bixarena/config';

const mockConfig = {
  config: {
    auth: { baseUrls: { csr: 'http://127.0.0.1:8111' } },
    links: { termsOfService: '' },
  },
};

const runGuard = () => TestBed.runInInjectionContext(() => authGuard({} as any, {} as any));

describe('authGuard', () => {
  let authService: AuthService;
  let gateService: BattleGateService;

  const setup = (platformId: string, isAuthenticated: boolean) => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        BattleGateService,
        { provide: PLATFORM_ID, useValue: platformId },
        { provide: ConfigService, useValue: mockConfig },
      ],
    });
    authService = TestBed.inject(AuthService);
    gateService = TestBed.inject(BattleGateService);
    jest.spyOn(authService, 'isAuthenticated').mockReturnValue(isAuthenticated);
  };

  it('returns true when authenticated', () => {
    setup('browser', true);
    expect(runGuard()).toBe(true);
  });

  it('returns false when unauthenticated', () => {
    setup('browser', false);
    expect(runGuard()).toBe(false);
  });

  it('sets showLoginModal to true when unauthenticated in browser', () => {
    setup('browser', false);
    runGuard();
    expect(gateService.showLoginModal()).toBe(true);
  });

  it('does not set showLoginModal when unauthenticated on server', () => {
    setup('server', false);
    runGuard();
    expect(gateService.showLoginModal()).toBe(false);
  });
});
