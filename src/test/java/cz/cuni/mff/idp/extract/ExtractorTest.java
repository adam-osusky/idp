package src.test.java.cz.cuni.mff.idp.extract;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;
import src.main.java.cz.cuni.mff.idp.doc.Doc;
import src.main.java.cz.cuni.mff.idp.doc.DocConverter;
import src.main.java.cz.cuni.mff.idp.extract.Extractor;
import src.main.java.cz.cuni.mff.idp.ocr.OcrResult;
import src.main.java.cz.cuni.mff.idp.ocr.TesseractOcr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The {@code ExtractorTest} class provides unit tests for the {@link Extractor} class.
 */
class ExtractorTest {

    /**
     * Tests extraction from a multi-page document. It checks if all target information was retrieved.
     */
    @Test
    void extractMultiPageTest() {
        Extractor extractor = new Extractor("src/test/java/cz/cuni/mff/idp/testdata/config.json");

        DocConverter converter = new DocConverter(300);
        TesseractOcr ocr = new TesseractOcr();

        Doc document = converter.loadDoc("src/test/java/cz/cuni/mff/idp/testdata/multi-page-invoice-2.pdf");
        List<OcrResult> ocrResultList = ocr.scanDoc(document);

        var extractions = extractor.extract(ocrResultList);

        Map<String, List<String>> extracted = new HashMap<>();

        for (JsonElement jsonElement : extractions.getAsJsonArray("1")) {
            JsonObject extraction = jsonElement.getAsJsonObject();
            extracted.computeIfAbsent(extraction.get("variable_name").getAsString(), k -> new ArrayList<>()).add(extraction.get("extracted").getAsString());
        }

        for (JsonElement jsonElement : extractions.getAsJsonArray("2")) {
            JsonObject extraction = jsonElement.getAsJsonObject();
            extracted.computeIfAbsent(extraction.get("variable_name").getAsString(), k -> new ArrayList<>()).add(extraction.get("extracted").getAsString());
        }

        assertTrue(extracted.containsKey("name"));
        assertTrue(extracted.containsKey("due_date"));
        assertTrue(extracted.containsKey("last_item"));
        assertTrue(extracted.containsKey("total"));

        assertIterableEquals(List.of("John", "von", "Neuman"), extracted.get("name"));
        assertIterableEquals(List.of("1.1.2024"), extracted.get("due_date"));
        assertIterableEquals(List.of("complete", "theorem"), extracted.get("last_item"));
        assertIterableEquals(List.of("$496,805.00"), extracted.get("total"));
    }

    /**
     * Tests extraction from a single-page document. It checks if all target information was retrieved.
     */
    @Test
    void extractSinglePageTest() {
        Extractor extractor = new Extractor("src/test/java/cz/cuni/mff/idp/testdata/config2.json");

        DocConverter converter = new DocConverter(100);
        TesseractOcr ocr = new TesseractOcr();

        Doc document = converter.loadDoc("src/test/java/cz/cuni/mff/idp/testdata/gallus-invoice-2.pdf");
        List<OcrResult> ocrResultList = ocr.scanDoc(document);

        var extractions = extractor.extract(ocrResultList);

        Map<String, List<String>> extracted = new HashMap<>();

        for (JsonElement jsonElement : extractions.getAsJsonArray("1")) {
            JsonObject extraction = jsonElement.getAsJsonObject();
            extracted.computeIfAbsent(extraction.get("variable_name").getAsString(), k -> new ArrayList<>()).add(extraction.get("extracted").getAsString());
        }

        assertTrue(extracted.containsKey("sender_name"));
        assertTrue(extracted.containsKey("invoice_id"));


        assertIterableEquals(List.of("GALLUS"), extracted.get("sender_name"));
        assertIterableEquals(List.of("1616516"), extracted.get("invoice_id"));
    }
}