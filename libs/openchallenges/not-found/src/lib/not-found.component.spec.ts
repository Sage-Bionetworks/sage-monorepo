import { HttpClientModule } from '@angular/common/http';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ConfigService } from '@sagebionetworks/openchallenges/web/angular/config';

import { NotFoundComponent } from './not-found.component';

describe('NotFoundComponent', () => {
  let component: NotFoundComponent;
  let fixture: ComponentFixture<NotFoundComponent>;

  const mockConfigService = {
    config: {
      app: { version: '1.0.0' },
      data: { updatedOn: '2025-01-01' },
      urls: {
        privacyPolicy: 'https://example.com/privacy',
        termsOfUse: 'https://example.com/terms',
      },
      api: {
        docs: { url: 'https://example.com/api-docs' },
      },
    },
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, RouterTestingModule, NotFoundComponent],
      providers: [{ provide: ConfigService, useValue: mockConfigService }],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NotFoundComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
