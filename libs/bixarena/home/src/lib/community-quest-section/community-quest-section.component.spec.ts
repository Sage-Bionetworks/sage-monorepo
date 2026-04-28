import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CommunityQuestSectionComponent } from './community-quest-section.component';

describe('CommunityQuestSectionComponent', () => {
  let fixture: ComponentFixture<CommunityQuestSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommunityQuestSectionComponent],
    }).compileComponents();
    fixture = TestBed.createComponent(CommunityQuestSectionComponent);
    fixture.detectChanges();
  });

  it('renders the section', () => {
    expect(fixture.nativeElement.querySelector('.community-quest-section')).toBeTruthy();
  });
});
