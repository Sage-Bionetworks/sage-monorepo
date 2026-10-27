import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Params } from '@angular/router';
import { QUERY_PARAMS } from '@sagebionetworks/agora/config';
import { BehaviorSubject } from 'rxjs';
import { GeneModelSelectorComponent } from './gene-model-selector.component';

const DEFAULT_MODEL = 'AD Diagnosis (males and females)';
const MODEL_WITH_PARENTHESES = 'AD Diagnosis x Sex (females only)';
const MODEL_WITH_PARENTHESES_STRIPPED = 'AD Diagnosis x Sex females only';
const MODELS = [DEFAULT_MODEL, MODEL_WITH_PARENTHESES];

describe('Component: Gene Model Selector', () => {
  let fixture: ComponentFixture<GeneModelSelectorComponent>;
  let component: GeneModelSelectorComponent;
  let queryParams: BehaviorSubject<Params>;

  beforeEach(async () => {
    queryParams = new BehaviorSubject<Params>({});
    await TestBed.configureTestingModule({
      providers: [{ provide: ActivatedRoute, useValue: { queryParams } }],
    }).compileComponents();
  });

  function createComponent(params: Params) {
    queryParams.next(params);
    fixture = TestBed.createComponent(GeneModelSelectorComponent);
    component = fixture.componentInstance;
    component.options = MODELS;
  }

  function setup(params: Params) {
    createComponent(params);
    fixture.detectChanges();
  }

  it('should create', () => {
    setup({});
    expect(component).toBeTruthy();
  });

  it('should select the model named in the query parameter', () => {
    setup({ [QUERY_PARAMS.MODEL]: MODEL_WITH_PARENTHESES });
    expect(component.selected.name).toBe(MODEL_WITH_PARENTHESES);
  });

  it('should select the model whose name matches a query parameter without parentheses', () => {
    setup({ [QUERY_PARAMS.MODEL]: MODEL_WITH_PARENTHESES_STRIPPED });
    expect(component.selected.name).toBe(MODEL_WITH_PARENTHESES);
  });

  it('should select the first model when there is no query parameter', () => {
    setup({});
    expect(component.selected.name).toBe(DEFAULT_MODEL);
  });

  it('should select the first model when the query parameter matches no model', () => {
    setup({ [QUERY_PARAMS.MODEL]: 'unknown model' });
    expect(component.selected.name).toBe(DEFAULT_MODEL);
  });

  it('should emit the selected model', () => {
    createComponent({ [QUERY_PARAMS.MODEL]: MODEL_WITH_PARENTHESES });
    const emitSpy = jest.spyOn(component.changeEvent, 'emit');
    fixture.detectChanges();
    expect(emitSpy).toHaveBeenCalledWith({ name: MODEL_WITH_PARENTHESES });
  });
});
