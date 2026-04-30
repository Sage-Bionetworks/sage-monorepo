import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { EMPTY } from 'rxjs';
import { BattleService as BattleApiService, BASE_PATH } from '@sagebionetworks/bixarena/api-client';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { BattleGateService } from '@sagebionetworks/bixarena/services';
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

  beforeEach(async () => {
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    globalThis.fetch = jest.fn().mockReturnValue(new Promise(() => {}));
    sessionStorage.clear();

    await TestBed.configureTestingModule({
      imports: [BattleComponent],
      providers: [
        provideHttpClient(),
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

  it('auto-submits a saved pending prompt on init', async () => {
    const gate = TestBed.inject(BattleGateService);
    gate.savePendingPrompt('hello biomedical world');
    jest.spyOn(BattleGateService.prototype, 'checkOnboarding').mockResolvedValue(true);

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
    jest.spyOn(BattleGateService.prototype, 'checkOnboarding').mockResolvedValue(true);

    fixture = TestBed.createComponent(BattleComponent);
    component = fixture.componentInstance;
    const submitSpy = jest.spyOn(component.state, 'submitPrompt').mockResolvedValue();
    fixture.detectChanges();
    await Promise.resolve();

    expect(submitSpy).toHaveBeenCalledWith('curated question', 'ep-99');
    expect(gate.consumePendingPrompt()).toBeNull();
  });
});
