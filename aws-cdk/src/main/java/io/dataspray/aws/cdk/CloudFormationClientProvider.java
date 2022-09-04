package io.dataspray.aws.cdk;

import com.google.common.collect.Maps;
import software.amazon.awssdk.services.cloudformation.CloudFormationClient;

import java.util.concurrent.ConcurrentMap;

/**
 * Represents toolkit information for an execution environment.
 */
public class CloudFormationClientProvider {

    private static final ConcurrentMap<String, CloudFormationClient> clients = Maps.newConcurrentMap();

    public static CloudFormationClient get(ResolvedEnvironment environment) {
        return clients.computeIfAbsent(environment.getName(), name -> CloudFormationClient.builder()
                .region(environment.getRegion())
                .credentialsProvider(environment.getCredentialsProvider())
                .build());
    }

    private CloudFormationClientProvider() {
        // Disallow ctor
    }
}
