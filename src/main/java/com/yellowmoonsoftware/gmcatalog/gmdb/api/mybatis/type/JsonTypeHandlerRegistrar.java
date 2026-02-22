package com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.type;

import com.yellowmoonsoftware.gmcatalog.gmdb.api.mybatis.r2dbc.BaseJsonbTypeHandlerAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.io.ResolverUtil;
import org.springframework.stereotype.Component;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.delegate.R2dbcMybatisConfiguration;
import pro.chenggang.project.reactive.mybatis.support.r2dbc.executor.type.support.ForceToUseR2dbcTypeHandlerAdapter;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
@Component
public class JsonTypeHandlerRegistrar {

    public TypeRegistrar withConfig(final R2dbcMybatisConfiguration config) {
        return new TypeRegistrar() {
            @Override
            public TypeRegistrar register(String packageName) {
                findCandidateClasses(packageName).forEach(this::register);
                return this;
            }

            @Override
            public TypeRegistrar register(Class<?> clazz) {
                log.debug("Registering JSONB type handler for {}", clazz.getTypeName());
                config.getTypeHandlerRegistry().register(clazz, ForceToUseR2dbcTypeHandlerAdapter.class);
                config.addR2dbcTypeHandlerAdapter(new BaseJsonbTypeHandlerAdapter<>(clazz));
                return this;
            }
        };
    }

    protected Stream<? extends Class<?>> findCandidateClasses(final String packageName) {
        return Stream.concat(
                resolveClassesMatching(new ResolverUtil.AnnotatedWith(JsonTypeHandler.class), packageName)
                        .flatMap(c -> c.getAnnotation(JsonTypeHandler.class).mapDescendants()
                                ? resolveClassesMatching(new ResolverUtil.IsA(c), packageName)
                                : Stream.of(c)),
                        Stream.concat(resolveMemberCandidates(packageName),
                                resolveConstructorArgCandidates(packageName)))
                .distinct();
    }

    protected Stream<? extends Class<?>> resolveMemberCandidates(final String packageName) {
        MemberAnnotatedWith jsonTypeHandlerAnnotationTest = MemberAnnotatedWith.annotation(JsonTypeHandler.class);

        return new ResolverUtil<>()
                .find(jsonTypeHandlerAnnotationTest, packageName)
                .getClasses()
                .stream()
                .flatMap(jsonTypeHandlerAnnotationTest::getAnnotatedMemberTypes);
    }

    protected Stream<? extends Class<?>> resolveClassesMatching(final ResolverUtil.Test test, final String packageName) {
        return new ResolverUtil<>()
                .find(test, packageName)
                .getClasses()
                .stream();
    }

    protected Stream<? extends Class<?>> resolveConstructorArgCandidates(final String packageName) {
        ConstructorArgAnnotatedWith jsonTypeHandlerAnnotationTest = ConstructorArgAnnotatedWith.annotation(JsonTypeHandler.class);

        return new ResolverUtil<>()
                .find(jsonTypeHandlerAnnotationTest, packageName)
                .getClasses()
                .stream()
                .flatMap(jsonTypeHandlerAnnotationTest::getAnnotatedConstructorArgsTypes);
    }

    @RequiredArgsConstructor(staticName = "annotation")
    static class ConstructorArgAnnotatedWith implements ResolverUtil.Test {
        private final Class<? extends Annotation> annotation;

        @Override
        public boolean matches(Class<?> type) {
            return getConstructorArgsForType(type)
                    .anyMatch(f -> f.isAnnotationPresent(annotation));
        }

        public Stream<? extends Class<?>> getAnnotatedConstructorArgsTypes(Class<?> type) {
            return getConstructorArgsForType(type)
                    .filter(m -> m.isAnnotationPresent(annotation))
                    .map(Parameter::getType)
                    .filter(Objects::nonNull);
        }

        protected Stream<? extends Parameter> getConstructorArgsForType(Class<?> type) {
            return Arrays.stream(type.getDeclaredConstructors())
                    .flatMap(c -> Arrays.stream(c.getParameters()));
        }
    }

    @RequiredArgsConstructor(staticName = "annotation")
    static class MemberAnnotatedWith implements ResolverUtil.Test {
        private final Class<? extends Annotation> annotation;

        @Override
        public boolean matches(Class<?> type) {
            return getMembersForType(type)
                    .anyMatch(f -> f.isAnnotationPresent(annotation));
        }

        public Stream<? extends Class<?>> getAnnotatedMemberTypes(Class<?> type) {
            return getMembersForType(type)
                    .filter(m -> m.isAnnotationPresent(annotation))
                    .map(this::extractMemberType)
                    .filter(Objects::nonNull);
        }

        protected Stream<? extends AccessibleObject> getMembersForType(Class<?> type) {
            return Stream.concat(Arrays.stream(type.getDeclaredFields()),
                    Arrays.stream(type.getMethods()));
        }

        protected Class<?> extractMemberType(final AccessibleObject m) {
            return switch (m) {
                case Field f -> f.getType();
                case Method mth -> mth.getReturnType();
                case null, default -> null;
            };
        }
    }
}
