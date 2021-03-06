package com.numble.team3.converter.infra;

import com.numble.team3.converter.domain.ConvertResult;
import com.numble.team3.converter.domain.ConvertVideoUtils;
import com.numble.team3.exception.convert.VideoConvertFailureException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bramp.ffmpeg.FFmpeg;
import net.bramp.ffmpeg.FFmpegExecutor;
import net.bramp.ffmpeg.FFprobe;
import net.bramp.ffmpeg.builder.FFmpegBuilder;
import net.bramp.ffmpeg.probe.FFmpegProbeResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
@RequiredArgsConstructor
public class VideoFfmpegUtils implements ConvertVideoUtils {

  @Value("${ffmpeg.ffmpegPath}")
  private String ffmpegPath;

  @Value("${ffmpeg.ffprobePath}")
  private String ffprobePath;

  private FFmpeg ffmpeg;
  private FFprobe ffprobe;

  @Value("${ffmpeg.chunkUnit}")
  private long VIDEO_CHUNK_UNIT;

  private final String CONVERT_DIRECTORY_NAME = File.separator + "convert" + File.separator;

  @PostConstruct
  public void init() {
    try {
      ffmpeg = new FFmpeg(ffmpegPath);
      ffprobe = new FFprobe(ffprobePath);
    } catch (Exception e) {
      log.error("[ffmpeg, ffprobe Error] log: {}", String.valueOf(e));
    }
    log.info("ffmpegPath: {}", ffmpegPath);
    log.info("ffprobePath: {}", ffprobePath);
  }

  @Override
  public String getFileOriginName(String filePath) {
    return filePath.substring(filePath.lastIndexOf(File.separator) + 1, filePath.lastIndexOf("."));
  }

  @Override
  public String getFileExt(String filePath) {
    return filePath.substring(filePath.lastIndexOf(".") + 1);
  }

  private String convertVideo(String dirFullPath, String fileFullPath) {
    String outputFullPath = dirFullPath + getFileOriginName(fileFullPath) + ".m3u8";
    makeEncodingDirectory(dirFullPath);
    log.info("[convert start] output path: {}", outputFullPath);
    FFmpegBuilder builder =
        new FFmpegBuilder()
            .setInput(fileFullPath)
            .overrideOutputFiles(true)
            .addOutput(outputFullPath)
            .setVideoCodec("libx264")
            .setAudioCodec("aac")
            .setFormat("hls")
            .addExtraArgs("-start_number", "0") // .st ??????(????????????) ?????? ??????
            .addExtraArgs("-hls_time", String.valueOf(VIDEO_CHUNK_UNIT)) // .st ?????? ?????? ??????
            .addExtraArgs("-hls_list_size", "0")
            .addExtraArgs("-force_key_frames", "expr:gte(t,n_forced*1)") // ???????????? ?????? ??????????????? ??????
            .disableSubtitle() // No subtiles
            .setStrict(FFmpegBuilder.Strict.EXPERIMENTAL) // Allow FFmpeg to use experimental specs
            .done();

    FFmpegExecutor executor = new FFmpegExecutor(ffmpeg, ffprobe);
    // Run a one-pass encode
    executor.createJob(builder).run();
    log.info("[convert completed] output path: {}", outputFullPath);
    return outputFullPath;
  }

  // ???????????? ????????? ?????? ????????? ??????????????? ??????
  private String makeEncodingDirectory(String dirFullPath) {
    log.info("[create directory recursive]: {}", dirFullPath);
    File file = new File(dirFullPath);
    if (!file.exists()) {
      file.mkdirs();
    }
    return dirFullPath;
  }

  @Override
  public String getRandomFileName() {
    return UUID.randomUUID().toString().substring(0, 10)
        + "_"
        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss"));
  }

  // ????????? ?????? ????????? ??????
  private Optional<File> convertMultipartToFile(String dirFullPath, MultipartFile multipartFile)
      throws IOException {
    makeEncodingDirectory(dirFullPath);
    File convertFile =
        new File(
            dirFullPath
                + File.separator
                + getRandomFileName()
                + "."
                + getFileExt(multipartFile.getOriginalFilename()));
    if (convertFile.createNewFile()) { // ?????? ????????? ????????? ????????? File??? ????????? (????????? ?????????????????? ?????? ?????????)
      try (FileOutputStream fos =
          new FileOutputStream(convertFile)) { // FileOutputStream ???????????? ????????? ????????? ??????????????? ???????????? ??????
        fos.write(multipartFile.getBytes());
      }
      log.info("[file convert complete] convert file name{}", convertFile.getPath());
      return Optional.of(convertFile);
    }
    log.info("[file convert failed] request filename: {}", multipartFile.getOriginalFilename());
    return Optional.empty();
  }

  public long extractVideoDuration(String filePath) throws IOException {
    log.info("[extract start video metadata] path: {}", filePath);
    FFmpegProbeResult probeResult = ffprobe.probe(filePath);
    log.info(
        "[extract start video metadata] duration: {}", probeResult.getStreams().get(0).duration);
    return (long) probeResult.getStreams().get(0).duration;
  }

  /*
  ???????????? ??? ??????, ?????? ????????? ?????? ?????? user.dir/convert/ ????????? ????????? ???????????? ?????? ????????? ??????
  return: ????????? ????????? ??? ??????
  * */
  @Override
  public String saveTempVideoForConvert(String dirName, MultipartFile videoFile)
      throws IOException {
    return convertMultipartToFile(dirName, videoFile)
        .orElseThrow(VideoConvertFailureException::new)
        .getAbsolutePath();
  }

  /*
  user.dir/convert/[UUID ??? 10??????]/convert/ <- ??? ????????? ????????? ????????? ??????
  return: ????????? ??????, S3??? ???????????? ???????????? ?????????, .m3u8 ????????? ?????????
  * */
  @Override
  public ConvertResult processConvertVideo(String dirPath, String filePath) throws IOException {
    long videoDuration = extractVideoDuration(filePath);
    String uploadDir = dirPath + CONVERT_DIRECTORY_NAME;
    String uploadFilePath = convertVideo(uploadDir, filePath);
    return new ConvertResult(videoDuration, uploadDir, uploadFilePath);
  }
}
