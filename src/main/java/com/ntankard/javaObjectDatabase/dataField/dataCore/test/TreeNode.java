package com.ntankard.javaObjectDatabase.dataField.dataCore.test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class TreeNode<T> {
    private String key;
    private T value;
    private List<TreeNode<T>> children;

    public TreeNode(String key, TreeNode<T>... children) {
        this.key = key;
        this.children = new ArrayList<>();
        for (TreeNode<T> child : children) {
            this.children.add(child);
        }
    }

    public String getKey() {
        return key;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public List<TreeNode<T>> getChildren() {
        return children;
    }

    public void addChild(TreeNode<T> child) {
        this.children.add(child);
    }

    public static <T> TreeNode<T> root(TreeNode<T>... children) {
        return new TreeNode<>("root", children);
    }

    public static <T> TreeNode<T> node(String key) {
        return new TreeNode<>(key);
    }
    public static <T> TreeNode<T> node(String key, TreeNode<T>... children) {
        return new TreeNode<>(key, children);
    }

    public TreeNode<T> find(String key) {
        if (this.key.equals(key)) {
            return this;
        }
        for (TreeNode<T> child : children) {
            TreeNode<T> found = child.find(key);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public void iterate(Consumer<TreeNode<T>> action) {
        action.accept(this);
        for (TreeNode<T> child : children) {
            child.iterate(action);
        }
    }

    public TreeNode<T> copy() {
        TreeNode<T> copiedNode = new TreeNode<T>(this.key);
        for (TreeNode<T> child : this.children) {
            copiedNode.children.add(child.copy());
        }
        return copiedNode;
    }

    public boolean isLeaf(){
        return children.size() == 0;
    }
}
