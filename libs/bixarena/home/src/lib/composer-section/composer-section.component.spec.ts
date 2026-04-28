import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PLATFORM_ID } from '@angular/core';
import { Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { AuthService, BattleGateService } from '@sagebionetworks/bixarena/services';
import { ComposerSectionComponent } from './composer-section.component';

const mockConfig = {
  config: {
    auth: { baseUrls: { csr: 'http://127.0.0.1:8111' } },
    battle: { promptLengthLimit: 5000 },
    links: { termsOfService: '' },
  },
};

describe('ComposerSectionComponent', () => {
  let fixture: ComponentFixture<ComposerSectionComponent>;
  let component: ComposerSectionComponent;
  let auth: AuthService;
  let gate: BattleGateService;
  let router: Router;

  beforeEach(async () => {
    sessionStorage.clear();

    await TestBed.configureTestingModule({
      imports: [ComposerSectionComponent],
      providers: [
        provideHttpClient(),
        { provide: PLATFORM_ID, useValue: 'browser' },
        { provide: ConfigService, useValue: mockConfig },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ComposerSectionComponent);
    component = fixture.componentInstance;
    auth = TestBed.inject(AuthService);
    gate = TestBed.inject(BattleGateService);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('uses the configured prompt length limit', () => {
    expect(component.promptLengthLimit).toBe(5000);
  });

  it('saves the prompt and navigates to /battle when authenticated', () => {
    auth.user.set({ sub: 'user-1', preferred_username: 'tester' });
    const navSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.onPromptSubmit('What is CRISPR?');

    expect(sessionStorage.getItem('bixarena.pendingPrompt')).toBe('What is CRISPR?');
    expect(navSpy).toHaveBeenCalledWith(['/battle']);
    expect(gate.showLoginModal()).toBe(false);
  });

  it('saves the prompt and opens the login modal when unauthenticated', () => {
    auth.user.set(null);
    const navSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    component.onPromptSubmit('What is CRISPR?');

    expect(sessionStorage.getItem('bixarena.pendingPrompt')).toBe('What is CRISPR?');
    expect(gate.showLoginModal()).toBe(true);
    expect(navSpy).not.toHaveBeenCalled();
  });
});
