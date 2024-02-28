package src.test.java.cz.cuni.mff.idp.doc;

import org.junit.jupiter.api.Test;
import src.main.java.cz.cuni.mff.idp.doc.DocConverter;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocConverterTest {

    @Test
    void pdfToPngs() throws IOException {
        DocConverter converter = new DocConverter(100);
        var pages = converter.pdfToPngs("src/test/java/cz/cuni/mff/idp/testdata/multi-page.pdf");
        assertEquals(13, pages.size());
    }

    @Test
    void loadDoc() throws IOException {
        DocConverter converter = new DocConverter(100);

        var pages = converter.loadDoc("src/test/java/cz/cuni/mff/idp/testdata/gallus-invoice-1.pdf");
        assertEquals(1, pages.size());

        pages = converter.loadDoc("src/test/java/cz/cuni/mff/idp/testdata/gallus-invoice-1.png");
        assertEquals(1, pages.size());

        pages = converter.pdfToPngs("src/test/java/cz/cuni/mff/idp/testdata/multi-page.pdf");
        assertEquals(13, pages.size());
    }
}