import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { APP_CONFIG, MOCK_APP_CONFIG } from '@sage-bionetworks/web/config';

import { OrgSearchComponent } from './org-search.component';

describe('OrgSearchComponent', () => {
  let component: OrgSearchComponent;
  let fixture: ComponentFixture<OrgSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OrgSearchComponent],
      providers: [{ provide: APP_CONFIG, useValue: MOCK_APP_CONFIG }],
      schemas: [CUSTOM_ELEMENTS_SCHEMA],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(OrgSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
