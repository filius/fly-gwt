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

package ru.fly.client.ui.panel.fieldset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.F;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.panel.Margin;
import ru.fly.client.ui.panel.SingleLayout;

/**
 *
 * Created by fil on 06.01.14.
 */
public class FieldSetPanel extends SingleLayout {

    private final FieldSetDecor decor = GWT.create(FieldSetDecor.class);
    private final FElement container;
    private final FElement header;

    public FieldSetPanel(){
        super(DOM.createDiv());
        container = DOM.createFieldSet().cast();
        container.setClassName(decor.css().fieldset());
        header = DOM.createLegend().cast();
        F.setEnableTextSelection(header, false);
    }

    @Override
    public FElement getContainerElement() {
        return container;
    }

    public FieldSetPanel(String headerText){
        this();
        setHeaderText(headerText);
    }

    public void setHeaderText(String text){
        header.setInnerText(text);
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getElement().appendChild(container);
        container.insertFirst(header);
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize(width, height);
        container.setPixelSize(width-2, height-2);
    }

    @Override
    protected void doLayout() {
        Widget child = getWidget();
        if(child != null && isAttached()){
            int w = getWidth(true) - 2;
            int h = getHeight(true) - header.getOffsetHeight() - 2;
            if(child.getLayoutData() instanceof Margin){
                Margin m = (Margin) child.getLayoutData();
                m.fillMargins(child);
                w = w - m.getLeft() - m.getRight();
                h = h - m.getTop() - m.getBottom();
            }
            child.setPixelSize(w, h);
        }
    }
}
