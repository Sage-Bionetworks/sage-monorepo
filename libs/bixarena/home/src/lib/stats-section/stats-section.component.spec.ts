import { ComponentFixture, TestBed } from '@angular/core/testing';
import { StatsSectionComponent } from './stats-section.component';

describe('StatsSectionComponent', () => {
  let fixture: ComponentFixture<StatsSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [StatsSectionComponent] }).compileComponents();
    fixture = TestBed.createComponent(StatsSectionComponent);
    fixture.detectChanges();
  });

  it('renders the section', () => {
    expect(fixture.nativeElement.querySelector('.stats-section')).toBeTruthy();
  });
});
