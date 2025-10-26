package org.sagebionetworks.bixarena.api.configuration;

import org.sagebionetworks.bixarena.api.model.dto.BattleSortDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSortDto;
import org.sagebionetworks.bixarena.api.model.dto.ExamplePromptSourceDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardHistorySortDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSnapshotSortDto;
import org.sagebionetworks.bixarena.api.model.dto.LeaderboardSortDto;
import org.sagebionetworks.bixarena.api.model.dto.LicenseDto;
import org.sagebionetworks.bixarena.api.model.dto.MessageRoleDto;
import org.sagebionetworks.bixarena.api.model.dto.ModelSortDto;
import org.sagebionetworks.bixarena.api.model.dto.SortDirectionDto;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

@Configuration(value = "org.sagebionetworks.bixarena.api.configuration.enumConverterConfiguration")
public class EnumConverterConfiguration {

    @Bean(name = "org.sagebionetworks.bixarena.api.configuration.EnumConverterConfiguration.battleSortConverter")
    Converter<String, BattleSortDto> battleSortConverter() {
        return new Converter<String, BattleSortDto>() {
            @Override
            public BattleSortDto convert(String source) {
                return BattleSortDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.bixarena.api.configuration.EnumConverterConfiguration.examplePromptSortConverter")
    Converter<String, ExamplePromptSortDto> examplePromptSortConverter() {
        return new Converter<String, ExamplePromptSortDto>() {
            @Override
            public ExamplePromptSortDto convert(String source) {
                return ExamplePromptSortDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.bixarena.api.configuration.EnumConverterConfiguration.examplePromptSourceConverter")
    Converter<String, ExamplePromptSourceDto> examplePromptSourceConverter() {
        return new Converter<String, ExamplePromptSourceDto>() {
            @Override
            public ExamplePromptSourceDto convert(String source) {
                return ExamplePromptSourceDto.fromValue(source);
            }
        };
    }
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
    @Bean(name = "org.sagebionetworks.bixarena.api.configuration.EnumConverterConfiguration.licenseConverter")
    Converter<String, LicenseDto> licenseConverter() {
        return new Converter<String, LicenseDto>() {
            @Override
            public LicenseDto convert(String source) {
                return LicenseDto.fromValue(source);
            }
        };
    }
    @Bean(name = "org.sagebionetworks.bixarena.api.configuration.EnumConverterConfiguration.messageRoleConverter")
    Converter<String, MessageRoleDto> messageRoleConverter() {
        return new Converter<String, MessageRoleDto>() {
            @Override
            public MessageRoleDto convert(String source) {
                return MessageRoleDto.fromValue(source);
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

}
