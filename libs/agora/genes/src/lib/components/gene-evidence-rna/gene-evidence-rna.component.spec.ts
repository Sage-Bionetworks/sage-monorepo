// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { HelperService } from '@sagebionetworks/agora/services';
import { GeneEvidenceRnaComponent } from './gene-evidence-rna.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Gene RNA', () => {
  let fixture: ComponentFixture<GeneEvidenceRnaComponent>;
  let component: GeneEvidenceRnaComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [HelperService, provideHttpClient()],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneEvidenceRnaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
