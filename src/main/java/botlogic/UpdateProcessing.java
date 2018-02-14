//package botlogic;
//
//import org.telegram.telegrambots.api.methods.send.SendMessage;
//import org.telegram.telegrambots.api.objects.Update;
//import org.telegram.telegrambots.exceptions.TelegramApiException;
//
//import java.util.Date;
//
//public class UpdateProcessing {
//
//    public static void documentsProcessing(Update update) {
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            String messageText = update.getMessage().getText();
//            long chatId = update.getMessage().getChatId();
//
//            SendMessage message = new SendMessage()
//                    .setText(messageText)
//                    .setChatId(chatId);
//
//            try {
//                execute(message);
//                System.out.println("---");
//                System.out.println(message + "\nMessage date: " + new Date() + "\nText: " + message.getText() + "\n---");
//            } catch (TelegramApiException ex) {
//                ex.printStackTrace();
//            }
//
//        }
//    }
//
//    public static void textProcessing
//}
