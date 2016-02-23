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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.F;
import ru.fly.client.ListStore;
import ru.fly.client.event.SelectEvent;
import ru.fly.client.event.UpdateEvent;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.listview.decor.ListViewDecor;
import ru.fly.shared.Getter;

import java.util.List;

/**
 * User: fil
 * Date: 05.08.13
 * Time: 21:49
 */
public class ListView<T> extends Component {

    protected final ListViewDecor decor;

    private final String LOAD_PROCESS = "Загрузка...";
    private final String EMPTY = "Список пуст";
    private ListStore<T> store;
    private Getter<T> getter;
    private boolean hasEmpty;
    private boolean rendered = false;
    private T selected;
    private HandlerRegistration storeListener;

    public ListView(ListViewDecor decor, ListStore<T> listStore, Getter<T> getter) {
        super(DOM.createDiv());
        this.decor = decor;
        this.store = listStore;
        this.getter = getter;
        addStyleName(decor.css().listView());
        storeListener = getStore().addUpdateHandler(new UpdateEvent.UpdateHandler() {
            @Override
            public void onUpdate() {
                redraw(true);
            }
        });
    }

    public ListView(ListStore<T> listStore, Getter<T> getter) {
        this(GWT.<ListViewDecor>create(ListViewDecor.class), listStore, getter);
    }

    public ListView(Getter<T> getter) {
        this(new ListStore<T>(), getter);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        F.setEnableTextSelection(getElement(), false);
        if (!rendered) {
            redraw();
        }
        addEventListeners();
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getElement().setTabIndex(F.getNextTabIdx());
    }

    private void addEventListeners() {
        final EventListener oldLnr = DOM.getEventListener(getElement());
        DOM.setEventListener(getElement(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if (oldLnr != null) {
                    oldLnr.onBrowserEvent(event);
                }
                if (event.getTypeInt() == Event.ONKEYDOWN) {
                    switch (event.getKeyCode()) {
                        case KeyCodes.KEY_UP:
                            selectPrev();
                            event.preventDefault();
                            break;
                        case KeyCodes.KEY_DOWN:
                            selectNext();
                            event.preventDefault();
                            break;
                        case KeyCodes.KEY_ENTER:
                            select(getSelected(), true);
                    }
                }
            }
        });
        DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.ONKEYDOWN);
    }

    public void focus() {
        getElement().focus();
    }

    public Getter<T> getGetter() {
        return getter;
    }

    public T getSelected() {
        return selected;
    }

    public ListStore<T> getStore() {
        return store;
    }

    public void removeStoreListener() {
        storeListener.removeHandler();
    }

    public void setLoading() {
        setHeight(40);
        getElement().setInnerHTML(LOAD_PROCESS);
    }

    public void fillData(List<T> list, boolean hasEmpty) {
        rendered = false;
        this.hasEmpty = hasEmpty;
        this.store.fill(list);
        redraw();
    }

    public int getMaxHeight() {
        if (store.isEmpty()) {
            return 40;
        }
        return ((hasEmpty) ? store.getList().size() + 1 : store.getList().size()) *
                (decor.css().pListViewItemHeight()) + decor.css().pListViewPadding() * 2 + 2;
    }

    public HandlerRegistration addSelectHandler(SelectEvent.SelectHandler<T> handler) {
        return addHandler(handler, SelectEvent.<T>getType());
    }

    public void select(T model) {
        select(model, true);
    }

    public void select(T model, boolean fireEvent) {
        if (selected != null) {
            FElement item = getItemElement(selected);
            if (item != null) {
                item.removeClassName(decor.css().selected());
            }
        }
        selected = model;
        FElement item = getItemElement(selected);
        if (item != null) {
            item.addClassName(decor.css().selected());
        }
        if (fireEvent) {
            fireEvent(new SelectEvent<T>(model));
        }
    }

    public void selectNext() {
        List<T> l = getStore().getList();
        T selected = getSelected();
        int idx = selected == null ? 0 : (l.indexOf(selected) + 1);
        if (idx >= l.size()) idx = l.size() - 1;
        T now = l.get(idx);
        if (now.equals(selected)) {
            return;
        }
        select(now, false);
    }

    public void selectPrev() {
        List<T> l = getStore().getList();
        T selected = getSelected();
        int idx = selected == null ? (l.size() - 1) : (l.indexOf(selected) - 1);
        if (idx < 0) idx = 0;
        T now = l.get(idx);
        if (now.equals(selected)) {
            return;
        }
        select(now, false);
    }

    public void clearSelection() {
        select(null, true);
    }

    public void forceRedraw() {
        redraw(true);
    }

    public void setHasEmpty(boolean hasEmpty) {
        boolean needRedraw = this.hasEmpty != hasEmpty;
        this.hasEmpty = hasEmpty;
        if (needRedraw) {
            redraw(true);
        }
    }

    protected void redraw() {
        redraw(false);
    }

    protected void redraw(boolean force) {
        if ((!isAttached() || rendered) && !force)
            return;
        getElement().removeAll();
        if (store.isEmpty()) {
            getElement().setInnerHTML(EMPTY);
        } else {
            getElement().setInnerHTML("");
            if (hasEmpty) {
                renderItem(null);
            }
            for (T item : store.getList()) {
                renderItem(item);
            }
        }
        rendered = true;
    }

    protected void renderItem(final T model) {
        FElement el = DOM.createDiv().cast();
        el.setClassName(decor.css().listViewItem());
        if (selected != null && selected.equals(model)) {
            el.addClassName(decor.css().selected());
        }
        Object display = getter.get(model);
        el.setInnerHTML((display == null) ? "" : display.toString());
        getElement().appendChild(el);
        DOM.setEventListener(el, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if (event.getTypeInt() == Event.ONCLICK) {
                    if (isEnabled()) {
                        select(model, true);
                    }
                }
            }
        });
        DOM.sinkEvents(el, Event.ONCLICK);
        el.listenOver(decor.css().over());
    }

    private FElement getItemElement(T model) {
        int pos = store.getList().indexOf(model);
        if (pos == -1)
            return null;
        if (hasEmpty)
            pos++;
        if (getElement().getChildCount() <= pos)
            return null;
        return getElement().getChild(pos).cast();
    }
}
