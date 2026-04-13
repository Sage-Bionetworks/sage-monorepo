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
    fixture.componentRef.setInput('anonymousName', 'Model A');
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
});
