package main.service.ServiceImpl;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import main.config.StorageConfig;
import main.exception.StorageException;
import main.service.ImageService;
import main.utils.RandomStringGenerator;
import org.apache.commons.io.FilenameUtils;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@AllArgsConstructor
public class ImageServiceImpl implements ImageService {

  private final StorageConfig storageConfig;

  public List<String> fileFormat = new ArrayList(Arrays.asList("jpg", "jpeg", "png"));

  public String uploadImage(MultipartFile uploadFile, String location)
      throws IllegalClassFormatException {
    StringBuilder pathBuilder = new StringBuilder();
    try {
      String fileExt = FilenameUtils.getExtension(uploadFile.getOriginalFilename());

      if (!fileFormat.contains(fileExt)) {
        throw new IllegalClassFormatException("Illegal file format");
      }

      pathBuilder
          .append(RandomStringGenerator.randomString(10))
          .append(".")
          .append(fileExt);

      Path file = Paths.get(location).resolve(pathBuilder.toString());

      try (InputStream inputStream = uploadFile.getInputStream()) {
        if (location.equals(storageConfig.getLocation().get("AVATARS"))) {
          ImageIO.write(resizeImg(inputStream), fileExt, file.toFile());
        } else {
          Files.copy(inputStream, file,
              StandardCopyOption.REPLACE_EXISTING);
        }
      }
      return "/" + file;
    } catch (IOException e) {
      throw new StorageException("Failed to store file " + pathBuilder, e);
    }
  }

  public void init() {
    storageConfig.getLocation().values().forEach(l -> {
      try {
        Files.createDirectories(Paths.get(l));
      } catch (IOException e) {
        throw new StorageException("Could not initialize storage", e);
      }
    });
  }

  private BufferedImage resizeImg(InputStream inputStream) throws IOException {
    return Scalr.resize(ImageIO.read(inputStream), Method.AUTOMATIC, Mode.AUTOMATIC, 100,
        Scalr.OP_ANTIALIAS);
  }

}