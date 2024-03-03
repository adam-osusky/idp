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

class ExtractorTest {

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
}