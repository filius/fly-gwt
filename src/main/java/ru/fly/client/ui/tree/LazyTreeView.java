package ru.fly.client.ui.tree;

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
    private boolean inRenderProcess = false;

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        addScrollListener();
    }

    @Override
    protected void redraw() {
        plainView.clear();
        for (T model : getTree().getStore().getChildren(null)) {
            addToPlainView(model);
        }
        getElement().removeAll();
        getElement().setHeight(plainView.size() * rowHeight);
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
                if (oldLnr != null)
                    oldLnr.onBrowserEvent(event);
                switch (event.getTypeInt()) {
                    case Event.ONSCROLL:
                        renderAreaExec.pass();
                        break;
                }
            }
        });
        DOM.sinkEvents(getElement(), DOM.getEventsSunk(getElement()) | Event.ONSCROLL);
    }

    private void renderViewArea() {
        if (inRenderProcess) {
            renderAreaExec.pass();
            return;
        }
        inRenderProcess = true;
        try {
            long top = getElement().getScrollTop();
            long bottom = top + getHeight(true);
            int stIdx = (int) (top / rowHeight);
            int enIdx = (int) (bottom / rowHeight) + 2;
            if (enIdx > plainView.size())
                enIdx = plainView.size();

            for (int i = stIdx; i < enIdx; i++) {
                T model = plainView.get(i);
                TreeRowItem<T> rowItem = renderedItems.get(model);
                if (rowItem == null) {
                    rowItem = renderItem(this, model, 0);
                    rowItem.setTop(rowHeight * i);
                }
                viewRows.put(i, rowItem);
            }
            Set<Integer> idxs = new HashSet<>(viewRows.keySet());
            for (Integer idx : idxs) {
                if (idx < stIdx || idx > enIdx) {
                    viewRows.get(idx).removeFromParent();
                    viewRows.remove(idx);
                }
            }
        } finally {
            inRenderProcess = false;
        }
    }

}
