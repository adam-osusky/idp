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

public class DocConverter {
    private final int dpi;

    public DocConverter(int dpi_value) {
        this.dpi = dpi_value;  // 600 default, at least 300
    }

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

    public List<BufferedImage> loadDocImages(String filepath) throws IOException {
        List<BufferedImage> loadedImages = new LinkedList<>();

        String fileExtension = getFileExtension(filepath);

        if ("pdf".equalsIgnoreCase(fileExtension)) {
            loadedImages = pdfToPngs(filepath);
        } else if ("png".equalsIgnoreCase(fileExtension)) {
            loadedImages.add(loadImage(filepath));
        } else {
            throw new IllegalArgumentException("Unsupported file type " + fileExtension + " of file " + filepath);
        }

        return loadedImages;
    }

    private String getFileExtension(String filepath) {
        int lastDotIndex = filepath.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return filepath.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    private BufferedImage loadImage(String filepath) throws IOException {
        try {
            return ImageIO.read(new File(filepath));
        } catch (IOException e) {
            System.err.println("Problem loading image file" + filepath + ": " + e.getMessage());
            throw new IOException(e.getMessage());
        }
    }

    public List<BufferedImage> pdfToPngs(String filepath) throws IOException {
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