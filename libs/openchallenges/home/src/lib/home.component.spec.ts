import { HttpClientModule } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfigService } from '@sagebionetworks/openchallenges/web/angular/config';

import { HomeComponent } from './home.component';
import { RouterTestingModule } from '@angular/router/testing';

// import ResizeObserver polyfill
global.ResizeObserver = jest.fn(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  const mockConfigService = {
    config: {
      app: {
        version: '1.0.0',
      },
      data: { updatedOn: '2025-01-01' },
      features: {
        announcement: { enabled: false },
      },
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
      imports: [HttpClientModule, RouterTestingModule, HomeComponent],
      providers: [{ provide: ConfigService, useValue: mockConfigService }],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
