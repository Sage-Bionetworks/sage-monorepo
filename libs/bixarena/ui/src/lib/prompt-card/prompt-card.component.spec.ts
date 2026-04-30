import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { PromptCardComponent } from './prompt-card.component';

describe('PromptCardComponent', () => {
  let fixture: ComponentFixture<PromptCardComponent>;
  let component: PromptCardComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({ imports: [PromptCardComponent] }).compileComponents();
    fixture = TestBed.createComponent(PromptCardComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('question', 'What is CRISPR?');
    fixture.detectChanges();
  });

  it('renders the question and default CTA text', () => {
    const q = fixture.debugElement.query(By.css('.question')).nativeElement;
    const cta = fixture.debugElement.query(By.css('.cta')).nativeElement;
    expect(q.textContent).toContain('What is CRISPR?');
    expect(cta.textContent).toContain('Battle this');
  });

  it('hides category chip when category is undefined', () => {
    expect(fixture.debugElement.query(By.css('.tag'))).toBeNull();
  });

  it('shows category chip when category is set', () => {
    fixture.componentRef.setInput('category', 'Genetics');
    fixture.detectChanges();
    const tag = fixture.debugElement.query(By.css('.tag')).nativeElement;
    expect(tag.textContent).toContain('Genetics');
  });

  it('hides battle count when battleCount is undefined', () => {
    expect(fixture.debugElement.query(By.css('.count'))).toBeNull();
  });

  it('shows battle count with correct pluralization', () => {
    fixture.componentRef.setInput('battleCount', 1);
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('.count')).nativeElement.textContent).toContain(
      '1 battle',
    );

    fixture.componentRef.setInput('battleCount', 5);
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('.count')).nativeElement.textContent).toContain(
      '5 battles',
    );
  });

  it('emits cardClick when the button is clicked', () => {
    const spy = jest.fn();
    component.cardClick.subscribe(spy);
    fixture.debugElement.query(By.css('.prompt-card')).nativeElement.click();
    expect(spy).toHaveBeenCalledTimes(1);
  });

  it('renders as a non-interactive skeleton when skeleton is true', () => {
    fixture.componentRef.setInput('skeleton', true);
    fixture.detectChanges();
    expect(fixture.debugElement.query(By.css('button.prompt-card'))).toBeNull();
    expect(fixture.debugElement.query(By.css('div.prompt-card.skeleton'))).toBeTruthy();
  });
});
