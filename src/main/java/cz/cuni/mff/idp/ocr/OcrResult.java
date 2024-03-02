package src.main.java.cz.cuni.mff.idp.ocr;

import java.util.LinkedList;
import java.util.List;

public class OcrResult {
    public List<Word> words;

    public OcrResult() {
        words = new LinkedList<>();
    }

    public record BoundingBox(int left, int top, int right, int bottom) {

        public int area() {
            return (right - left) * (bottom - top);
        }

        public int intersection(BoundingBox other) {
            int xOverlap = Math.max(0, Math.min(this.right, other.right) - Math.max(this.left, other.left));
            int yOverlap = Math.max(0, Math.min(this.bottom, other.bottom) - Math.max(this.top, other.top));
            return xOverlap * yOverlap;
        }

        public double IoU(BoundingBox other) {
            int intersection = this.intersection(other);
            int union = this.area() + other.area() - intersection;

            if (union == 0) {
                return 0.0;
            }

            return (double) intersection / union;
        }
    }

    public record Word(String str, BoundingBox bbox) {
    }
}