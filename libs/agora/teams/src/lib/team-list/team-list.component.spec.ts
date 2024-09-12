// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { TestBed, ComponentFixture } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { TeamListComponent } from './team-list.component';
import { TeamService } from '../../services';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Team List', () => {
  let fixture: ComponentFixture<TeamListComponent>;
  let component: TeamListComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [TeamListComponent],
      imports: [RouterTestingModule],
      providers: [TeamService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(TeamListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
