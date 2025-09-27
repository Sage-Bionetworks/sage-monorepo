// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { provideHttpClient } from '@angular/common/http';
import { GeneService } from '@sagebionetworks/agora/api-client';
import { HelperService, SvgIconService } from '@sagebionetworks/agora/services';
import { SvgIconServiceStub } from '@sagebionetworks/agora/testing';
import { GeneEvidenceMetabolomicsComponent } from './gene-evidence-metabolomics.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Gene Metabolomics', () => {
  let fixture: ComponentFixture<GeneEvidenceMetabolomicsComponent>;
  let component: GeneEvidenceMetabolomicsComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [
        GeneService,
        HelperService,
        provideHttpClient(),
        { provide: SvgIconService, useClass: SvgIconServiceStub },
      ],
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
