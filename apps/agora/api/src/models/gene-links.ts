import { GeneNetworkLinks } from '@sagebionetworks/agora/api-client';
import { Schema, model } from 'mongoose';

const GeneLinkSchema = new Schema<GeneNetworkLinks>(
  {
    geneA_ensembl_gene_id: { type: String, required: true },
    geneB_ensembl_gene_id: { type: String, required: true },
    geneA_external_gene_name: { type: String, required: true },
    geneB_external_gene_name: { type: String, required: true },
    brainRegion: { type: String, required: true },
  },
  { collection: 'geneslinks' },
);

export const GeneLinkCollection = model<GeneNetworkLinks>('GeneLinksCollection', GeneLinkSchema);
