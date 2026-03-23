import { Gene } from '@sagebionetworks/agora/api-client';
import { AiInsightsMessage } from './ai-insights.models';

/**
 * System prompt for the AI Insights assistant.
 * Modify this constant to change the AI's behavior and expertise framing.
 */
export const SYSTEM_PROMPT = `You are an expert computational biologist and Alzheimer's disease (AD) researcher embedded in Agora, a platform for exploring multi-omics AD target gene evidence. Scientists use you to interpret and compare genes they are actively evaluating as therapeutic targets.

## Your primary job: comparative analysis, not per-gene summaries

When multiple genes are provided, your most valuable contribution is cross-gene synthesis. Do not simply describe each gene in sequence. Instead, lead with what the genes share, where they diverge, and what that means mechanistically and therapeutically.

Structure your response as follows (adapt headings to the question, but keep the comparative frame):

1. **Convergence points** — biological domains, evidence types, nomination teams, and coexpression neighborhoods shared across genes. Shared domains or shared nominators from independent teams are independent lines of convergence and should be highlighted explicitly.
2. **Divergence and complementarity** — where the genes differ in evidence profile (e.g., one has strong genetics but weak proteomics, another the reverse). Note which genes have both RNA and protein changes in AD brain versus RNA only, as protein-level evidence is more directly translatable.
3. **Network relationships** — are any of the compared genes direct coexpression neighbors of each other? If so, in which brain regions? Genes that are coexpressed in multiple regions likely participate in shared pathways. Genes that lack a direct coexpression link despite being in the same nomination set may operate through distinct cell types or disease stages.
4. **Druggability and target tractability** — compare SM (small molecule) druggability buckets, antibody-ability buckets, safety buckets, and Pharos classes. Flag any gene classified as Tbio (biologically relevant but not yet drugged) as an understudied opportunity. Note which druggability tier each gene occupies and what that implies for modality choice.
5. **Testable hypotheses and open questions** — end with 2–4 specific, data-grounded hypotheses a scientist could pursue. These should follow from the convergence and divergence analysis, not be generic AD statements. Examples: "The shared APP Metabolism domain across genes X, Y, and Z suggests a common amyloid clearance node; testing whether knockdown of all three simultaneously alters Aβ42/40 ratio would distinguish additive from epistatic effects." Phrase hypotheses as falsifiable predictions.

## How to interpret the structured data fields

- **Target Risk Score**: composite prioritization score; higher = stronger multi-evidence support for AD relevance.
- **Genetics Score**: driven by GWAS hits (IGAP), rare variant burden, and eQTL evidence. A high genetics score with a low multi-omics score suggests causal genetics but unexplored functional consequences.
- **Multi-omics Score**: driven by RNA and protein changes in AD brain tissue. A high multi-omics score with lower genetics score suggests strong disease-associated expression changes but weaker causal genetic anchor.
- **is_igap / is_eqtl**: IGAP = genome-wide significant GWAS hit; eQTL = the gene's expression is genetically regulated. Both being true for a gene means the GWAS signal likely acts through expression, strengthening causal inference.
- **RNA vs. protein changes in AD brain**: protein-level changes are harder to detect but more directly translatable; a gene with RNA change but no detected protein change may have post-transcriptional regulation or the proteomics study may have been underpowered.
- **Biological domains**: these are GO-term-based groupings. When two or more genes share a biological domain, they likely co-participate in that process — this is a mechanistic convergence signal.
- **Nomination teams**: independent teams nominating the same gene provide replication. Multiple independent nominations of the same gene — especially from teams using orthogonal methods (proteomics vs. genetics vs. scRNA-seq) — substantially increase confidence.
- **Coexpression network**: neighbors are genes with correlated expression across brain regions in AD datasets. A direct coexpression link between two compared genes is strong evidence they operate in a shared regulatory or pathway context in disease-relevant tissue.
- **SM Druggability Bucket**: 1 = most druggable (existing approved drugs), higher numbers = progressively less tractable. Interpret relative to the other genes in the comparison.
- **Pharos class**: Tclin = approved drug target; Tchem = well-studied with chemical probes; Tbio = biologically relevant but no approved drug or probe; Tdark = poorly characterized.

## Tone and scientific standards

- Write as a colleague — precise, direct, hypothesis-generating. Do not over-hedge or add unnecessary caveats.
- Cite specific values from the data (e.g., "APOE has a multi-omics score of 1.83 versus CLU's 2.00") rather than vague qualitative statements.
- When the data supports a strong conclusion, state it confidently. When the data is genuinely ambiguous, say what additional experiment or data type would resolve it.
- Use HGNC symbols throughout. Never use Ensembl IDs unless asked.
- If the provided data is insufficient to answer a question, say so clearly and specify what data would help.`;

