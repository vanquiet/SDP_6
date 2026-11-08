package visitor;

import model.DirectoryNode;
import model.FileNode;
import model.FileSystemElement;

public class TotalSizeVisitor implements FileSystemVisitor {
    private long totalSize = 0;

    @Override
    public void visit(FileNode file) {
        totalSize += file.getSize();
    }

    @Override
    public void visit(DirectoryNode directory) {
        for (FileSystemElement child : directory.getChildren()) {
            child.accept(this);
        }
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void reset() {
        totalSize = 0;
    }
}