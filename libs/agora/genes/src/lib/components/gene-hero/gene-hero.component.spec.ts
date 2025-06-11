// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { ComponentFixture, TestBed } from '@angular/core/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { geneMock1, geneMock3 } from '@sagebionetworks/agora/testing';
import { GeneHeroComponent } from './gene-hero.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Gene Hero', () => {
  let fixture: ComponentFixture<GeneHeroComponent>;
  let component: GeneHeroComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({}).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneHeroComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show nomination and TEP text for MSN if nomination exists and is either is_tep or is_adi is true', () => {
    const expected = 'Nominated Target, Selected for Target Enabling Resource Development';

    component.gene = geneMock1;
    component.gene.is_tep = true;
    component.gene.is_adi = false;
    fixture.detectChanges();

    let el = element.querySelector('.gene-hero-nominated') as HTMLElement;

    expect(el.textContent?.trim()).toBe(expected);

    component.gene = geneMock1;
    component.gene.is_tep = false;
    component.gene.is_adi = true;
    fixture.detectChanges();

    el = element.querySelector('.gene-hero-nominated') as HTMLElement;

    expect(el.textContent?.trim()).toBe(expected);
  });

  it('should show nomination and not show TEP text for MSN if both is_tep and is_adi is false', () => {
    const expected = 'Nominated Target';

    component.gene = geneMock1;
    component.gene.is_tep = false;
    component.gene.is_adi = false;
    fixture.detectChanges();

    const el = element.querySelector('.gene-hero-nominated') as HTMLElement;

    expect(el.textContent?.trim()).toBe(expected);
  });

  it('should not show nomination and show TEP text for HCK if nominations is null and either is_tep or is_adi is true', () => {
    const expected = 'Selected for Target Enabling Resource Development';

    component.gene = geneMock3;
    component.gene.is_tep = false;
    component.gene.is_adi = true;
    fixture.detectChanges();

    let el = element.querySelector('.gene-hero-nominated') as HTMLElement;

    expect(el.textContent?.trim()).toBe(expected);

    component.gene = geneMock3;
    component.gene.is_adi = false;
    component.gene.is_tep = true;
    fixture.detectChanges();

    el = element.querySelector('.gene-hero-nominated') as HTMLElement;

    expect(el.textContent?.trim()).toBe(expected);
  });

  it('should not show nomination and not show TEP text for HCK if nominations is null and both is_tep or is_adi is false', () => {
    component.gene = geneMock3;
    component.gene.is_adi = false;
    component.gene.is_tep = false;
    fixture.detectChanges();

    const el = element.querySelector('.gene-hero-nominated');

    expect(el).toBe(null);
  });

  it('should comma separate and order the biodomains alphabetically', () => {
    component.gene = geneMock1;
    const expected =
      'Immune Response, Lipid Metabolism, Structural Stabilization, Synapse, Vasculature';
    expect(component.getBiodomains()).toBe(expected);
  });

  it('should return the ensembl permalink', () => {
    component.gene = geneMock1;
    expect(component.getEnsemblUrl()).toBe(
      'https://may2015.archive.ensembl.org/Homo_sapiens/Gene/Summary?db=core;g=ENSG00000264794',
    );
  });

  it('should return a url with ensembl id', () => {
    component.gene = geneMock1;
    expect(component.getPossibleReplacementsURL()).toBe(
      'https://useast.ensembl.org/Homo_sapiens/Gene/Idhistory?g=ENSG00000147065',
    );
  });
});
