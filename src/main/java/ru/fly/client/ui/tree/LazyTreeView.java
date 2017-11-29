package ru.fly.client.ui.tree;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.util.LastPassExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author fil.
 * @deprecated not really implemented NOT use it
 */
public class LazyTreeView<T> extends TreeView<T> {

    private final List<T> plainView = new ArrayList<>();
    private final Map<Integer, TreeRowItem<T>> viewRows = new HashMap<>();
    private final LastPassExecutor renderAreaExec = new LastPassExecutor(.01) {
        @Override
        protected void exec(Object param) {
            renderViewArea();
        }
    };

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        addScrollListener();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.ONSCROLL);
    }

    @Override
    protected void redraw() {
        plainView.clear();
        for (T model : getTree().getStore().getChildren(null)) {
            addToPlainView(model);
        }
        inner.clear();
        inner.setHeight(plainView.size() * rowHeight);
        renderAreaExec.pass();
    }

    private void addToPlainView(T model) {
        plainView.add(model);
        if (getTree().getStore().isExpanded(model)) {
            for (T ch : getTree().getStore().getChildren(model)) {
                addToPlainView(ch);
            }
        }
    }

    private void addScrollListener() {
        final EventListener oldLnr = DOM.getEventListener(getElement());
        DOM.setEventListener(getElement(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                switch (event.getTypeInt()) {
                    case Event.ONSCROLL:
                        renderAreaExec.pass();
                        break;
                }
                if (oldLnr != null) {
                    oldLnr.onBrowserEvent(event);
                }
            }
        });
    }

    private void renderViewArea() {
        long top = getElement().getScrollTop();
        long bottom = top + getHeight(true);
        int stIdx = (int) (top / rowHeight);
        int enIdx = (int) (bottom / rowHeight) + 2;
        GWT.log(top + " " + bottom + " " + stIdx + " " + enIdx);
        if (enIdx > plainView.size()) {
            enIdx = plainView.size();
        }
        int cou = 0;
        while (cou < (enIdx - stIdx)) {
            T model = plainView.get(stIdx + cou);
            TreeRowItem<T> rowItem = renderedItems.get(model);
            if (rowItem == null) {
                rowItem = renderItem(inner, model, 0);
                rowItem.setTop(rowHeight * cou);
            }
            viewRows.put(stIdx + cou, rowItem);
            cou++;
        }
        Set<Integer> idxs = new HashSet<>(viewRows.keySet());
        for (Integer idx : idxs) {
            if (idx < stIdx || idx > enIdx) {
                viewRows.get(idx).removeFromParent();
                viewRows.remove(idx);
            }
        }
    }

}
