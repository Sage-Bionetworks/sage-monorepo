import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { provideMarkdown } from 'ngx-markdown';
import { ModelPanelComponent } from './model-panel.component';
import { INITIAL_STREAM_STATE } from '../battle.types';
import { CONTINUE_PROMPT } from '../battle.constants';

describe('ModelPanelComponent', () => {
  let component: ModelPanelComponent;
  let fixture: ComponentFixture<ModelPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModelPanelComponent],
      providers: [provideMarkdown()],
    }).compileComponents();

    fixture = TestBed.createComponent(ModelPanelComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('modelId', 'model1');
    fixture.componentRef.setInput('streamState', INITIAL_STREAM_STATE);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should show cursor during streaming', () => {
    fixture.componentRef.setInput('streamState', {
      ...INITIAL_STREAM_STATE,
      text: 'Hello',
      status: 'streaming',
    });
    expect(component.displayText()).toBe('Hello\u258C');
  });

  it('should not show cursor when complete', () => {
    fixture.componentRef.setInput('streamState', {
      ...INITIAL_STREAM_STATE,
      text: 'Hello world',
      status: 'complete',
    });
    expect(component.displayText()).toBe('Hello world');
  });

  it('should have no model name by default', () => {
    expect(component.modelName()).toBeNull();
  });

  it('should return streaming status from stream state', () => {
    fixture.componentRef.setInput('streamState', {
      ...INITIAL_STREAM_STATE,
      status: 'streaming',
    });
    expect(component.streamState().status).toBe('streaming');
  });

  describe('continue button', () => {
    it('renders when complete and finishReason is length', () => {
      fixture.componentRef.setInput('streamState', {
        ...INITIAL_STREAM_STATE,
        status: 'complete',
        finishReason: 'length',
      });
      fixture.detectChanges();

      const btn = fixture.debugElement.query(By.css('.action-btn'));
      expect(btn).toBeTruthy();
      expect(btn.nativeElement.getAttribute('title')).toBe('Continue generating');
      expect(btn.nativeElement.textContent).toContain('Continue the response');
    });

    it('does not render when finishReason is stop', () => {
      fixture.componentRef.setInput('streamState', {
        ...INITIAL_STREAM_STATE,
        status: 'complete',
        finishReason: 'stop',
      });
      fixture.detectChanges();

      expect(fixture.debugElement.query(By.css('.action-btn'))).toBeNull();
    });

    it('does not render while still streaming', () => {
      fixture.componentRef.setInput('streamState', {
        ...INITIAL_STREAM_STATE,
        status: 'streaming',
        finishReason: 'length',
      });
      fixture.detectChanges();

      expect(fixture.debugElement.query(By.css('.action-btn'))).toBeNull();
    });

    it('hides the continue prompt from the rendered transcript', () => {
      fixture.componentRef.setInput('streamState', {
        ...INITIAL_STREAM_STATE,
        messages: [
          { role: 'user', content: 'Original question' },
          { role: 'assistant', content: 'Truncated answer' },
          { role: 'user', content: CONTINUE_PROMPT },
          { role: 'assistant', content: 'Continuation text' },
        ],
        status: 'complete',
        finishReason: 'stop',
      });
      fixture.detectChanges();

      const userMsgs = fixture.debugElement
        .queryAll(By.css('.user-msg'))
        .map((e) => e.nativeElement.textContent.trim());
      expect(userMsgs).toEqual(['Original question']);
    });

    it('emits continue when clicked', () => {
      fixture.componentRef.setInput('streamState', {
        ...INITIAL_STREAM_STATE,
        status: 'complete',
        finishReason: 'length',
      });
      fixture.detectChanges();

      const spy = jest.fn();
      component.continue.subscribe(spy);
      fixture.debugElement.query(By.css('.action-btn')).nativeElement.click();
      expect(spy).toHaveBeenCalledTimes(1);
    });
  });
});
