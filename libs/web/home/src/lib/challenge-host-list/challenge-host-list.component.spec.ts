import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChallengeHostListComponent } from './challenge-host-list.component';

describe('ChallengeHostListComponent', () => {
  let component: ChallengeHostListComponent;
  let fixture: ComponentFixture<ChallengeHostListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ChallengeHostListComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChallengeHostListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
