// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { GenesService } from '@sagebionetworks/agora/api-client-angular';
import { HelperService } from '@sagebionetworks/agora/services';
import { GeneEvidenceMetabolomicsComponent } from './gene-evidence-metabolomics.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Gene Metabolomics', () => {
  let fixture: ComponentFixture<GeneEvidenceMetabolomicsComponent>;
  let component: GeneEvidenceMetabolomicsComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [GenesService, HelperService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneEvidenceMetabolomicsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
