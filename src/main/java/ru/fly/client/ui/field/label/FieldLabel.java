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

package ru.fly.client.ui.field.label;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.ui.Component;
import ru.fly.client.F;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.panel.Layout;

/**
 * User: fil
 * Date: 10.08.13
 * Time: 10:58
 */
public class FieldLabel extends Component implements Layout {

    private final LabelDecor decor = GWT.create(LabelDecor.class);

    private static final int DEFAULT_WIDTH = 100;
    private final int labelMargin = decor.css().pLabelMargin() * 2;

    public static class FieldLabelR extends FieldLabel{

        public FieldLabelR(Widget fld, String label){
            this(fld, label, DEFAULT_WIDTH);

        }

        public FieldLabelR(Widget fld, String label, int labelWidth){
            super(fld, label, labelWidth);
            setLabelRight(true);
        }

    }

    public static class FieldLabelT extends FieldLabel{

        public FieldLabelT(Widget fld, String label){
            this(fld, label, false);
        }
        public FieldLabelT(Widget fld, String label, boolean floatLeft){
            this(fld, label, DEFAULT_WIDTH, floatLeft);
        }

        public FieldLabelT(Widget fld, String label, int labelWidth){
            this(fld, label, labelWidth, false);
        }

        public FieldLabelT(Widget fld, String label, int labelWidth, boolean floatLeft){
            super(fld, label, labelWidth);
            setTop(true);
            if(floatLeft)
                setFloatLeft();
        }

    }

    private Widget fld;
    private String label;
    private FElement lblEl;
    private int labelWidth;
    private boolean labelRight = false;
    private boolean top = false;

    public FieldLabel(Widget fld, String label){
        this(fld, label, DEFAULT_WIDTH);
    }

    public FieldLabel(Widget fld, String label, int labelWidth) {
        super(DOM.createDiv());
        setStyleName(decor.css().fieldLabel());
        this.fld = fld;
        this.label = label;
        this.labelWidth = labelWidth;
        setLabelWidth(labelWidth);
    }

    @Override
    public void layout(boolean force) {
        if(fld != null && fld instanceof Layout)
            ((Layout) fld).layout(force);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        layout(true);
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible) {
            layout(true);
        }
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();

        lblEl = DOM.createDiv().cast();
        lblEl.setClassName(decor.css().label());
        getElement().appendChild(lblEl);
        setLabelText(label);

        F.render(this, fld);

        setLabelWidth(labelWidth);
        setLabelRight(labelRight);
        setTop(top);
    }

    private void setLabelWidth(int labelWidth){
        if(top){
            fld.getElement().getStyle().clearMarginLeft();
        }else{
            fld.getElement().getStyle().setMarginLeft(labelWidth, Style.Unit.PX);
        }
        if(lblEl != null){
            lblEl.setWidth(labelWidth-labelMargin);
        }
    }

    public void setLabelRight(boolean right){
        this.labelRight = right;
        if(lblEl != null && right)
            lblEl.getStyle().setTextAlign(Style.TextAlign.RIGHT);
    }

    public void setTop(boolean top){
        this.top = top;
        if(lblEl != null){
            if(top){
                lblEl.getStyle().clearFloat();
//                setHeight(fld.getHeight()+lblEl.getOffsetHeight());
            }else{
                lblEl.getStyle().setFloat(Style.Float.LEFT);
//                setHeight(fld.getHeight());
            }
        }
    }

    public Widget getField(){
        return fld;
    }

    public void setLabelText(String text){
        this.label = text;
        if(isAttached())
            lblEl.setInnerHTML("<span>"+label+"&nbsp;:</span>");
    }

}
