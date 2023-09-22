import { SeoData } from '@sagebionetworks/shared/util';

export const getSeoData = (): SeoData => {
  return new SeoData({
    title: 'OpenChallenges: A Centralized Hub for Biomedical Challenges',
    description:
      'Data challenges have played a significant role in driving biomedical breakthroughs by engaging researchers, data scientists, and experts from various fields to collaborate on complex problems. The OpenChallenges initiative addresses current pain points like fragmented challenge information and lack of standardization. OpenChallenges.io is a centralized hub for biomedical challenges that empowers participants with the most up-to-date information about relevant challenges, while providing organizers with standardized challenge event templates and intelligence.',
    url: '',
    imageUrl:
      'https://dev.openchallenges.io/img/6IP26BBsWLts21OynZ4HfpfSTag=/140x140/logo/ibm.svg?a=svg2',
    imageAlt: 'OpenChallenges logo',
    publishDate: '2023-09-20T00:00:00Z',
    jsonLds: [],
  });
};