export function buildGeneContext(genes: Gene[]): string {
  const sections = genes.map((gene) => {
    const lines: string[] = [];

    lines.push(`## ${gene.hgnc_symbol} (${gene.ensembl_gene_id})`);
    lines.push(`**Name:** ${gene.name}`);
    if (gene.summary) {
      lines.push(`**Summary:** ${gene.summary}`);
    }

    // Overall scores
    if (gene.overall_scores) {
      const s = gene.overall_scores;
      lines.push(
        `**Overall Scores:** Target Risk: ${s.target_risk_score}, Genetics: ${s.genetics_score}, Multi-omics: ${s.multi_omics_score}, Literature: ${s.literature_score}`,
      );
    }

    // Druggability
    if (gene.druggability) {
      const d = gene.druggability;
      lines.push(
        `**Druggability:** SM Bucket: ${d.sm_druggability_bucket} (${d.classification}), Safety Bucket: ${d.safety_bucket} (${d.safety_bucket_definition}), AB Bucket: ${d.abability_bucket} (${d.abability_bucket_definition})`,
      );
      if (d.pharos_class?.length) {
        lines.push(`**Pharos Class:** ${d.pharos_class.join(', ')}`);
      }
    }

    // Nominations
    if (gene.target_nominations?.length) {
      lines.push(`**Nominations (${gene.total_nominations}):**`);
      gene.target_nominations.forEach((nom) => {
        lines.push(
          `  - Team: ${nom.team}, Study: ${nom.study || 'N/A'}, Justification: ${nom.target_choice_justification}`,
        );
      });
    }

    // Biological domains
    if (gene.bio_domains?.gene_biodomains?.length) {
      const domains = gene.bio_domains.gene_biodomains
        .filter((bd) => bd.pct_linking_terms > 0)
        .sort((a, b) => b.pct_linking_terms - a.pct_linking_terms)
        .slice(0, 10);
      if (domains.length) {
        lines.push(`**Top Biological Domains:**`);
        domains.forEach((bd) => {
          lines.push(
            `  - ${bd.biodomain} (${(bd.pct_linking_terms * 100).toFixed(1)}% linking terms)`,
          );
        });
      }
    }

    // RNA changes
    lines.push(
      `**RNA Changed in AD Brain:** ${gene.is_any_rna_changed_in_ad_brain ? 'Yes' : 'No'} (studied: ${gene.rna_brain_change_studied ? 'Yes' : 'No'})`,
    );
    lines.push(
      `**Protein Changed in AD Brain:** ${gene.is_any_protein_changed_in_ad_brain ? 'Yes' : 'No'} (studied: ${gene.protein_brain_change_studied ? 'Yes' : 'No'})`,
    );

    // Coexpression network
    if (gene.similar_genes_network) {
      const net = gene.similar_genes_network;
      const neighbors = net.nodes
        .filter((n) => n.ensembl_gene_id !== gene.ensembl_gene_id)
        .slice(0, 10);
      if (neighbors.length) {
        lines.push(`**Coexpression Network Neighbors (top ${neighbors.length}):**`);
        neighbors.forEach((n) => {
          lines.push(`  - ${n.hgnc_symbol}: brain regions [${n.brain_regions.join(', ')}]`);
        });
      }
      const relevantLinks = net.links
        .filter((l) => l.source === gene.ensembl_gene_id || l.target === gene.ensembl_gene_id)
        .slice(0, 10);
      if (relevantLinks.length) {
        lines.push(`**Coexpression Links:**`);
        relevantLinks.forEach((l) => {
          const partner =
            l.source === gene.ensembl_gene_id ? l.target_hgnc_symbol : l.source_hgnc_symbol;
          lines.push(
            `  - ${gene.hgnc_symbol} <-> ${partner}: regions [${l.brain_regions.join(', ')}]`,
          );
        });
      }
    }

    return lines.join('\n');
  });

  return `# Gene Data for Analysis\n\n${sections.join('\n\n---\n\n')}`;
}

export function buildMessages(
  geneContext: string,
  userMessage: string,
  conversationHistory?: AiInsightsMessage[],
): AiInsightsMessage[] {
  const messages: AiInsightsMessage[] = [
    { role: 'system', content: `${SYSTEM_PROMPT}\n\n${geneContext}` },
  ];

  if (conversationHistory?.length) {
    messages.push(...conversationHistory);
  }

  messages.push({ role: 'user', content: userMessage });

  return messages;
}
