package ru.fly.client;

import java.util.ArrayList;
import java.util.List;

/**
 * User: fil
 * Date: 22.04.15
 */
public class TreeStoreItem<T>{

    private T model;
    private List<TreeStoreItem<T>> children = new ArrayList<>();
    private boolean expanded = false;

    /** hide constructor for TreeStore */
    protected TreeStoreItem(T model){
        setModel(model);
    }

    protected T getModel() {
        return model;
    }

    protected void setModel(T model) {
        this.model = model;
    }

    protected List<TreeStoreItem<T>> getChildren() {
        return children;
    }

    protected void setChildren(List<TreeStoreItem<T>> children) {
        this.children = children;
    }

    protected boolean isExpanded() {
        return expanded;
    }

    protected void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    protected List<T> getModelChildren(){
        List<T> ret = new ArrayList<>();
        for(TreeStoreItem<T> item : children){
            ret.add(item.getModel());
        }
        return ret;
    }
}
