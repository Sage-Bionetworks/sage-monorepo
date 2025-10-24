import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AboutComponent } from './about.component';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { ConfigService } from '@sagebionetworks/openchallenges/web/angular/config';

describe('AboutComponent', () => {
  let component: AboutComponent;
  let fixture: ComponentFixture<AboutComponent>;

  const mockConfigService = {
    config: {
      app: { version: '1.0.0' },
      data: { updatedOn: '2025-01-01' },
      links: {
        privacyPolicy: 'https://example.com/privacy',
        termsOfUse: 'https://example.com/terms',
      },
      api: {
        docsUrl: 'https://example.com/api-docs',
      },
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [
        provideRouter([]),
        provideHttpClient(),
        { provide: ConfigService, useValue: mockConfigService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AboutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
