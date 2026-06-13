package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.mappers;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PubMapperXmlTest {

    private static final Path MAPPER_PATH = Path.of(
            "src/main/resources/com/yellowmoonsoftware/gmcatalog/gmdb/api/mybatis/mappers/PubMapper.xml");

    @Test
    void pubSearchFiltersByPublicationIndexIdWhenCriteriaSuppliesIt() throws IOException {
        final String mapperXml = Files.readString(MAPPER_PATH);

        assertThat(mapperXml)
                .contains("<if test=\"criteria.pubIndexId != null\">")
                .contains("and pi.id = #{criteria.pubIndexId}");
    }
}
