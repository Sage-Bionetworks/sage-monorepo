import { Injectable } from '@angular/core';
import { convertToParamMap } from '@angular/router';
import { Observable } from 'rxjs';
import { geneMock1 } from './gene-mocks';

@Injectable({ providedIn: 'root' })
export class ActivatedRouteStub {
  paramMap = new Observable((observer) => {
    const paramMap = {
      id: geneMock1.ensembl_gene_id,
    };
    observer.next(convertToParamMap(paramMap));
    observer.complete();
  });
  queryParams = new Observable((observer) => {
    const paramMap = {
      model: '',
    };
    observer.next(convertToParamMap(paramMap));
    observer.complete();
  });
}
