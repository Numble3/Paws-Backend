package com.numble.team3.common.infra;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Uploader {
  private final AmazonS3Client amazonS3Client;
  private final String IMAGE_UPLOAD_DIR = "numble-image";

  @Value("${cloud.aws.s3.bucket}")
  public String bucket;

  public String imageUpload(MultipartFile imageFile) throws IOException {
    File uploadFile =
        convert(imageFile) // 파일 변환할 수 없으면 에러
            .orElseThrow(
                () -> new IllegalArgumentException("error: MultipartFile -> File convert fail"));

    return upload(uploadFile, IMAGE_UPLOAD_DIR);
  }

  //디렉토리 경로에 있는 모든 파일을 업로드, .m3u8 파일을 반환
  public String uploadDirectoryWithM3u8(String dirPath){
    File dir = new File(System.getProperty("user.dir") + File.separator + dirPath);
    File[] files = dir.listFiles();
    String accessIndexPath = null, accessFilePath;
    for(File file : files){
      String ext = file.getPath().substring(file.getPath().lastIndexOf("."));
      log.info("Upload File Path: {}, ext: {}", file.getPath(), ext);
      if(ext.equals(".m3u8")){
        accessIndexPath = putS3(file, dirPath + "/" + file.getName());
        log.info("S3 Access Index File Path: {}", accessIndexPath);
      }
      else{
        accessFilePath = putS3(file, dirPath + "/" + file.getName());
        log.info("S3 Access Video File Path: {}", accessFilePath);
      }
    }
    return accessIndexPath;
  }

  // S3로 파일 업로드하기
  private String upload(File uploadFile, String dirName) {
    String fileName = dirName + "/" + UUID.randomUUID() + uploadFile.getName(); // S3에 저장된 파일 이름
    String uploadImageUrl = putS3(uploadFile, fileName); // s3로 업로드
    removeNewFile(uploadFile);
    return uploadImageUrl;
  }

  // S3로 업로드
  private String putS3(File uploadFile, String fileName) {
    amazonS3Client.putObject(
        new PutObjectRequest(bucket, fileName, uploadFile)
            .withCannedAcl(CannedAccessControlList.PublicRead));
    return amazonS3Client.getUrl(bucket, fileName).toString();
  }

  // 로컬에 저장된 이미지 지우기
  private void removeNewFile(File targetFile) {
    if (targetFile.delete()) {
      log.info("File delete success");
      return;
    }
    log.info("File delete fail");
  }

  // 로컬에 파일 업로드 하기
  private Optional<File> convert(MultipartFile file) throws IOException {
    log.info("convert file path: {}", System.getProperty("user.dir") + File.separator + file.getOriginalFilename());
    File convertFile =
        new File(System.getProperty("user.dir") + File.separator + file.getOriginalFilename());
    if (convertFile.createNewFile()) { // 바로 위에서 지정한 경로에 File이 생성됨 (경로가 잘못되었다면 생성 불가능)
      try (FileOutputStream fos =
          new FileOutputStream(convertFile)) { // FileOutputStream 데이터를 파일에 바이트 스트림으로 저장하기 위함
        fos.write(file.getBytes());
      }
      return Optional.of(convertFile);
    }

    return Optional.empty();
  }
}
