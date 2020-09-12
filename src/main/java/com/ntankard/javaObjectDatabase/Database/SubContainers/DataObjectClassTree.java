package com.ntankard.javaObjectDatabase.Database.SubContainers;

import com.ntankard.javaObjectDatabase.CoreObject.DataObject;

import java.util.*;

public class DataObjectClassTree extends Container<Integer, Integer> {

    private TreeNode<Class<? extends DataObject>> classTreeRoot = new TreeNode<>(DataObject.class);

    /**
     * {@inheritDoc
     */
    @Override
    public void add(DataObject toAdd) {
        add(toAdd.getClass());
    }

    /**
     * Add just based on the class
     *
     * @param aClass The class to add
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public void add(Class<? extends DataObject> aClass) {
        List<Class<? extends DataObject>> classes = new ArrayList<>();

        // Get all the classes the base one inherits from
        do {
            classes.add(aClass);
            // Jump up the inheritance tree
            aClass = (Class<? extends DataObject>) aClass.getSuperclass();
        } while (DataObject.class.isAssignableFrom(aClass));

        // Add the classes to the tree
        Collections.reverse(classes);
        TreeNode node = classTreeRoot;
        for (Class aClass1 : classes) {

            // Ignore the root
            if (aClass1.equals(DataObject.class)) {
                if (!node.data.equals(DataObject.class)) {
                    throw new RuntimeException("Corrupt map");
                }
                continue;
            }

            // Add a leaf if needed
            if (!node.containsChild(aClass1)) {
                node.addChild(aClass1);
            }

            // Navigate down the Tree in parallel with the classes
            node = node.getChild(aClass1);
        }
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void remove(DataObject dataObject) {
    }

    /**
     * Get the number of all the leaves
     *
     * @return The number of all the leaves
     */
    public int size() {
        return classTreeRoot.size();
    }

    /**
     * Get the root of the tree
     *
     * @return The root of the tree
     */
    public TreeNode<Class<? extends DataObject>> getClassTreeRoot() {
        return classTreeRoot;
    }
}
