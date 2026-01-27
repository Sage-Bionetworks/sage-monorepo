import { definePreset } from '@primeng/themes';
import { BasePreset } from '@sagebionetworks/explorers/themes';

export const ModelAdPreset = definePreset(BasePreset, {
  components: {
    tooltip: {
      root: {
        borderRadius: 0,
        shadow: '0 8px 24px rgba(53, 58, 63, 0.15)',
      },
      colorScheme: {
        light: {
          root: {
            background: 'white',
            color: '#22252A',
          },
        },
      },
    },
  },
});
