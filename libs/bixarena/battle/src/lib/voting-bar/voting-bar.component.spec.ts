import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { VotingBarComponent } from './voting-bar.component';

describe('VotingBarComponent', () => {
  let component: VotingBarComponent;
  let fixture: ComponentFixture<VotingBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VotingBarComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(VotingBarComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('phase', 'voting');
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit model1 vote', () => {
    const spy = jest.fn();
    component.vote.subscribe(spy);
    component.voteModel1();
    expect(spy).toHaveBeenCalledWith('model1');
  });

  it('should emit tie vote', () => {
    const spy = jest.fn();
    component.vote.subscribe(spy);
    component.voteTie();
    expect(spy).toHaveBeenCalledWith('tie');
  });

  it('should emit model2 vote', () => {
    const spy = jest.fn();
    component.vote.subscribe(spy);
    component.voteModel2();
    expect(spy).toHaveBeenCalledWith('model2');
  });

  describe('validating phase', () => {
    it('renders the validation row with copy and progress bar', () => {
      fixture.componentRef.setInput('phase', 'validating');
      fixture.detectChanges();

      const row = fixture.debugElement.query(By.css('.validation-row'));
      expect(row).toBeTruthy();
      expect(row.nativeElement.getAttribute('role')).toBe('status');
      expect(row.nativeElement.textContent).toContain('Validating');
      expect(row.nativeElement.textContent).toContain('biomedical relevance');
      expect(fixture.debugElement.query(By.css('.validation-spinner'))).toBeTruthy();
      expect(fixture.debugElement.query(By.css('.validation-fill'))).toBeTruthy();
    });

    it('hides vote buttons and post-reveal nudge during validation', () => {
      fixture.componentRef.setInput('phase', 'validating');
      fixture.detectChanges();

      expect(fixture.debugElement.query(By.css('.vb'))).toBeNull();
      expect(fixture.debugElement.query(By.css('.nudge'))).toBeNull();
    });

    it('does not render the validation row in voting or reveal phases', () => {
      fixture.componentRef.setInput('phase', 'voting');
      fixture.detectChanges();
      expect(fixture.debugElement.query(By.css('.validation-row'))).toBeNull();

      fixture.componentRef.setInput('phase', 'reveal');
      fixture.detectChanges();
      expect(fixture.debugElement.query(By.css('.validation-row'))).toBeNull();
    });
  });
});
