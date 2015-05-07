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

package ru.fly.client.ui.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import ru.fly.client.event.*;
import ru.fly.client.ui.Component;
import ru.fly.client.F;
import ru.fly.client.ListStore;
import ru.fly.client.ui.grid.decor.GridDecor;

import java.util.List;

/**
 * User: fil
 * Date: 31.08.13
 * Time: 14:16
 */
public class Grid<T> extends Component {

    private LoadConfig<T> loadConfig = new LoadConfig<>();
    private ListStore<T> store;
    private Header<T> header;
    private GridView<T> view;

    public Grid(List<ColumnConfig<T>> cols) {
        this(cols, null);
    }

    public Grid(List<ColumnConfig<T>> cols, GridView<T> view) {
        this(GWT.<GridDecor>create(GridDecor.class), cols, view);
    }

    public Grid(GridDecor decor, List<ColumnConfig<T>> cols, GridView<T> view) {
        super(DOM.createDiv());
        setStyleName(decor.css().grid());
        header = new Header<>(cols);
        header.addOrderChangeHandler(new OrderChangeHandler() {
            @Override
            public void onChange(String orderField, boolean asc) {
                getLoadConfig().setOrderBy(orderField);
                getLoadConfig().setAsc(asc);
                if (isAttached())
                    fireEvent(new OrderChangeEvent(orderField, asc));
            }
        });
        header.addGridColumnResizeHandler(new GridColumnResizeEvent.GridColumnResizeHandler() {
            @Override
            public void onResize(ColumnConfig config) {
                redraw();
            }
        });
        store = new ListStore<T>();
        store.addUpdateHandler(new UpdateEvent.UpdateHandler() {
            @Override
            public void onUpdate() {
                redraw();
            }
        });
        if(view == null)
            setGridView(new GridView<T>());
        else
            setGridView(view);
    }

    public void setColumnConfigs(List<ColumnConfig<T>> cc){
        getHeader().getColumnConfigs().clear();
        getHeader().getColumnConfigs().addAll(cc);
        redraw();
    }

    public void setGridView(GridView<T> view){
        this.view = view;
        view.setGrid(this);
        header.setGridView(view);
        view.addHandler(new SelectEvent.SelectHandler<T>() {
            @Override
            public void onSelect(T object) {
                getLoadConfig().setSelection(object);
                fireEvent(new SelectEvent<T>(getSelected()));
            }
        }, SelectEvent.<T>getType());
        view.addHandler(new GridRowDblClickHandler<T>() {
            @Override
            public void onClick(T object) {
                fireEvent(new GridRowDblClickEvent<T>(object));
            }
        }, GridRowDblClickEvent.<T>getType());
    }

    public LoadConfig<T> getLoadConfig(){
        return loadConfig;
    }

    public ListStore<T> getStore(){
        return store;
    }

    public Header<T> getHeader(){
        return header;
    }

    public GridView<T> getView(){
        return view;
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        F.render(this, getHeader());
        F.render(this, getView());
        redraw();
    }

    public void redraw(){
        if(!isAttached())
            return;
        updateHeader();
        updateView();
    }

    private void updateHeader(){
        if(getHeader() != null)
            getHeader().redraw();
    }

    private void updateView(){
        if(getView() != null)
            getView().redraw();
    }

    public T getSelected(){
        T sel = getLoadConfig().getSelection();
        return store.contains(sel) ? store.get(sel) : null;
    }

    public void select(T model){
        getView().select(model);
        getLoadConfig().setSelection(model);
    }

    public void selectFirst(){
        select(getStore().size() > 0 ? getStore().get(0) : null);
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize(width, height);
        redraw();
    }

    public HandlerRegistration addSelectHandler(SelectEvent.SelectHandler<T> h){
        return addHandler(h, SelectEvent.<T>getType());
    }

    public HandlerRegistration addRowDblClickHandler(GridRowDblClickHandler<T> h){
        return addHandler(h, GridRowDblClickEvent.<T>getType());
    }

    public HandlerRegistration addOrderChangeHandler(OrderChangeHandler h){
        return addHandler(h, OrderChangeEvent.getType());
    }
}
