import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { of, throwError } from 'rxjs';
import { ExamplePromptPage, ExamplePromptService } from '@sagebionetworks/bixarena/api-client';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { AuthService, BattleGateService } from '@sagebionetworks/bixarena/services';
import { ComposerSectionComponent } from './composer-section.component';

const mockConfig = {
  config: {
    auth: { baseUrls: { csr: 'http://127.0.0.1:8000' } },
    battle: { promptLengthLimit: 5000 },
    links: { termsOfService: '' },
  },
};

const STATIC_PLACEHOLDER = 'Ask anything biomedical...';

function pageOf(question: string | null): ExamplePromptPage {
  return {
    examplePrompts: question ? [{ question } as ExamplePromptPage['examplePrompts'][number]] : [],
  } as ExamplePromptPage;
}

function setReducedMotion(matches: boolean): void {
  Object.defineProperty(window, 'matchMedia', {
    writable: true,
    configurable: true,
    value: jest.fn().mockReturnValue({
      matches,
      media: '(prefers-reduced-motion: reduce)',
      addListener: jest.fn(),
      removeListener: jest.fn(),
      addEventListener: jest.fn(),
      removeEventListener: jest.fn(),
      dispatchEvent: jest.fn(),
    }),
  });
}

describe('ComposerSectionComponent', () => {
  let fixture: ComponentFixture<ComposerSectionComponent>;
  let component: ComposerSectionComponent;
  let auth: AuthService;
  let gate: BattleGateService;
  let router: Router;
  let listSpy: jest.Mock;

  async function setup(initialPage: ExamplePromptPage = pageOf('What is CRISPR?')) {
    listSpy = jest.fn().mockReturnValue(of(initialPage));
    sessionStorage.clear();

    await TestBed.configureTestingModule({
      imports: [ComposerSectionComponent],
      providers: [
        provideHttpClient(),
        { provide: PLATFORM_ID, useValue: 'browser' },
        { provide: ConfigService, useValue: mockConfig },
        { provide: ExamplePromptService, useValue: { listExamplePrompts: listSpy } },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ComposerSectionComponent);
    component = fixture.componentInstance;
    auth = TestBed.inject(AuthService);
    gate = TestBed.inject(BattleGateService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  }

  beforeEach(() => {
    jest.useFakeTimers();
    setReducedMotion(false);
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  it('uses the configured prompt length limit', async () => {
    await setup();
    expect(component.promptLengthLimit).toBe(5000);
  });

  it('saves the prompt and navigates to /battle when authenticated', async () => {
    await setup();
    auth.user.set({ sub: 'user-1', preferred_username: 'tester' });
    const navSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.onPromptSubmit('What is CRISPR?');

    expect(sessionStorage.getItem('bixarena.pendingPrompt')).toBe('What is CRISPR?');
    expect(navSpy).toHaveBeenCalledWith(['/battle']);
    expect(gate.showLoginModal()).toBe(false);
  });

  it('saves the prompt and opens the login modal when unauthenticated', async () => {
    await setup();
    auth.user.set(null);
    const navSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.onPromptSubmit('What is CRISPR?');

    expect(sessionStorage.getItem('bixarena.pendingPrompt')).toBe('What is CRISPR?');
    expect(gate.showLoginModal()).toBe(true);
    expect(navSpy).not.toHaveBeenCalled();
  });

  it('skips animation and shows the full prompt when prefers-reduced-motion is set', async () => {
    setReducedMotion(true);
    await setup(pageOf('Static prompt'));
    expect(component.placeholder()).toBe('Static prompt');
  });

  it('keeps the static placeholder when the API returns no prompts', async () => {
    await setup(pageOf(null));
    jest.advanceTimersByTime(200);
    expect(component.placeholder()).toBe(STATIC_PLACEHOLDER);
  });

  it('keeps the static placeholder when the API call fails', async () => {
    listSpy = jest.fn().mockReturnValue(throwError(() => new Error('boom')));
    sessionStorage.clear();
    await TestBed.configureTestingModule({
      imports: [ComposerSectionComponent],
      providers: [
        provideHttpClient(),
        { provide: PLATFORM_ID, useValue: 'browser' },
        { provide: ConfigService, useValue: mockConfig },
        { provide: ExamplePromptService, useValue: { listExamplePrompts: listSpy } },
      ],
    }).compileComponents();
    fixture = TestBed.createComponent(ComposerSectionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    expect(component.placeholder()).toBe(STATIC_PLACEHOLDER);
  });
});
