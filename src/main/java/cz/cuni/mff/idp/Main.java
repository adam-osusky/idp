package src.main.java.cz.cuni.mff.idp;

import com.google.gson.JsonObject;
import src.main.java.cz.cuni.mff.idp.doc.Doc;
import src.main.java.cz.cuni.mff.idp.doc.DocConverter;
import src.main.java.cz.cuni.mff.idp.extract.Extractor;
import src.main.java.cz.cuni.mff.idp.ocr.OcrResult;
import src.main.java.cz.cuni.mff.idp.ocr.TesseractOcr;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * The Main class serves as the entry point for the document extraction application.
 * It takes input parameters, performs OCR scanning, extracts information, and writes the results to a JSON file.
 */
public class Main {

    /**
     * The main method to run the document extraction application.
     *
     * @param args Command-line arguments:
     *             - args[0]: Path to the configuration file.
     *             - args[1]: DPI (Dots Per Inch) for document conversion.
     *             - args[2]: Path to the input document (PDF or image).
     *             - args[3]: Path to the output JSON file.
     */
    public static void main(String[] args) {
        if (args.length != 4) {
            System.out.println("Usage: java src.main.java.cz.cuni.mff.idp.Main.java <config> <dpi> <file-to-extract> <output-path>");
            System.exit(1);
        }

        Extractor extractor = new Extractor(args[0]);

        int dpi;
        try {
            dpi = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.printf("Second argument <%s> must be int. \n%s", args[1], e.getMessage());
            return;
        }

        DocConverter converter = new DocConverter(dpi);
        TesseractOcr ocr = new TesseractOcr();

        Doc document = converter.loadDoc(args[2]);
        List<OcrResult> ocrResultList = ocr.scanDoc(document);
        JsonObject extractions = extractor.extract(ocrResultList);

        String jsonString = extractions.toString();
        try {
            Files.writeString(Path.of(args[3]), jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}