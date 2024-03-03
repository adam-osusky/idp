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
    float intersectionThreshold = 0.5F;

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
                String name = targetJson.get("variable_name").getAsString();
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

    public JsonArray extractPage(OcrResult ocrResult, int page) {
        var pageTargets = targets.get(page);
        JsonArray pageExtractions = new JsonArray();

        for (var word : ocrResult.words) {
            float area = word.bbox().area();

            for (var target : pageTargets) {
                float intersection = word.bbox().intersection(target.bbox()) / area;

                if (intersection >= intersectionThreshold) {
                    pageExtractions.add(createExtractObject(target.variableName(), page, word));
                }

            }
        }

        return pageExtractions;
    }

    private static JsonObject createExtractObject(String variableName, int page, OcrResult.Word word) {
        JsonObject targetObject = new JsonObject();
        targetObject.addProperty("variable_name", variableName);
        targetObject.addProperty("page", page);

        targetObject.addProperty("extracted", word.str());

        targetObject.addProperty("left", word.bbox().left());
        targetObject.addProperty("top", word.bbox().top());
        targetObject.addProperty("right", word.bbox().right());
        targetObject.addProperty("bottom", word.bbox().bottom());

        return targetObject;
    }

    public JsonObject extract(List<OcrResult> ocrResultList) {
        JsonObject extractions = new JsonObject();

        int page = 1;
        for (var ocrResult : ocrResultList) {
            JsonArray pageExtractions = extractPage(ocrResult, page);
            extractions.add(String.valueOf(page), pageExtractions);
            page++;
        }

        return extractions;
    }

}
