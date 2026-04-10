import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { EMPTY } from 'rxjs';
import { BattleService as BattleApiService, BASE_PATH } from '@sagebionetworks/bixarena/api-client';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { BattleComponent } from './battle.component';

const mockConfig = {
  config: {
    battle: { promptLengthLimit: 5000, roundLimit: 20, promptUseLimit: 5 },
  },
};

describe('BattleComponent', () => {
  let component: BattleComponent;
  let fixture: ComponentFixture<BattleComponent>;

  beforeEach(async () => {
    // eslint-disable-next-line @typescript-eslint/no-empty-function
    globalThis.fetch = jest.fn().mockReturnValue(new Promise(() => {}));

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
});
