package com.yellowmoonsoftware.gmcatalog.gmdb.api.config;

import graphql.schema.idl.RuntimeWiring;
import graphql.scalars.ExtendedScalars;
import org.junit.jupiter.api.Test;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GraphQLConfigTest {

    @Test
    void configureRegistersExpectedScalars() {
        GraphQLConfig config = new GraphQLConfig();
        RuntimeWiring.Builder builder = mock(RuntimeWiring.Builder.class);
        when(builder.scalar(ExtendedScalars.GraphQLLong)).thenReturn(builder);
        when(builder.scalar(ExtendedScalars.Date)).thenReturn(builder);
        when(builder.scalar(ExtendedScalars.Json)).thenReturn(builder);

        RuntimeWiringConfigurer configurer = config.configure();
        configurer.configure(builder);

        var inOrder = inOrder(builder);
        inOrder.verify(builder).scalar(ExtendedScalars.GraphQLLong);
        inOrder.verify(builder).scalar(ExtendedScalars.Date);
        inOrder.verify(builder).scalar(ExtendedScalars.Json);
        inOrder.verifyNoMoreInteractions();
    }
}
