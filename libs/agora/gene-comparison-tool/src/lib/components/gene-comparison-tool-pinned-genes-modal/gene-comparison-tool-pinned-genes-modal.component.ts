import { Component, Output, EventEmitter, ViewEncapsulation, Input } from '@angular/core';
import { GCTGene } from '@sagebionetworks/agora/api-client';
import { DialogModule } from 'primeng/dialog';

@Component({
  selector: 'agora-gene-comparison-tool-pinned-genes-modal',
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

  @Output() changeEvent: EventEmitter<boolean> = new EventEmitter();

  show() {
    this.isActive = true;
  }

  hide() {
    this.isActive = false;
  }

  cancel() {
    this.changeEvent.emit(false);
    this.hide();
  }

  proceed() {
    this.changeEvent.emit(true);
    this.hide();
  }
}
