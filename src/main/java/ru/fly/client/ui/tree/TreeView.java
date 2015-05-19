package ru.fly.client.ui.tree;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.F;
import ru.fly.client.event.SelectEvent;
import ru.fly.client.log.Log;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.tree.decor.TreeDecor;
import ru.fly.shared.FlyException;

import java.util.HashMap;
import java.util.Map;

/**
 * User: fil
 * Date: 22.04.15
 */
public class TreeView<T> extends Component implements SelectEvent.HasSelectHandler<T> {

    private Tree<T> tree;
    private Map<T, TreeRowItem<T>> renderedItems = new HashMap<>();
    private T selected;

    public TreeView() {
        super(DOM.createDiv());
        F.setEnableTextSelection(getElement(), false);
    }

    protected Tree<T> getTree() {
        return tree;
    }

    protected void setTree(Tree<T> tree) {
        this.tree = tree;
    }

    protected T getSelected(){
        return selected;
    }

    protected void select(T model){
        TreeRowItem<T> row;
        if(selected != null){
            row = getRowItem(selected);
            if(row == null){
                Log.warn("Cant found TreeRowItem: "+model);
            }else{
                row.setSelected(false);
            }
        }
        if(model != null) {
            if((selected != null && model.equals(selected)) || !getTree().getStore().contains(model)){
                if(selected != null) {
                    selected = null;
                    fireEvent(new SelectEvent<T>(null));
                }
            } else {
                selected = model;
                row = getRowItem(selected);
                if(row == null){
                    Log.warn("Cant found TreeRowItem: "+selected);
                }else{
                    row.setSelected(true);
                }
                fireEvent(new SelectEvent<>(selected));
            }
        }else{
            if(selected != null){
                selected = null;
                fireEvent(new SelectEvent<T>(null));
            }
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        redraw();
    }

    protected void redraw(){
        if(!isAttached())
            return;
        getElement().removeAll();
        renderedItems.clear();
        for(T model : tree.getStore().getChildren(null)){
            renderItem(this, model, 0);
        }
    }

    /** expand all children recursive */
    public void expandAll(T model){
        TreeRowItem<T> item = getRowItem(model);
        if(item == null){
            throw new FlyException("Cant found row item!");
        }else{
            item.expand();
            for (T child : tree.getStore().getChildren(model)) {
                expandAll(child);
            }
        }
    }

    private void renderItem(Widget parent, T model, int lvl){
        TreeRowItem<T> item = new TreeRowItem<T>(tree, model, lvl, false){
            @Override
            protected void onExpand(T model) {
                for (T child : tree.getStore().getChildren(model)) {
                    renderItem(this, child, getLvl() + 1);
                }
            }

            @Override
            protected void onCollapse(T model) {
                getContainerElement().removeAll();
            }

            @Override
            protected void onClick(T model) {
                if(getTree().isEnabled() && getTree().getGetter().isSelectable(model)) {
                    select(model);
                }
            }
        };
        if(selected != null && model.equals(selected)){
            item.setSelected(true);
        }
        F.render(parent, item);
        renderedItems.put(model, item);
        if(tree.getStore().isEmpty()) {
            for (T child : tree.getStore().getChildren(model)) {
                renderItem(item, child, lvl + 1);
            }
        }
    }

    @Override
    public HandlerRegistration addSelectHandler(SelectEvent.SelectHandler<T> h) {
        return addHandler(h, SelectEvent.<T>getType());
    }

    private TreeRowItem<T> getRowItem(T model){
        for(T m : renderedItems.keySet()){
            if(m.equals(model)){
                return renderedItems.get(m);
            }
        }
        return null;
    }
}
