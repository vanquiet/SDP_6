package visitor;

import model.DirectoryNode;
import model.FileNode;
import model.FileSystemElement;

public class StatisticsVisitor implements FileSystemVisitor {
    private int fileCount = 0;
    private int directoryCount = 0;
    private int currentDepth = 0;
    private int maxDepth = 0;

    @Override
    public void visit(FileNode file) {
        fileCount++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        directoryCount++;
        if (currentDepth > maxDepth) {
            maxDepth = currentDepth;
        }

        currentDepth++;
        for (FileSystemElement child : directory.getChildren()) {
            child.accept(this);
        }
        currentDepth--;
    }

    public int getFileCount() {
        return fileCount;
    }

    public int getDirectoryCount() {
        return directoryCount;
    }

    public int getMaxDepth() {
        return maxDepth;
    }

    public void reset() {
        fileCount = 0;
        directoryCount = 0;
        currentDepth = 0;
        maxDepth = 0;
    }
}