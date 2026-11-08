import model.DirectoryNode;
import model.FileNode;
import visitor.SearchVisitor;
import visitor.StatisticsVisitor;
import visitor.TotalSizeVisitor;

public class Main {
    public static void main(String[] args) {
        DirectoryNode root = new DirectoryNode("root");

        DirectoryNode docs = new DirectoryNode("docs");
        docs.addElement(new FileNode("resume.pdf", 2048));
        docs.addElement(new FileNode("essay.docx", 4096));

        DirectoryNode photos = new DirectoryNode("photos");
        DirectoryNode vacation = new DirectoryNode("vacation");
        vacation.addElement(new FileNode("sea.jpg", 10240));
        vacation.addElement(new FileNode("mountain.jpg", 15360));
        photos.addElement(vacation);

        root.addElement(docs);
        root.addElement(photos);
        root.addElement(new FileNode("config.sys", 512));

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        System.out.println("Total size: " + sizeVisitor.getTotalSize() + " bytes");

        SearchVisitor searchVisitor = new SearchVisitor(".jpg", 12000);
        root.accept(searchVisitor);
        System.out.println("Search results:");
        for (String match : searchVisitor.getMatches()) {
            System.out.println(" - " + match);
        }

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        root.accept(statsVisitor);
        System.out.println("Statistics:");
        System.out.println(" - Files: " + statsVisitor.getFileCount());
        System.out.println(" - Directories: " + statsVisitor.getDirectoryCount());
        System.out.println(" - Max Depth: " + statsVisitor.getMaxDepth());
    }
}