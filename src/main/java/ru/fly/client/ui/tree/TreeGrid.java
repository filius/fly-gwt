package ru.fly.client.ui.tree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import ru.fly.client.F;
import ru.fly.client.TreeStore;
import ru.fly.client.event.CollapseEvent;
import ru.fly.client.event.ExpandEvent;
import ru.fly.client.event.GridRowDblClickEvent;
import ru.fly.client.event.SelectEvent;
import ru.fly.client.event.UpdateEvent;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.grid.ColumnConfig;
import ru.fly.client.ui.tree.decor.TreeDecor;

import java.util.List;

/**
 * User: fil
 * Date: 03.05.15
 */
public class TreeGrid<T> extends Component implements SelectEvent.HasSelectHandler<T>, ExpandEvent.HasExpandHandler<T>,
        CollapseEvent.HasCollapseHandler<T> {

    private final TreeDecor decor;
    private final TreeGetter<T> getter;
    private TreeStore<T> store;
    private Header<T> header;
    private TreeGridView<T> view;

    public TreeGrid(TreeGetter<T> getter, List<ColumnConfig<T>> cols) {
        this(GWT.<TreeDecor>create(TreeDecor.class), getter, cols);
    }

    public TreeGrid(TreeDecor decor, TreeGetter<T> getter, List<ColumnConfig<T>> cols) {
        super(DOM.createDiv());
        this.decor = decor;
        this.getter = getter;
        setStyleName(decor.css().tree());
        header = new Header<>(decor, cols);
        store = new TreeStore<>();
        store.addUpdateHandler(new UpdateEvent.UpdateHandler() {
            @Override
            public void onUpdate() {
                redraw();
            }
        });
        if(view == null) {
            setGridView(new TreeGridView<T>());
        }else {
            setGridView(view);
        }
        view.setTree(this);
    }

    public void setGridView(TreeGridView<T> view){
        this.view = view;
        view.addHandler(new SelectEvent.SelectHandler<T>() {
            @Override
            public void onSelect(T object) {
                fireEvent(new SelectEvent<T>(getSelected()));
            }
        }, SelectEvent.<T>getType());
        view.addGridRowDblClickHandler(new GridRowDblClickEvent.GridRowDblClickHandler<T>() {
            @Override
            public void onClick(T object) {
                fireEvent(new GridRowDblClickEvent<T>(object));
            }
        });
        view.addExpandHandler(new ExpandEvent.ExpandHandler<T>() {
            @Override
            public void onExpand(T object) {
                TreeGrid.this.fireEvent(new ExpandEvent<T>(object));
            }
        });
        view.addCollapseHandler(new CollapseEvent.CollapseHandler<T>() {
            @Override
            public void onCollapse(T object) {
                TreeGrid.this.fireEvent(new CollapseEvent<T>(object));
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

    public Header<T> getHeader(){
        return header;
    }

    public TreeGridView<T> getView(){
        return view;
    }

    public void redraw(){
        if(!isAttached())
            return;
        updateHeader();
        updateView();
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        F.render(this, getHeader());
        F.render(this, getView());
        redraw();
    }

    private void updateHeader(){
        if(getHeader() != null)
            getHeader().redraw();
    }

    private void updateView(){
        if(getView() != null)
            getView().redraw();
    }

    public void expandAll(){
        for(T child : getStore().getChildren(null)){
            getView().expandAll(child);
        }
    }

    public void select(T model){
        getView().select(model);
    }

    public T getSelected(){
        return getView().getSelected();
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

    // -------------- privates ----------------

}
