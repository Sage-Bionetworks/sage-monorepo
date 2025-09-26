// -------------------------------------------------------------------------- //
// External
// -------------------------------------------------------------------------- //
import { Schema, model } from 'mongoose';

// -------------------------------------------------------------------------- //
// Internal
// -------------------------------------------------------------------------- //
import { ProteinDifferentialExpression } from '@sagebionetworks/agora/api-client';

// -------------------------------------------------------------------------- //
// Schemas
// -------------------------------------------------------------------------- //
const ProteinDifferentialExpressionSchema = new Schema<ProteinDifferentialExpression>(
  {
    _id: { type: String, required: true },
    uniqid: { type: String, required: true },
    hgnc_symbol: { type: String, required: true },
    uniprotid: { type: String, required: true },
    ensembl_gene_id: { type: String, required: true },
    tissue: { type: String, required: true },
    log2_fc: { type: Number, required: true },
    ci_upr: { type: Number, required: true },
    ci_lwr: { type: Number, required: true },
    pval: { type: Number, required: true },
    cor_pval: { type: Number, required: true },
  },
  { collection: 'genesproteomics' },
);

const ProteomicsSRMSchema = new Schema<ProteinDifferentialExpression>(
  {
    _id: { type: String, required: true },
    uniqid: { type: String, required: true },
    hgnc_symbol: { type: String, required: true },
    uniprotid: { type: String, required: true },
    ensembl_gene_id: { type: String, required: true },
    tissue: { type: String, required: true },
    log2_fc: { type: Number, required: true },
    ci_upr: { type: Number, required: true },
    ci_lwr: { type: Number, required: true },
    pval: { type: Number, required: true },
    cor_pval: { type: Number, required: true },
  },
  { collection: 'proteomicssrm' },
);

const ProteomicsTMTSchema = new Schema<ProteinDifferentialExpression>(
  {
    _id: { type: String, required: true },
    uniqid: { type: String, required: true },
    hgnc_symbol: { type: String, required: true },
    uniprotid: { type: String, required: true },
    ensembl_gene_id: { type: String, required: true },
    tissue: { type: String, required: true },
    log2_fc: { type: Number, required: true },
    ci_upr: { type: Number, required: true },
    ci_lwr: { type: Number, required: true },
    pval: { type: Number, required: true },
    cor_pval: { type: Number, required: true },
  },
  { collection: 'proteomicstmt' },
);

// -------------------------------------------------------------------------- //
// Models
// -------------------------------------------------------------------------- //
export const ProteomicsLFQCollection = model<ProteinDifferentialExpression>(
  'ProteomicsLFQCollection',
  ProteinDifferentialExpressionSchema,
);

export const ProteomicsSRMCollection = model<ProteinDifferentialExpression>(
  'ProteomicsSRMCollection',
  ProteomicsSRMSchema,
);

export const ProteomicsTMTCollection = model<ProteinDifferentialExpression>(
  'ProteomicsTMTCollection',
  ProteomicsTMTSchema,
);
