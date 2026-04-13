import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PromptComposerComponent } from './prompt-composer.component';

describe('PromptComposerComponent', () => {
  let component: PromptComposerComponent;
  let fixture: ComponentFixture<PromptComposerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PromptComposerComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(PromptComposerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit promptSubmit on submit with text', () => {
    const spy = jest.fn();
    component.promptSubmit.subscribe(spy);
    component.text.set('test prompt');
    component.submit();
    expect(spy).toHaveBeenCalledWith('test prompt');
  });

  it('should not emit when text is empty', () => {
    const spy = jest.fn();
    component.promptSubmit.subscribe(spy);
    component.text.set('   ');
    component.submit();
    expect(spy).not.toHaveBeenCalled();
  });

  it('should not emit when disabled', () => {
    const spy = jest.fn();
    component.promptSubmit.subscribe(spy);
    fixture.componentRef.setInput('disabled', true);
    component.text.set('test');
    component.submit();
    expect(spy).not.toHaveBeenCalled();
  });

  it('should clear text after submit', () => {
    component.text.set('test');
    component.submit();
    expect(component.text()).toBe('');
  });

  it('isHot should be true when text is non-empty and not disabled', () => {
    component.text.set('hello');
    expect(component.isHot()).toBe(true);
  });

  it('isHot should be false when disabled', () => {
    fixture.componentRef.setInput('disabled', true);
    component.text.set('hello');
    expect(component.isHot()).toBe(false);
  });
});
