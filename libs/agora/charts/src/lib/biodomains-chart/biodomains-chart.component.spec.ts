// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { HelperService } from '@sagebionetworks/agora/services';
import { getRandomInt } from '@sagebionetworks/shared/util';
import { geneMock1 } from '@sagebionetworks/agora/testing';
import { BiodomainsChartComponent } from './biodomains-chart.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Biodomains Chart', () => {
  let fixture: ComponentFixture<BiodomainsChartComponent>;
  let component: BiodomainsChartComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [],
      providers: [HelperService],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(BiodomainsChartComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have tooltip text if there are linking terms', () => {
    component.geneName = geneMock1.hgnc_symbol || geneMock1.ensembl_gene_id;
    const linkingTerms = getRandomInt(1, 100);
    const expected = 'Click to explore to GO Terms that link this biological domain to MSN';
    expect(component.getToolTipText(linkingTerms)).toBe(expected);
  });

  it('should have tooltip text if there are no linking terms', () => {
    component.geneName = geneMock1.hgnc_symbol || geneMock1.ensembl_gene_id;
    const linkingTerms = 0;
    const expected = 'No GO Terms link this biological domain to MSN';
    expect(component.getToolTipText(linkingTerms)).toBe(expected);
  });
});
