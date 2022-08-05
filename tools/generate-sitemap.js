const SitemapGenerator = require('sitemap-generator');

// require url to crawl
if (process.argv.length < 3) {
  console.log('Missing URL to crawl.')
  process.exit(1);
}

// require path to output sitemap.xml
if (process.argv.length < 4) {
  console.log('Missing output sitemap location.')
  process.exit(1);
}

// isSitemapValid = (sitemapFilepath) => {
//   return true;
// }

const siteUrl = process.argv[2];
const sitemapFilepath = process.argv[3];

console.log(sitemapFilepath)

// create generator
const generator = SitemapGenerator(siteUrl, {
  changeFreq: 'weekly',
  filepath: sitemapFilepath,
  lastMod: true,
  maxDepth: 2,
  maxEntriesPerFile: 50000,
  priorityMap: [1.0, 0.8, 0.6, 0.4, 0.2, 0],
  stripQuerystring: false
});

// register event listeners
generator.on('done', () => {
  console.log('sitemaps created');
  // if (!isSitemapValid(sitemapFilepath)) {
  //   console.error('sitemap.xml has only one url. Does the site crawled use server-side rendering?');
  //   process.exit(1);
  // }
});

generator.on('error', (error) => {
  console.log(error);
  process.exit(1);
});

// start the crawler
generator.start();
