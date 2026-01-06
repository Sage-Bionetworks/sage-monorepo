import { Component, Input, OnInit } from '@angular/core';

import { Gene } from '@sagebionetworks/agora/api-client';
import { ResourceCard } from '@sagebionetworks/agora/models';

@Component({
  selector: 'agora-gene-resources',
  imports: [],
  templateUrl: './gene-resources.component.html',
  styleUrls: ['./gene-resources.component.scss'],
})
export class GeneResourcesComponent implements OnInit {
  @Input() gene: Gene | undefined;

  additionalResources: ResourceCard[] = [];
  drugDevelopmentResources: ResourceCard[] = [];

  ngOnInit(): void {
    this.init();
  }

  getPubADLink() {
    // Pub AD links should have hgnc symbol
    if (this.gene?.hgnc_symbol) {
      return `https://adexplorer.medicine.iu.edu/pubad/external/${this.gene.hgnc_symbol}`;
    }
    return 'https://adexplorer.medicine.iu.edu/pubad';
  }

  init() {
    if (!this.gene) {
      return;
    }

    this.drugDevelopmentResources = [
      {
        title: 'Chemical Probes',
        description:
          'View expert reviews and evaluations of any chemical probes that are available for this target.',
        linkText: 'Visit Chemical Probes',
        link: `https://www.chemicalprobes.org/?q=${this.gene?.hgnc_symbol}`,
      },
      {
        title: 'ClinPGx',
        description: 'Search for information on gene-drug and gene-phenotype relationships.',
        linkText: 'Visit ClinPGx',
        link: 'https://www.clinpgx.org',
      },
      {
        title: 'Open Targets',
        description:
          'View evidence on the validity of this therapeutic target based on genome-scale experiments and analysis.',
        linkText: 'Visit Open Targets',
        link: `https://platform.opentargets.org/target/${this.gene?.ensembl_gene_id}`,
      },
      {
        title: 'Pharos',
        description:
          'View information about this target in the Knowledge Management Center for the Illuminating the Druggable Genome program.',
        linkText: 'Visit Pharos',
        link: `https://pharos.nih.gov/targets?q=${this.gene?.ensembl_gene_id}`,
      },
      {
        title: 'Probe Miner',
        description:
          'Search for information on chemical probes based on large-scale, publicly available, medicinal chemistry data.',
        linkText: 'Visit Probe Miner',
        link: 'https://probeminer.icr.ac.uk/#/',
      },
      {
        title: 'Protein Data Bank',
        description: 'Search for experimental and computed 3D protein structure information.',
        linkText: 'Visit PDB',
        link: 'https://www.rcsb.org',
      },
    ];

    this.additionalResources = [
      {
        title: 'AD Atlas',
        description:
          'Perform interactive network and enrichment analyses on this target using a heterogenous network of multiomic, association, and endophenotypic data.',
        linkText: 'Visit AD Atlas',
        link: `https://adatlas.org/?type=geneEnsembl&ids=${this.gene?.ensembl_gene_id}`,
      },
      {
        title: 'Alzforum',
        description:
          'Visit Alzforum for news and information resources about AD and related disorders.',
        linkText: 'Visit Alzforum',
        link: 'https://www.alzforum.org',
      },
      {
        title: 'AlzPED',
        description:
          'Search for information on preclinical efficacy studies of candidate AD therapeutics.',
        linkText: 'Visit AlzPED',
        link: 'https://alzped.nia.nih.gov',
      },
      {
        title: 'AMP-PD Target Explorer',
        description:
          "View evidence about whether this target is associated with Parkinson's Disease.",
        linkText: 'Visit AMP-PD',
        link: `https://target-explorer.amp-pd.org/genes/target-search?gene=${this.gene?.ensembl_gene_id}`,
      },
      {
        title: 'Brain Knowledge Platform',
        description:
          'View single nucleus RNAseq results for this target using the Allen Institute SEA-AD Comparative Viewer.',
        linkText: 'Visit Brain Knowledge Platform',
        link: `https://knowledge.brain-map.org/data/5IU4U8BP711TR6KZ843/2CD0HDC5PS6A58T0P6E/compare?geneOption=${this.gene?.hgnc_symbol ?? this.gene?.ensembl_gene_id}`,
      },
      {
        title: 'Gene Ontology',
        description:
          'View the GO terms associated with this target and explore ontology-related tools.',
        linkText: 'Visit AmiGO 2',
        link: `https://amigo.geneontology.org/amigo/search/annotation?q=${this.gene?.hgnc_symbol ?? this.gene?.ensembl_gene_id}`,
      },
      {
        title: 'GeneCards',
        description:
          'View integrated information about this target gathered from a comprehensive collection of public sources.',
        linkText: 'Visit GeneCards',
        link: `https://www.genecards.org/cgi-bin/carddisp.pl?gene=${this.gene?.hgnc_symbol ?? this.gene?.ensembl_gene_id}`,
      },
      {
        title: 'Genomics DB',
        description:
          "View information about this target on the National Institute on Aging Genetics of Alzheimer's Disease Data Storage Site (NIAGADS) Genomics Database.",
        linkText: 'Visit Genomics DB',
        link: `https://www.niagads.org/genomics/app/record/gene/${this.gene?.ensembl_gene_id}`,
      },
      {
        title: 'Pub AD',
        description: 'View dementia-related publication information for this target.',
        linkText: 'Visit PubAD',
        link: `${this.getPubADLink()}`,
      },
      {
        title: 'Reactome Pathways',
        description: 'View the reactome pathway information for this target on Ensembl.',
        linkText: 'Visit Ensembl',
        link: `https://www.ensembl.org/Homo_sapiens/Gene/Pathway?g=${this.gene?.ensembl_gene_id}`,
      },
      {
        title: 'SEA-AD',
        description:
          'Explore the Seattle Alzheimer’s Disease Brain Cell Atlas resources from the Allen Institute.',
        linkText: 'Visit SEA-AD',
        link: 'https://portal.brain-map.org/explore/seattle-alzheimers-disease',
      },
      {
        title: 'UniProtKB',
        description: 'View protein sequence and functional information about this target.',
        linkText: 'Visit UniProtKB',
        link: `https://www.uniprot.org/uniprotkb?query=${this.gene?.ensembl_gene_id}`,
      },
    ];
  }
}
