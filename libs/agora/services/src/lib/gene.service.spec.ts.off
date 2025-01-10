// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { TestBed } from '@angular/core/testing';
import {
  HttpClientTestingModule,
  HttpTestingController,
} from '@angular/common/http/testing';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { GeneService } from '.';
import { ApiService } from '../../../core/services';
import { geneMock1, gctGeneMock1, distributionMock, bioDomainsMock, bioDomainInfoMock } from '../../../testing';

// -------------------------------------------------------------------------- //
// Tests
// -------------------------------------------------------------------------- //
describe('Service: Gene', () => {
  let geneService: GeneService;
  let httpMock: HttpTestingController;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [GeneService, ApiService],
    });

    geneService = TestBed.inject(GeneService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should create', () => {
    expect(geneService).toBeDefined();
  });

  it('should get gene', () => {
    const mockResponse = geneMock1;

    geneService.getGene(geneMock1.ensembl_gene_id).subscribe((response) => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('/api/genes/' + geneMock1.ensembl_gene_id);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should get statistical models', () => {
    expect(geneService.getStatisticalModels(geneMock1).length).not.toEqual(0);
  });

  it('should get similar genes network', () => {
    expect(
      geneService.getSimilarGenesNetwork(geneMock1)?.nodes?.length
    ).not.toEqual(0);
  });

  it('should get distribution', () => {
    const mockResponse = distributionMock;

    geneService.getDistribution().subscribe((response) => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('/api/distribution');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should get comparison genes', () => {
    const mockResponse = { items: [gctGeneMock1] };

    geneService
      .getComparisonGenes(
        'RNA - Differential Expression',
        'AD Diagnosis (males and females)'
      )
      .subscribe((response) => {
        expect(response).toEqual(mockResponse);
      });

    const req = httpMock.expectOne(
      '/api/genes/comparison?category=RNA%20-%20Differential%20Expression&subCategory=AD%20Diagnosis%20(males%20and%20females)'
    );
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should get biodomains', () => {
    const mockResponse = bioDomainsMock.gene_biodomains;

    geneService.getBiodomains(bioDomainsMock.ensembl_gene_id).subscribe((response) => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('/api/biodomains/' + bioDomainsMock.ensembl_gene_id);
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should get all biodomains', () => {
    const mockResponse = bioDomainInfoMock;

    geneService.getAllBiodomains().subscribe((response) => {
      expect(response).toEqual(mockResponse);
    });

    const req = httpMock.expectOne('/api/biodomains');
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });
});
