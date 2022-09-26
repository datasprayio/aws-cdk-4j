package io.dataspray.aws.cdk;

import com.google.common.collect.ImmutableList;
import io.dataspray.aws.cdk.text.Ansi;
import io.dataspray.aws.cdk.text.table.Cell;
import io.dataspray.aws.cdk.text.table.Column;
import io.dataspray.aws.cdk.text.table.TableWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.cloudformation.model.ResourceStatus;
import software.amazon.awssdk.services.cloudformation.model.StackEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Stack event listener that logs new stack events in form of a table.
 */
public class LoggingStackEventListener implements Consumer<StackEvent> {

    private static final Logger logger = LoggerFactory.getLogger(LoggingStackEventListener.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final List<Column> COLUMNS = ImmutableList.of(
            Column.of("Timestamp", 20),
            Column.of("Logical ID", 32),
            Column.of("Status", 32),
            Column.of("Status Reason", 64)
    );

    private final Instant notBefore;
    private final TableWriter tableWriter;

    public LoggingStackEventListener(Instant notBefore) {
        this.notBefore = notBefore;
        this.tableWriter = TableWriter.of(line -> logger.info(line.trim()), COLUMNS);
    }

    @Override
    public void accept(StackEvent event) {
        if (event.timestamp().isBefore(notBefore)) {
            return;
        }

        Optional<Ansi.Color> colorOpt;
        if (event.resourceStatus() == ResourceStatus.UNKNOWN_TO_SDK_VERSION) {
            colorOpt = Optional.empty();
        } else {
            String status = event.resourceStatus().toString();
            if (status.endsWith("_IN_PROGRESS")) {
                colorOpt = Optional.of(Ansi.Color.BLUE);
            } else if (status.endsWith("_FAILED")) {
                colorOpt = Optional.of(Ansi.Color.RED);
            } else {
                colorOpt = Optional.of(Ansi.Color.GREEN);
            }
        }

        Cell statusReason = Optional.ofNullable(event.resourceStatusReason())
                .map(reason -> colorOpt.isPresent() ? Cell.of(reason, colorOpt.get()) : Cell.of(reason))
                .orElse(Cell.blank());

        List<Cell> row = ImmutableList.of(
                Cell.of(ZonedDateTime.from(event.timestamp().atZone(ZoneId.systemDefault())).format(DATE_TIME_FORMATTER)),
                Cell.of(event.logicalResourceId()),
                colorOpt.isPresent() ? Cell.of(event.resourceStatusAsString(), colorOpt.get()) : Cell.of(event.resourceStatusAsString()),
                statusReason
        );

        tableWriter.print(row);
    }
}
