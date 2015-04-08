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
import ru.fly.client.event.SelectHandler;
import ru.fly.client.log.Log;
import ru.fly.client.ui.field.combobox.ComboBoxDecor;
import ru.fly.shared.Getter;
import ru.fly.client.ui.listview.ListView;
import ru.fly.client.util.LastPassExecutor;

import java.util.List;

/**
 * User: fil
 * Date: 24.09.13
 * Time: 21:53
 */
public class VariantTextField<T> extends TextField {

    private final ComboBoxDecor cbDecor = GWT.create(ComboBoxDecor.class);

    private Loader<String, List<T>> loader;
    private ListStore<T> store = new ListStore<T>();
    protected Expander expander;
    private ListView<T> listView;
    private Getter<T> getter;
    private Getter<T> expandGetter;
    private String query = null;
    private LastPassExecutor queryExec = new LastPassExecutor() {
        @Override
        protected void exec(Object param) {
            if(param != null && ((String)param).isEmpty()) param = null;
            if(query != param && beforeQuery((param == null)?null:param.toString())){
                if(param == null)
                    query = null;
                else
                    query = param.toString();
                store.clear();
                expander.expand(true);
            }
        }
    };
    private String loaderUID = F.getUID();

    public VariantTextField(Getter<T> getter, Getter<T> expandGetter){
        super();
        this.getter = getter;
        this.expandGetter = expandGetter;
    }

    private ListView<T> getListView(){
        if(listView == null){
            listView = new ListView<T>(expandGetter);
            listView.addSelectHandler(new SelectHandler<T>() {
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

    protected boolean beforeQuery(String query){
        return true;
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        expander = new Expander(getElement(), null) {
            @Override
            public void onExpand() {
                getListView().setWidth(getWidth());
                getListView().setPosition(-10000, -10000);
                RootPanel.get().add(getListView());

                if(store.isEmpty() || query != null){
                    getListView().setLoading();
                    load();
                }
                updatePositionAndSize();
            }

            @Override
            public void onCollapse() {
                getListView().removeFromParent();
                if(query != null && !query.isEmpty()){
                    query = null;
                    store.clear();
                }
            }
        };
        expander.setEnabled(isEnabled());
    }

    public void setLoader(Loader<String, List<T>> loader){
        this.loader = loader;
    }

    private void load(){
        if(loader != null){
            loader.load(query, new LastRespAsyncCallback<List<T>>(loaderUID) {
                @Override
                public void onSuccessLast(List<T> result) {
                    store.clear();
                    if(result == null || result.size() < 1){
                        expander.collapse();
                    }else{
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
                if(oldLnr != null)
                    oldLnr.onBrowserEvent(event);
                if (event.getTypeInt() == Event.ONKEYUP)
                    queryExec.pass(((InputElement) getInputElement().cast()).getValue());
            }
        });
        DOM.sinkEvents(getInputElement(), DOM.getEventsSunk(getInputElement()) | Event.ONKEYUP);
    }

    @Override
    public void setEnabled(boolean val) {
        super.setEnabled(val);
        if(expander != null)
            expander.setEnabled(val);
    }

    public void rebuildListView(){
        if(isAttached())
            getListView().fillData(store.getList(), false);
        updatePositionAndSize();
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
        getListView().setHeight(height);
        getListView().setPosition(left, top);
    }

    public HandlerRegistration addSelectHandler(SelectHandler<T> handler) {
        return getListView().addSelectHandler(handler);
    }
}
