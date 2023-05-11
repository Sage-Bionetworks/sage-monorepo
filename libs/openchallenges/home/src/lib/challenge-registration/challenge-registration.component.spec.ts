import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChallengeRegistrationComponent } from './challenge-registration.component';

describe('ChallengeRegistrationComponent', () => {
  let component: ChallengeRegistrationComponent;
  let fixture: ComponentFixture<ChallengeRegistrationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChallengeRegistrationComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChallengeRegistrationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
