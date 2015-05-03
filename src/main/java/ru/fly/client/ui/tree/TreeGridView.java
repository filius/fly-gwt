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

    protected void select(T model){
        TreeGridRowItem<T> row;
        if(selected != null){
            row = renderedItems.get(selected);
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
                row = renderedItems.get(selected);
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
}
