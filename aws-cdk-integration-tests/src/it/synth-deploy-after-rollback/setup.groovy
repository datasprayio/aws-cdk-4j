import io.dataspray.aws.cdk.Stacks
import io.dataspray.aws.cdk.TemplateRef
import software.amazon.awssdk.services.cloudformation.CloudFormationClient
import software.amazon.awssdk.services.cloudformation.model.StackStatus

CloudFormationClient cfnClient = CloudFormationClient.create();

File invalidTemplate = new File(basedir, "invalid-template.json")
def stack = Stacks.createStack(cfnClient, "synth-deploy-after-rollback-test-stack", TemplateRef.fromString(invalidTemplate.text))
stack = Stacks.awaitCompletion(cfnClient, stack)
assert stack?.stackStatus() == StackStatus.ROLLBACK_COMPLETE
