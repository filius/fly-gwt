package ru.fly.client.ui.tree;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.log.Log;
import ru.fly.client.ui.Container;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.tree.decor.TreeDecor;

/**
 * User: fil
 * Date: 22.04.15
 */
public abstract class TreeRowItem<T> extends Container {

    private final Tree<T> tree;
    private final TreeDecor decor;
    private final T model;
    private final int lvl;
    private final FElement row;
    private final FElement childrenContainer;
    private final FElement arrowElement;
    private final boolean folder;
    private final boolean hasChildren;
    private boolean expanded;

    public TreeRowItem(Tree<T> tree, T model, int lvl, boolean expanded) {
        super(DOM.createDiv());
        this.tree = tree;
        this.decor = tree.getDecor();
        this.model = model;
        this.lvl = lvl;
        this.folder = tree.getGetter().isFolder(model);
        this.hasChildren = tree.getGetter().hasChildren(model);
        this.expanded = expanded;
        setStyleName(tree.getDecor().css().treeRowItem());
        row = DOM.createDiv().cast();
        row.setClassName(decor.css().treeRowItemHeader());
        if(folder){
            row.addClassName(decor.css().folder());
            if(!hasChildren){
                row.addClassName(decor.css().empty());
            }
        }
        row.setPaddingLeft(lvl * 16);

        FElement headerInner = DOM.createDiv().cast();
        row.appendChild(headerInner);
        headerInner.setClassName(decor.css().treeRowItemHeaderInner());

        arrowElement = DOM.createDiv().cast();
        arrowElement.setClassName(decor.css().arrow());
        headerInner.appendChild(arrowElement);

        FElement iconElement = DOM.createDiv().cast();
        iconElement.setClassName(decor.css().icon());
        headerInner.appendChild(iconElement);

        FElement textEl = DOM.createSpan().cast();
        try{
            textEl.setInnerHTML(tree.getGetter().get(model));
        }catch (NullPointerException e){
            textEl.setInnerText("");
            Log.warn("Unprocessed NULL model", new RuntimeException(e));
        }
        textEl.setClassName(decor.css().text());
        headerInner.appendChild(textEl);

        childrenContainer = DOM.createDiv().cast();
    }

    protected boolean isExpanded(){
        return expanded;
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

    protected abstract void onExpand(T model);

    protected abstract void onCollapse(T model);

    protected abstract void onClick(T model);

    protected int getLvl() {
        return lvl;
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
                        if(hasChildren) {
                            expandCollapse();
                        }
                        break;
                    case Event.ONCLICK:
                        onClick(model);
                        break;
                }
            }
        });
        DOM.sinkEvents(row, Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONCLICK | Event.ONDBLCLICK);
        if(hasChildren) {
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
            DOM.sinkEvents(arrowElement, Event.ONCLICK);
        }
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
