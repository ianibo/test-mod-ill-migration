package com.k_int.ill.files

import org.hibernate.Hibernate;
import org.springframework.web.multipart.MultipartFile;

import com.k_int.ill.referenceData.SettingsData;
import com.k_int.institution.Institution;
import com.k_int.settings.InstitutionSettingsService
import com.k_int.web.toolkit.files.FileObject;
import com.k_int.web.toolkit.files.FileUpload;
import com.k_int.web.toolkit.files.LOBFileObject;
import com.k_int.web.toolkit.files.S3FileObject;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

/**
 * Copied from web toolkit ce so we could change where it obtained its settings from
 */
class FileUploadService {

    InstitutionSettingsService institutionSettingsService;

  public static final String LOB_STORAGE_ENGINE='LOB';
  public static final String S3_STORAGE_ENGINE='S3';

  public FileUpload save(Institution institution, MultipartFile file) {
    // See if a default storage engine app-setting has been set
    String default_storage_engine = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_ENGINE);
    // If so, save using that, default back to LOB storage engine
    return save(institution, file, default_storage_engine ?: LOB_STORAGE_ENGINE);
  }

  public FileUpload save(Institution institution, MultipartFile file, String storageEngine) {

    FileUpload result = null;

    switch ( storageEngine ) {
      case 'S3':
        result = S3save(institution, file)
        break;
      case 'LOB':
      default:
        result = LOBsave(file)
        break;
    }

    log.debug("FileUploadService::save(...,${storageEngine}) returning ${result}");

    return result;
  }

  private FileUpload LOBsave(MultipartFile file) {

    log.debug("LOBsave...");

    // Create our object to house our file data.
    FileObject fobject = new LOBFileObject ()
    fobject.fileContents = file

    FileUpload fileUpload = new FileUpload()
    fileUpload.fileContentType = file.contentType
    fileUpload.fileName = file.originalFilename
    fileUpload.fileSize = file.size
    fileUpload.fileObject = fobject

    fileUpload.save(flush:true)
    fileUpload
  }

  // Create a FileObject from the given stream details
  private FileObject s3FileObjectFromStream(
      Institution institution,
      String object_key,
      InputStream is,
      long stream_size,
      long offset
  ) {

    String s3_endpoint = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_ENDPOINT);
    String s3_access_key = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_ACCESS_KEY);
    String s3_secret_key = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_SECRET_KEY);
    String s3_bucket = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_BUCKET_NAME);
    String s3_region = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_BUCKET_REGION) ?: 'us-east-1';

    log.debug("s3FileObjectFromStream ${s3_endpoint} ${s3_access_key} ${s3_secret_key} ${s3_bucket} ${s3_region}");

    // Create a minioClient with the MinIO server playground, its access key and secret key.
    // See https://blogs.ashrithgn.com/spring-boot-uploading-and-downloading-file-from-minio-object-store/
    MinioClient minioClient =
          MinioClient.builder()
              .endpoint(s3_endpoint)
              .credentials(s3_access_key, s3_secret_key)
              .build();

     minioClient.putObject(
       PutObjectArgs.builder()
         .bucket(s3_bucket)
         .region(s3_region)
         .object(object_key)
         .stream(is, stream_size, offset)
         .build());

    FileObject fobject = new S3FileObject()
    fobject.s3ref=object_key

    return fobject
  }

  private FileUpload S3save(Institution institution, MultipartFile file) {

    log.debug("S3save....");
    FileUpload fileUpload = null;

    try {

      String object_uuid = java.util.UUID.randomUUID().toString()
      String s3_object_prefix = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_OBJECT_PREFIX);
      String object_key = "${s3_object_prefix?:''}${object_uuid}-${file.originalFilename}"

      FileObject fobject = s3FileObjectFromStream(institution, object_key, file.getInputStream(), file.size, -1);
      fileUpload = new FileUpload()
      fileUpload.fileContentType = file.contentType
      fileUpload.fileName = file.originalFilename
      fileUpload.fileSize = file.size
      fileUpload.fileObject = fobject

      fileUpload.save(flush:true)
    }
    catch ( Exception e ) {
      log.error("Problem with S3 updload",e);
    }

    return fileUpload
  }


  // Take the identified file_upload and move it's storage engine
  public boolean migrate(Institution institution, FileUpload file_upload, String target_engine) {
    boolean result = true;
    switch ( target_engine ) {
      case LOB_STORAGE_ENGINE:
        if ( ! ( file_upload.fobject instanceof LOBFileObject ) ) {  // Don't migrate if it's already LOB
          throw new RuntimeException("Migration to LOB storage not implemented");
        }
        break;
      case S3_STORAGE_ENGINE:
        if ( ! ( file_upload.fobject instanceof S3FileObject ) ) { // Don't migrate if it's already S3

        }
        break;
    }
    return result;
  }

  public boolean migrateAtMost(Institution institution, int n, String from, String to) {

    List<FileUpload> list_to_migrate = null;
    Map meta_params = [:]
    if ( n > 0 ) {
      meta_params.max = n
    }
    switch ( from ) {
      case LOB_STORAGE_ENGINE:
        list_to_migrate = LOBFileObject.executeQuery('select l.fileUpload from LOBFileObject as l', [:], meta_params);
        break;
      case S3_STORAGE_ENGINE:
        list_to_migrate = LOBFileObject.executeQuery('select l.fileUpload from S3FileObject as l', [:], meta_params);
        break;
    }

    switch ( to ) {
      case LOB_STORAGE_ENGINE:
        throw new RuntimeException("Migration TO LOB storage not implemented");
        break;
      case S3_STORAGE_ENGINE:
        String s3_object_prefix = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_OBJECT_PREFIX);

        // Firstly - check that S3 is configured properly
        if ( checkS3Configured() ) {
          list_to_migrate.each { file_object_to_migrate ->

            String object_uuid = java.util.UUID.randomUUID().toString()
            String object_key = "${s3_object_prefix?:''}${object_uuid}-${file_object_to_migrate.fileName}"
            log.debug("Migrate ${file_object_to_migrate} to S3: ${object_key}");
            log.debug("Create S3 object for LOB object size=${file_object_to_migrate.fileSize}");
            FileObject original = file_object_to_migrate.fileObject
            FileObject replacement = s3FileObjectFromStream(
                institution,
                object_key,
                file_object_to_migrate.fileObject.fileContents.getBinaryStream(),
                file_object_to_migrate.fileSize, -1
            );

            if ( replacement ) {
              replacement.fileUpload = file_object_to_migrate;
              replacement.save(flush:true, failOnError:true);
              FileUpload.executeUpdate('update FileUpload set fileObject=:a where id=:b',[a:replacement, b:file_object_to_migrate.id]);
              FileObject.executeUpdate('delete from FileObject where id = :a',[a:original.id]);
            }
          }
        }
        break;
    }
  }

  private boolean checkS3Configured(Institution institution) {
    String s3_endpoint = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_ENDPOINT);
    String s3_access_key = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_ACCESS_KEY);
    String s3_secret_key = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_SECRET_KEY);
    String s3_bucket = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_BUCKET_NAME);

    return ( ( s3_endpoint != null ) &&
             ( s3_access_key != null ) &&
             ( s3_secret_key != null ) &&
             ( s3_bucket != null ) )
  }

  /**
   * Return the inputStream for the given S3FileObject so we can stream the contents to a user
   */
  private InputStream getS3FileStream(Institution institution, S3FileObject fo) {
    String s3_endpoint = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_ENDPOINT);
    String s3_access_key = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_ACCESS_KEY);
    String s3_secret_key = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_SECRET_KEY);
    String s3_bucket = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_BUCKET_NAME);
    String s3_region = institutionSettingsService.getSettingValue(institution, SettingsData.SETTING_FILE_STORAGE_S3_BUCKET_REGION) ?: 'us-east-1';

    // Create a minioClient with the MinIO server playground, its access key and secret key.
    // See https://blogs.ashrithgn.com/spring-boot-uploading-and-downloading-file-from-minio-object-store/
    MinioClient minioClient =
          MinioClient.builder()
              .endpoint(s3_endpoint)
              .credentials(s3_access_key, s3_secret_key)
              .build();

    log.debug("Attempt to retrieve file ${fo.s3ref} from bucket ${s3_bucket}");

    // return minioClient.getObject(s3_bucket, fo.s3ref)
    return minioClient.getObject(
             GetObjectArgs.builder()
             .bucket(s3_bucket)
             .region(s3_region)
             .object(fo.s3ref)
             .build());
  }

  private InputStream getInputStreamFor(Institution institution, FileObject fo) {

    InputStream result = null;

    if ( fo != null ) {
      if ( S3FileObject.isAssignableFrom(fo.class) ) {
        S3FileObject s3_file_object = (S3FileObject) Hibernate.unproxy(fo);
        result = getS3FileStream(institution, s3_file_object);
      }
      else if ( LOBFileObject.isAssignableFrom(fo.class) ) {
        LOBFileObject lob_file_object = (LOBFileObject) Hibernate.unproxy(fo)
        result = lob_file_object.fileContents.binaryStream
      }
      else {
        throw new RuntimeException("Unknown class for file object: ${fo?.class?.name}");
      }
    }

    return result;
  }
}
