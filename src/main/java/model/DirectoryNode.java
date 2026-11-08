package model;

import visitor.FileSystemVisitor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DirectoryNode implements FileSystemElement {
    private final String name;
    private final List<FileSystemElement> children = new ArrayList<>();
    private DirectoryNode parent;

    public DirectoryNode(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Directory name cannot be empty");
        }
        this.name = name;
    }

    public void addElement(FileSystemElement element) {
        if (element == null) {
            throw new IllegalArgumentException("Element cannot be null");
        }
        if (element == this) {
            throw new IllegalArgumentException("Cannot add directory to itself");
        }
        for (FileSystemElement child : children) {
            if (child.getName().equals(element.getName())) {
                throw new IllegalArgumentException("Duplicate sibling name: " + element.getName());
            }
        }
        if (element.getParent() != null) {
            throw new IllegalStateException("Element already has a parent");
        }

        DirectoryNode curr = this;
        while (curr != null) {
            if (curr == element) {
                throw new IllegalArgumentException("Cycle detected");
            }
            curr = curr.getParent();
        }

        element.setParent(this);
        children.add(element);
    }

    public List<FileSystemElement> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public DirectoryNode getParent() {
        return parent;
    }

    @Override
    public void setParent(DirectoryNode parent) {
        this.parent = parent;
    }

    @Override
    public void accept(FileSystemVisitor visitor) {
        visitor.visit(this);
    }
}