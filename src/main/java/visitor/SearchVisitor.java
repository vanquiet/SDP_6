package visitor;

import model.DirectoryNode;
import model.FileNode;
import model.FileSystemElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SearchVisitor implements FileSystemVisitor {
    private final String query;
    private final long minSize;
    private final List<String> matches = new ArrayList<>();
    private final List<String> currentPath = new ArrayList<>();
    private boolean isRoot = true;

    public SearchVisitor(String query, long minSize) {
        this.query = (query == null) ? "" : query;
        this.minSize = Math.max(0, minSize);
    }

    @Override
    public void visit(FileNode file) {
        if (file.getName().contains(query) && file.getSize() >= minSize) {
            if (currentPath.isEmpty()) {
                matches.add(file.getName());
            } else {
                matches.add(String.join("/", currentPath) + "/" + file.getName());
            }
        }
    }

    @Override
    public void visit(DirectoryNode directory) {
        boolean wasRoot = isRoot;
        if (wasRoot) {
            isRoot = false;
        } else {
            currentPath.add(directory.getName());
        }

        for (FileSystemElement child : directory.getChildren()) {
            child.accept(this);
        }

        if (!wasRoot) {
            currentPath.remove(currentPath.size() - 1);
        }
    }

    public List<String> getMatches() {
        List<String> sorted = new ArrayList<>(matches);
        Collections.sort(sorted);
        return Collections.unmodifiableList(sorted);
    }

    public void reset() {
        matches.clear();
        currentPath.clear();
        isRoot = true;
    }
}