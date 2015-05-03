package ru.fly.client.ui.tree;

import com.google.gwt.user.client.DOM;
import ru.fly.client.ui.Component;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.grid.ColumnConfig;
import ru.fly.client.ui.tree.decor.TreeDecor;

import java.util.List;

/**
 * User: fil
 * Date: 03.05.15
 */
public class Header<T> extends Component {

    private final TreeDecor decor;
    private List<ColumnConfig<T>> cols;

    public Header(TreeDecor decor, List<ColumnConfig<T>> cols) {
        super(DOM.createDiv());
        this.decor = decor;
        setStyleName(decor.css().gridHdr());
        this.cols = cols;
    }

    public List<ColumnConfig<T>> getColumnConfigs(){
        return cols;
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
        }
    }

    // -------------------- privates ---------------------

    private int calculateLeft(int idx){
        int ret = 0;
        for(int i=0; i<idx; i++){
            ColumnConfig c = cols.get(i);
            ret += (c.getWidth() == -1 ? c.getCalculatedWidth() : c.getWidth());
        }
        return ret;
    }

}
