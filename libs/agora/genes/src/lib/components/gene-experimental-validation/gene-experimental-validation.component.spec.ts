// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { GenesService, TeamsService } from '@sagebionetworks/agora/api-client-angular';
import { HelperService } from '@sagebionetworks/agora/services';
import { ExperimentalValidationComponent } from './gene-experimental-validation.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Gene Experimental Validation', () => {
  let fixture: ComponentFixture<ExperimentalValidationComponent>;
  let component: ExperimentalValidationComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [GenesService, TeamsService, HelperService, provideHttpClient()],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(ExperimentalValidationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
