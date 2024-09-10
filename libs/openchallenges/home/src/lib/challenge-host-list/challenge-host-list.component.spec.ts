import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';

import { ChallengeHostListComponent } from './challenge-host-list.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('ChallengeHostListComponent', () => {
  let component: ChallengeHostListComponent;
  let fixture: ComponentFixture<ChallengeHostListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, ChallengeHostListComponent, RouterTestingModule],
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
