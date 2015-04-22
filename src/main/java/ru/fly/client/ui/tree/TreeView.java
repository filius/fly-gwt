package ru.fly.client.ui.tree;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.F;
import ru.fly.client.ui.Component;

/**
 * User: fil
 * Date: 22.04.15
 */
public class TreeView<T> extends Component {

    private Tree<T> tree;

    public TreeView() {
        super(DOM.createDiv());
        F.setEnableTextSelection(getElement(), false);
    }

    protected Tree<T> getTree() {
        return tree;
    }

    protected void setTree(Tree<T> tree) {
        this.tree = tree;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        redraw();
    }

    protected void redraw(){
        if(!isAttached())
            return;
        getElement().removeAll();
        for(T model : tree.getStore().getChildren(null)){
            renderItem(this, model, 0);
        }
    }

    private void renderItem(Widget parent, T model, int lvl){
        TreeRowItem<T> item = new TreeRowItem<T>(model, tree.getGetter(), lvl, false){
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
        };
        F.render(parent, item);
        if(tree.getStore().isEmpty()) {
            for (T child : tree.getStore().getChildren(model)) {
                renderItem(item, child, lvl + 1);
            }
        }
    }

}
