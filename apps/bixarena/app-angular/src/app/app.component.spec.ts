import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { AuthService, ThemeService } from '@sagebionetworks/bixarena/services';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        provideRouter([]),
        {
          provide: ConfigService,
          useValue: {
            config: {
              app: { version: 'test' },
              links: {
                termsOfService: '',
                contact: '',
                feedback: '',
                sageBionetworks: '',
              },
              auth: { baseUrls: { csr: '' } },
              analytics: {
                googleTagManager: { enabled: false, id: '' },
              },
            },
          },
        },
        {
          provide: ThemeService,
          useValue: { init: () => undefined, toggle: () => undefined, isDark: () => false },
        },
        {
          provide: AuthService,
          useValue: {
            user: () => null,
            isAuthenticated: () => false,
            cachedUsername: () => null,
            login: () => undefined,
            logout: () => undefined,
            init: () => Promise.resolve(),
          },
        },
      ],
    }).compileComponents();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });
});
