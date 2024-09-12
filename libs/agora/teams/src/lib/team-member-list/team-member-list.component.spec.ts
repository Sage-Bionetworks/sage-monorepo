// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { TeamMemberListComponent } from './team-member-list.component';
import { TeamService } from '../../services';
// import { ApiService } from '../../../../core/services';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Team Member List', () => {
  let fixture: ComponentFixture<TeamMemberListComponent>;
  let component: TeamMemberListComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TeamMemberListComponent],
      imports: [RouterTestingModule, HttpClientTestingModule],
      providers: [TeamService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(TeamMemberListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
