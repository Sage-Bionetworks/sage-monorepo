import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ConfigService } from '@sagebionetworks/bixarena/config';
import { ThemeService } from '@sagebionetworks/bixarena/services';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppComponent],
      providers: [
        provideRouter([]),
        {
          provide: ConfigService,
          useValue: { config: { app: { version: 'test' } } },
        },
        {
          provide: ThemeService,
          useValue: { init: () => undefined, toggle: () => undefined, isDark: () => false },
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
