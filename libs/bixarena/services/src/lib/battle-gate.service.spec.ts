import { TestBed } from '@angular/core/testing';
import { PLATFORM_ID, signal } from '@angular/core';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { AuthService } from './auth.service';
import { BattleGateService } from './battle-gate.service';
import { OnboardingService } from './onboarding.service';

const mockConfig = {
  config: {
    auth: { baseUrls: { csr: 'http://127.0.0.1:8111' } },
    links: { termsOfService: '' },
  },
};

describe('BattleGateService', () => {
  let service: BattleGateService;
  let authService: AuthService;
  const hasCompleted = signal(false);
  const mockOnboardingService = {
    hasCompleted,
    markComplete: jest.fn(),
  };

  beforeEach(() => {
    hasCompleted.set(false);
    mockOnboardingService.markComplete.mockReset();

    TestBed.configureTestingModule({
      providers: [
        BattleGateService,
        AuthService,
        { provide: OnboardingService, useValue: mockOnboardingService },
        { provide: PLATFORM_ID, useValue: 'browser' },
        { provide: ConfigService, useValue: mockConfig },
      ],
    });
    service = TestBed.inject(BattleGateService);
    authService = TestBed.inject(AuthService);
  });

  describe('checkOnboarding()', () => {
    it('resolves true immediately when onboarding is already completed', async () => {
      hasCompleted.set(true);
      expect(await service.checkOnboarding()).toBe(true);
    });

    it('shows onboarding modal when not completed', () => {
      void service.checkOnboarding();
      expect(service.showOnboardingModal()).toBe(true);
    });

    it('resolves true when completed via onOnboardingComplete', async () => {
      const promise = service.checkOnboarding();
      service.onOnboardingComplete(false);
      expect(await promise).toBe(true);
    });

    it('resolves false when dismissed via onOnboardingDismiss', async () => {
      const promise = service.checkOnboarding();
      service.onOnboardingDismiss();
      expect(await promise).toBe(false);
    });
  });

  describe('onOnboardingComplete()', () => {
    it('hides onboarding modal', () => {
      void service.checkOnboarding();
      service.onOnboardingComplete(false);
      expect(service.showOnboardingModal()).toBe(false);
    });

    it('calls markComplete with dontShowAgain flag', () => {
      void service.checkOnboarding();
      service.onOnboardingComplete(true);
      expect(mockOnboardingService.markComplete).toHaveBeenCalledWith(true);
    });
  });

  describe('onOnboardingDismiss()', () => {
    it('hides onboarding modal', () => {
      void service.checkOnboarding();
      service.onOnboardingDismiss();
      expect(service.showOnboardingModal()).toBe(false);
    });
  });

  describe('onLoginComplete()', () => {
    it('hides login modal and calls auth login', () => {
      jest.spyOn(authService, 'login').mockImplementation(jest.fn());
      service.showLoginModal.set(true);
      service.onLoginComplete();
      expect(service.showLoginModal()).toBe(false);
      expect(authService.login).toHaveBeenCalled();
    });
  });

  describe('onLoginCancel()', () => {
    it('hides login modal', () => {
      service.showLoginModal.set(true);
      service.onLoginCancel();
      expect(service.showLoginModal()).toBe(false);
    });
  });
});
