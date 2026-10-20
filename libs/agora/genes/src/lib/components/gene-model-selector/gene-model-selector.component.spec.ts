import { BrowserPlatformLocation, PlatformLocation } from '@angular/common';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { QUERY_PARAMS } from '@sagebionetworks/agora/config';
import { GeneModelSelectorComponent } from './gene-model-selector.component';

const GENE_RNA_PATH = '/genes/ENSG00000178209/evidence/rna';
const DEFAULT_MODEL = 'AD Diagnosis (males and females)';
const MODEL_WITH_PARENTHESES = 'AD Diagnosis x Sex (females only)';
const MODEL_WITH_PARENTHESES_STRIPPED = 'AD Diagnosis x Sex females only';
const MODELS = [DEFAULT_MODEL, MODEL_WITH_PARENTHESES];

function setUrl(url: string) {
  window.history.replaceState({}, '', url);
}

function getModelFromUrl() {
  return new URLSearchParams(window.location.search).get(QUERY_PARAMS.MODEL);
}

function urlWithModel(model: string) {
  return `${GENE_RNA_PATH}?${QUERY_PARAMS.MODEL}=${encodeURIComponent(model)}`;
}

describe('Component: Gene Model Selector', () => {
  let fixture: ComponentFixture<GeneModelSelectorComponent>;
  let component: GeneModelSelectorComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      // the testing module's mock platform location doesn't write to window.location
      providers: [{ provide: PlatformLocation, useClass: BrowserPlatformLocation }],
    }).compileComponents();
  });

  afterEach(() => {
    setUrl('/');
  });

  function createComponent(url: string, models = MODELS) {
    setUrl(url);
    fixture = TestBed.createComponent(GeneModelSelectorComponent);
    component = fixture.componentInstance;
    component.options = models;
  }

  function setup(url: string, models = MODELS) {
    createComponent(url, models);
    fixture.detectChanges();
  }

  function selectModel(model: string) {
    const option = component.options.find((o) => o.name === model);
    if (!option) throw new Error(`no option for ${model}`);
    component.selected = option;
    component.onSelectionChange();
  }

  it('should create', () => {
    setup(GENE_RNA_PATH);
    expect(component).toBeTruthy();
  });

  describe('initial selection', () => {
    it('should select the model named in the query parameter', () => {
      setup(urlWithModel(MODEL_WITH_PARENTHESES));
      expect(component.selected.name).toBe(MODEL_WITH_PARENTHESES);
    });

    it('should select the model whose name matches a query parameter without parentheses', () => {
      setup(urlWithModel(MODEL_WITH_PARENTHESES_STRIPPED));
      expect(component.selected.name).toBe(MODEL_WITH_PARENTHESES);
    });

    it('should select the first model when there is no query parameter', () => {
      setup(GENE_RNA_PATH);
      expect(component.selected.name).toBe(DEFAULT_MODEL);
    });

    it('should select the first model when the query parameter matches no model', () => {
      setup(urlWithModel('unknown model'));
      expect(component.selected.name).toBe(DEFAULT_MODEL);
    });

    it('should emit the selected model', () => {
      createComponent(urlWithModel(MODEL_WITH_PARENTHESES));
      const emitSpy = jest.spyOn(component.changeEvent, 'emit');
      fixture.detectChanges();
      expect(emitSpy).toHaveBeenCalledWith({ name: MODEL_WITH_PARENTHESES });
    });

    it('should not change the url', () => {
      const url = urlWithModel(MODEL_WITH_PARENTHESES_STRIPPED);
      setup(url);
      expect(`${window.location.pathname}${window.location.search}`).toBe(url);
    });
  });

  describe('selection change', () => {
    it('should set the model query parameter to the selected model', () => {
      setup(GENE_RNA_PATH);
      selectModel(MODEL_WITH_PARENTHESES);
      expect(getModelFromUrl()).toBe(MODEL_WITH_PARENTHESES);
    });

    it('should remove the model query parameter when the default model is selected', () => {
      setup(urlWithModel(MODEL_WITH_PARENTHESES));
      selectModel(DEFAULT_MODEL);
      expect(window.location.search).toBe('');
    });

    it('should treat the first model in the options as the default', () => {
      setup(GENE_RNA_PATH, [MODEL_WITH_PARENTHESES, DEFAULT_MODEL]);
      selectModel(DEFAULT_MODEL);
      expect(getModelFromUrl()).toBe(DEFAULT_MODEL);
      selectModel(MODEL_WITH_PARENTHESES);
      expect(getModelFromUrl()).toBeNull();
    });

    it('should keep other query parameters and the hash fragment', () => {
      setup(`${GENE_RNA_PATH}?other=value#consistency-of-change`);
      selectModel(MODEL_WITH_PARENTHESES);
      expect(getModelFromUrl()).toBe(MODEL_WITH_PARENTHESES);
      expect(new URLSearchParams(window.location.search).get('other')).toBe('value');
      expect(window.location.hash).toBe('#consistency-of-change');
    });

    it('should emit the selected model', () => {
      setup(GENE_RNA_PATH);
      const emitSpy = jest.spyOn(component.changeEvent, 'emit');
      selectModel(MODEL_WITH_PARENTHESES);
      expect(emitSpy).toHaveBeenCalledWith({ name: MODEL_WITH_PARENTHESES });
    });
  });
});
