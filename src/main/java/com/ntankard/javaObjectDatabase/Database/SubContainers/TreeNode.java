package com.ntankard.javaObjectDatabase.Database.SubContainers;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class TreeNode<T> {

    /**
     * The core data for this node
     */
    public T data;

    /**
     * Parent of this node (null for root node)
     */
    public TreeNode<T> parent;

    /**
     * All children of this node
     */
    public List<TreeNode<T>> children;

    /**
     * Constructor
     */
    public TreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<TreeNode<T>>();
    }

    /**
     * Add a new children to this node and the tree
     *
     * @param child The child to add
     * @return The new child
     */
    public TreeNode<T> addChild(T child) {
        TreeNode<T> childNode = new TreeNode<T>(child);
        childNode.parent = this;
        this.children.add(childNode);
        sortByChildren();
        return childNode;
    }

    /**
     * Dose this Node have a direct child that contains this specific value
     *
     * @param value The value to check
     * @return True if this Node have a direct child that contains this specific value
     */
    public boolean containsChild(T value) {
        return getChild(value) != null;
    }

    /**
     * Get the direct child of this Node that contains a specific value
     *
     * @param data The child value to find
     * @return The direct child of this Node that contains a specific value, or null
     */
    public TreeNode<T> getChild(T data) {
        for (TreeNode<T> child : children) {
            if (child.data.equals(data)) {
                return child;
            }
        }
        return null;
    }

    /**
     * Sort this Nodes children from most, to least sub children
     */
    public void sortByChildren() {
        children.sort(Comparator.comparingInt(o -> o.children.size()));
        Collections.reverse(children);
        if (parent != null) {
            parent.sortByChildren();
        }
    }

    /**
     * Get the number of all the leaves
     *
     * @return The number of all the leaves
     */
    public int size() {
        int total = 0;
        for (TreeNode treeNode : children) {
            total = treeNode.size() + 1;
        }
        return total;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public String toString() {
        return data.toString();
    }
}
