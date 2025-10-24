import { HttpClientModule } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConfigService } from '@sagebionetworks/openchallenges/web/angular/config';
import { RouterTestingModule } from '@angular/router/testing';
import { ChallengeSearchComponent } from './challenge-search.component';

describe('ChallengeSearchComponent', () => {
  let component: ChallengeSearchComponent;
  let fixture: ComponentFixture<ChallengeSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientModule, ChallengeSearchComponent, RouterTestingModule],
      providers: [ConfigService],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChallengeSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
