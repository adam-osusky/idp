package src.main.java.cz.cuni.mff.idp.ocr;

import src.main.java.cz.cuni.mff.idp.doc.Doc;
import src.main.java.cz.cuni.mff.idp.doc.Doc.DocPage;

import java.util.LinkedList;
import java.util.List;

public interface OcrEngine {
    OcrResult scanDocPage(DocPage page);

    default List<OcrResult> scanDoc(Doc document) {
        LinkedList<OcrResult> scannedPages = new LinkedList<>();

        for (var page : document.pages) {
            scannedPages.add(scanDocPage(page));
        }

        return scannedPages;
    }
}
