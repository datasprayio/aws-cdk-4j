import io.dataspray.aws.cdk.Stacks
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.cloudformation.model.StackStatus

import java.util.stream.Stream

final STACK_NAME = "synth-deploy-basic-test-stack-as-library"
final TOOLKIT_STACK_NAME = "basic-it-cdk-toolkit-as-library"

System.properties.'aws.profile' = AWS_PROFILE
CloudFormationClient cfnClient = CloudFormationClient.create();

try {
    def stack = Stacks.findStack(cfnClient, STACK_NAME).orElse(null);
    assert stack?.stackStatus() == StackStatus.CREATE_COMPLETE

    def parameterValue = Stacks.findOutput(stack, "ParameterValue")
            .map { output -> output.outputValue() }
            .orElse(null)
    assert parameterValue == "OverriddenValue"

    def tagValue = stack.tags().stream()
            .filter({ tag -> tag.key() == "testTag" })
            .map({ tag -> tag.value() })
            .findAny()
            .orElse(null)
    assert tagValue == "testTagValue"
    def stage = Stacks.findOutput(stack, "Stage")
            .map { output -> output.outputValue() }
            .orElse(null)
    assert stage == "test"

    def toolkitStack = Stacks.findStack(cfnClient, TOOLKIT_STACK_NAME).orElse(null);
    assert toolkitStack?.stackStatus() == null
} finally {
    def stack = Stacks.findStack(cfnClient, STACK_NAME)
            .map { s -> Stacks.deleteStack(cfnClient, s.stackName()) }
            .orElse(null)
    def toolkitStack = Stacks.findStack(cfnClient, TOOLKIT_STACK_NAME)
            .map { s -> ToolkitStacks.deleteToolkitStack(cfnClient, s) }
            .orElse(null)

    Stream.of(stack, toolkitStack)
            .filter(Objects::nonNull)
            .forEach { s -> Stacks.awaitCompletion(cfnClient, s) }
}
