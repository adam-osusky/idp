package src.main.java.cz.cuni.mff.idp.ocr;

import src.main.java.cz.cuni.mff.idp.doc.Doc.DocPage;

public interface OcrEngine {
    OcrResult scan(DocPage page);
}
