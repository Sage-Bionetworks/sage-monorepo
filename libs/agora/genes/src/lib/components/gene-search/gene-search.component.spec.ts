import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GeneSearchComponent } from './gene-search.component';
import { provideHttpClient } from '@angular/common/http';

describe('GeneSearchComponent', () => {
  let component: GeneSearchComponent;
  let fixture: ComponentFixture<GeneSearchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeneSearchComponent],
      providers: [provideHttpClient()],
    }).compileComponents();

    fixture = TestBed.createComponent(GeneSearchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
