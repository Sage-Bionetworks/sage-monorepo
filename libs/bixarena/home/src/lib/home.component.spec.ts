import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { provideRouter } from '@angular/router';
import { AuthService, BattleGateService } from '@sagebionetworks/bixarena/services';
import { HomeComponent } from './home.component';

describe('HomeComponent', () => {
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    // Strip section imports so they don't mount real services (each child
    // fires HTTP at construction). This spec only asserts layout shell.
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: { isAuthenticated: () => false } },
        {
          provide: BattleGateService,
          useValue: { hasPendingPrompt: () => false, clearPending: () => undefined },
        },
      ],
    })
      .overrideComponent(HomeComponent, {
        set: { imports: [], schemas: [NO_ERRORS_SCHEMA] },
      })
      .compileComponents();
    fixture = TestBed.createComponent(HomeComponent);
    fixture.detectChanges();
  });

  it('renders the hero title with biomedical highlighted', () => {
    const root = fixture.nativeElement as HTMLElement;
    const title = root.querySelector('.hero .title');
    expect(title?.textContent?.replace(/\s+/g, ' ').trim()).toBe(
      'Your vote shapes the future ofbiomedical AI',
    );
    expect(title?.querySelector('em')?.textContent).toBe('biomedical');
  });
});
