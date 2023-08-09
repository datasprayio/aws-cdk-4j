package io.dataspray.aws.cdk;

import com.google.common.net.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.core.async.BlockingOutputStreamAsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.CompletedUpload;
import software.amazon.awssdk.transfer.s3.model.UploadRequest;
import software.amazon.awssdk.utils.CancellableOutputStream;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.CompletableFuture;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Publishes file assets to S3.
 */
public class FileAssetPublisher {

    private static final Logger logger = LoggerFactory.getLogger(FileAssetPublisher.class);
    private static final int MINIMUM_PART_SIZE = 5 * 1024 * 1024;

    private S3AsyncClient s3Client;
    private S3TransferManager s3TransferManager;

    /**
     * Uploads a file or a directory (zipping it before uploading) to S3 bucket.
     *
     * @param file       the file or directory to be uploaded
     * @param objectName the name of the object in the bucket
     * @param bucketName the name of the bucket
     * @throws IOException if I/O error occurs while uploading a file or directory
     */
    public void publish(Path file, String objectName, String bucketName, ResolvedEnvironment environment) throws IOException {
        logger.info("Publishing file asset, file={}, bucketName={}, objectName={}", file, bucketName, objectName);
        if (Files.isDirectory(file)) {
            publishDirectory(file, objectName, bucketName, environment);
        } else {
            publishFile(file, objectName, bucketName, environment);
        }
    }

    /**
     * Uploads a string as a file (zipping it before uploading) to S3 bucket.
     *
     * @param data       the content of the file
     * @param objectName the name of the object in the bucket
     * @param bucketName the name of the bucket
     * @throws IOException if I/O error occurs while uploading a file or directory
     */
    public void publish(byte[] data, String objectName, String bucketName, ResolvedEnvironment environment) throws IOException {
        logger.info("Publishing inline content asset, bucketName={}, objectName={}", bucketName, objectName);
        publishFile(data, objectName, bucketName, environment);
    }

    /**
     * Zips the directory and uploads it to S3 bucket.
     */
    private void publishDirectory(Path directory, String objectName, String bucketName, ResolvedEnvironment environment) throws IOException {
        BlockingOutputStreamAsyncRequestBody body = AsyncRequestBody.forBlockingOutputStream(null);
        CompletableFuture<CompletedUpload> uploadFuture = upload(environment, body, bucketName, objectName, MediaType.ZIP.toString());
        try (
                CancellableOutputStream outputStream = body.outputStream();
                ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)
        ) {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    ZipEntry zipEntry = new ZipEntry(directory.relativize(file).toString());
                    zipOutputStream.putNextEntry(zipEntry);
                    Files.copy(file, zipOutputStream);
                    zipOutputStream.closeEntry();
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        uploadFuture.join();
    }

    /**
     * Uploads the bytes to S3 bucket.
     */
    private void publishFile(byte[] data, String objectName, String bucketName, ResolvedEnvironment environment) throws IOException {
        upload(environment, AsyncRequestBody.fromBytes(data), bucketName, objectName).join();
    }

    /**
     * Uploads the file to S3 bucket.
     */
    private void publishFile(Path file, String objectName, String bucketName, ResolvedEnvironment environment) throws IOException {
        upload(environment, AsyncRequestBody.fromFile(file), bucketName, objectName).join();
    }

    private S3AsyncClient getS3Client(ResolvedEnvironment environment) {
        if (this.s3Client == null) {
            this.s3Client = S3AsyncClient.crtBuilder()
                    .region(environment.getRegion())
                    .credentialsProvider(environment.getCredentialsProvider())
                    .minimumPartSizeInBytes((long) MINIMUM_PART_SIZE)
                    .build();
        }

        return s3Client;
    }

    private S3TransferManager getS3TransferManager(ResolvedEnvironment environment) {
        if (this.s3TransferManager == null) {
            this.s3TransferManager = S3TransferManager.builder()
                    .s3Client(getS3Client(environment))
                    .build();
        }

        return s3TransferManager;
    }

    private CompletableFuture<CompletedUpload> upload(ResolvedEnvironment environment, AsyncRequestBody body, String bucketName, String objectKey) {
        return upload(environment, body, bucketName, objectKey, null);
    }

    private CompletableFuture<CompletedUpload> upload(ResolvedEnvironment environment, AsyncRequestBody body, String bucketName, String objectKey, @Nullable String contentType) {
        return upload(environment, body, bucketName, objectKey, contentType, MINIMUM_PART_SIZE);
    }

    private CompletableFuture<CompletedUpload> upload(ResolvedEnvironment environment, AsyncRequestBody body, String bucketName, String objectKey, @Nullable String contentType, int partSize) {
        if (partSize <= 0) {
            throw new IllegalArgumentException("The minimum part size is 5 MB (" + MINIMUM_PART_SIZE + " bytes)");
        }
        return getS3TransferManager(environment).upload(UploadRequest.builder()
                        .putObjectRequest(PutObjectRequest.builder()
                                .bucket(bucketName)
                                .key(objectKey)
                                .contentType(contentType)
                                .build())
                        .requestBody(body)
                        .build())
                .completionFuture();
    }
}
