/*
 * Copyright 2015 Valeriy Filatov.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package ru.fly.client.ui.listview;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.F;
import ru.fly.client.ListStore;
import ru.fly.client.event.UpdateHandler;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.FElement;
import ru.fly.client.event.SelectEvent;
import ru.fly.shared.Getter;

import java.util.List;

/**
 * User: fil
 * Date: 05.08.13
 * Time: 21:49
 */
public class ListView<T> extends Component {

    protected final ListViewDecor decor = GWT.create(ListViewDecor.class);

    private final String LOAD_PROCESS = "Загрузка...";
    private final String EMPTY = "Список пуст";
    private boolean hasEmpty;
    private boolean rendered = false;

    private Getter<T> getter;
    protected ListStore<T> store = new ListStore<T>();
    private T selected;

    public ListView(Getter<T> getter){
        super(DOM.createDiv());
        this.getter = getter;
        addStyleName(decor.css().listview());
    }

    public Getter<T> getGetter(){
        return getter;
    }

    public void listenStoreUpdate(){
        store.addUpdateHandler(new UpdateHandler() {
            @Override
            public void onUpdate() {
                redraw(true);
            }
        });
    }

    public T getSelected(){
        return selected;
    }

    public ListStore<T> getStore(){
        return store;
    }

    public void setLoading(){
        setHeight(40);
        getElement().setInnerHTML(LOAD_PROCESS);
    }

    public void fillData(List<T> list, boolean hasEmpty){
        rendered = false;
        this.hasEmpty = hasEmpty;
        this.store.fill(list);
        redraw();
    }

    public int getMaxHeight(){
        if(store.isEmpty())
            return 40;
        return ((hasEmpty)?store.getList().size()+1:store.getList().size()) *
                (decor.css().pLlistViewItemHeight()) + decor.css().pLlistViewPadding() * 2 + 2;
    }

    public HandlerRegistration addSelectHandler(SelectEvent.SelectHandler<T> handler) {
        return addHandler(handler, SelectEvent.<T>getType());
    }

    public void select(T model){
        select(model, true);
    }

    public void select(T model, boolean fireEvent){
        if(selected != null){
            FElement item = getItemElement(selected);
            if(item != null){
                item.removeClassName(decor.css().selected());
            }
        }
        selected = model;
        FElement item = getItemElement(selected);
        if(item != null){
            item.addClassName(decor.css().selected());
        }
        if(fireEvent)
            fireEvent(new SelectEvent<T>(model));
    }

    public void clearSelection(){
        select(null, true);
    }

    public void forceRedraw(){
        redraw(true);
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
            for(T item : store.getList()){
                renderItem(item);
            }
        }
        rendered = true;
    }

    protected void renderItem(final T model){
        FElement el = DOM.createDiv().cast();
        el.setClassName(decor.css().listviewItem());
        if(selected != null && selected.equals(model)){
            el.addClassName(decor.css().selected());
        }
        Object display = getter.get(model);
        el.setInnerHTML((display == null)?"":display.toString());
        getElement().appendChild(el);
        DOM.setEventListener(el, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if(event.getTypeInt() == Event.ONCLICK){
                    if(isEnabled()){
                        select(model,true);
                    }
                }
            }
        });
        DOM.sinkEvents(el, Event.ONCLICK);
        el.listenOver(decor.css().over());
    }

    private FElement getItemElement(T model){
        int pos = store.getList().indexOf(model);
        if(pos == -1)
            return null;
        if(hasEmpty)
            pos++;
        if(getElement().getChildCount() <= pos)
            return null;
        return getElement().getChild(pos).cast();
    }
}
