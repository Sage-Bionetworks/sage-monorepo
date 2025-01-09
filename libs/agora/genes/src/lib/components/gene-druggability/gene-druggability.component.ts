import { Component, Input, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import * as d3 from 'd3';
import { CommonModule } from '@angular/common';
import { AccordionModule } from 'primeng/accordion';
import { Druggability, Gene } from '@sagebionetworks/agora/api-client-angular';

export interface GeneResourceType {
  title: string;
  description: string;
  linkText: string;
  link: string;
}

@Component({
  selector: 'agora-gene-druggability',
  imports: [CommonModule, AccordionModule],
  templateUrl: './gene-druggability.component.html',
  styleUrls: ['./gene-druggability.component.scss'],
})
export class GeneDruggabilityComponent {
  route = inject(ActivatedRoute);

  _gene: Gene | undefined;
  get gene(): Gene | undefined {
    return this._gene;
  }
  @Input() set gene(gene: Gene | undefined) {
    this._gene = gene;
    this.init();
  }

  druggability: Druggability = {} as Druggability;
  bucketsSM: number[] = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14];
  bucketsAB: number[] = [1, 2, 3, 4, 5, 6, 7];
  bucketsSF: number[] = [1, 2, 3, 4, 5, 6];
  currentBucketSM = 1;
  currentBucketAB = 1;
  currentBucketSF = 1;
  classes: string[] = ['A', 'B', 'C', 'D', 'E', 'F'];
  additionalResources: GeneResourceType[] = [];

  init() {
    // Update the initial buckets
    if (this.gene) {
      if (!this.gene.druggability) {
        this.gene.druggability = [
          {
            sm_druggability_bucket: this.getDefaultBucketNumber(),
            safety_bucket: this.getDefaultBucketNumber(),
            abability_bucket: this.getDefaultBucketNumber(),
            pharos_class: '',
            classification: this.getDefaultText(),
            safety_bucket_definition: this.getDefaultText(),
            abability_bucket_definition: this.getDefaultText(),
          },
        ];
      }

      this.druggability = this.gene.druggability[0];
      this.currentBucketSM = this.druggability.sm_druggability_bucket;
      this.currentBucketAB = this.druggability.abability_bucket;
      this.currentBucketSF = this.druggability.safety_bucket;
    }
  }

  getDefaultBucketNumber(): number {
    return 100;
  }

  getDefaultTitle(): string {
    return 'Not analyzed';
  }

  getDefaultText(): string {
    return 'No score is currently available for this gene.';
  }

  getDruggabilitySMTitle(bucket: number): string {
    switch (bucket) {
      case 1:
        return 'Small molecule druggable';
      case 2:
        return 'Targetable by Homology';
      case 3:
        return 'Targetable by structure';
      case 4:
        return 'Targetable by homologous structure';
      case 5:
        return 'Probably small molecule druggable';
      case 6:
        return 'Probably small molecule druggable by homology';
      case 7:
        return 'Potentially targetable by protein family structure';
      case 8:
        return 'Endogenous ligand';
      case 9:
        return 'Potentially small molecule druggable by family (active ligand)';
      case 10:
        return 'Potentially small molecule druggable by family (low activity ligand)';
      case 11:
        return 'Druggable protein class, no other information';
      case 12:
        return 'Potentially low ligandability';
      case 13:
        return 'Unknown';
      case 14:
        return 'Non-protein target';
      default:
        return this.getDefaultTitle();
    }
  }

  getDruggabilityABTitle(bucket: number): string {
    switch (bucket) {
      case 1:
        return 'Ideal';
      case 2:
        return 'Highly accessible';
      case 3:
        return 'Accessible';
      case 4:
        return 'Probably accessible';
      case 5:
        return 'Probably inaccessible';
      case 6:
        return 'Inaccessible';
      case 7:
        return 'Unknown';
      default:
        return this.getDefaultTitle();
    }
  }

  getDruggabilitySFTitle(bucket: number): string {
    switch (bucket) {
      case 1:
        return 'Lowest risk';
      case 2:
        return 'Lower risk';
      case 3:
        return 'Potential risks';
      case 4:
        return 'Probable risks';
      case 5:
        return 'Potentially unsafe in humans';
      case 6:
        return 'Unknown';
      default:
        return this.getDefaultTitle();
    }
  }

  getDruggabilitySMText(bucket: number): string {
    switch (bucket) {
      case 1:
        return (
          'Protein with a small molecule ligand identified from ChEMBL, meeting ' +
          'TCRD activity criteria'
        );
      case 2:
        return (
          '>=40% homologous to a protein with a small molecule ligand identified ' +
          'from ChEMBL, meeting TCRD activity criteria'
        );
      case 3:
        return (
          'Structurally druggable protein, based on the presence of a druggable ' +
          'pocket in the protein (DrugEBIlity/CanSAR)'
        );
      case 4:
        return (
          '>=40% homologous to a structurally druggable protein, based on the ' +
          'presence of a druggable pocket in the homologous protein (DrugEBIlity/CanSAR)'
        );
      case 5:
        return (
          'Protein with a small molecule ligand identified from ChEMBL data, but ' +
          'the ligand does not meeting TCRD activity criteria'
        );
      case 6:
        return (
          '>=40% homologous to a protein with a small molecule ligand identified ' +
          'from ChEMBL data, but the ligand does not meeting TCRD activity criteria'
        );
      case 7:
        return (
          'Is a member of a gene family which has a protein member with a druggable pocket ' +
          'in the protein structure.'
        );
      case 8:
        return 'Has an identified endogenous ligand according from IUPHAR.';
      case 9:
        return 'Is a member of a gene family which has a member with an small molecule ligand identified from ChEMBL data, meeting TCRD activity criteria.';
      case 10:
        return 'Is a member of a gene family which has a protein member with a ligand which does not meet TCRD activity criteria.';
      case 11:
        return (
          'Is a member of a PHAROS druggable class of protein (enzyme, receptor, ' +
          'ion channel, nuclear hormone receptor, kinase) but does not meet any of the ' +
          'criteria above'
        );
      case 12:
        return 'Has a structure but there is no evidence of a druggable pocket';
      case 13:
        return (
          'There is no information on ligands or structure in any of the categories ' + 'above'
        );
      case 14:
        return 'New modality indicated';
      default:
        return this.getDefaultText();
    }
  }

  getDruggabilityABText(bucket: number): string {
    switch (bucket) {
      case 1:
        return 'Secreted protein. Highly accessible to antibody-based therapies';
      case 2:
        return (
          'Component of the extracellular matrix (ECM). Highly accessible to ' +
          'antibody-based therapies, but potentially less so than secreted proteins'
        );
      case 3:
        return (
          'Cell membrane-bound proteins. Highly accessible to antibody-based ' +
          'therapies, but potentially less so than secreted proteins or ECM components'
        );
      case 4:
        return (
          'Limited evidence that target is a secreted protein, ECM component or ' +
          'cell membrane-bound protein'
        );
      case 5:
        return (
          'Protein located in the cytosol. Not practically accessible to ' +
          'antibody-based therapies, but may be more easily accessible to other modalities'
        );
      case 6:
        return 'Protein located in intracellular compartment';
      case 7:
        return 'Dark target. Paucity of biological knowledge means progress will be ' + 'difficult';
      default:
        return this.getDefaultText();
    }
  }

  getDruggabilitySFText(bucket: number): string {
    switch (bucket) {
      case 1:
        return (
          'Clinical data, evidence of tolerable safety profile in desired modality; ' +
          'target has a drug in phase IV in the appropriate modality, with good safety profile.'
        );
      case 2:
        return (
          'No major issues found from gene expression, genetic or pharmacological ' +
          'profiling, but has not been extensively tested in humans.'
        );
      case 3:
        return (
          'Two or fewer of: high off-target gene expression, cancer driver, ' +
          'essential gene, associated deleterious genetic disorder, HPO phenotype ' +
          'associated gene, or black box warning on clinically used drug.'
        );

      case 4:
        return (
          'More than two of: high off target gene expression, cancer driver, ' +
          'essential gene, associated deleterious genetic disorder, HPO phenotype ' +
          'associated gene, or black box warning on clinically used drug.'
        );
      case 5:
        return (
          'Clinical data with evidence of intolerable safety profile/adverse drug reactions ' +
          'in the desired modality and with target engagement. Drug for target withdrawn on those grounds.'
        );
      case 6:
        return 'Insufficient data available for safety assessment.';
      default:
        return this.getDefaultText();
    }
  }

  getBucketNumberText(bucket: number): string {
    return bucket !== 100 ? bucket.toString(10) : '–';
  }

  getBucketTextColor(bucket: number, section: string): string {
    let range = 0;

    if (section === 'sm') {
      range = 13;
    } else if (section === 'ab') {
      range = 7;
    } else if (section === 'sf') {
      range = 6;
    }

    return bucket < range ? '#fff' : '#000';
  }

  getIconStyle(bucket: number, section: string): string {
    return '16px solid ' + this.getBucketBGColor(bucket, section);
  }

  getBucketBGColor(bucket: number, section: string): string {
    const i = d3.interpolateRgb('#20A386', '#440D54');
    let range = 0;

    if (section === 'sm') {
      range = 12;
    } else if (section === 'ab') {
      range = 6;
    } else if (section === 'sf') {
      range = 5;
    }

    if (bucket === 100) {
      return '#C3C7D1';
    }

    if (bucket <= range && bucket > 0) {
      return d3.hcl(i(bucket / range)).hex();
    } else if (bucket === range + 1) {
      return '#C3C7D1';
    } else if (bucket === range + 2) {
      return '#AFDDDF';
    }

    return '#fff';
  }

  getBucketIconStyle(isSelection: boolean): object {
    const widthString = isSelection ? '62px' : '36px';
    const iconClass: object = {
      width: widthString,
    };

    return iconClass;
  }

  getClassText(bucket: number): any {
    switch (bucket) {
      case 1:
        return 'Class A';
      case 2:
      case 3:
      case 4:
        return 'Class B';
      case 5:
      case 6:
        return 'Class C';
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
        return 'Class D';
      case 12:
      case 13:
        return 'Class E';
      case 14:
        return 'Class F';
      default:
        return 'Not analyzed';
    }
  }

  getClassTextMargin(isSelection: boolean): string {
    return isSelection ? '6px' : '12px';
  }

  setCurrentBucket(bucket: number, section: string) {
    if (section === 'sm') {
      this.currentBucketSM = bucket;
    } else if (section === 'ab') {
      this.currentBucketAB = bucket;
    } else if (section === 'sf') {
      this.currentBucketSF = bucket;
    }
  }

  resetBucket(event: any) {
    if (this.gene && event.index !== undefined) {
      if (event.index === 0) {
        this.currentBucketSM = this.druggability.sm_druggability_bucket;
      } else if (event.index === 1) {
        this.currentBucketAB = this.druggability.abability_bucket;
      } else if (event.index === 2) {
        this.currentBucketSF = this.druggability.safety_bucket;
      }
    }
  }
}
