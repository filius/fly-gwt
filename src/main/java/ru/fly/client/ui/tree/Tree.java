package ru.fly.client.ui.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import ru.fly.client.F;
import ru.fly.client.TreeStore;
import ru.fly.client.event.UpdateHandler;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.tree.decor.TreeDecor;

/**
 * User: fil
 * Date: 05.03.15
 */
public class Tree<T> extends Component {

    protected final TreeDecor decor = GWT.create(TreeDecor.class);

    private TreeGetter<T> getter;
    protected TreeStore<T> store;
    private TreeView<T> treeView;

    public Tree(TreeGetter<T> getter) {
        super(DOM.createDiv());
        this.getter = getter;
        addStyleName(decor.css().tree());
        store = new TreeStore<>();
        store.addUpdateHandler(new UpdateHandler() {
            @Override
            public void onUpdate() {
                redraw();
            }
        });
        treeView = new TreeView<>();
        treeView.setTree(this);
    }

    public TreeGetter<T> getGetter(){
        return getter;
    }

    public TreeStore<T> getStore(){
        return store;
    }

    public TreeView getView(){
        return treeView;
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

}
