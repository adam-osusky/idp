package src.test.java.cz.cuni.mff.idp.doc;

import org.junit.jupiter.api.Test;
import src.main.java.cz.cuni.mff.idp.doc.Doc;
import src.main.java.cz.cuni.mff.idp.doc.DocConverter;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DocConverterTest {

    @Test
    void pdfToPngs() throws IOException {
        DocConverter converter = new DocConverter(300);
        var pages = converter.pdfToPngs("src/test/java/cz/cuni/mff/idp/testdata/multi-page-invoice-1.pdf");
        assertEquals(2, pages.size());
    }

    @Test
    void loadDocImages() throws IOException {
        DocConverter converter = new DocConverter(300);

        var pages = converter.loadDocImages("src/test/java/cz/cuni/mff/idp/testdata/gallus-invoice-1.pdf");
        assertEquals(1, pages.size());

        pages = converter.loadDocImages("src/test/java/cz/cuni/mff/idp/testdata/gallus-invoice-1.png");
        assertEquals(1, pages.size());

        pages = converter.pdfToPngs("src/test/java/cz/cuni/mff/idp/testdata/multi-page-invoice-1.pdf");
        assertEquals(2, pages.size());
    }

    @Test
    void loadDoc() {
        DocConverter converter = new DocConverter(300);

        Doc document = converter.loadDoc("src/test/java/cz/cuni/mff/idp/testdata/gallus-invoice-1.pdf");
        assertEquals(1, document.pages.size());

        document = converter.loadDoc("src/test/java/cz/cuni/mff/idp/testdata/gallus-invoice-1.png");
        assertEquals(1, document.pages.size());

        document = converter.loadDoc("src/test/java/cz/cuni/mff/idp/testdata/multi-page-invoice-1.pdf");
        assertEquals(2, document.pages.size());
    }
}