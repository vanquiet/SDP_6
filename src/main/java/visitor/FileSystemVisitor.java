package visitor;

import model.DirectoryNode;
import model.FileNode;

public interface FileSystemVisitor {
    void visit(FileNode file);
    void visit(DirectoryNode directory);
}