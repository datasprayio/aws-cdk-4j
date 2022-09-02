import io.dataspray.aws.cdk.Stacks
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.cloudformation.model.StackStatus

CloudFormationClient cfnClient = CloudFormationClient.create();

try {
    def stack = Stacks.findStack(cfnClient, "synth-deploy-basic-test-stack-as-library").orElse(null);
    assert stack?.stackStatus() == StackStatus.CREATE_COMPLETE

    def parameterValue = Stacks.findOutput(stack, "ParameterValue")
            .map { output -> output.outputValue() }
            .orElse(null)
    assert parameterValue == "OverriddenValue"

    def tagValue = stack.tags().stream()
            .filter({ tag -> tag.key() == "testTag"})
            .map({ tag -> tag.value() })
            .findAny()
            .orElse(null)
    assert tagValue == "testTagValue"
    def stage = Stacks.findOutput(stack, "Stage")
            .map {output -> output.outputValue() }
            .orElse(null)
    assert stage == "test"

    def toolkitStack = Stacks.findStack(cfnClient, "basic-cdk-toolkit").orElse(null);
    assert toolkitStack == null
} finally {
    Stacks.findStack(cfnClient, "synth-deploy-basic-test-stack-as-library")
            .ifPresent{stack -> Stacks.awaitCompletion(cfnClient, Stacks.deleteStack(cfnClient, stack.stackName()))}
}
