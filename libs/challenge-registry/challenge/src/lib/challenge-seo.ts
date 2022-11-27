import { Challenge } from '@sagebionetworks/api-client-angular-deprecated';
import { SeoData } from '@sagebionetworks/shared/util';

const getChallengeSeoData = (challenge: Challenge): SeoData => {
  return {
    title: getTitle(challenge),
    metas: {
      // AUTHOR: {
      //   content: `${userProfile.username}`,
      // },
      DESCRIPTION: {
        content: getDescription(challenge),
      },
      // IMAGE: {
      //   content: 'https://i.imgur.com/VeZfqCw.png',
      // },
      // PUBLISH_DATE: {
      //   content: '',
      // },
      TITLE: {
        content: getTitle(challenge),
      },
      TWITTER_TITLE: {
        content: getTitle(challenge),
      },
      TWITTER_CARD: {
        content: 'summary_large_image',
      },
      TWITTER_DESCRIPTION: {
        content: getDescription(challenge),
      },
      // TWITTER_IMAGE: {
      //   content: 'https://i.imgur.com/VeZfqCw.png',
      // },
      // TWITTER_IMAGE_ALT: {
      //   content: `The avatar of the user ${userProfile.username}`,
      // },
      // TWITTER_SITE: {
      //   content: '',
      // },
      TYPE: {
        content: 'website',
      },
      // URL: {
      //   content: url,
      // },
    },
    jsonLds: [getChallengeJsonLdData(challenge)],
  };
};

const getTitle = (challenge: Challenge): string => {
  return `${challenge.name} - Challenge Registry`;
};

const getDescription = (challenge: Challenge): string => {
  return challenge.description || challenge.name;
};

const getChallengeJsonLdData = (challenge: Challenge): any => {
  return {
    '@context': 'http://schema.org',
    '@type': 'Event',
    name: challenge.name,
    startDate: '2025-07-21T19:00-05:00',
    endDate: '2025-07-21T23:00-05:00',
    eventAttendanceMode: 'https://schema.org/OfflineEventAttendanceMode',
    eventStatus: 'https://schema.org/EventScheduled',
    location: {
      '@type': 'Place',
      name: 'Snickerpark Stadium',
      address: {
        '@type': 'PostalAddress',
        streetAddress: '100 West Snickerpark Dr',
        addressLocality: 'Snickertown',
        postalCode: '19019',
        addressRegion: 'PA',
        addressCountry: 'US',
      },
    },
    image: [
      'https://example.com/photos/1x1/photo.jpg',
      'https://example.com/photos/4x3/photo.jpg',
      'https://example.com/photos/16x9/photo.jpg',
    ],
    description:
      "The Adventures of Kira and Morrison is coming to Snickertown in a can't miss performance.",
    offers: {
      '@type': 'Offer',
      url: 'https://www.example.com/event_offer/12345_201803180430',
      price: '30',
      priceCurrency: 'USD',
      availability: 'https://schema.org/InStock',
      validFrom: '2024-05-21T12:00',
    },
    performer: {
      '@type': 'PerformingGroup',
      name: 'Kira and Morrison',
    },
    organizer: {
      '@type': 'Organization',
      name: 'Kira and Morrison Music',
      url: 'https://kiraandmorrisonmusic.com',
    },
  };
};

export { getChallengeSeoData };
