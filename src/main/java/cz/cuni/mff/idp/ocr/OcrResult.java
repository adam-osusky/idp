package src.main.java.cz.cuni.mff.idp.ocr;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code OcrResult} class represents the result of an OCR analysis.
 */
public class OcrResult {
    public List<Word> words;

    public OcrResult() {
        words = new LinkedList<>();
    }

    public record BoundingBox(int left, int top, int right, int bottom) {

        public int area() {
            return (right - left) * (bottom - top);
        }

        /**
         * Calculates the intersection area between this and other bounding boxes.
         * Simply finding top left intersection point and right bottom intersection point.
         * Afterward computing their difference. If it is negative then returns zero.
         *
         * @param other The other bounding box to compare with.
         * @return The area of the intersection between the two bounding boxes.
         */
        public int intersection(BoundingBox other) {
            int xOverlap = Math.max(0, Math.min(this.right, other.right) - Math.max(this.left, other.left));
            int yOverlap = Math.max(0, Math.min(this.bottom, other.bottom) - Math.max(this.top, other.top));
            return xOverlap * yOverlap;
        }

        /**
         * Calculates the Intersection over Union (IoU) between two bounding boxes.
         * IoU = intersection / union
         *
         * @param other The other bounding box to compare with.
         * @return The IoU between the two bounding boxes.
         */
        public double IoU(BoundingBox other) {
            int intersection = this.intersection(other);
            int union = this.area() + other.area() - intersection;

            if (union == 0) {
                return 0.0;
            }

            return (double) intersection / union;
        }
    }

    /**
     * The {@code Word} record represents a recognized word along with its bounding box.
     */
    public record Word(String str, BoundingBox bbox) {
    }
}