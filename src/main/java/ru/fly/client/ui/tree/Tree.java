package ru.fly.client.ui.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.F;
import ru.fly.client.ListStore;
import ru.fly.client.TreeStore;
import ru.fly.client.event.SelectEvent;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.listview.ListViewDecor;
import ru.fly.client.ui.tree.decor.TreeDecor;
import ru.fly.shared.Getter;

/**
 * User: fil
 * Date: 05.03.15
 */
public class Tree<T> extends Component {

    protected final TreeDecor decor = GWT.create(TreeDecor.class);

    private final String EMPTY = "Список пуст";

    private Getter<T> getter;
    protected TreeStore<T> store = new TreeStore<T>();
    private T selected;
    private boolean rendered = false;
    private boolean hasEmpty;

    public Tree(Getter<T> getter) {
        super(DOM.createDiv());
        this.getter = getter;
        addStyleName(decor.css().tree());
    }

    public void select(T model){
        select(model, true);
    }

    public void select(T model, boolean fireEvent){
//        if(selected != null){
//            FElement item = getItemElement(selected);
//            if(item != null){
//                item.removeClassName(decor.css().selected());
//            }
//        }
//        selected = model;
//        FElement item = getItemElement(selected);
//        if(item != null){
//            item.addClassName(decor.css().selected());
//        }
        if(fireEvent)
            fireEvent(new SelectEvent<T>(model));
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        F.setEnableTextSelection(getElement(), false);
        if(!rendered){
            redraw();
        }
    }

    protected void redraw(){
        redraw(false);
    }

    protected void redraw(boolean force){
        if((!isAttached() || rendered) && !force)
            return;
        getElement().removeAll();
        if(store.isEmpty()){
            getElement().setInnerHTML(EMPTY);
        }else{
            getElement().setInnerHTML("");
            if(hasEmpty){
                renderItem(null);
            }
            for(T item : store.getChildren(null)){
                renderItem(item);
            }
        }
        rendered = true;
    }

    protected void renderItem(final T model){
//        FElement el = DOM.createDiv().cast();
//        el.setClassName(decor.css().listviewItem());
//        if(selected != null && selected.equals(model)){
//            el.addClassName(decor.css().selected());
//        }
//        Object display = getter.get(model);
//        el.setInnerHTML((display == null)?"":display.toString());
//        getElement().appendChild(el);
//        DOM.setEventListener(el, new EventListener() {
//            @Override
//            public void onBrowserEvent(Event event) {
//                if(event.getTypeInt() == Event.ONCLICK){
//                    if(isEnabled()){
//                        select(model,true);
//                    }
//                }
//            }
//        });
//        DOM.sinkEvents(el, Event.ONCLICK);
//        el.listenOver(decor.css().over());
    }

//    private FElement getItemElement(T model){
//        int pos = store.getList().indexOf(model);
//        if(pos == -1)
//            return null;
//        if(hasEmpty)
//            pos++;
//        if(getElement().getChildCount() <= pos)
//            return null;
//        return getElement().getChild(pos).cast();
//    }

}
