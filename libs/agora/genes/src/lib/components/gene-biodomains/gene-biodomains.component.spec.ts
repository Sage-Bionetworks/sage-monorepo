// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { HelperService } from '@sagebionetworks/agora/services';
import { emptyBioDomainMock, geneMock1 } from '@sagebionetworks/agora/testing';
import { GeneBioDomainsComponent } from './gene-biodomains.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Gene Biodomains', () => {
  let fixture: ComponentFixture<GeneBioDomainsComponent>;
  let component: GeneBioDomainsComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [HelperService, provideRouter([])],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneBioDomainsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return an empty string when getGeneName() is called with undefined gene', () => {
    component.gene = undefined;
    const result = component.getGeneName();
    expect(result).toBe('');
  });

  it('should return gene name when getGeneName() is called with a defined gene', () => {
    component.gene = geneMock1;
    const result = component.getGeneName();
    expect(result).toBe('MSN');
  });

  it('should return NO LINKING GO TERMS when getHeaderText() is called with no go terms', () => {
    component.selectedBioDomain = emptyBioDomainMock;
    component.goTerms = [];
    const result = component.getHeaderText();
    expect(result).toBe(`NO LINKING GO TERMS FOR ${emptyBioDomainMock.biodomain.toUpperCase()}`);
  });
});
