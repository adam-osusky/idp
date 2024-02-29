package src.main.java.cz.cuni.mff.idp.doc;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class Doc {
    public List<DocPage> pages = new LinkedList<>();

    public record DocPage(BufferedImage image) {}
}
