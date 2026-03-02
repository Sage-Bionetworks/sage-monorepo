import { HttpParams } from '@angular/common/http';
import { EventEmitter, Injectable, Output } from '@angular/core';
import { round } from 'lodash';

declare global {
  interface Navigator {
    msSaveBlob?: (blob: any, defaultName?: string) => boolean;
  }
}

@Injectable({
  providedIn: 'root',
})
export class HelperService {
  loading = false;

  gctSelection: string[] = [];
  modelSelection = '';

  @Output() loadingChange: EventEmitter<any> = new EventEmitter();

  setLoading(state: boolean) {
    this.loading = state;
    this.loadingChange.emit(null);
  }

  getLoading() {
    return this.loading;
  }

  getGCTColumnSortIconTooltipText(column: string): string {
    switch (column.toUpperCase()) {
      case 'RISK SCORE':
        return 'Sort by Target Risk Score value';
      case 'MULTI-OMIC':
        return 'Sort by Multi-omic Risk Score value';
      case 'GENETIC':
        return 'Sort by Genetic Risk Score value';
      default:
        return 'Sort by log2 fold change value';
    }
  }

  getGCTColumnTooltipText(column: string): string {
    switch (column.toUpperCase()) {
      case 'RISK SCORE':
        return 'Target Risk Score';
      case 'MULTI-OMIC':
        return 'Multi-omic Risk Score';
      case 'GENETIC':
        return 'Genetic Risk Score';
      case 'ACC':
        return 'Anterior Cingulate Cortex';
      case 'AntPFC':
        return 'Anterior Prefrontal Cortex';
      case 'BRAAK':
        return 'Neurofibrillary Tangles';
      case 'CBE':
        return 'Cerebellum';
      case 'CERAD':
        return 'Neuritic Plaques';
      case 'COGDX':
        return 'Clinical Consensus Diagnosis';
      case 'DCFDX':
        return 'Clinical Cognitive Diagnosis';
      case 'DLPFC':
        return 'Dorsolateral Prefrontal Cortex';
      case 'FP':
        return 'Frontal Pole';
      case 'IFG':
        return 'Inferior Frontal Gyrus';
      case 'MFG':
        return 'Middle Frontal Gyrus';
      case 'PCC':
        return 'Posterior Cingulate Cortex';
      case 'PHG':
        return 'Parahippocampal Gyrus';
      case 'STG':
        return 'Superior Temporal Gyrus';
      case 'TCX':
        return 'Temporal Cortex';
      default:
        return '';
    }
  }

  roundNumberAsString(num: string, fixed: number): string {
    const n = parseFloat(num);
    return this.roundNumber(n, fixed);
  }

  roundNumber(num: number, decimalPlaces: number): string {
    // lodash will drop trailing zeros
    const roundedResult = round(num, decimalPlaces);
    // pad trailing zeros as necessary
    const paddedResult = roundedResult.toFixed(decimalPlaces);
    // return as string
    return paddedResult.toString();
  }

  setGCTSelection(genes: string[]) {
    this.gctSelection = genes;
  }

  getGCTSelection() {
    return this.gctSelection;
  }

  deleteGCTSelection() {
    this.gctSelection = [];
  }

  getColor(name: string) {
    if (name === 'primary') return '#3c4a63';
    if (name === 'secondary') return '#8b8ad1';
    if (name === 'tertiary') return '#42c7bb';
    if (name === 'action-primary') return '#5081a7';
    return '';
  }

  addSingleUrlParam(url: string, paramName: string, paramValue: string | number | boolean) {
    const param = new HttpParams().set(paramName, paramValue);
    return `${url.split('?')[0]}?${param.toString()}`;
  }

  capitalizeFirstLetterOfString(s: string) {
    if (s.length === 0) return '';
    return s[0].toUpperCase() + s.slice(1);
  }
}
