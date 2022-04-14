package com.ntankard.javaObjectDatabase.database.subContainers;

import com.ntankard.javaObjectDatabase.dataObject.DataObject;
import com.ntankard.javaObjectDatabase.dataObject.interfaces.FileInterface;
import com.ntankard.javaObjectDatabase.exception.nonCorrupting.NonCorruptingException;

import java.util.Map;

/**
 * A container holding a tree listing all know files in the save file that do not have a DataObject attached to them
 *
 * @author Nicholas Tankard
 */
public class UnprocessedFileMap extends Container<String, Map<String, FileInterface>> {

    /**
     * A tree of all files that are in the filed directly of the save file and do not have DataObjects attached to them
     */
    private TreeNode<String> folderTree = null;

    /**
     * @inheritDoc
     */
    @Override
    public void add(DataObject dataObject) {
        if (this.folderTree == null) {
            throw new NonCorruptingException("The know files have not been set yet");
        }
        if (FileInterface.class.isAssignableFrom(dataObject.getClass())) {
            FileInterface fileInterface = (FileInterface) dataObject;
            String[] path = fileInterface.getContainerPath().split("\\\\");
            TreeNode<String> node = folderTree;
            for (String step : path) {
                node = node.getChild(step);
                if (node == null) {
                    throw new RuntimeException();
                }
            }
            if (node.removeChild(fileInterface.getFileName()) == null) {
                throw new RuntimeException();
            }

            TreeNode<String> parent;
            while (!node.isRoot()) {
                if (node.size() != 0) {
                    break;
                }
                parent = node.getParent();
                parent.removeChild(node.data);
                node = parent;
            }
        }
    }

    /**
     * @inheritDoc
     */
    @Override
    public void remove(DataObject dataObject) {
        if (this.folderTree == null) {
            throw new NonCorruptingException("The know files have not been set yet");
        }
        if (FileInterface.class.isAssignableFrom(dataObject.getClass())) {
            FileInterface fileInterface = (FileInterface) dataObject;
            String[] path = fileInterface.getContainerPath().split("\\\\");
            TreeNode<String> node = folderTree;
            for (String step : path) {
                if (node.getChild(step) == null) {
                    node = node.addChild(step);
                } else {
                    node = node.getChild(step);
                }
            }
            if (node.getChild(fileInterface.getFileName()) != null) {
                throw new RuntimeException();
            }
            node.addChild(fileInterface.getFileName());
        }
    }

    /**
     * Get the root node
     *
     * @return The root node
     */
    public TreeNode<String> getFolderTree() {
        return folderTree;
    }

    /**
     * Set the tree node
     *
     * @param folderTree The root node
     */
    public void setFolderTree(TreeNode<String> folderTree) {
        if (this.folderTree != null) {
            throw new NonCorruptingException("The file tree can not be set twice");
        }
        this.folderTree = folderTree;
    }
}
