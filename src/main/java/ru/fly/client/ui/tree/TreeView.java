package ru.fly.client.ui.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.F;
import ru.fly.client.TreeStoreItem;
import ru.fly.client.event.SelectEvent;
import ru.fly.client.log.Log;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.tree.decor.TreeDecor;
import ru.fly.shared.FlyException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fil
 */
public class TreeView<T> extends Component implements SelectEvent.HasSelectHandler<T> {

    private final TreeDecor decor;
    protected final int rowHeight;
    protected final Map<T, TreeRowItem<T>> renderedItems = new HashMap<>();

    private Tree<T> tree;
    private T selected;

    public TreeView() {
        this(GWT.<TreeDecor>create(TreeDecor.class));
    }

    public TreeView(TreeDecor decor) {
        super(DOM.createDiv());
        this.decor = decor;
        rowHeight = decor.css().pTreeRowHeight();
        F.setEnableTextSelection(getElement(), false);
    }

    protected Tree<T> getTree() {
        return tree;
    }

    protected void setTree(Tree<T> tree) {
        this.tree = tree;
    }

    protected T getSelected() {
        if (selected != null && tree.getStore().contains(selected)) {
            return selected;
        } else {
            return null;
        }
    }

    protected void select(T model, boolean fire) {
        if (selected != null) {
            TreeRowItem<T> row = getRowItem(selected);
            if (row != null) {
                row.setSelected(false);
            }
        }
        selected = null;
        if (model != null) {
            selected = model;
            TreeRowItem<T> row = expandTo(selected);
            if (row != null) {
                row.setSelected(true);
            }
        }
        if (fire) {
            fireEvent(new SelectEvent<>(selected));
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        redraw();
    }

    protected void redraw() {
        if (!isAttached())
            return;
        getElement().removeAll();
        renderedItems.clear();
        for (T model : tree.getStore().getChildren(null)) {
            renderItem(this, model, 0);
        }
        expandTo(selected);
    }

    /**
     * expand all children recursive
     */
    public void expandAll(T model) {
        TreeRowItem<T> item = getRowItem(model);
        if (item == null) {
            throw new FlyException("Cant found row item!");
        } else {
            item.expand();
            for (T child : tree.getStore().getChildren(model)) {
                expandAll(child);
            }
        }
    }

    protected TreeRowItem<T> renderItem(Widget parent, T model, int lvl) {
        TreeRowItem<T> item = buildRowItem(model, lvl);
        if (selected != null && model.equals(selected)) {
            item.setSelected(true);
        }
        F.render(parent, item);
        renderedItems.put(model, item);
        if (tree.getStore().isExpanded(model)) {
            item.expand();
        }
        return item;
    }

    private TreeRowItem<T> buildRowItem(T model, int lvl) {
        return new TreeRowItem<T>(tree, model, lvl, false) {
            @Override
            protected void onExpand(T model) {
                for (T child : tree.getStore().getChildren(model)) {
                    renderItem(this, child, getLvl() + 1);
                }
            }

            @Override
            protected void onCollapse(T model) {
                getContainerElement().removeAll();
            }

            @Override
            protected void onClick(T model) {
                if (getTree().isEnabled() && getTree().getGetter().isSelectable(model)) {
                    if (model.equals(selected)) {
                        select(null, true);
                    } else {
                        select(model, true);
                    }
                }
            }
        };
    }

    @Override
    public HandlerRegistration addSelectHandler(SelectEvent.SelectHandler<T> h) {
        return addHandler(h, SelectEvent.<T>getType());
    }

    private TreeRowItem<T> getRowItem(T model) {
        for (T m : renderedItems.keySet()) {
            if (m.equals(model)) {
                return renderedItems.get(m);
            }
        }
        return null;
    }

    private TreeRowItem<T> expandTo(T model) {
        TreeRowItem<T> row = getRowItem(model);
        if (row == null) {
            TreeStoreItem<T> item = tree.getStore().getItem(model);
            if (item != null && item.getParent() != null) {
                TreeRowItem<T> parentRow = expandTo(item.getParent());
                if (parentRow == null) {
                    Log.warn("Cant found tree row item");
                    return null;
                }
                if (!parentRow.isExpanded()) {
                    parentRow.expand();
                }
            }
            return getRowItem(model);
        } else {
            return row;
        }
    }
}
