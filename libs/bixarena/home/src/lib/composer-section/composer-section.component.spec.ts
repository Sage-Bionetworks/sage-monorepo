import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ComposerSectionComponent } from './composer-section.component';

describe('ComposerSectionComponent', () => {
  let fixture: ComponentFixture<ComposerSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ComposerSectionComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(ComposerSectionComponent);
    fixture.detectChanges();
  });

  it('renders the section', () => {
    expect(fixture.nativeElement.querySelector('.composer-section')).toBeTruthy();
  });
});
