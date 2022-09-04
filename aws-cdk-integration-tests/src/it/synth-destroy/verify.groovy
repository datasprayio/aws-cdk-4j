import io.dataspray.aws.cdk.Stacks
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.cloudformation.model.StackStatus

final STACK_NAME = "destroy-test-stack"

System.properties.'aws.profile' = AWS_PROFILE
CloudFormationClient cfnClient = CloudFormationClient.create();

try {
    def stack = Stacks.findStack(cfnClient, STACK_NAME).orElse(null)
    assert stack?.stackStatus() == null || stack?.stackStatus() == StackStatus.DELETE_COMPLETE
} finally {
    Stacks.findStack(cfnClient, STACK_NAME)
            .ifPresent { stack -> Stacks.awaitCompletion(cfnClient, Stacks.deleteStack(cfnClient, stack.stackName())) }
}
