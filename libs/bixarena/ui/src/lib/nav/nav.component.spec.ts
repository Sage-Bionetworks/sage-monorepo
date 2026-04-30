import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { AuthService } from '@sagebionetworks/bixarena/services';
import { NavComponent } from './nav.component';

describe('NavComponent', () => {
  let component: NavComponent;
  let fixture: ComponentFixture<NavComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NavComponent],
      providers: [
        provideRouter([]),
        {
          provide: ConfigService,
          useValue: {
            config: {
              auth: { baseUrls: { csr: 'http://localhost:8113' } },
            },
          },
        },
        {
          provide: AuthService,
          useValue: {
            user: () => null,
            isAuthenticated: () => false,
            cachedUser: () => null,
            login: () => undefined,
            logout: () => undefined,
            init: () => Promise.resolve(),
          },
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(NavComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('starts with menu closed', () => {
    expect(component.menuOpen()).toBe(false);
  });

  it('toggleMenu flips menuOpen', () => {
    component.toggleMenu();
    expect(component.menuOpen()).toBe(true);
    component.toggleMenu();
    expect(component.menuOpen()).toBe(false);
  });

  it('closeMenu sets menuOpen to false when open', () => {
    component.toggleMenu();
    expect(component.menuOpen()).toBe(true);
    component.closeMenu();
    expect(component.menuOpen()).toBe(false);
  });

  it('Escape key closes the menu', () => {
    component.toggleMenu();
    expect(component.menuOpen()).toBe(true);
    component.onEscape();
    expect(component.menuOpen()).toBe(false);
  });

  it('resize past md closes the menu', () => {
    component.toggleMenu();
    expect(component.menuOpen()).toBe(true);
    Object.defineProperty(window, 'innerWidth', { configurable: true, value: 1024 });
    component.onResize();
    expect(component.menuOpen()).toBe(false);
  });
});
