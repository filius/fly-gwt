package ru.fly.client.ui.tree;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.F;
import ru.fly.client.event.SelectEvent;
import ru.fly.client.log.Log;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.FElement;
import ru.fly.shared.FlyException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: fil
 * Date: 03.05.15
 */
public class TreeGridView<T> extends Component implements SelectEvent.HasSelectHandler<T> {

    private TreeGrid<T> tree;
    private Map<T, TreeGridRowItem<T>> renderedItems = new HashMap<>();
    private T selected;
    protected FElement inner;

    public TreeGridView() {
        super(DOM.createDiv());
        F.setEnableTextSelection(getElement(), false);
    }

    protected TreeGrid<T> getTree() {
        return tree;
    }

    protected void setTree(TreeGrid<T> tree) {
        this.tree = tree;
        setStyleName(tree.getDecor().css().treeGridView());
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getElement().setTabIndex(F.getNextTabIdx());
        addViewListeners();

        inner = DOM.createDiv().cast();
        getElement().appendChild(inner);
    }

    protected void redraw(){
        if(!isAttached())
            return;
        inner.removeAll();
        getElement().removeAll();
        renderedItems.clear();
        if(getTree().getHeader().getColumnConfigs().size() < 1){
            throw new FlyException("Column size must be greater than 0");
        }else {
            for (T model : tree.getStore().getChildren(null)) {
                renderItem(this, model, 0);
            }
        }
    }

    private void renderItem(Widget parent, T model, int lvl){
        TreeGridRowItem<T> item = new TreeGridRowItem<T>(tree, model, lvl, false){
            @Override
            protected List<T> onExpand(T model) {
                List<T> children = tree.getStore().getChildren(model);
                for (T child : children) {
                    renderItem(this, child, getLvl() + 1);
                }
                return children;
            }

            @Override
            protected void onCollapse(T model) {
                getContainerElement().removeAll();
            }

            @Override
            protected void onClick(T model) {
                if(getTree().getGetter().isSelectable(model)) {
                    select(model);
                }
            }
        };
        F.render(parent, item);
        renderedItems.put(model, item);
        if(tree.getStore().isEmpty()) {
            for (T child : tree.getStore().getChildren(model)) {
                renderItem(item, child, lvl + 1);
            }
        }
    }

    /** expand children from one row */
    public void expandRow(T model){
        TreeGridRowItem<T> item = getRowItem(model);
        if(item == null){
            throw new FlyException("Cant found row item!");
        }else{
            item.expand();
        }
    }

    /** expand all children recursive */
    public void expandAll(T model){
        TreeGridRowItem<T> item = getRowItem(model);
        if(item == null){
            throw new FlyException("Cant found row item!");
        }else{
            item.expand();
            for (T child : tree.getStore().getChildren(model)) {
                expandAll(child);
            }
        }
    }

    protected T getSelected(){
        if(selected != null && tree.getStore().contains(selected)) {
            return selected;
        }else{
            return null;
        }
    }

    protected void select(T model){
        TreeGridRowItem<T> row;
        if(selected != null){
            row = getRowItem(selected);
            if(row == null){
                Log.warn("Cant found TreeRowItem: " + model);
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

    private void addViewListeners(){
        final EventListener oldLnr = DOM.getEventListener(getElement());
//        DOM.setEventListener(getElement(), new EventListener() {
//            @Override
//            public void onBrowserEvent(Event event) {
//                if(grid == null || !grid.isEnabled())
//                    return;
//                if (oldLnr != null)
//                    oldLnr.onBrowserEvent(event);
//                switch (event.getTypeInt()) {
//                    case Event.ONCLICK:
//                        getElement().focus();
//                        break;
//                    case Event.ONKEYDOWN:
//                        int code = event.getKeyCode();
//                        if (code == 38) {
//                            selectPrev();
//                            event.preventDefault();
//                        } else if (code == 40) {
//                            selectNext();
//                            event.preventDefault();
//                        }
//                        break;
//                }
//            }
//        });
        DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.ONKEYDOWN | Event.ONCLICK);
    }

    @Override
    public HandlerRegistration addSelectHandler(SelectEvent.SelectHandler<T> h) {
        return null;
    }

    private TreeGridRowItem<T> getRowItem(T model){
        for(T m : renderedItems.keySet()){
            if(m.equals(model)){
                return renderedItems.get(m);
            }
        }
        return null;
    }
}
