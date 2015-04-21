package ru.fly.client;

import ru.fly.client.ui.EventBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: fil
 * Date: 21.04.15
 */
public class TreeStore<T> extends EventBase {

    private class TreeStoreItem<M>{

        private M model;
        private List<TreeStoreItem<M>> children = new ArrayList<>();
        private boolean expanded = false;

        public TreeStoreItem(M model){
            setModel(model);
        }

        public M getModel() {
            return model;
        }

        public void setModel(M model) {
            this.model = model;
        }

        public List<TreeStoreItem<M>> getChildren() {
            return children;
        }

        public void setChildren(List<TreeStoreItem<M>> children) {
            this.children = children;
        }

        public boolean isExpanded() {
            return expanded;
        }

        public void setExpanded(boolean expanded) {
            this.expanded = expanded;
        }

        public List<M> getModelChildren(){
            List<M> ret = new ArrayList<>();
            for(TreeStoreItem<M> item : children){
                ret.add(item.getModel());
            }
            return ret;
        }
    }

    private TreeStoreItem<T> root = new TreeStoreItem<>(null);
    private Map<T, TreeStoreItem<T>> links = new HashMap<>();

    public boolean isEmpty(){
        return root.getChildren().isEmpty();
    }

    public void add(T parent, T model){
        if(model == null){
            throw new IllegalArgumentException("Model cant be NULL!");
        }
        TreeStoreItem<T> item = new TreeStoreItem<>(model);
        findItem(parent).getChildren().add(item);
        links.put(model, item);
    }

    public void addAll(T parent, List<T> models){
        TreeStoreItem<T> parentItem = findItem(parent);
        for(T model : models){
            if(model == null){
                throw new IllegalArgumentException("Model cant be NULL!");
            }
            TreeStoreItem<T> item = new TreeStoreItem<>(model);
            parentItem.getChildren().add(item);
            links.put(model, item);
        }
    }

    public List<T> getChildren(T parent){
        return findItem(parent).getModelChildren();
    }

    // ------------------ privates -------------------

    private TreeStoreItem<T> findItem(T model){
        if(model == null){
            return root;
        }else{
            return links.get(model);
        }
    }

}
