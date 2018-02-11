package recognizition;


import botlogic.Common;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import static botlogic.Common.drawRectangle;


public class Recognition {
    @JsonFormat
    public static HashMap<String, String> recognizeEmotions(String imageUrl) throws URISyntaxException, IOException {
        HashMap<String, String> resultParams = new HashMap<>();
        String resultAsJson = "";
        String processedResult = "";

        HttpClient httpClient = new DefaultHttpClient();

        // Отправка изображения на обработку в Microsoft Azure
        try {
            URIBuilder uriBuilder = new URIBuilder("https://westus.api.cognitive.microsoft.com/emotion/v1.0/recognize");
            URI uri = uriBuilder.build();

            HttpPost request = new HttpPost(uri);
            request.setHeader("Content-Type", "application/json");
            request.setHeader("Ocp-Apim-Subscription-Key", "dfa4cf9b01a545bdaa97ffadf7041511");

            StringEntity reqEntity = new StringEntity("{ \"url\": \"" + imageUrl + "\" }");
            request.setEntity(reqEntity);

            HttpResponse response = httpClient.execute(request);
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                resultAsJson = EntityUtils.toString(entity);
                EntityUtils.consume(entity);
                JSONParser parser = new JSONParser();
                JSONArray array = (JSONArray) parser.parse(resultAsJson);
                JSONObject mainJsonObj = (JSONObject) array.get(0);
                JSONObject jsonScores;
                JSONObject jsonRectangle;

                // JSON объект со скорингом
                jsonScores = (JSONObject) mainJsonObj.get("scores");
                HashMap<String, Object> scoresParams = new ObjectMapper().readValue(jsonScores.toJSONString(), HashMap.class);

                // JSON объект с параметрами области лица
                jsonRectangle = (JSONObject) mainJsonObj.get("faceRectangle");
                HashMap<String, Object> rectangleParams = new ObjectMapper().readValue(jsonRectangle.toJSONString(), HashMap.class);

                FaceRectangle rectangle = new FaceRectangle(rectangleParams, imageUrl);
                resultParams.put("path", drawRectangle(rectangle));
                resultParams.put("scores", Common.parseMapToString(scoresParams));

                System.out.println(resultAsJson);


            } else {
                System.out.println("*** Response from Microsoft Azure contains null ***");
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return resultParams;

    }
}
