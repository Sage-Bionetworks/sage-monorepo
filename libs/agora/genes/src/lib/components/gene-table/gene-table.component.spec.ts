import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GeneTableComponent } from './gene-table.component';

describe('GeneSearchComponent', () => {
  let component: GeneTableComponent;
  let fixture: ComponentFixture<GeneTableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeneTableComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(GeneTableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
