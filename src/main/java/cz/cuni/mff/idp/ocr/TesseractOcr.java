package src.main.java.cz.cuni.mff.idp.ocr;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.LeptonicaFrameConverter;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.ResultIterator;
import org.bytedeco.tesseract.TessBaseAPI;
import src.main.java.cz.cuni.mff.idp.doc.Doc;

import java.util.List;

import static org.bytedeco.tesseract.global.tesseract.RIL_WORD;

public class TesseractOcr implements OcrEngine {
    private final String language = "eng";

    @Override
    public OcrResult scanDocPage(Doc.DocPage page) {

        try (TessBaseAPI api = new TessBaseAPI();
             Java2DFrameConverter converter = new Java2DFrameConverter();
             LeptonicaFrameConverter converter2 = new LeptonicaFrameConverter();
             PIX pix = converter2.convert(converter.convert(page.image()))) {
            if (api.Init(null, language) != 0) {
                throw new TesseractException("Could not initialize tesseract.");
            }

            api.SetImage(pix);
            api.GetUTF8Text();

            final ResultIterator ri = api.GetIterator();

            IntPointer left = new IntPointer(0);
            IntPointer top = new IntPointer(0);
            IntPointer right = new IntPointer(0);
            IntPointer bottom = new IntPointer(0);

            OcrResult ocrResult = new OcrResult();

            ri.Begin();
            do {

                BytePointer word = ri.GetUTF8Text(RIL_WORD);
//                float conf = ri.Confidence(RIL_WORD);
                boolean box = ri.BoundingBox(RIL_WORD, left, top, right, bottom);
                OcrResult.BoundingBox bbox = new OcrResult.BoundingBox(left.get(), top.get(), right.get(), bottom.get());
                ocrResult.words.add(new OcrResult.Word(word.getString(), bbox));

            } while (ri.Next(RIL_WORD));

            return ocrResult;

        } catch (Exception e) {
            throw new TesseractException(e.getMessage());
        }
    }

    @Override
    public List<OcrResult> scanDoc(Doc document) {
        return null;
    }
}
