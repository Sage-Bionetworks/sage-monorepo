import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ModelPanelComponent } from './model-panel.component';
import { INITIAL_STREAM_STATE } from '../battle.types';

describe('ModelPanelComponent', () => {
  let component: ModelPanelComponent;
  let fixture: ComponentFixture<ModelPanelComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModelPanelComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ModelPanelComponent);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('label', 'Model A');
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

  it('should show label when no revealed name', () => {
    expect(component.revealedName()).toBeNull();
  });

  it('should return correct status class', () => {
    fixture.componentRef.setInput('streamState', {
      ...INITIAL_STREAM_STATE,
      status: 'streaming',
    });
    expect(component.statusClass()).toBe('live');
  });
});
