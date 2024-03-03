package src.main.java.cz.cuni.mff.idp.doc;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * The {@code DocConverter} class provides methods for loading and converting documents.
 * It is used for getting {@link Doc} representation that can be used for ocr.
 */
public class DocConverter {
    /**
     * The DPI value (dots per inch) for image conversion.
     * It should be set to a value of an original document for
     * successful ocr scanning with tesseract.
     */
    private final int dpi;

    public DocConverter(int dpi_value) {
        this.dpi = dpi_value;
    }

    /**
     * Loads a document from the given file path. If IO error then
     * it returns empty {@link  Doc} class.
     *
     * @param filepath The path of the document file.
     * @return A Doc object representing the loaded document.
     */
    public Doc loadDoc(String filepath) {
        Doc document = new Doc();

        try {
            List<BufferedImage> page_images = loadDocImages(filepath);

            for (var page_image : page_images) {
                document.pages.add(new Doc.DocPage(page_image));
            }
        } catch (IOException e) {
            System.err.printf("While loading a documennt %s error : %s", filepath, e.getMessage());
        }

        return document;
    }

    /**
     * Loads images from a document file based on the file extension.
     * One page constitutes to one image.
     *
     * @param filepath The path of the document file.
     * @return A list of BufferedImage objects representing document pages.
     * @throws IOException If an I/O error occurs during the loading process.
     */
    public List<BufferedImage> loadDocImages(String filepath) throws IOException {
        List<BufferedImage> loadedImages = new LinkedList<>();

        String fileExtension = getFileExtension(filepath);

        if ("pdf".equalsIgnoreCase(fileExtension)) {
            loadedImages = pdfToImages(filepath);
        } else if ("png".equalsIgnoreCase(fileExtension)) {
            loadedImages.add(loadImage(filepath));
        } else {
            throw new IllegalArgumentException("Unsupported file type " + fileExtension + " of file " + filepath);
        }

        return loadedImages;
    }

    /**
     * Extracts the file extension from the given file path.
     * Simply looks at the last dot.
     *
     * @param filepath The path of the file.
     * @return The lowercase file extension or an empty string if not found.
     */
    private String getFileExtension(String filepath) {
        int lastDotIndex = filepath.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filepath.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    /**
     * Loads a BufferedImage from an image file.
     *
     * @param filepath The path of the image file.
     * @return The loaded BufferedImage.
     * @throws IOException If an I/O error occurs during the loading process.
     */
    private BufferedImage loadImage(String filepath) throws IOException {
        try {
            return ImageIO.read(new File(filepath));
        } catch (IOException e) {
            System.err.println("Problem loading image file" + filepath + ": " + e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

    /**
     * Gets from a PDF document a list of BufferedImages representing its pages.
     *
     * @param filepath The path of the PDF document.
     * @return A list of BufferedImage objects representing the pages of the PDF document.
     * @throws IOException If an I/O error occurs during the conversion process.
     */
    public List<BufferedImage> pdfToImages(String filepath) throws IOException {
        LinkedList<BufferedImage> image_pages = new LinkedList<>();

        try (PDDocument document = Loader.loadPDF(new File(filepath))) {

            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                // Render the page as an image
                BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, this.dpi);
                image_pages.add(image);
            }

        } catch (IOException e) {
            System.err.println("Problem with converting pdf to png images of file " + filepath);
            throw e;
        }

        return image_pages;
    }
}