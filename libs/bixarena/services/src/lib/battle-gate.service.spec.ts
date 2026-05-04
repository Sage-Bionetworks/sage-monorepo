import { TestBed } from '@angular/core/testing';
import { PLATFORM_ID, signal } from '@angular/core';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { AuthService } from './auth.service';
import { BattleGateService } from './battle-gate.service';
import { OnboardingService } from './onboarding.service';

const mockConfig = {
  config: {
    auth: { baseUrls: { csr: 'http://127.0.0.1:8000' } },
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
    sessionStorage.clear();

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

    it('clears any pending prompt so a later /battle visit lands on empty composer', () => {
      service.savePendingPrompt('Q1', 'ep-p1');
      service.showLoginModal.set(true);
      service.onLoginCancel();
      expect(service.consumePendingPrompt()).toBeNull();
    });
  });

  describe('pending prompt', () => {
    it('savePendingPrompt persists trimmed text and consumePendingPrompt returns + clears it', () => {
      service.savePendingPrompt('  hello world  ');
      expect(service.consumePendingPrompt()).toEqual({
        prompt: 'hello world',
        examplePromptId: null,
      });
      expect(service.consumePendingPrompt()).toBeNull();
    });

    it('savePendingPrompt persists the example_prompt FK when provided', () => {
      service.savePendingPrompt('curated question', 'ep-42');
      expect(service.consumePendingPrompt()).toEqual({
        prompt: 'curated question',
        examplePromptId: 'ep-42',
      });
    });

    it('savePendingPrompt without an FK clears any previously saved FK', () => {
      service.savePendingPrompt('first', 'ep-old');
      service.savePendingPrompt('second');
      expect(service.consumePendingPrompt()).toEqual({
        prompt: 'second',
        examplePromptId: null,
      });
    });

    it('savePendingPrompt ignores empty / whitespace input', () => {
      service.savePendingPrompt('   ');
      expect(service.consumePendingPrompt()).toBeNull();
    });

    it('consumePendingPrompt returns null when nothing is saved', () => {
      expect(service.consumePendingPrompt()).toBeNull();
    });

    it('consumePendingPrompt returns null and clears both keys when prompt is whitespace-only', () => {
      sessionStorage.setItem('bixarena.pendingPrompt', '   ');
      sessionStorage.setItem('bixarena.pendingExamplePromptId', 'ep-stale');
      expect(service.consumePendingPrompt()).toBeNull();
      expect(sessionStorage.getItem('bixarena.pendingPrompt')).toBeNull();
      expect(sessionStorage.getItem('bixarena.pendingExamplePromptId')).toBeNull();
    });
  });
});
