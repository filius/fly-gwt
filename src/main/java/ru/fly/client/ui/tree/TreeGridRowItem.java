package ru.fly.client.ui.tree;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.F;
import ru.fly.client.log.Log;
import ru.fly.client.ui.Container;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.grid.ColumnConfig;
import ru.fly.client.ui.tree.decor.TreeDecor;

import java.util.List;

/**
 * User: fil
 * Date: 03.05.15
 */
public abstract class TreeGridRowItem<T> extends Container {

    private final TreeDecor decor;
    private final T model;
    private final int lvl;
    private final FElement row;
    private final FElement childrenContainer;
    private final FElement arrowElement;
    private final boolean folder;
    private boolean expanded;

    public TreeGridRowItem(TreeGrid<T> treeGrid, T model, int lvl, boolean expanded){
        super(DOM.createDiv());
        decor = treeGrid.getDecor();
        this.model = model;
        this.lvl = lvl;
        this.folder = treeGrid.getGetter().isFolder(model);
        this.expanded = expanded;
        row = DOM.createDiv().cast();
        row.setClassName(decor.css().treeRowItemHeader());
        row.addClassName(decor.css().treeGridRowItemHeader());
        if(folder){
            row.addClassName(decor.css().folder());
        }

        arrowElement = DOM.createDiv().cast();
        arrowElement.setClassName(decor.css().arrow());
        FElement iconElement = DOM.createDiv().cast();
        iconElement.setClassName(decor.css().icon());

        List<ColumnConfig<T>> cols = treeGrid.getHeader().getColumnConfigs();
        for(int i=0; i<cols.size(); i++){
            ColumnConfig<T> c = cols.get(i);
            FElement col = DOM.createDiv().cast();
            col.setClassName(decor.css().treeGridCol());
            col.setWidth(c.getWidth() == -1 ? c.getCalculatedWidth() : c.getWidth());
            col.setLeft(c.getLeft());
            row.appendChild(col);
            if (i == 0) {
                col.setPaddingLeft(lvl * 16);
                col.appendChild(arrowElement);
                col.appendChild(iconElement);
            }
            if (c.getRenderer() != null) {
                Widget w = c.getRenderer().render(model);
                if (w != null) {
                    col.appendChild(w.getElement());
                    F.attach(w);
                }
            } else {
                FElement textEl = DOM.createSpan().cast();
                try{
                    textEl.setInnerHTML(c.getGetter().get(model));
                }catch (NullPointerException e){
                    textEl.setInnerText("");
                    Log.warn("Unprocessed NULL model", new RuntimeException(e));
                }
                col.appendChild(textEl);
            }
        }
        childrenContainer = DOM.createDiv().cast();
    }

    protected abstract List<T> onExpand(T model);

    protected abstract void onCollapse(T model);

    protected abstract void onClick(T model);

    protected int getLvl() {
        return lvl;
    }

    protected void expand(){
        if(expanded){
            return;
        }
        row.addClassName(decor.css().expanded());
        onExpand(model);
        expanded = true;
    }

    protected void collapse(){
        if(!expanded){
            return;
        }
        expanded = false;
        onCollapse(model);
        row.removeClassName(decor.css().expanded());
    }

    protected void setSelected(boolean val){
        if(val) {
            row.addClassName(decor.css().selected());
        }else{
            row.removeClassName(decor.css().selected());
        }
    }

    @Override
    public FElement getContainerElement() {
        return childrenContainer;
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getElement().appendChild(row);
        getElement().appendChild(childrenContainer);
        addEventListeners();
    }

    private void addEventListeners(){
        DOM.setEventListener(row, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                switch (event.getTypeInt()) {
                    case Event.ONMOUSEOVER:
                        row.addClassName(decor.css().over());
                        break;
                    case Event.ONMOUSEOUT:
                        row.removeClassName(decor.css().over());
                        break;
                    case Event.ONDBLCLICK:
                        expandCollapse();
                        break;
                    case Event.ONCLICK:
                        onClick(model);
                        break;
                }
            }
        });
        DOM.sinkEvents(row, Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONCLICK | Event.ONDBLCLICK);
        DOM.setEventListener(arrowElement, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                switch (event.getTypeInt()) {
                    case Event.ONCLICK:
                        expandCollapse();
                        event.stopPropagation();
                        break;
                }
            }
        });
        DOM.sinkEvents(arrowElement,Event.ONCLICK);
    }

    private void expandCollapse(){
        if(folder) {
            if (expanded) {
                collapse();
            } else {
                expand();
            }
        }
    }

}
