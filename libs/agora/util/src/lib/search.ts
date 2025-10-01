export function isEnsemblId(query: string): boolean {
  return query.toLowerCase().startsWith('ensg');
}

export function sanitizeSearchQuery(query: string): string {
  return query.trim().replace(/[^a-z0-9-_]/gi, '');
}
