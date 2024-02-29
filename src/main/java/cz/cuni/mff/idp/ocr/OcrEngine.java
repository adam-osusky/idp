package src.main.java.cz.cuni.mff.idp.ocr;

import src.main.java.cz.cuni.mff.idp.doc.Doc;
import src.main.java.cz.cuni.mff.idp.doc.Doc.DocPage;

import java.util.List;

public interface OcrEngine {
    OcrResult scanDocPage(DocPage page);

    List<OcrResult> scanDoc(Doc document);
}
