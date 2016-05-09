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
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.F;
import ru.fly.client.event.GridRowDblClickEvent;
import ru.fly.client.event.SelectEvent;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.grid.decor.GridDecor;

import java.util.List;

/**
 * @author fil
 */
public class GridView<T> extends Component {

    private final GridDecor decor;
    protected final int rowHeight;
    protected Grid<T> grid;
    protected FElement inner;
    private Element lastSelectedElement;

    public GridView(){
        this(GWT.<GridDecor>create(GridDecor.class));
    }

    public GridView(GridDecor decor) {
        super(DOM.createDiv());
        this.decor = decor;
        this.rowHeight = decor.css().pGridRowHeight() + decor.css().pGridRowBorderBottom();
        setStyleName(decor.css().gridView());
    }

    public void focus(){
        getElement().focus();
    }

    protected void setGrid(Grid<T> grid){
        this.grid = grid;
    }

    protected Grid<T> getGrid(){
        return grid;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        redraw();
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getElement().setTabIndex(F.getNextTabIdx());
        addViewListeners();

        inner = DOM.createDiv().cast();
        getElement().appendChild(inner);
    }

    protected void redraw(){
        if(!isAttached())
            return;
        inner.removeAll();
        boolean strip = false;
        for(T model : grid.getStore().getList()){
            renderRow(model, strip);
            strip = !strip;
        }
    }

    protected FElement renderRow(T model, boolean strip){
        final FElement row = DOM.createDiv().cast();
        row.setClassName(decor.css().gridViewRow());
        inner.appendChild(row);
        if(strip) row.addClassName(decor.css().strip());
        for(ColumnConfig<T> c : grid.getHeader().getColumnConfigs()){
            FElement col = DOM.createDiv().cast();
            col.setClassName(decor.css().gridViewCol());
            if(c.getRenderer() != null){
                Widget w = c.getRenderer().render(model);
                if(w != null) {
                    col.appendChild(w.getElement());
                    F.attach(w);
                }
            }else{
                String val = c.getGetter().get(model);
                col.setInnerHTML(val == null ? "" : val);
            }
            col.setWidth(c.getWidth() == -1 ? c.getCalculatedWidth() : c.getWidth());
            col.setLeft(c.getLeft());
            row.appendChild(col);
        }
        addRowListeners(row, model);
        T selected = grid.getSelected();
        if(selected != null && selected.equals(model)){
            lastSelectedElement = row;
            row.addClassName(decor.css().selected());
        }
        return row;
    }

    protected void onRowOver(FElement rowEl, T model){}

    protected void onRowOut(FElement rowEl, T model){}

    public void select(T model){
        int idx = grid.getStore().getList().indexOf(model);
        setSelected(getRowElement(idx), model, true);
    }

    /**
     * Scroll view to selected Row
     */
    public void checkView(){
        T selected = grid.getSelected();
        if(selected == null)
            return;
        int idx = grid.getStore().getList().indexOf(selected);
        int top = idx * rowHeight;
        int bottom = top + rowHeight;
        int h = getHeight(true);
        int scroolTop = getElement().getScrollTop();
        if(bottom > h+scroolTop){
            getElement().setScrollTop(bottom - h);
        }else if(top < scroolTop){
            getElement().setScrollTop(top);
        }
    }

    private void setSelected(Element rowEl, T model, boolean force){
        if(lastSelectedElement != null)
            lastSelectedElement.removeClassName(decor.css().selected());
        T selected = grid.getSelected();
        if(model == null || (model.equals(selected) && !force)){
            selected = null;
        }else{
            lastSelectedElement = rowEl;
            if(lastSelectedElement != null)
                lastSelectedElement.addClassName(decor.css().selected());
            selected = model;
        }
        fireEvent(new SelectEvent<T>(selected));
    }

    private void addViewListeners(){
        final EventListener oldLnr = DOM.getEventListener(getElement());
        DOM.setEventListener(getElement(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if(grid == null || !grid.isEnabled())
                    return;
                if (oldLnr != null)
                    oldLnr.onBrowserEvent(event);
                switch (event.getTypeInt()) {
                    case Event.ONCLICK:
                        getElement().focus();
                        break;
                    case Event.ONKEYDOWN:
                        int code = event.getKeyCode();
                        if (code == 38) {
                            selectPrev();
                            event.preventDefault();
                        } else if (code == 40) {
                            selectNext();
                            event.preventDefault();
                        }
                        break;
                }
            }
        });
        DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.ONKEYDOWN | Event.ONCLICK);
    }

    private void addRowListeners(final FElement el, final T model){
        DOM.setEventListener(el, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if(grid == null || !grid.isEnabled())
                    return;
                switch (event.getTypeInt()){
                    case Event.ONMOUSEOVER:
                        el.addClassName(decor.css().over());
                        onRowOver(el, model);
                        break;
                    case Event.ONMOUSEOUT:
                        el.removeClassName(decor.css().over());
                        onRowOut(el, model);
                        break;
                    case Event.ONCLICK:
                        setSelected(el, model, false);
                        break;
                    case Event.ONDBLCLICK:
                        fireEvent(new GridRowDblClickEvent<T>(model));
                        F.clearSelection();
                        break;
                }
            }
        });
        DOM.sinkEvents(el, Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONCLICK | Event.ONDBLCLICK);
    }

    protected FElement getRowElement(int idx){
        if(inner != null)
            return inner.getChildNodes().getItem(idx).cast();
        else
            return null;
    }

    private void selectNext(){
        List<T> l = grid.getStore().getList();
        T selected = grid.getSelected();
        int idx = selected == null ? 0 : (l.indexOf(selected) + 1);
        if(idx >= l.size()) idx = l.size()-1;
        T now = l.get(idx);
        if(now.equals(selected))
            return;
        setSelected(getRowElement(idx), now, true);
        checkView();
    }

    private void selectPrev(){
        List<T> l = grid.getStore().getList();
        T selected = grid.getSelected();
        int idx = selected == null ? (l.size()-1) : (l.indexOf(selected) - 1);
        if(idx < 0) idx = 0;
        T now = l.get(idx);
        if(now.equals(selected))
            return;
        setSelected(getRowElement(idx), now, true);
        checkView();
    }

}
