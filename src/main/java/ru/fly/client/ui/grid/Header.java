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
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.EndCallback;
import ru.fly.client.dnd.Dragger;
import ru.fly.client.dnd.Point;
import ru.fly.client.dnd.Rect;
import ru.fly.client.event.GridColumnResizeEvent;
import ru.fly.client.event.OrderChangeEvent;
import ru.fly.client.event.OrderChangeHandler;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.FElement;

import java.util.List;

/**
 * User: fil
 * Date: 31.08.13
 * Time: 14:43
 */
public class Header<T> extends Component implements GridColumnResizeEvent.HasGridColumnResizeHandler{

    private class OrderColumn{
        private FElement orderEl;
        private ColumnConfig<T> orderColumn;
    }

    private final GridDecor decor = GWT.create(GridDecor.class);
    private GridView<T> gridView;
    private List<ColumnConfig<T>> cols;
    private OrderColumn orderInfo = new OrderColumn();

    public Header(List<ColumnConfig<T>> cols) {
        super(DOM.createDiv());
        setStyleName(decor.css().gridHdr());
        this.cols = cols;
    }

    public void setGridView(GridView<T> gridView){
        this.gridView = gridView;
    }

    public List<ColumnConfig<T>> getColumnConfigs(){
        return cols;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        redraw();
    }

    public void redraw(){
        int w = getWidth() - 20;
        getElement().removeAll();
        int calcCou = 0;
        for(ColumnConfig cc : cols){
            if(cc.getWidth() != -1){
                w -= cc.getWidth();
            }else{
                calcCou++;
            }
        }

        for(int i=0; i<cols.size(); i++){
            final ColumnConfig<T> c = cols.get(i);
            int left = calculateLeft(i);
            c.setLeft(left);
            if(c.getWidth() == -1){
                c.setCalculatedWidth(w / calcCou);
            }
            final FElement col = DOM.createDiv().cast();
            col.setTitle(c.getTitle());
            col.setClassName(decor.css().gridHdrCol());
            col.setInnerHTML(c.getTitle());
            col.setWidth(c.getWidth() == -1 ? c.getCalculatedWidth() : c.getWidth());
            col.setLeft(left);
            getElement().appendChild(col);
            if(c.isOrderable()){
                makeOrderable(col, c);
            }
            if(c.isResizable() && i != cols.size()-1){
                makeResizable(col, c);
            }
        }
        if(orderInfo.orderColumn != null){
            if(orderInfo.orderColumn.getSortDirection() == SortDirection.DESC)
                orderInfo.orderEl.addClassName(decor.css().desc());
            else
                orderInfo.orderEl.addClassName(decor.css().asc());
        }
    }

    private void makeOrderable(final FElement col, final ColumnConfig<T> c){
        FElement ascDesc = DOM.createDiv().cast();
        ascDesc.setClassName(decor.css().order());
        col.appendChild(ascDesc);
        DOM.setEventListener(col, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if(orderInfo.orderEl != null){
                    orderInfo.orderEl.removeClassName(decor.css().asc());
                    orderInfo.orderEl.removeClassName(decor.css().desc());
                }
                if(orderInfo.orderColumn == null || orderInfo.orderColumn != c){
                    orderInfo.orderEl = col;
                    orderInfo.orderColumn = c;
                    c.asc();
                }else{
                    if(c.getSortDirection() == SortDirection.ASC){
                        c.desc();
                    }else{
                        c.asc();
                    }
                }
                if(c.getSortDirection() == SortDirection.DESC)
                    orderInfo.orderEl.addClassName(decor.css().desc());
                else
                    orderInfo.orderEl.addClassName(decor.css().asc());
                fireEvent(new OrderChangeEvent(c.getOrderBy(), c.getSortDirection() == SortDirection.ASC));
            }
        });
        DOM.sinkEvents(col, Event.ONCLICK);
        if(orderInfo.orderColumn == null && c.getSortDirection() != null){
            orderInfo.orderEl = col;
            orderInfo.orderColumn = c;
            c.asc();
            fireEvent(new OrderChangeEvent(c.getOrderBy(), c.getSortDirection() == SortDirection.ASC));
        }else if(orderInfo.orderColumn == c){
            orderInfo.orderEl = col;
        }
    }

    private void makeResizable(final FElement colEl, final ColumnConfig<T> c){
        final FElement splitEl = DOM.createDiv().cast();
        splitEl.setClassName(decor.css().split());
        colEl.appendChild(splitEl);
        DOM.setEventListener(splitEl, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if(event.getTypeInt() == Event.ONMOUSEDOWN){
                    final FElement dragLine = DOM.createDiv().cast();
                    dragLine.setClassName(decor.css().dragLine());
                    final FElement gridEl = gridView.getGrid().getElement();
                    gridEl.appendChild(dragLine);
                    dragLine.setLeft(splitEl.getRelativeLeft(gridEl) + splitEl.getWidth(true));
                    gridEl.getStyle().setCursor(Style.Cursor.COL_RESIZE);
                    new Dragger(dragLine, event, new EndCallback<Point>() {
                        @Override
                        public void onEnd(Point result) {
                            dragLine.removeFromParent();
                            gridEl.getStyle().clearCursor();
                            doDnDResizeColumn(c, result.x);
                        }
                    }).setLockY(true).setBoundingRect(getBoundRect(colEl, cols.indexOf(c)));
                    event.stopPropagation();
                }
            }
        });
        DOM.sinkEvents(splitEl, Event.ONMOUSEDOWN);
    }

    private Rect getBoundRect(FElement colEl, int colIdx){
        int left = colEl.getAbsoluteLeft();
        ColumnConfig<T> col = cols.get(colIdx);
        int right = left + (col.getWidth() == -1 ? col.getCalculatedWidth() : col.getWidth());
        if(colIdx + 1 < cols.size()){
            ColumnConfig<T> nextCol = cols.get(colIdx+1);
            right += (nextCol.getWidth() == -1 ? nextCol.getCalculatedWidth() : nextCol.getWidth());
        }
        left += 20;
        right -= 20;
        GWT.log(left+" "+right);
        if(left > right){
            left = right;
        }
        final FElement gridEl = gridView.getGrid().getElement();
        return new Rect(left, gridEl.getAbsoluteTop(), right, gridEl.getAbsoluteBottom());
    }

    private void doDnDResizeColumn(ColumnConfig<T> c, int dx){
        c.setWidth((c.getWidth() == -1 ? c.getCalculatedWidth() : c.getWidth()) + dx);
        ColumnConfig<T> nextCol = cols.get(cols.indexOf(c)+1);
        if(nextCol.getWidth() != -1) {
            nextCol.setWidth(nextCol.getWidth() - dx);
        }
        fireEvent(new GridColumnResizeEvent(c));
    }

    private int calculateLeft(int idx){
        int ret = 0;
        for(int i=0; i<idx; i++){
            ColumnConfig c = cols.get(i);
            ret += (c.getWidth() == -1 ? c.getCalculatedWidth() : c.getWidth());
        }
        return ret;
    }

    public HandlerRegistration addOrderChangeHandler(OrderChangeHandler h){
        return addHandler(h, OrderChangeEvent.getType());
    }

    @Override
    public HandlerRegistration addGridColumnResizeHandler(GridColumnResizeEvent.GridColumnResizeHandler h) {
        return addHandler(h, GridColumnResizeEvent.getType());
    }

}
