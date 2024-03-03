package src.main.java.cz.cuni.mff.idp.extract;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import src.main.java.cz.cuni.mff.idp.ocr.OcrResult;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Extractor {
    Map<Integer, List<Target>> targets = new HashMap<>();

    public Extractor(String configFile) {
        JsonObject config = loadJson(configFile);

        if (config == null) {
            System.err.println("Extractor is initialized with an empty config!");
            return;
        }

        setUp(config);

    }

    record Target(String variableName, int page, OcrResult.BoundingBox bbox) {
    }

    public static JsonObject loadJson(String filePath) {
        try (FileReader fileReader = new FileReader(filePath)) {
            return JsonParser.parseReader(fileReader).getAsJsonObject();
        } catch (IOException e) {
            System.err.printf("Problem with loading json %s \n%s", filePath, e.getMessage());
            return null;
        }
    }

    private void setUp(JsonObject config) {
        JsonArray targetArray = config.getAsJsonArray("targets");

        for (int i = 0; i < targetArray.size(); i++) {
            JsonObject targetJson = targetArray.get(i).getAsJsonObject();

            try {
                String name = targetJson.get("variable_name").toString();
                int page = targetJson.get("page").getAsInt();
                int left = targetJson.get("left").getAsInt();
                int top = targetJson.get("top").getAsInt();
                int right = targetJson.get("right").getAsInt();
                int bottom = targetJson.get("bottom").getAsInt();
                OcrResult.BoundingBox bbox = new OcrResult.BoundingBox(left, top, right, bottom);
                Target target = new Target(name, page, bbox);
                targets.computeIfAbsent(page, k -> new LinkedList<>()).add(target);
            } catch (Exception e) {
                System.err.printf("Problem with extraction target %s so it will be ignored.\n%s", targetJson.toString(), e.getMessage());
            }
        }
    }

    public JsonObject extract(OcrResult ocrResult) {

        return null;
    }

}
