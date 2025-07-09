import { CommonModule } from '@angular/common';
import { Component, inject, Input, ViewChild } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { faArrowRightArrowLeft, faDownload } from '@fortawesome/free-solid-svg-icons';
import { Gene } from '@sagebionetworks/agora/api-client-angular';
import { GeneTableColumn } from '@sagebionetworks/agora/models';
import { HelperService } from '@sagebionetworks/agora/services';
import { CapitalizeBooleanPipe } from '@sagebionetworks/explorers/util';
import { Table, TableModule } from 'primeng/table';
import { TooltipModule } from 'primeng/tooltip';
import screenfull from 'screenfull';
import { GeneTableColumnSelectorComponent } from '../gene-table-column-selector/gene-table-column-selector.component';

@Component({
  selector: 'agora-gene-table',
  imports: [
    CommonModule,
    RouterModule,
    TableModule,
    TooltipModule,
    FormsModule,
    FontAwesomeModule,
    GeneTableColumnSelectorComponent,
    CapitalizeBooleanPipe,
  ],
  templateUrl: './gene-table.component.html',
  styleUrls: ['./gene-table.component.scss'],
})
export class GeneTableComponent {
  helperService = inject(HelperService);
  router = inject(Router);

  _genes: Gene[] = [];
  get genes(): Gene[] {
    return this._genes;
  }
  @Input() set genes(genes: Gene[]) {
    this._genes = genes.map((gene) => {
      gene.hgnc_symbol = gene.hgnc_symbol || gene.ensembl_gene_id;
      return gene;
    });
  }

  // Search ----------------------------------------------------------------- //

  _searchTerm = '';
  get searchTerm(): string {
    return this._searchTerm;
  }
  @Input() set searchTerm(term: string) {
    this._searchTerm = term;
    this.table.filterGlobal(this._searchTerm, 'contains');
  }

  // Columns ---------------------------------------------------------------- //

  @Input() requiredColumns: string[] = ['hgnc_symbol'];
  optionalColumns: GeneTableColumn[] = [];

  _columns: GeneTableColumn[] = [
    { field: 'hgnc_symbol', header: 'Gene Symbol', selected: true },
    { field: 'ensembl_gene_id', header: 'Ensembl Gene ID', selected: true },
  ];
  @Input() get columns(): GeneTableColumn[] {
    return this._columns;
  }
  set columns(columns: GeneTableColumn[]) {
    this._columns = columns.map((c: GeneTableColumn) => {
      if (!c.width) {
        c.width = Math.max(94 + c.header.length * 12, 250);
      }
      return c;
    });
    this.selectedColumns = this.columns.filter(
      (c: GeneTableColumn) => c.selected || this.requiredColumns.includes(c.field),
    );
    this.optionalColumns = this.columns.filter(
      (c: GeneTableColumn) => !this.requiredColumns.includes(c.field),
    );
  }

  _selectedColumns: GeneTableColumn[] = [];
  @Input() get selectedColumns(): GeneTableColumn[] {
    return this._selectedColumns;
  }
  set selectedColumns(column: GeneTableColumn[]) {
    this._selectedColumns = this.columns.filter((c) => column.includes(c));
  }

  @Input() className = '';
  @Input() heading = 'Nominated Target List';
  @Input() exportFilename = 'gene-list';
  @Input() gctLink: boolean | { [key: string]: string } = false;
  @Input() gctLinkTooltip = 'Use Agora Gene Comparison Tool to compare all genes in this list.';

  @Input() sortField = '';
  @Input() sortOrder = -1;

  @ViewChild('table', { static: true }) table: Table = {} as Table;

  gctIcon = faArrowRightArrowLeft;
  downloadIcon = faDownload;

  customSort(event: any) {
    event.data.sort((gene1: any, gene2: any) => {
      let result = null;
      let a = null;
      let b = null;

      if ('hgnc_symbol' === event.field) {
        a = gene1.hgnc_symbol || gene1.ensembl_gene_id;
        b = gene2.hgnc_symbol || gene2.ensembl_gene_id;
      } else {
        a = gene1[event.field];
        b = gene2[event.field];
      }

      if (a == null && b != null) {
        result = -1;
      } else if (a != null && b == null) {
        result = 1;
      } else if (a == null && b == null) {
        result = 0;
      } else if (typeof a === 'string' && typeof b === 'string') {
        result = a.localeCompare(b);
      } else {
        result = a < b ? -1 : a > b ? 1 : 0;
      }

      return event.order * (result || 0);
    });
  }

  navigateToGene(gene: any) {
    // https://github.com/angular/angular/issues/45202
    // eslint-disable-next-line @typescript-eslint/no-floating-promises
    this.router.navigate(['/genes/' + gene.ensembl_gene_id]);
  }

  isFullscreen() {
    return screenfull && screenfull.isFullscreen;
  }

  getWindowClass(): string {
    return screenfull && !screenfull.isFullscreen
      ? ' pi pi-window-maximize table-icon absolute-icon-left'
      : ' pi pi-window-minimize table-icon absolute-icon-left';
  }

  getFullscreenClass(): string {
    return screenfull && screenfull.isFullscreen ? 'fullscreen-table' : '';
  }

  toggleFullscreen() {
    if (!screenfull.isEnabled || typeof document === 'undefined') {
      return;
    }

    const el = document.getElementsByClassName('gene-table');

    if (el[0]) {
      if (!screenfull.isFullscreen) {
        screenfull.request(el[0]);
      } else {
        screenfull.exit();
      }
    }
  }

  navigateToGeneComparisonTool() {
    if (typeof this.gctLink === 'object') {
      // https://github.com/angular/angular/issues/45202
      // eslint-disable-next-line @typescript-eslint/no-floating-promises
      this.router.navigate(['/genes/comparison'], {
        queryParams: this.gctLink,
      });
    } else {
      const ids: string[] = this._genes.map((g: Gene) => g.ensembl_gene_id);
      this.helperService.setGCTSelection(ids);
      // https://github.com/angular/angular/issues/45202
      // eslint-disable-next-line @typescript-eslint/no-floating-promises
      this.router.navigate(['/genes/comparison']);
    }
  }

  onColumnChange(columns: GeneTableColumn[]) {
    this.selectedColumns = this.columns.filter(
      (c: GeneTableColumn) => c.selected || this.requiredColumns.includes(c.field),
    );
  }
}
