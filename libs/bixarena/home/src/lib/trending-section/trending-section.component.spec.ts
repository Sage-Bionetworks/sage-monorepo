import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TrendingSectionComponent } from './trending-section.component';

describe('TrendingSectionComponent', () => {
  let fixture: ComponentFixture<TrendingSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TrendingSectionComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(TrendingSectionComponent);
    fixture.detectChanges();
  });

  it('renders the section', () => {
    expect(fixture.nativeElement.querySelector('.trending-section')).toBeTruthy();
  });
});
