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

package ru.fly.client.ui.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;
import ru.fly.client.F;
import ru.fly.client.LastRespAsyncCallback;
import ru.fly.client.ListStore;
import ru.fly.client.Loader;
import ru.fly.client.event.SelectEvent;
import ru.fly.client.log.Log;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.field.combobox.decor.ComboBoxDecor;
import ru.fly.client.ui.listview.ListView;
import ru.fly.client.util.LastPassExecutor;
import ru.fly.shared.Getter;
import ru.fly.shared.util.StringUtils;

import java.util.Collection;

/**
 * @author fil
 */
public class VariantTextField<T> extends TextField implements SelectEvent.HasSelectHandler<T> {

    private final ComboBoxDecor cbDecor = GWT.create(ComboBoxDecor.class);

    private Loader<String, Collection<T>> loader;
    private ListStore<T> store = new ListStore<T>();
    protected Expander expander;
    private ListView<T> listView;
    private Getter<T> getter;
    private Getter<T> expandGetter;
    private String query = null;
    private LastPassExecutor<String> queryExec = new LastPassExecutor<String>() {
        @Override
        protected void exec(String param) {
            if (param != null && param.trim().isEmpty()) param = null;
            if (!StringUtils.equalsTrim(query, param)) {
                query = param;
                store.clear();
                expander.expand(true);
            }
        }
    };
    private String loaderUID = F.getUID();

    public VariantTextField(Getter<T> getter, Getter<T> expandGetter) {
        super();
        this.getter = getter;
        this.expandGetter = expandGetter;
    }

    protected ListView<T> getListView() {
        if (listView == null) {
            listView = new ListView<T>(expandGetter);
            listView.setFireWalkingSelect(false);
            listView.addSelectHandler(new SelectEvent.SelectHandler<T>() {
                @Override
                public void onSelect(T object) {
                    setValue(getter.get(object));
                    expander.collapse();
                    VariantTextField.this.fireEvent(new SelectEvent<T>(object));
                }
            });
            listView.addStyleName(cbDecor.css().listView());
        }
        return listView;
    }

    protected boolean beforeQuery(String query) {
        return true;
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        expander = new Expander(getElement()) {
            @Override
            protected FElement getExpandedElement() {
                return null;
            }

            @Override
            public void onExpand() {
                getListView().setPosition(-10000, -10000);
                RootPanel.get().add(getListView());

                if (store.isEmpty() || query != null) {
                    getListView().setLoading();
                    load();
                }
                updatePositionAndSize();
            }

            @Override
            public void onCollapse() {
                getListView().removeFromParent();
                if (query != null && !query.isEmpty()) {
                    query = null;
                    store.clear();
                }
            }

            @Override
            public boolean isEnabled() {
                return VariantTextField.this.isEnabled();
            }
        };
    }

    @SuppressWarnings("unchecked")
    public void setLoader(Loader<String, ? extends Collection<T>> loader) {
        this.loader = (Loader<String, Collection<T>>) loader;
    }

    private void load() {
        if (loader != null) {
            loader.load(query, new LastRespAsyncCallback<Collection<T>>(loaderUID) {
                @Override
                public void onSuccessLast(Collection<T> result) {
                    store.clear();
                    if (result == null || result.size() < 1 || !isFocused()) {
                        expander.collapse();
                    } else {
                        store.addAll(result);
                        rebuildListView();
                    }
                }

                @Override
                public void onFailureLast(Throwable caught) {
                    Log.error("RPC error", caught);
                    store.clear();
                    rebuildListView();
                }
            });
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        final EventListener oldLnr = DOM.getEventListener(getInputElement());
        DOM.setEventListener(getInputElement(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if (oldLnr != null) {
                    oldLnr.onBrowserEvent(event);
                }
                if (event.getTypeInt() == Event.ONKEYUP) {
                    queryExec.pass(((InputElement) getInputElement().cast()).getValue());
                    switch (event.getKeyCode()) {
                        case KeyCodes.KEY_DOWN:
                            getListView().selectNext();
                            break;
                        case KeyCodes.KEY_UP:
                            getListView().selectPrev();
                            break;
                        case KeyCodes.KEY_ENTER:
                            getListView().select(getListView().getSelected());
                            break;
                    }
                }
            }
        });
        DOM.sinkEvents(getInputElement(), DOM.getEventsSunk(getInputElement()) | Event.ONKEYUP);
    }

    public void rebuildListView() {
        if (isAttached()) {
            getListView().fillData(store.getList(), false);
        }
        updatePositionAndSize();
    }

    private void updatePositionAndSize() {
        if (!expander.isExpanded()) {
            return;
        }
        ListView<T> listView = getListView();
        int top = getElement().getAbsoluteTop() + getHeight();
        int left = getElement().getAbsoluteLeft();
        int wndViewWidth = Window.getClientWidth() + Window.getScrollLeft() - 20;
        int wndViewHeight = Window.getClientHeight() + Window.getScrollTop() - 20;
        // ListView width calculation
        if (listView.getWidth() < getWidth()) {
            listView.setWidth(getWidth());
        } else if (listView.getWidth() > (wndViewWidth - getElement().getAbsoluteLeft())) {
            listView.setWidth(wndViewWidth - getElement().getAbsoluteLeft() + 2);
        }

        // now we may recalculate position and height
        listView.clearHeight();
        int height = listView.getHeight();
        if (top < wndViewHeight / 2) {
            if (height > wndViewHeight - top) {
                height = wndViewHeight - top;
            }
        } else {
            if (height > getElement().getAbsoluteTop() - 20) {
                height = getElement().getAbsoluteTop() - 20;
                top = 20;
            } else {
                top = getElement().getAbsoluteTop() - height;
            }
        }
        listView.setPosition(left, top);
        // change height
        listView.setHeight(height);
    }

    public HandlerRegistration addSelectHandler(SelectEvent.SelectHandler<T> handler) {
        return getListView().addSelectHandler(handler);
    }
}
