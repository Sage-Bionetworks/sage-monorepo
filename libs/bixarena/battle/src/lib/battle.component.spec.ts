import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideNoopAnimations } from '@angular/platform-browser/animations';
import { EMPTY } from 'rxjs';
import { BattleService as BattleApiService, BASE_PATH } from '@sagebionetworks/bixarena/api-client';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { BattleGateService, OnboardingService } from '@sagebionetworks/bixarena/services';
import { BattleComponent } from './battle.component';

const mockConfig = {
  config: {
    battle: { promptLengthLimit: 5000, roundLimit: 20, promptUseLimit: 5 },
    links: { termsOfService: 'https://example.com/terms' },
  },
};

describe('BattleComponent', () => {
  let component: BattleComponent;
  let fixture: ComponentFixture<BattleComponent>;
  let onboarding: OnboardingService;

  beforeEach(async () => {
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    globalThis.fetch = jest.fn().mockReturnValue(new Promise(() => {}));
    sessionStorage.clear();
    localStorage.clear();

    Object.defineProperty(window, 'matchMedia', {
      writable: true,
      configurable: true,
      value: jest.fn().mockReturnValue({
        matches: false,
        media: '',
        addListener: jest.fn(),
        removeListener: jest.fn(),
        addEventListener: jest.fn(),
        removeEventListener: jest.fn(),
        dispatchEvent: jest.fn(),
      }),
    });

    await TestBed.configureTestingModule({
      imports: [BattleComponent],
      providers: [
        provideHttpClient(),
        provideNoopAnimations(),
        { provide: PLATFORM_ID, useValue: 'browser' },
        { provide: BASE_PATH, useValue: 'http://test/api/v1' },
        { provide: ConfigService, useValue: mockConfig },
        {
          provide: BattleApiService,
          useValue: {
            createBattle: jest.fn().mockReturnValue(EMPTY),
            createBattleRound: jest.fn().mockReturnValue(EMPTY),
            createBattleEvaluation: jest.fn().mockReturnValue(EMPTY),
            updateBattle: jest.fn().mockReturnValue(EMPTY),
          },
        },
      ],
    }).compileComponents();

    onboarding = TestBed.inject(OnboardingService);
    onboarding.markSeen();

    fixture = TestBed.createComponent(BattleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should start in landing phase', () => {
    expect(component.state.phase()).toBe('landing');
  });

  it('does not consume a pending prompt when none is saved', () => {
    const consumeSpy = jest.spyOn(BattleGateService.prototype, 'consumePendingPrompt');
    fixture = TestBed.createComponent(BattleComponent);
    fixture.detectChanges();
    expect(consumeSpy).toHaveBeenCalledTimes(1);
    expect(consumeSpy.mock.results[0].value).toBeNull();
  });

  it('auto-submits a saved pending prompt on init when onboarding has been seen', async () => {
    const gate = TestBed.inject(BattleGateService);
    gate.savePendingPrompt('hello biomedical world');

    fixture = TestBed.createComponent(BattleComponent);
    component = fixture.componentInstance;
    const submitSpy = jest.spyOn(component.state, 'submitPrompt').mockResolvedValue();
    fixture.detectChanges();
    await Promise.resolve();

    expect(submitSpy).toHaveBeenCalledWith('hello biomedical world', null);
    expect(gate.consumePendingPrompt()).toBeNull();
  });

  it('forwards a pending example_prompt id when present', async () => {
    const gate = TestBed.inject(BattleGateService);
    gate.savePendingPrompt('curated question', 'ep-99');

    fixture = TestBed.createComponent(BattleComponent);
    component = fixture.componentInstance;
    const submitSpy = jest.spyOn(component.state, 'submitPrompt').mockResolvedValue();
    fixture.detectChanges();
    await Promise.resolve();

    expect(submitSpy).toHaveBeenCalledWith('curated question', 'ep-99');
    expect(gate.consumePendingPrompt()).toBeNull();
  });

  it('opens onboarding modal and defers pending prompt for first-time users', async () => {
    localStorage.clear();
    const gate = TestBed.inject(BattleGateService);
    gate.savePendingPrompt('deferred prompt', 'ep-1');

    fixture = TestBed.createComponent(BattleComponent);
    component = fixture.componentInstance;
    const submitSpy = jest.spyOn(component.state, 'submitPrompt').mockResolvedValue();
    fixture.detectChanges();
    await Promise.resolve();

    expect(component.showOnboardingModal()).toBe(true);
    expect(submitSpy).not.toHaveBeenCalled();

    component.onOnboardingClose();
    await Promise.resolve();

    expect(component.showOnboardingModal()).toBe(false);
    expect(submitSpy).toHaveBeenCalledWith('deferred prompt', 'ep-1');
    expect(onboarding.hasSeen()).toBe(true);
  });

  it('opens onboarding without a pending prompt for first-time users with no submission', () => {
    localStorage.clear();

    fixture = TestBed.createComponent(BattleComponent);
    component = fixture.componentInstance;
    const submitSpy = jest.spyOn(component.state, 'submitPrompt').mockResolvedValue();
    fixture.detectChanges();

    expect(component.showOnboardingModal()).toBe(true);
    expect(submitSpy).not.toHaveBeenCalled();

    component.onOnboardingClose();

    expect(component.showOnboardingModal()).toBe(false);
    expect(submitSpy).not.toHaveBeenCalled();
  });
});
