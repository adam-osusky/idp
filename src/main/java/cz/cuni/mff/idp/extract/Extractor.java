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

/**
 * The {@code Extractor} class facilitates the extraction of information from OCR results based on predefined targets.
 */
public class Extractor {
    Map<Integer, List<Target>> targets = new HashMap<>();
    float intersectionThreshold = 0.5F;

    /**
     * Constructs an {@code Extractor} instance using the provided configuration file.
     * The configuration file must be json with structure {"targets" : [target_1, targer_2, ...]}
     * where target_i is json with specified bbox coordinates, page number, and class for the extraction
     * data. For an example look at src/test/java/cz/cuni/mff/idp/testdata/config.json
     *
     * @param configFile The path to the configuration file in JSON format.
     */
    public Extractor(String configFile) {
        JsonObject config = loadJson(configFile);

        if (config == null) {
            System.err.println("Extractor is initialized with an empty config!");
            return;
        }

        setUp(config);

    }

    /**
     * Record representing an extraction target with variable name, page number, and bounding box.
     */
    record Target(String variableName, int page, OcrResult.BoundingBox bbox) {
    }

    /**
     * Loads a JSON file and returns its content as a {@link JsonObject}.
     *
     * @param filePath The path to the JSON file.
     * @return The parsed JSON object, or {@code null} if an error occurs during loading.
     */
    public static JsonObject loadJson(String filePath) {
        try (FileReader fileReader = new FileReader(filePath)) {
            return JsonParser.parseReader(fileReader).getAsJsonObject();
        } catch (IOException e) {
            System.err.printf("Problem with loading json %s \n%s", filePath, e.getMessage());
            return null;
        }
    }

    /**
     * Sets up extraction targets based on the provided configuration.
     * It assumes structure as in src/test/java/cz/cuni/mff/idp/testdata/config.json
     *
     * @param config The configuration JSON object.
     */
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

    /**
     * Extracts information from an OCR result for a specific page based on predefined targets.
     * It just checks if intersection of word and any target area is at least {@link Extractor#intersectionThreshold}
     *
     * @param ocrResult The OCR result for the page.
     * @param page      The page number.
     * @return A {@link JsonArray} containing the extracted information for the specified page.
     */
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

    /**
     * Creates a {@link JsonObject} representing an extracted result.
     *
     * @param variableName The variable name of the extraction target.
     * @param page         The page number.
     * @param word         The OCR result word.
     * @return A {@link JsonObject} representing the extracted result.
     */
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

    /**
     * Extracts information from OCR results for the entire document based on predefined targets.
     * Simply calls {@link Extractor#extractPage(OcrResult, int)} on every page.
     *
     * @param ocrResultList The list of OCR results for each page.
     * @return A {@link JsonObject} containing the extracted information for each page.
     */
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
