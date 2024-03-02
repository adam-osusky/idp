package src.test.java.cz.cuni.mff.idp.ocr;

import org.junit.jupiter.api.Test;
import src.main.java.cz.cuni.mff.idp.doc.Doc;
import src.main.java.cz.cuni.mff.idp.doc.DocConverter;
import src.main.java.cz.cuni.mff.idp.ocr.OcrResult;
import src.main.java.cz.cuni.mff.idp.ocr.TesseractOcr;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TesseractOcrTest {

    void firstPageTest(OcrResult page) {
        OcrResult.Word Albert = new OcrResult.Word("Albert", new OcrResult.BoundingBox(200, 582, 316, 614));
        boolean foundAlbert = false;
        OcrResult.Word price = new OcrResult.Word("$1,124.00", new OcrResult.BoundingBox(2156, 644, 2363, 683));
        boolean foundPrice = false;

        for (var word : page.words) {
            if ("Albert".equals(word.str())) {
                if (Albert.bbox().IoU(word.bbox()) > 0.5) {
                    foundAlbert = true;
                }
            }

            if ("$1,124.00".equals(word.str())) {
                if (price.bbox().IoU(word.bbox()) > 0.5) {
                    foundPrice = true;
                }
            }
        }

        assertTrue(foundAlbert);
        assertTrue(foundPrice);
    }

    void secondPageTest(OcrResult page) {
        OcrResult.Word iphone = new OcrResult.Word("iphone", new OcrResult.BoundingBox(187, 1582, 317, 1614));
        boolean foundIphone = false;
        OcrResult.Word Tax = new OcrResult.Word("Tax", new OcrResult.BoundingBox(1750, 1946, 1817, 1975));
        boolean foundTax = false;

        for (var word : page.words) {
            if ("iphone".equals(word.str())) {
                if (iphone.bbox().IoU(word.bbox()) > 0.5) {
                    foundIphone = true;
                }
            }

            if ("Tax".equals(word.str())) {
                if (Tax.bbox().IoU(word.bbox()) > 0.5) {
                    foundTax = true;
                }
            }
        }

        assertTrue(foundIphone);
        assertTrue(foundTax);
    }

    @Test
    void pageTest() {
        DocConverter converter = new DocConverter(300);
        TesseractOcr ocr = new TesseractOcr();

        Doc document = converter.loadDoc("src/test/java/cz/cuni/mff/idp/testdata/multi-page-invoice-1.pdf");

        OcrResult firstPage = ocr.scanDocPage(document.pages.getFirst());
        firstPageTest(firstPage);
    }

    @Test
    void docTest() {
        DocConverter converter = new DocConverter(300);
        TesseractOcr ocr = new TesseractOcr();

        Doc document = converter.loadDoc("src/test/java/cz/cuni/mff/idp/testdata/multi-page-invoice-1.pdf");

        List<OcrResult> ocrResultList = ocr.scanDoc(document);

        OcrResult firstPage = ocrResultList.getFirst();
        firstPageTest(firstPage);

        OcrResult secondPage = ocrResultList.getLast();
        secondPageTest(secondPage);

    }
}