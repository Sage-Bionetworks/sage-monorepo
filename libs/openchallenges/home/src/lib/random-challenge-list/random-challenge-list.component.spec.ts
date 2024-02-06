import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientModule } from '@angular/common/http';

import { RandomChallengeListComponent } from './random-challenge-list.component';
import { RouterTestingModule } from '@angular/router/testing';

describe('RandomChallengeListComponent', () => {
  let component: RandomChallengeListComponent;
  let fixture: ComponentFixture<RandomChallengeListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        HttpClientModule,
        RandomChallengeListComponent,
        RouterTestingModule,
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RandomChallengeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
