package src.main.java.cz.cuni.mff.idp.ocr;

import java.util.LinkedList;
import java.util.List;

public class OcrResult {
    public List<Word> words;

    public OcrResult() {
        words = new LinkedList<>();
    }

    public record BoundingBox(int left, int top, int right, int bottom) {
    }

    public record Word(String str, BoundingBox bbox) {
    }
}