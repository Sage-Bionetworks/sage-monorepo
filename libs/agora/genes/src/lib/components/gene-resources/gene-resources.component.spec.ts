import { ComponentFixture, TestBed } from '@angular/core/testing';
import { geneMock1, noHGNCgeneMock } from '@sagebionetworks/agora/testing';
import { GeneResourcesComponent } from './gene-resources.component';

describe('Component: Gene Resources', () => {
  let fixture: ComponentFixture<GeneResourcesComponent>;
  let component: GeneResourcesComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({}).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneResourcesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not display TREAT-AD resource section if is_tep and is_adi is false', () => {
    component.gene = geneMock1;
    component.gene.is_adi = false;
    component.gene.is_tep = false;

    fixture.detectChanges();

    const header = element.querySelector('#target-enabling-resources-header');
    expect(header).toBe(null);

    const resource_url = element.querySelector('#target-enabling-resources-url');
    expect(resource_url).toBe(null);
  });

  it('should display TREAT-AD resource sections if is_tep or is_adi is true', () => {
    component.gene = geneMock1;
    component.gene.is_adi = false;
    component.gene.is_tep = true;

    fixture.detectChanges();

    const expectedHeader = 'Target Enabling Resources';
    let elHeader = element.querySelector('#target-enabling-resources-header') as HTMLElement;
    expect(elHeader.textContent?.trim()).toBe(expectedHeader);

    const expectedCard1 = 'Target Enabling Resources';
    let elCard1 = element.querySelector('#target-enabling-resources-card1') as HTMLElement;
    expect(elCard1.textContent?.trim()).toBe(expectedCard1);

    const expectedCard2 = 'Target Portfolio';
    const elCard2 = element.querySelector('#target-enabling-resources-card2') as HTMLElement;
    expect(elCard2.textContent?.trim()).toBe(expectedCard2);

    // adi is false so card3 should be null
    const card3 = element.querySelector('#target-enabling-resources-card3');
    expect(card3).toBe(null);

    // switch the booleans on adi and tep
    component.gene = geneMock1;
    component.gene.is_adi = true;
    component.gene.is_tep = false;

    fixture.detectChanges();

    elHeader = element.querySelector('#target-enabling-resources-header') as HTMLElement;
    expect(elHeader.textContent?.trim()).toBe(expectedHeader);

    elCard1 = element.querySelector('#target-enabling-resources-card1') as HTMLElement;
    expect(elCard1.textContent?.trim()).toBe(expectedCard1);

    // tep is false so card2 should be null
    const card2 = element.querySelector('#target-enabling-resources-card2');
    expect(card2).toBe(null);

    const expectedCard3 = 'AD Informer Set';
    const elCard3 = element.querySelector('#target-enabling-resources-card3') as HTMLElement;
    expect(elCard3.textContent?.trim()).toBe(expectedCard3);
  });

  it('should have an hgnc link to Pub AD if the gene has an hgnc symbol', () => {
    component.gene = geneMock1;
    component.init();

    fixture.detectChanges();

    const expectedLinkAddress = 'https://adexplorer.medicine.iu.edu/pubad/external/MSN';

    const additionalResourceLinks = element.querySelectorAll(
      'a.additional-resource-links.link.no-bold',
    );

    let pubADLink: Element | undefined;
    additionalResourceLinks.forEach((a) => {
      if (a.textContent?.trim() === 'Visit PubAD') {
        pubADLink = a;
      }
    });

    expect(pubADLink).toBeTruthy();

    const result = pubADLink?.getAttribute('href');
    expect(result).toBe(expectedLinkAddress);
  });

  it('should have a default link to Pub AD if the gene does not have an hgnc symbol', () => {
    component.gene = noHGNCgeneMock;
    component.init();

    fixture.detectChanges();

    const expectedLinkAddress = 'https://adexplorer.medicine.iu.edu/pubad';

    const additionalResourceLinks = element.querySelectorAll(
      'a.additional-resource-links.link.no-bold',
    );

    let pubADLink: Element | undefined;
    additionalResourceLinks.forEach((a) => {
      if (a.textContent?.trim() === 'Visit PubAD') {
        pubADLink = a;
      }
    });

    expect(pubADLink).toBeTruthy();

    const result = pubADLink?.getAttribute('href');
    expect(result).toBe(expectedLinkAddress);
  });
});
