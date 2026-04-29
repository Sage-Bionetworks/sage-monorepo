import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import {
  ExamplePrompt,
  ExamplePromptPage,
  ExamplePromptService,
  ExamplePromptSort,
} from '@sagebionetworks/bixarena/api-client';
import { AuthService, BattleGateService } from '@sagebionetworks/bixarena/services';
import { TrendingSectionComponent } from './trending-section.component';

function pageOf(prompts: Partial<ExamplePrompt>[]): ExamplePromptPage {
  return { examplePrompts: prompts as ExamplePrompt[] } as ExamplePromptPage;
}

const SAMPLE_PROMPTS: Partial<ExamplePrompt>[] = [
  { id: 'p1', question: 'Q1', battleCount: 12 },
  { id: 'p2', question: 'Q2', battleCount: 8 },
  { id: 'p3', question: 'Q3', battleCount: 5 },
];

describe('TrendingSectionComponent', () => {
  let fixture: ComponentFixture<TrendingSectionComponent>;
  let component: TrendingSectionComponent;
  let auth: AuthService;
  let gate: BattleGateService;
  let router: Router;
  let listSpy: jest.Mock;

  async function setup(returnValue = of(pageOf(SAMPLE_PROMPTS))) {
    listSpy = jest.fn().mockReturnValue(returnValue);
    sessionStorage.clear();

    await TestBed.configureTestingModule({
      imports: [TrendingSectionComponent],
      providers: [
        provideHttpClient(),
        { provide: PLATFORM_ID, useValue: 'browser' },
        { provide: ExamplePromptService, useValue: { listExamplePrompts: listSpy } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TrendingSectionComponent);
    component = fixture.componentInstance;
    auth = TestBed.inject(AuthService);
    gate = TestBed.inject(BattleGateService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  }

  it('queries trending prompts with sort=usage and lookback=7', async () => {
    await setup();
    expect(listSpy).toHaveBeenCalledTimes(1);
    expect(listSpy.mock.calls[0][0]).toMatchObject({
      sort: ExamplePromptSort.Usage,
      lookback: 7,
      pageSize: 3,
    });
  });

  it('renders three cards when the API returns data', async () => {
    await setup();
    expect(fixture.nativeElement.querySelector('.trending-section')).toBeTruthy();
    const cards = fixture.nativeElement.querySelectorAll('bixarena-prompt-card');
    expect(cards.length).toBe(3);
  });

  it('hides the section when the API returns no prompts', async () => {
    await setup(of(pageOf([])));
    expect(fixture.nativeElement.querySelector('.trending-section')).toBeNull();
    expect(component.visible()).toBe(false);
  });

  it('hides the section when the API call fails', async () => {
    await setup(throwError(() => new Error('boom')));
    expect(fixture.nativeElement.querySelector('.trending-section')).toBeNull();
    expect(component.visible()).toBe(false);
  });

  it('saves the prompt and navigates to /battle when authenticated', async () => {
    await setup();
    auth.user.set({ sub: 'user-1', preferred_username: 'tester' });
    const navSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.onCardClick(SAMPLE_PROMPTS[0] as ExamplePrompt);

    expect(sessionStorage.getItem('bixarena.pendingPrompt')).toBe('Q1');
    expect(navSpy).toHaveBeenCalledWith(['/battle']);
    expect(gate.showLoginModal()).toBe(false);
  });

  it('saves the prompt and opens the login modal when unauthenticated', async () => {
    await setup();
    auth.user.set(null);
    const navSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.onCardClick(SAMPLE_PROMPTS[0] as ExamplePrompt);

    expect(sessionStorage.getItem('bixarena.pendingPrompt')).toBe('Q1');
    expect(gate.showLoginModal()).toBe(true);
    expect(navSpy).not.toHaveBeenCalled();
  });
});
