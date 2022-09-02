package io.dataspray.aws.cdk;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class StackDefinition {

    @Nonnull
    private final String stackName;

    @Nonnull
    private final Map<String, Object> template;

    @Nonnull
    private final String environment;

    @Nullable
    private final Integer requiredToolkitStackVersion;

    @Nonnull
    private final Map<String, ParameterDefinition> parameters;

    @Nonnull
    private final Map<String, String> parameterValues;

    @Nonnull
    private final Map<String, Map<String, Object>> resources;

    @Nonnull
    private final List<String> dependencies;

    private StackDefinition(@Nonnull String stackName,
                            @Nonnull Map<String, Object> template,
                            @Nonnull String environment,
                            @Nullable Integer requiredToolkitStackVersion,
                            @Nullable Map<String, ParameterDefinition> parameters,
                            @Nullable Map<String, String> parameterValues,
                            @Nullable Map<String, Map<String, Object>> resources,
                            @Nullable List<String> dependencies) {
        this.stackName = Objects.requireNonNull(stackName, "Stack name can't be null");
        this.template = Objects.requireNonNull(template, "Template can't be null");
        this.environment = Objects.requireNonNull(environment, "Environment can't be null");
        this.requiredToolkitStackVersion = requiredToolkitStackVersion;
        this.parameters = parameters != null ? ImmutableMap.copyOf(parameters) : ImmutableMap.of();
        this.parameterValues = parameterValues != null ? ImmutableMap.copyOf(parameterValues) : ImmutableMap.of();
        this.resources = resources != null ? ImmutableMap.copyOf(resources) : ImmutableMap.of();
        this.dependencies = dependencies != null ? ImmutableList.copyOf(dependencies) : ImmutableList.of();
    }

    @Nonnull
    public String getStackName() {
        return stackName;
    }

    @Nonnull
    public Map<String, Object> getTemplate() {
        return template;
    }

    @Nonnull
    public String getEnvironment() {
        return environment;
    }

    @Nullable
    public Integer getRequiredToolkitStackVersion() {
        return requiredToolkitStackVersion;
    }

    @Nonnull
    public Map<String, ParameterDefinition> getParameters() {
        return parameters;
    }

    @Nonnull
    public Map<String, String> getParameterValues() {
        return parameterValues;
    }

    @Nonnull
    public Map<String, Map<String, Object>> getResources() {
        return resources;
    }

    @Nonnull
    public List<String> getDependencies() {
        return dependencies;
    }

    @Override
    public String toString() {
        return "StackDefinition{" +
                "stackName='" + stackName + '\'' +
                ", template=" + template +
                ", environment='" + environment + '\'' +
                ", requiredToolkitStackVersion=" + requiredToolkitStackVersion +
                ", parameters=" + parameters +
                ", parameterValues=" + parameterValues +
                ", resources=" + resources +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        private String stackName;
        private Map<String, Object> template;
        private String environment;
        private Integer requiredToolkitStackVersion;
        private Map<String, ParameterDefinition> parameters;
        private Map<String, String> parameterValues;
        private Map<String, Map<String, Object>> resources;
        private List<String> dependencies;

        private Builder() {
        }

        public Builder withStackName(@Nonnull String stackName) {
            this.stackName = stackName;
            return this;
        }

        public Builder withTemplate(@Nonnull Map<String, Object> template) {
            this.template = template;
            return this;
        }

        public Builder withEnvironment(@Nonnull String environment) {
            this.environment = environment;
            return this;
        }

        public Builder withRequiredToolkitStackVersion(@Nullable Integer requiredToolkitStackVersion) {
            this.requiredToolkitStackVersion = requiredToolkitStackVersion;
            return this;
        }

        public Builder withParameters(@Nullable Map<String, ParameterDefinition> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder withParameterValues(@Nullable Map<String, String> parameterValues) {
            this.parameterValues = parameterValues;
            return this;
        }

        public Builder withResources(@Nullable Map<String, Map<String, Object>> resources) {
            this.resources = resources;
            return this;
        }

        public Builder withDependencies(@Nullable List<String> dependencies) {
            this.dependencies = dependencies;
            return this;
        }

        public StackDefinition build() {
            return new StackDefinition(stackName, template, environment, requiredToolkitStackVersion, parameters,
                    parameterValues, resources, dependencies);
        }
    }
}
