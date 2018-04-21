package botlogic;

import constants.Constants;
import org.telegram.telegrambots.api.methods.GetFile;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.api.objects.Document;
import org.telegram.telegrambots.api.objects.File;
import org.telegram.telegrambots.api.objects.PhotoSize;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import recognizition.Recognition;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class BotCore extends TelegramLongPollingBot {
    public static final String botName = "";


    public void onUpdateReceived(Update update) {
        // Text message echo
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            SendMessage message = new SendMessage()
                    .setText(messageText)
                    .setChatId(chatId);

            try {
                execute(message);
                System.out.println("---");
                System.out.println(message + "\nMessage date: " + new Date() + "\nText: " + message.getText() + "\n---");
            } catch (TelegramApiException ex) {
                ex.printStackTrace();
            }

        }

        // обработка присланных документов
        if (update.hasMessage() && update.getMessage().hasDocument()) {
            String mimeType;

            System.out.println("\n* Залетел документ");
            Document document = update.getMessage().getDocument();

            System.out.println("* file MIME TYPE: " + document.getMimeType());
            mimeType = document.getMimeType();

            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(document.getFileId());
            System.out.println("fileId: " + getFileMethod.getFileId());

            try {
                File file = execute(getFileMethod);
                String filePath = file.getFilePath();
                String downloadUrl = file.getFileUrl(this.getBotToken(), filePath);

                System.out.println("* Uploading file to Telegram server. URL: " + downloadUrl);
                // загрузка файла на сервер Телеграма
                downloadFile(filePath);
                Common.downloadRequest(downloadUrl, mimeType);
                System.out.println("Загрузил файл с filePath: " + filePath);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        }

        // обработка присланных фотографий (iPhone)
        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            HashMap<String, String> resultParams;
            System.out.println("\n* Incoming message is PHOTO");

            List<PhotoSize> photos = update.getMessage().getPhoto();
            String fileId = photos.stream()
                    .sorted(Comparator.comparing(PhotoSize::getFileSize).reversed())
                    .findFirst()
                    .orElse(null).getFileId();

            GetFile getFileMethod = new GetFile();
            getFileMethod.setFileId(fileId);
            System.out.println("* Photo ID: " + getFileMethod.getFileId());

            // загрузка полученной фотографии на сервер Telegram
            try {
                File file = execute(getFileMethod);
                String photoPath = file.getFilePath();
                String downloadUrl = file.getFileUrl(this.getBotToken(), photoPath);

                // распознавание эмоций на фото
                try {
                    System.out.println("* Trying recognize photo...");
                    resultParams = Recognition.recognizeEmotions(downloadUrl);
                    System.out.println("\n* Emotions on photo: ");
                    System.out.println(resultParams.get("scores"));

                    // отправка фотографии с отрисованой областью лица
                    SendPhoto sendPhotoRequest = new SendPhoto();
                    sendPhotoRequest.setChatId(update.getMessage().getChatId());
                    sendPhotoRequest.setNewPhoto(new java.io.File(resultParams.get("path")));
                    sendPhoto(sendPhotoRequest);

                    // отправка сообщения с набором эмоций
                    SendMessage message = new SendMessage()
                            .setText("Recognized emotions:\n" + resultParams.get("scores"))
                            .setChatId(update.getMessage().getChatId());
                    execute(message);

                } catch (URISyntaxException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

    }

    public String getBotUsername() {
        return "myamazingbot";
    }

    @Override
    public String getBotToken() {
        return Constants.BOT_TOKEN;
    }


}
