import { provideHttpClient } from '@angular/common/http';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GeneService, OverallScoresDistribution } from '@sagebionetworks/agora/api-client-angular';
import {
  geneMock1,
  geneMock2,
  overallScoresMock1,
  overallScoresMock2,
} from '@sagebionetworks/agora/testing';
import { GeneSoeChartsComponent } from './gene-soe-charts.component';

describe('Component: Gene SOE Charts', () => {
  let fixture: ComponentFixture<GeneSoeChartsComponent>;
  let component: GeneSoeChartsComponent;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      providers: [GeneService, provideHttpClient()],
    }).compileComponents();
  });

  beforeEach(async () => {
    fixture = TestBed.createComponent(GeneSoeChartsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should sort target risk score first', () => {
    let data: OverallScoresDistribution[] = overallScoresMock1;
    component.customSortDistributions(data);
    expect(data[0].name).toBe('Target Risk Score');
    data = overallScoresMock2;
    component.customSortDistributions(data);
    expect(data[0].name).toBe('Target Risk Score');
  });

  it('should sort genetic risk score second', () => {
    let data: OverallScoresDistribution[] = overallScoresMock1;
    component.customSortDistributions(data);
    expect(data[1].name).toBe('Genetic Risk Score');
    data = overallScoresMock2;
    component.customSortDistributions(data);
    expect(data[1].name).toBe('Genetic Risk Score');
  });

  it('should sort multi-omic risk score last', () => {
    let data: OverallScoresDistribution[] = overallScoresMock1;
    component.customSortDistributions(data);
    expect(data[2].name).toBe('Multi-omic Risk Score');
    data = overallScoresMock2;
    component.customSortDistributions(data);
    expect(data[2].name).toBe('Multi-omic Risk Score');
  });

  it('should handle scores properly', () => {
    const data = geneMock1;
    component.gene = data;
    const result = component.getGeneOverallScores('Genetic Risk Score');
    expect(result).toBe(0.36140442487816);
  });

  it('should handle missing scores properly', () => {
    const data = geneMock2;
    component.gene = data;
    const result = component.getGeneOverallScores('Genetic Risk Score');
    expect(result).toBe(null);
  });
});
