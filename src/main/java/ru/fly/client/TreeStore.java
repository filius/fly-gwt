package ru.fly.client;

import ru.fly.client.event.UpdateEvent;
import ru.fly.client.ui.EventBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: fil
 * Date: 21.04.15
 */
public class TreeStore<T> extends EventBase {

    private TreeStoreItem<T> root = new TreeStoreItem<>(null);
    private Map<T, TreeStoreItem<T>> links = new HashMap<>();

    public boolean isEmpty(){
        return root.getChildren().isEmpty();
    }

    public void clear(){
        root = new TreeStoreItem<>(null);
        links.clear();
        fireEvent(new UpdateEvent());
    }

    public void add(T parent, T model){
        if(model == null){
            throw new IllegalArgumentException("Model cant be NULL!");
        }
        TreeStoreItem<T> item = new TreeStoreItem<>(model);
        findItem(parent).getChildren().add(item);
        links.put(model, item);
        fireEvent(new UpdateEvent());
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
        fireEvent(new UpdateEvent());
    }

    public boolean contains(T model){
        for(T m : links.keySet()){
            if(m.equals(model)){
                return true;
            }
        }
        return false;
    }

    public boolean isExpanded(T model){
        return findItem(model).isExpanded();
    }

    public List<T> getChildren(T parent){
        return findItem(parent).getModelChildren();
    }

    public List<TreeStoreItem<T>> getItemChildren(T parent){
        return findItem(parent).getChildren();
    }

    public void addUpdateHandler(UpdateEvent.UpdateHandler lnr){
        addHandler(lnr, UpdateEvent.getType());
    }

    // ------------------ privates -------------------

    protected TreeStoreItem<T> findItem(T model){
        if(model == null){
            return root;
        }else{
            for(T m : links.keySet()){
                if(m.equals(model)){
                    return links.get(m);
                }
            }
            return null;
        }
    }

}
