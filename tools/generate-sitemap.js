const SitemapGenerator = require('sitemap-generator');

// create generator
const generator = SitemapGenerator('http://localhost:4200', {
  changeFreq: 'weekly',
  filepath: './sitemap.xml',
  lastMod: true,
  maxDepth: 0,
  maxEntriesPerFile: 50000,
  priorityMap: [1.0, 0.8, 0.6, 0.4, 0.2, 0],
  stripQuerystring: false
});

// register event listeners
generator.on('done', () => {
  console.log('sitemaps created');
});

generator.on('error', (error) => {
  console.log(error);
});

// start the crawler
generator.start();