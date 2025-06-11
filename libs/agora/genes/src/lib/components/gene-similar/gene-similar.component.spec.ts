import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { GenesService } from '@sagebionetworks/agora/api-client-angular';
import { HelperService } from '@sagebionetworks/agora/services';
import { geneMock1, geneMock2, geneMock3 } from '@sagebionetworks/agora/testing';
import { GeneSimilarComponent } from './gene-similar.component';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Component: Gene Similar', () => {
  let fixture: ComponentFixture<GeneSimilarComponent>;
  let component: GeneSimilarComponent;
  let element: HTMLElement;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [GenesService, HelperService, provideRouter([]), provideHttpClient()],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneSimilarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
    element = fixture.nativeElement;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should order by hgnc-symbol ascending', () => {
    const expectedHgnc = [geneMock1.hgnc_symbol, geneMock2.hgnc_symbol, geneMock3.hgnc_symbol];
    expectedHgnc.sort();

    component.genes = [geneMock1, geneMock2, geneMock3];
    fixture.detectChanges();

    const table = element.querySelector('.similar-gene-table');
    expect(table).not.toBeNull();

    const rows = Array.from(table?.querySelectorAll('tbody tr') || []) as HTMLTableRowElement[];
    expect(rows.length).toBe(3);

    const actualHgnc = rows.map((row) => row.cells[0].textContent?.trim());
    expect(actualHgnc).toEqual(expectedHgnc);
  });
});
