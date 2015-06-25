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
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.RootPanel;
import ru.fly.client.F;
import ru.fly.client.LastRespAsyncCallback;
import ru.fly.client.event.SelectEvent;
import ru.fly.client.event.UpdateEvent;
import ru.fly.client.log.Log;
import ru.fly.client.ui.FElement;
import ru.fly.client.ListStore;
import ru.fly.client.ui.field.combobox.decor.ComboBoxDecor;
import ru.fly.client.ui.listview.ListView;
import ru.fly.client.Loader;
import ru.fly.client.ui.field.TriggerField;
import ru.fly.shared.Getter;
import ru.fly.client.util.LastPassExecutor;
import ru.fly.shared.util.StringUtils;

import java.util.List;

/**
 * User: fil
 * Date: 05.08.13
 * Time: 21:45
 */
public class ComboBox<T> extends TriggerField<T> {

    private final ComboBoxDecor decor;

    private Loader<String, List<T>> loader;
    private ListStore<T> store = new ListStore<T>();
    private ListView<T> listView;
    private Getter<T> getter;
    private boolean hasEmpty;
    private String query = null;
    private LastPassExecutor<String> queryExec = new LastPassExecutor<String>() {
        @Override
        protected void exec(String param) {
            if(param != null && param.trim().isEmpty()) param = null;
            if(!StringUtils.equalsTrim(query, param)){
                query = param;
                store.clear();
                expander.expand(true);
            }
        }
    };
    private String loaderUID = F.getUID();
    private boolean selectOnly = true;
    private boolean needRedraw = false;
    private boolean alwaysLoad = false;

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
    }

    public ComboBoxDecor getDecor(){
        return decor;
    }

    @Override
    protected FElement buildTriggerElement() {
        FElement ret = DOM.createDiv().cast();
        ret.addClassName(decor.css().comboBoxTrigger());
        FElement trIcon = DOM.createDiv().cast();
        ret.appendChild(trIcon);
        trIcon.setClassName(decor.css().comboBoxTriggerIcon());
        return ret;
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        view.addClassName(decor.css().comboBoxView());
        setSelectOnly(selectOnly);
        if(tr == null){
            addStyleName(decor.css().untriggered());
        }
    }

    @Override
    protected FElement getExpandedElement() {
        return getListView().getElement();
    }

    @Override
    protected FElement buildViewElement() {
        return DOM.createInputText().cast();
    }

    public void setGetter(Getter<T> getter){
        this.getter = getter;
    }

    public void setSelectOnly(boolean selectOnly){
        this.selectOnly = selectOnly;
        if(view != null){
            ((InputElement)view.cast()).setReadOnly(selectOnly);
        }
    }

    public void setAlwaysLoad(boolean val){
        alwaysLoad = val;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        DOM.setEventListener(view, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if (event.getTypeInt() == Event.ONKEYUP && !selectOnly) {
                    queryExec.pass(((InputElement) view.cast()).getValue());
                }
            }
        });
        DOM.sinkEvents(view, Event.ONKEYUP);
    }

    /**
     * очищает поле и удаляет все записи из его хранилища
     */
    public void invalidate(){
        clear();
        store.clear();
    }

    @Override
    public void setValue(T value) {
        super.setValue(value);
        if(view != null){
            String str = "";
            // ловим тут случай когда в геттере не обработан NULL
            try{
                str = getter.get(value);
            } catch (Exception e){
                Log.warn("Не обработан NULL", e);
            }
            ((InputElement)view.cast()).setValue(str);
        }
        getListView().select(value, false);
    }

    public void setHasEmpty(boolean hasEmpty){
        this.hasEmpty = hasEmpty;
        getListView().setHasEmpty(hasEmpty);
    }

    public void setLoader(Loader<String, List<T>> loader){
        this.loader = loader;
    }

    private void updatePositionAndSize(){
        if(!expander.isExpanded())
            return;
        int top = getElement().getAbsoluteTop() + getHeight();
        int left = getElement().getAbsoluteLeft();
        int wndViewHeight = Window.getClientHeight()+Window.getScrollTop() - 20;

        int height = getListView().getMaxHeight();
        if(top < wndViewHeight / 2){
            if(height > wndViewHeight - top){
                height = wndViewHeight - top;
            }
        }else{
            if(height > getElement().getAbsoluteTop() - 20){
                height = getElement().getAbsoluteTop() - 20;
                top = 20;
            }else{
                top = getElement().getAbsoluteTop() - height;
            }
        }
        if(getListView().getWidth() < getWidth())
            getListView().setWidth(getWidth());
        getListView().setHeight(height);
        getListView().setPosition(left, top);
    }

    private ListView<T> getListView(){
        if(listView == null){
            listView = new ListView<>(store, getter);
            listView.addSelectHandler(new SelectEvent.SelectHandler<T>() {
                @Override
                public void onSelect(T object) {
                    expander.collapse();
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

    @Override
    protected void onExpand(){
        getListView().setPosition(-10000, -10000);
        RootPanel.get().add(getListView());

        if(alwaysLoad || needRedraw || store.isEmpty() || query != null){
            getListView().setLoading();
            load();
        }
        updatePositionAndSize();
    }

    @Override
    protected void onCollapse(){
        getListView().removeFromParent();
        if(query != null && !query.isEmpty()){
            query = null;
            store.clear();
        }
    }

    private void load(){
        if(loader != null){
            loader.load(query, new LastRespAsyncCallback<List<T>>(loaderUID) {
                @Override
                public void onSuccessLast(List<T> result) {
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
        }else{
            redrawListView();
        }
    }

    public String getQuery(){
        return query;
    }

    public void redrawListView(){
        if(isAttached()){
            getListView().forceRedraw();
            getListView().getStyle().clearWidth();
        }
        updatePositionAndSize();
        needRedraw = false;
    }

    public ListStore<T> getStore(){
        return store;
    }

    public HandlerRegistration addSelectHandler(SelectEvent.SelectHandler<T> handler) {
        return addHandler(handler, SelectEvent.<T>getType());
    }

}
