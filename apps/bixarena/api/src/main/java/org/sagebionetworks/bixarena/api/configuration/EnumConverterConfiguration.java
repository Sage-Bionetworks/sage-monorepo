package org.sagebionetworks.bixarena.api.configuration;

import org.sagebionetworks.bixarena.api.model.dto.LeaderboardHistorySortDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotSortDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSortDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelSearchQueryDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelSortDto;
import org.sagebionetworks.bixarena.api.model.dto.SortDirectionDto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration(value = "org.sagebionetworks.bixarena.api.configuration.enumConverterConfiguration")
public class EnumConverterConfiguration {

    @Bean(name = "org.sagebionetworks.bixarena.api.configuration.EnumConverterConfiguration.leaderboardHistorySortConverter")
    Converter<String, LeaderboardHistorySortDto> leaderboardHistorySortConverter() {
        return new Converter<String, LeaderboardHistorySortDto>() {
            @Override
            public LeaderboardHistorySortDto convert(String source) {
                return LeaderboardHistorySortDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.bixarena.api.configuration.EnumConverterConfiguration.leaderboardSnapshotSortConverter")
    Converter<String, LeaderboardSnapshotSortDto> leaderboardSnapshotSortConverter() {
        return new Converter<String, LeaderboardSnapshotSortDto>() {
            @Override
            public LeaderboardSnapshotSortDto convert(String source) {
                return LeaderboardSnapshotSortDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.bixarena.api.configuration.EnumConverterConfiguration.leaderboardSortConverter")
    Converter<String, LeaderboardSortDto> leaderboardSortConverter() {
        return new Converter<String, LeaderboardSortDto>() {
            @Override
            public LeaderboardSortDto convert(String source) {
                return LeaderboardSortDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.bixarena.api.configuration.EnumConverterConfiguration.modelSortConverter")
    Converter<String, ModelSortDto> modelSortConverter() {
        return new Converter<String, ModelSortDto>() {
            @Override
            public ModelSortDto convert(String source) {
                return ModelSortDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.bixarena.api.configuration.EnumConverterConfiguration.sortDirectionConverter")
    Converter<String, SortDirectionDto> sortDirectionConverter() {
        return new Converter<String, SortDirectionDto>() {
            @Override
            public SortDirectionDto convert(String source) {
                return SortDirectionDto.fromValue(source);
            }
        };
    }

    @Bean(name = "org.sagebionetworks.bixarena.api.configuration.EnumConverterConfiguration.licenseConverter")
    Converter<String, ModelSearchQueryDto.LicenseEnum> licenseConverter() {
        return new Converter<String, ModelSearchQueryDto.LicenseEnum>() {
            @Override
            public ModelSearchQueryDto.LicenseEnum convert(String source) {
                return ModelSearchQueryDto.LicenseEnum.fromValue(source);
            }
        };
    }

}
