import io.dataspray.aws.cdk.Stacks
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.cloudformation.model.StackStatus
import software.amazon.awssdk.services.ecr.EcrClient
import software.amazon.awssdk.services.ecr.model.DeleteRepositoryRequest

import java.util.stream.Collectors
import java.util.stream.Stream

final STACK_NAME = "synth-deploy-ecs-service-test-stack"
final TOOLKIT_STACK_NAME = "ecs-service-it-cdk-toolkit"

CloudFormationClient cfnClient = CloudFormationClient.create();

try {
    File cloudAssemblyDirectory = new File(basedir, "cdk-stack/target/cdk.out");
    assert cloudAssemblyDirectory.exists() && cloudAssemblyDirectory.directory

    def stack = Stacks.findStack(cfnClient, STACK_NAME).orElse(null);
    assert stack?.stackStatus() == StackStatus.CREATE_COMPLETE

    def toolkitStack = Stacks.findStack(cfnClient, TOOLKIT_STACK_NAME).orElse(null);
    assert toolkitStack == null
} finally {
    def stacks = Stream.of(STACK_NAME, TOOLKIT_STACK_NAME)
            .map(stackName -> Stacks.findStack(cfnClient, stackName).orElse(null))
            .filter(Objects::nonNull)
            .map(stack -> Stacks.deleteStack(cfnClient, stack.stackName()))
            .collect(Collectors.toList())

    stacks.stream()
            .map(stack -> Stacks.awaitCompletion(cfnClient, stack))
            .forEach(stack -> {
                stack?.stackStatus() == StackStatus.DELETE_COMPLETE
            })

    def ecrClient = EcrClient.create()
    deleteRepository(ecrClient, "aws-cdk/assets")
}

def deleteRepository(EcrClient client, String repositoryName) {
    def deleteRequest = DeleteRepositoryRequest.builder()
            .repositoryName(repositoryName)
            .force(true)
            .build()
    client.deleteRepository(deleteRequest)
}
