import model.DirectoryNode;
import model.FileNode;
import visitor.FileSystemVisitor;
import visitor.SearchVisitor;
import visitor.StatisticsVisitor;
import visitor.TotalSizeVisitor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemVisitorTest {

    private DirectoryNode root;
    private DirectoryNode src;
    private DirectoryNode testDir;
    private FileNode pomFile;
    private FileNode mainCode;
    private FileNode testCode;

    @BeforeEach
    void setUp() {
        root = new DirectoryNode("project");
        src = new DirectoryNode("src");
        testDir = new DirectoryNode("test");

        pomFile = new FileNode("pom.xml", 800);
        mainCode = new FileNode("App.java", 1500);
        testCode = new FileNode("AppTest.java", 1200);

        src.addElement(mainCode);
        testDir.addElement(testCode);

        root.addElement(pomFile);
        root.addElement(src);
        root.addElement(testDir);
    }

    @Test
    void testT1_DoubleDispatchRouting() {
        boolean[] flags = new boolean[2];

        FileSystemVisitor testVisitor = new FileSystemVisitor() {
            @Override
            public void visit(FileNode file) {
                flags[0] = true;
            }

            @Override
            public void visit(DirectoryNode directory) {
                flags[1] = true;
            }
        };

        pomFile.accept(testVisitor);
        assertTrue(flags[0]);
        assertFalse(flags[1]);

        src.accept(testVisitor);
        assertTrue(flags[1]);
    }

    @Test
    void testT2_SizeAccumulationAcrossNesting() {
        TotalSizeVisitor visitor = new TotalSizeVisitor();
        root.accept(visitor);

        assertEquals(800 + 1500 + 1200, visitor.getTotalSize());
    }

    @Test
    void testT3_SearchCombinesNameAndMinSize() {
        SearchVisitor visitor = new SearchVisitor("App", 1300);
        root.accept(visitor);

        List<String> results = visitor.getMatches();
        assertEquals(1, results.size());
        assertEquals("src/App.java", results.get(0));
    }

    @Test
    void testT4_SearchDeterministicRootRelativePaths() {
        SearchVisitor visitor = new SearchVisitor(".java", 0);
        root.accept(visitor);

        List<String> results = visitor.getMatches();
        assertEquals(2, results.size());
        assertEquals("src/App.java", results.get(0));
        assertEquals("test/AppTest.java", results.get(1));
    }

    @Test
    void testT5_StatisticsCountsAndMaxDepth() {
        StatisticsVisitor stats = new StatisticsVisitor();
        root.accept(stats);

        assertEquals(3, stats.getFileCount());
        assertEquals(3, stats.getDirectoryCount());
        assertEquals(2, stats.getMaxDepth());
    }

    @Test
    void testT6_EmptyStructureAndNoMatches() {
        DirectoryNode emptyRoot = new DirectoryNode("empty");

        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        emptyRoot.accept(sizeVisitor);
        assertEquals(0, sizeVisitor.getTotalSize());

        StatisticsVisitor statsVisitor = new StatisticsVisitor();
        emptyRoot.accept(statsVisitor);
        assertEquals(0, statsVisitor.getFileCount());
        assertEquals(1, statsVisitor.getDirectoryCount());
        assertEquals(0, statsVisitor.getMaxDepth());

        SearchVisitor searchVisitor = new SearchVisitor("not_existing", 0);
        emptyRoot.accept(searchVisitor);
        assertTrue(searchVisitor.getMatches().isEmpty());
    }

    @Test
    void testT7_RepeatExecutionNoStateLeak() {
        TotalSizeVisitor sizeVisitor = new TotalSizeVisitor();
        root.accept(sizeVisitor);
        long firstRun = sizeVisitor.getTotalSize();

        sizeVisitor.reset();
        root.accept(sizeVisitor);
        long secondRun = sizeVisitor.getTotalSize();

        assertEquals(firstRun, secondRun);

        SearchVisitor searchVisitor = new SearchVisitor(".java", 0);
        root.accept(searchVisitor);
        int firstCount = searchVisitor.getMatches().size();

        searchVisitor.reset();
        root.accept(searchVisitor);
        int secondCount = searchVisitor.getMatches().size();

        assertEquals(firstCount, secondCount);
    }

    @Test
    void testT8_TreeIntegrityEnforced() {
        assertThrows(IllegalArgumentException.class, () -> new FileNode("bad.txt", -50));

        assertThrows(IllegalArgumentException.class, () -> {
            root.addElement(new FileNode("pom.xml", 100));
        });

        assertThrows(IllegalStateException.class, () -> {
            src.addElement(pomFile);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            root.addElement(root);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            src.addElement(root);
        });
    }
}