import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { ActivatedRouteStub } from '@sagebionetworks/agora/testing';
import { GeneModelSelectorComponent } from './gene-model-selector.component';

describe('Component: Gene Model Selector', () => {
  let fixture: ComponentFixture<GeneModelSelectorComponent>;
  let component: GeneModelSelectorComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [{ provide: ActivatedRoute, useValue: new ActivatedRouteStub() }],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneModelSelectorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
