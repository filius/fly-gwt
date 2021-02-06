package ru.fly.client.ui.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import ru.fly.client.F;
import ru.fly.client.TreeStore;
import ru.fly.client.event.BeforeCollapseEvent;
import ru.fly.client.event.BeforeExpandEvent;
import ru.fly.client.event.CollapseEvent;
import ru.fly.client.event.ExpandEvent;
import ru.fly.client.event.SelectEvent;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.tree.decor.TreeDecor;
import ru.fly.client.util.LastPassExecutor;
import ru.fly.shared.FlyException;

/**
 * @author fil
 */
public class Tree<T> extends Component implements SelectEvent.HasSelectHandler<T>,
        ExpandEvent.HasExpandHandler<T>, CollapseEvent.HasCollapseHandler<T>,
        BeforeExpandEvent.HasBeforeExpandHandler<T>, BeforeCollapseEvent.HasBeforeCollapseHandler<T> {

    private final LastPassExecutor<T> redrawExec = new LastPassExecutor<T>(.5) {
        @Override
        protected void exec(T param) {
            if (getView() != null) {
                getView().markDirty();
                getView().redraw();
            }
        }
    };
    private final TreeDecor decor;
    private final TreeGetter<T> getter;
    private final TreeStore<T> store;
    private TreeView<T> treeView;

    public Tree(TreeGetter<T> getter) {
        this(GWT.<TreeDecor>create(TreeDecor.class), getter);
    }

    public Tree(TreeDecor decor, TreeGetter<T> getter) {
        super(DOM.createDiv());
        if (decor == null) {
            throw new FlyException("Decorator for Tree component cant be NULL!");
        }
        this.decor = decor;
        this.getter = getter;
        addStyleName(decor.css().tree());
        store = new TreeStore<>();
        setView(new TreeView<T>());
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        F.render(this, getView());
    }

    @Override
    public HandlerRegistration addSelectHandler(SelectEvent.SelectHandler<T> h) {
        return addHandler(h, SelectEvent.<T>getType());
    }

    @Override
    public HandlerRegistration addCollapseHandler(CollapseEvent.CollapseHandler<T> h) {
        return addHandler(h, CollapseEvent.<T>getType());
    }

    @Override
    public HandlerRegistration addExpandHandler(ExpandEvent.ExpandHandler<T> h) {
        return addHandler(h, ExpandEvent.<T>getType());
    }

    @Override
    public HandlerRegistration addBeforeCollapseHandler(BeforeCollapseEvent.BeforeCollapseHandler<T> h) {
        return addHandler(h, BeforeCollapseEvent.<T>getType());
    }

    @Override
    public HandlerRegistration addBeforeExpandHandler(BeforeExpandEvent.BeforeExpandHandler<T> h) {
        return addHandler(h, BeforeExpandEvent.<T>getType());
    }

    public TreeDecor getDecor() {
        return decor;
    }

    public TreeGetter<T> getGetter() {
        return getter;
    }

    public TreeStore<T> getStore() {
        return store;
    }

    public void setView(TreeView<T> view) {
        treeView = view;
        treeView.setTree(this);
        treeView.addSelectHandler(new SelectEvent.SelectHandler<T>() {
            @Override
            public void onSelect(T object) {
                Tree.this.fireEvent(new SelectEvent<>(object));
            }
        });
        treeView.addExpandHandler(new ExpandEvent.ExpandHandler<T>() {
            @Override
            public void onExpand(T object) {
                Tree.this.fireEvent(new ExpandEvent<>(object));
            }
        });
        treeView.addCollapseHandler(new CollapseEvent.CollapseHandler<T>() {
            @Override
            public void onCollapse(T object) {
                Tree.this.fireEvent(new CollapseEvent<>(object));
            }
        });
        treeView.addBeforeExpandHandler(new BeforeExpandEvent.BeforeExpandHandler<T>() {
            @Override
            public void onBeforeExpand(T object) {
                Tree.this.fireEvent(new BeforeExpandEvent<>(object));
            }
        });
        treeView.addBeforeCollapseHandler(new BeforeCollapseEvent.BeforeCollapseHandler<T>() {
            @Override
            public void onBeforeCollapse(T object) {
                Tree.this.fireEvent(new BeforeCollapseEvent<>(object));
            }
        });
    }

    public TreeView<T> getView() {
        return treeView;
    }

    public T getSelected() {
        return getView().getSelected();
    }

    public void select(T model) {
        if (getView() != null) {
            getView().select(model, true);
        }
    }

    public void clear() {
        getStore().clear();
        getView().select(null, false);
    }

    public void expandAll() {
        for (T child : getStore().getChildren(null)) {
            getView().expandAll(child);
        }
    }

    /**
     * fully redraw tree.
     */
    public void redraw() {
        if (!isAttached()) {
            return;
        }
        redrawExec.pass();
    }
}
