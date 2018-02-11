package com.company;

import org.apache.commons.lang.RandomStringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;

public class Common {

    public static void downloadRequest(String urlStr, String mimeType) {
        try {
            System.out.println("* Trying to create file at directory and record all content here...");
            downloadUsingNIO(urlStr, mimeType);
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    private static void downloadUsingStream(String urlStr, String file) throws IOException {
        URL url = new URL(urlStr);

        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        FileOutputStream fis = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int count = 0;

        while ((count = bis.read(buffer, 0, 1024)) != -1) {
            fis.write(buffer, 0, count);
        }
        fis.close();
        bis.close();
    }

    /**
     * Выгрузка файлов с сервера Телеграма в директорию
     *
     * @param urlStr
     * @param mimeType
     * @throws IOException
     */
    private static void downloadUsingNIO(String urlStr, String mimeType) throws IOException {
        String path = Constants.DOWNLOAD_DIRECTORY;
        String ext = "";
        switch (mimeType) {

            case ("text/plain"):
                ext = "txt";
                path = path + ext;
                break;

            case ("image/png"):
                ext = "png";
                path = path + "png";
                break;

            case ("image/jpeg"):
                ext = "jpeg";
                path = path + "jpeg";
                break;

            case ("jpg"):
                ext = "jpg";
                path = path + "jpg";
                break;
        }

        System.out.println(mimeType);

        File directory = new File(path);
        String fileName = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(8), ext);
        File file = new File(directory, fileName);

        URL url = new URL(urlStr);

        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(file);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        fos.close();
        rbc.close();

    }

    /**
     * Вывод всего содержимого мапы в одну строку
     */
    public static String parseMapToString(HashMap<String, Object> map) {
        String resultString = "";

        for (HashMap.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();
            resultString = resultString + "\"" + key + "\" : " + value + "\n";

        }
        return resultString;
    }

    /**
     * Отрисовка на фотографии области с лицом
     */
    public static String drawRectangle(FaceRectangle rectangle) {
        BufferedImage img = null;
        try {
            // чтение и отрисовка области
            URL url = new URL(rectangle.getImageUrl());
            img = ImageIO.read(url);
            Graphics2D g2d = img.createGraphics();
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(5));
            g2d.drawRect(rectangle.getLeft(), rectangle.getTop(), rectangle.getWidth(), rectangle.getHeight());
            g2d.dispose();

            File directory = new File(Constants.DOWNLOAD_DIRECTORY + "jpg");
            String fileName = String.format("%s.%s", RandomStringUtils.randomAlphanumeric(8), "jpg");
            String fullPath = directory + "\\" + fileName;
            File file = new File(directory, fileName);

            // перезапись изображения
            System.out.println("* Запись файла с областью лица. \n* Path: " + fullPath);
            ImageIO.write(img, "jpg", new FileOutputStream(file));

            return fullPath;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
