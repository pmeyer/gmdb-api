package com.yellowmoon.gmdb.graphql;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.Value;
import graphql.schema.*;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.lang.NonNull;

import java.util.Locale;

/// The UploadScalar class defines a custom GraphQL scalar type for handling file uploads.
/// This scalar is meant to represent an input-only type for file uploads in a GraphQL schema.
/// It implements the Coercing interface, which provides methods for serializing, parsing values,
/// and parsing literals for the custom scalar type.
///
/// This scalar is primarily used in scenarios where file uploads are processed
/// using GraphQL mutations. It expects the input to be of type `FilePart`.
///
/// Key functionalities include:
/// - Validation of the input during value parsing.
/// - Restriction against serialization and literal parsing as it is input-only.
///
/// Usage context involves integrating file upload operations within a GraphQL server
/// built on Spring WebFlux and Spring for GraphQL, making use of reactive-stream handling.
public class UploadScalar implements Coercing<FilePart, Void> {

    public static final GraphQLScalarType INSTANCE = GraphQLScalarType.newScalar()
            .name("Upload")
            .coercing(new UploadScalar())
            .build();

    @Override
    public Void serialize(@NonNull Object dataFetcherResult, @NonNull GraphQLContext graphQLContext, @NonNull Locale locale) throws CoercingSerializeException {
        throw new CoercingSerializeException("Upload is an input-only type and cannot be serialized");
    }

    @Override
    public FilePart parseValue(@NonNull final Object input,
                               @NonNull final GraphQLContext graphQLContext,
                               @NonNull final Locale locale) throws CoercingParseValueException {
        if (input instanceof FilePart fileInput) {
            return fileInput;
        }
        throw new CoercingParseValueException("Expected type FilePart but was " + input.getClass().getName());
    }

    @Override
    public FilePart parseLiteral(@NonNull final Value<?> input,
                                 @NonNull final CoercedVariables variables,
                                 @NonNull final GraphQLContext graphQLContext,
                                 @NonNull final Locale locale) throws CoercingParseLiteralException {
        throw new CoercingParseLiteralException("Upload is an input-only type and cannot be parsed from literals");
    }
}
