package ru.fly.client.ui.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.ui.Container;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.tree.decor.TreeDecor;

/**
 * User: fil
 * Date: 22.04.15
 */
public abstract class TreeRowItem<T> extends Container {

    private final TreeDecor decor = GWT.create(TreeDecor.class);

    private final T model;
    private final int lvl;
    private final FElement header;
    private final FElement childrenContainer;
    private final FElement arrowElement;
    private final boolean folder;
    private boolean expanded;

    public TreeRowItem(T model, TreeGetter<T> getter, int lvl, boolean expanded) {
        super(DOM.createDiv());
        this.model = model;
        this.lvl = lvl;
        this.folder = getter.isFolder(model);
        this.expanded = expanded;
        setStyleName(decor.css().treeRowItem());
        header = DOM.createDiv().cast();
        header.setClassName(decor.css().treeRowItemHeader());
        if(folder){
            header.addClassName(decor.css().folder());
        }
        header.setPaddingLeft(lvl * 16);

        arrowElement = DOM.createDiv().cast();
        arrowElement.setClassName(decor.css().arrow());
        header.appendChild(arrowElement);

        FElement iconElement = DOM.createDiv().cast();
        iconElement.setClassName(decor.css().icon());
        header.appendChild(iconElement);

        FElement text = DOM.createSpan().cast();
        text.setInnerText(getter.get(model));
        text.setClassName(decor.css().text());
        header.appendChild(text);

        childrenContainer = DOM.createDiv().cast();
    }

    protected abstract void onExpand(T model);

    protected abstract void onCollapse(T model);

    protected abstract void onClick(T model);

    protected int getLvl() {
        return lvl;
    }

    protected void setSelected(boolean val){
        if(val) {
            header.addClassName(decor.css().selected());
        }else{
            header.removeClassName(decor.css().selected());
        }
    }

    @Override
    public FElement getContainerElement() {
        return childrenContainer;
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getElement().appendChild(header);
        getElement().appendChild(childrenContainer);
        addEventListeners();
    }

    private void addEventListeners(){
        DOM.setEventListener(header, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                switch (event.getTypeInt()) {
                    case Event.ONMOUSEOVER:
                        header.addClassName(decor.css().over());
                        break;
                    case Event.ONMOUSEOUT:
                        header.removeClassName(decor.css().over());
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
        DOM.sinkEvents(header, Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONCLICK | Event.ONDBLCLICK);
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
                onCollapse(model);
                header.removeClassName(decor.css().expanded());
            } else {
                onExpand(model);
                header.addClassName(decor.css().expanded());
            }
            expanded = !expanded;
        }
    }

}
