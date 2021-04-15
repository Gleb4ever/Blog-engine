package main.service;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.IllegalClassFormatException;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;
import org.springframework.web.multipart.MultipartFile;

public interface ImageService {

  String uploadImage(MultipartFile uploadFile, String location) throws IllegalClassFormatException;

  void init();

  private BufferedImage resizeImg(InputStream inputStream) throws IOException {
    return Scalr.resize(ImageIO.read(inputStream), Method.AUTOMATIC, Mode.AUTOMATIC, 100,
        Scalr.OP_ANTIALIAS);
  }
}