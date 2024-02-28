package src.main.java.cz.cuni.mff.idp.ocr;

import java.util.HashMap;
import java.util.Map;

public class OcrResult {
    private Map<String, BoundingBox> wordBoundingBoxMap;

    public OcrResult() {
        wordBoundingBoxMap = new HashMap<>();
    }

    public void addWord(String word, BoundingBox boundingBox) {
        wordBoundingBoxMap.put(word, boundingBox);
    }

    public BoundingBox getBoundingBox(String word) {
        return wordBoundingBoxMap.get(word);
    }

    public record BoundingBox(int left, int top, int right, int bottom) {}

}