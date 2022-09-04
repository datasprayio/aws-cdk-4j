import io.dataspray.aws.cdk.Stacks
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.cloudformation.model.StackStatus

final STACK_NAME = "synth-deploy-after-rollback-test-stack"

System.properties.'aws.profile' = AWS_PROFILE
CloudFormationClient cfnClient = CloudFormationClient.create();

try {
    def stack = Stacks.findStack(cfnClient, STACK_NAME).orElse(null)
    assert stack?.stackStatus() == StackStatus.CREATE_COMPLETE
} finally {
    Stacks.findStack(cfnClient, STACK_NAME)
            .map { s -> Stacks.deleteStack(cfnClient, s.stackName()) }
            .ifPresent { s -> Stacks.awaitCompletion(cfnClient, s) }
}
