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

package ru.fly.client.ui.field.combobox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
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
import ru.fly.client.event.UpdateEvent;
import ru.fly.client.log.Log;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.field.Field;
import ru.fly.client.ui.field.TriggerController;
import ru.fly.client.ui.field.combobox.decor.ComboBoxDecor;
import ru.fly.client.ui.listview.ListView;
import ru.fly.client.util.LastPassExecutor;
import ru.fly.shared.Getter;
import ru.fly.shared.util.StringUtils;

import java.util.Collection;

/**
 * User: fil
 * Date: 05.08.13
 * Time: 21:45
 */
public class ComboBox<T> extends Field<T> {

    private final ComboBoxDecor decor;

    private TriggerController triggerController;
    private Loader<String, Collection<T>> loader;
    private ListStore<T> store = new ListStore<T>();
    private ListView<T> listView;
    private Getter<T> getter;
    private boolean hasEmpty;
    private String query = null;
    private LastPassExecutor<String> queryExec = new LastPassExecutor<String>() {
        @Override
        protected void exec(String param) {
            if (param != null && param.trim().isEmpty()) param = null;
            if (!StringUtils.equalsTrim(query, param)) {
                query = param;
                store.clear();
                triggerController.expand(true);
            }
        }
    };
    private String loaderUID = F.getUID();
    private boolean selectOnly = true;
    private boolean needRedraw = false;
    private boolean alwaysLoad = false;
    private FElement viewElement;
    private FElement triggerElement;

    public ComboBox(Getter<T> getter) {
        this(GWT.<ComboBoxDecor>create(ComboBoxDecor.class), getter);
    }

    public ComboBox(ComboBoxDecor decor, Getter<T> getter) {
        this.decor = decor;
        addStyleName(decor.css().comboBox());
        setGetter(getter);
        store.addUpdateHandler(new UpdateEvent.UpdateHandler() {
            @Override
            public void onUpdate() {
                needRedraw = true;
            }
        });
        viewElement = buildViewElement();
        triggerElement = buildTriggerElement();
        triggerController = new TriggerController(this, triggerElement) {
            @Override
            protected FElement getExpandedElement() {
                return getListView().getElement();
            }

            @Override
            public void onExpand() {
                ComboBox.this.onExpand();
            }

            @Override
            public void onCollapse() {
                ComboBox.this.onCollapse();
            }

            @Override
            public boolean isEnabled() {
                return ComboBox.this.isEnabled();
            }
        };
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getElement().appendChild(viewElement);
        getElement().appendChild(triggerElement);
        setSelectOnly(selectOnly);
    }

    public ComboBoxDecor getDecor() {
        return decor;
    }

    protected FElement buildTriggerElement() {
        FElement ret = DOM.createDiv().cast();
        ret.addClassName(decor.css().comboBoxTrigger());
        FElement trIcon = DOM.createDiv().cast();
        ret.appendChild(trIcon);
        trIcon.setClassName(decor.css().comboBoxTriggerIcon());
        return ret;
    }

    public void setViewWordWrap(boolean wrap) {
        if (wrap) {
            getListView().addStyleName(decor.css().lineWrap());
        } else {
            getListView().removeStyleName(decor.css().lineWrap());
        }
    }

    protected FElement buildViewElement() {
        FElement ret = DOM.createInputText().cast();
        ret.addClassName(decor.css().comboBoxView());
        return ret;
    }

    protected FElement getViewElement() {
        return viewElement;
    }

    public void setGetter(Getter<T> getter) {
        this.getter = getter;
    }

    public void setSelectOnly(boolean selectOnly) {
        this.selectOnly = selectOnly;
        if (viewElement != null) {
            ((InputElement) viewElement.cast()).setReadOnly(selectOnly);
        }
    }

    public void setAlwaysLoad(boolean val) {
        alwaysLoad = val;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        DOM.setEventListener(viewElement, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if (event.getTypeInt() == Event.ONKEYUP && !selectOnly) {
                    queryExec.pass(((InputElement) viewElement.cast()).getValue());
                }
            }
        });
        DOM.sinkEvents(viewElement, Event.ONKEYUP);
    }

    /**
     * очищает поле и удаляет все записи из его хранилища
     */
    public void invalidate() {
        clear();
        store.clear();
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
        if (viewElement != null) {
            String str = "";
            // ловим тут случай когда в геттере не обработан NULL
            try {
                str = getter.get(value);
            } catch (Exception e) {
                Log.warn("Не обработан NULL", e);
            }
            ((InputElement) viewElement.cast()).setValue(str);
        }
        getListView().select(value, false);
    }

    public void setHasEmpty(boolean hasEmpty) {
        this.hasEmpty = hasEmpty;
        getListView().setHasEmpty(hasEmpty);
    }

    @SuppressWarnings("unchecked")
    public void setLoader(Loader<String, ? extends Collection<T>> loader) {
        this.loader = (Loader<String, Collection<T>>) loader;
    }

    private void updatePositionAndSize() {
        if (!triggerController.isExpanded())
            return;
        int top = getElement().getAbsoluteTop() + getHeight();
        int left = getElement().getAbsoluteLeft();
        int wndViewWidth = Window.getClientWidth() + Window.getScrollLeft() - 20;
        int wndViewHeight = Window.getClientHeight() + Window.getScrollTop() - 20;

        if (getListView().getWidth() < getWidth()) {
            getListView().setWidth(getWidth());
        } else if (getListView().getWidth() > (wndViewWidth - getElement().getAbsoluteLeft())) {
            getListView().setWidth(wndViewWidth - getElement().getAbsoluteLeft());
        }
        getListView().clearHeight();
        int height = getListView().getHeight();
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
        getListView().setHeight(height);
        getListView().setPosition(left, top);
        getListView().focus();
    }

    protected ListView<T> getListView() {
        if (listView == null) {
            listView = new ListView<>(store, getter);
            listView.addSelectHandler(new SelectEvent.SelectHandler<T>() {
                @Override
                public void onSelect(T object) {
                    triggerController.collapse();
                    setValue(object);
                    fireEvent(new SelectEvent<T>(object));
                }
            });
            listView.setHasEmpty(hasEmpty);
            listView.addStyleName(decor.css().listView());
            listView.removeStoreListener();
        }
        return listView;
    }

    protected void onExpand() {
        getListView().setPosition(-10000, -10000);
        RootPanel.get().add(getListView());

        if (alwaysLoad || needRedraw || store.isEmpty() || query != null) {
            getListView().setLoading();
            load();
        }
        updatePositionAndSize();
    }

    protected void onCollapse() {
        getListView().removeFromParent();
        if (query != null && !query.isEmpty()) {
            query = null;
            store.clear();
        }
    }

    private void load() {
        if (loader != null) {
            loader.load(query, new LastRespAsyncCallback<Collection<T>>(loaderUID) {
                @Override
                public void onSuccessLast(Collection<T> result) {
                    store.fill(result);
                    redrawListView();
                }

                @Override
                public void onFailureLast(Throwable caught) {
                    Log.error(caught.getMessage(), caught);
                    store.clear();
                    redrawListView();
                }
            });
        } else {
            redrawListView();
        }
    }

    public String getQuery() {
        return query;
    }

    public void redrawListView() {
        if (isAttached()) {
            getListView().forceRedraw();
            getListView().getStyle().clearWidth();
        }
        updatePositionAndSize();
        needRedraw = false;
    }

    public ListStore<T> getStore() {
        return store;
    }

    public HandlerRegistration addSelectHandler(SelectEvent.SelectHandler<T> handler) {
        return addHandler(handler, SelectEvent.<T>getType());
    }

}
