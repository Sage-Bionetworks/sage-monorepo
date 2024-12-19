/* eslint-disable @angular-eslint/no-output-on-prefix */
import { Component, Output, EventEmitter, ViewEncapsulation, Input } from '@angular/core';
import { GCTGene } from '@sagebionetworks/agora/api-client-angular';
import { DialogModule } from 'primeng/dialog';

@Component({
  selector: 'agora-gene-comparison-tool-pinned-genes-modal',
  standalone: true,
  imports: [DialogModule],
  templateUrl: './gene-comparison-tool-pinned-genes-modal.component.html',
  styleUrls: ['./gene-comparison-tool-pinned-genes-modal.component.scss'],
  encapsulation: ViewEncapsulation.None,
})
export class GeneComparisonToolPinnedGenesModalComponent {
  @Input() pinnedGenes: GCTGene[] = [];
  @Input() pendingPinnedGenes: GCTGene[] = [];
  @Input() maxPinnedGenes = 5;

  isActive = false;

  @Output() onChange: EventEmitter<boolean> = new EventEmitter();

  show() {
    this.isActive = true;
  }

  hide() {
    this.isActive = false;
  }

  cancel() {
    this.onChange.emit(false);
    this.hide();
  }

  proceed() {
    this.onChange.emit(true);
    this.hide();
  }
}
