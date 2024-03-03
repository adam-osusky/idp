package src.main.java.cz.cuni.mff.idp.ocr;

import src.main.java.cz.cuni.mff.idp.doc.Doc;
import src.main.java.cz.cuni.mff.idp.doc.Doc.DocPage;

import java.util.LinkedList;
import java.util.List;

/**
 * The {@code OcrEngine} interface defines methods for Optical Character Recognition (OCR) engines.
 * Implementations of this interface can use various OCR models.
 */
public interface OcrEngine {

    /**
     * Scans the content of a document page using the OCR engine.
     *
     * @param page The document page to be scanned.
     * @return An {@link OcrResult} containing the OCR analysis results.
     */
    OcrResult scanDocPage(DocPage page);

    /**
     * Scans the entire document using the OCR engine. Uses {@link  OcrEngine#scanDocPage(DocPage)}
     * on every page and concatenates the results into a List.
     *
     * @param document The document to be scanned.
     * @return A list of {@link OcrResult} objects containing OCR results for each page.
     */
    default List<OcrResult> scanDoc(Doc document) {
        LinkedList<OcrResult> scannedPages = new LinkedList<>();

        for (var page : document.pages) {
            scannedPages.add(scanDocPage(page));
        }

        return scannedPages;
    }
}
