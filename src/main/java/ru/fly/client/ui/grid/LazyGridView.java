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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.grid.decor.GridDecor;
import ru.fly.client.util.LastPassExecutor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fil
 */
public class LazyGridView<T> extends GridView<T> {

    private Map<Integer, FElement> viewRows = new HashMap<Integer, FElement>();
    private LastPassExecutor renderAreaExec = new LastPassExecutor(.01) {
        @Override
        protected void exec(Object param) {
            renderViewArea();
        }
    };
    private boolean inRenderProcess = false;

    public LazyGridView(){
        this(GWT.<GridDecor>create(GridDecor.class));
    }

    public LazyGridView(GridDecor decor) {
        super(decor);
        addStyleName(decor.css().gridViewLazy());
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        addScrollListener();
    }

    @Override
    protected void redraw() {
        if(inner != null){
            inner.removeAll();
            inner.setHeight(grid.getStore().size() * rowHeight);
        }
        viewRows.clear();
        renderAreaExec.pass();
    }

    private void addScrollListener(){
        final EventListener oldLnr = DOM.getEventListener(getElement());
        DOM.setEventListener(getElement(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if(oldLnr != null)
                    oldLnr.onBrowserEvent(event);
                switch (event.getTypeInt()){
                    case Event.ONSCROLL:
                        renderAreaExec.pass();
                        break;
                }
            }
        });
        DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.ONSCROLL);
    }

    @Override
    protected FElement getRowElement(int idx) {
        return viewRows.get(idx);
    }

    private void renderViewArea(){
        if(inRenderProcess){
            renderAreaExec.pass();
            return;
        }
        try {
            inRenderProcess = true;
            List<T> l = grid.getStore().getList();

            long top = getElement().getScrollTop();
            long bottom = top + getHeight(true);
            int stIdx = (int) (top / rowHeight);
            int enIdx = (int) (bottom / rowHeight) + 2;
            if (enIdx > l.size()) {
                enIdx = l.size();
            }

            for (int i = stIdx; i < enIdx; i++) {
                if (!viewRows.containsKey(i)) {
                    FElement row = renderRow(l.get(i), i % 2 == 0);
                    row.setTop(rowHeight * i);
                    viewRows.put(i, row);
                }
            }
            Set<Integer> idxs = new HashSet<Integer>(viewRows.keySet());
            for (Integer idx : idxs) {
                if (idx < stIdx || idx > enIdx) {
                    viewRows.get(idx).removeFromParent();
                    viewRows.remove(idx);
                }
            }
        }finally {
            inRenderProcess = false;
        }
    }

}
