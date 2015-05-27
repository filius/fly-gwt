package ru.fly.client.ui.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import ru.fly.client.F;
import ru.fly.client.TreeStore;
import ru.fly.client.event.SelectEvent;
import ru.fly.client.event.UpdateEvent;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.tree.decor.TreeDecor;
import ru.fly.shared.FlyException;

/**
 * User: fil
 * Date: 05.03.15
 */
public class Tree<T> extends Component implements SelectEvent.HasSelectHandler<T> {


    private final TreeDecor decor;
    private TreeGetter<T> getter;
    protected TreeStore<T> store;
    private TreeView<T> treeView;

    public Tree(TreeGetter<T> getter) {
        this(GWT.<TreeDecor>create(TreeDecor.class), getter);
    }

    public Tree(TreeDecor decor, TreeGetter<T> getter) {
        super(DOM.createDiv());
        if(decor == null){
            throw new FlyException("Decorator for Tree component cant be NULL!");
        }
        this.decor = decor;
        this.getter = getter;
        addStyleName(decor.css().tree());
        store = new TreeStore<>();
        store.addUpdateHandler(new UpdateEvent.UpdateHandler() {
            @Override
            public void onUpdate() {
                redraw();
            }
        });
        treeView = new TreeView<>();
        treeView.setTree(this);
        treeView.addSelectHandler(new SelectEvent.SelectHandler<T>() {
            @Override
            public void onSelect(T object) {
                Tree.this.fireEvent(new SelectEvent<>(object));
            }
        });
    }

    public TreeDecor getDecor(){
        return decor;
    }

    public TreeGetter<T> getGetter(){
        return getter;
    }

    public TreeStore<T> getStore(){
        return store;
    }

    public TreeView<T> getView(){
        return treeView;
    }

    public T getSelected(){
        return getView().getSelected();
    }

    public void select(T model){
        getView().select(model, true);
    }

    public void clear(){
        getStore().clear();
        getView().select(null, false);
    }

    public void expandAll(){
        for(T child : getStore().getChildren(null)){
            getView().expandAll(child);
        }
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        F.render(this, getView());
        redraw();
    }

    public void redraw(){
        if(!isAttached())
            return;
        updateView();
    }

    private void updateView(){
        if(getView() != null)
            getView().redraw();
    }

    @Override
    public HandlerRegistration addSelectHandler(SelectEvent.SelectHandler<T> h) {
        return addHandler(h, SelectEvent.<T>getType());
    }
}
