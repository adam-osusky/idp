package src.main.java.cz.cuni.mff.idp.ocr;

/**
 * Exception class representing an error that occurred during Tesseract OCR processing.
 */
public class TesseractException extends RuntimeException {
    public TesseractException(String message) {
        super(message);
    }
}
