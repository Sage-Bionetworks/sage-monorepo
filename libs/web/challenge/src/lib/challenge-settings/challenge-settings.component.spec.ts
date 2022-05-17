import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChallengeSettingsComponent } from './challenge-settings.component';

describe('ChallengeSettingsComponent', () => {
  let component: ChallengeSettingsComponent;
  let fixture: ComponentFixture<ChallengeSettingsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChallengeSettingsComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChallengeSettingsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
