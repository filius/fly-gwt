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

package ru.fly.client.ui.panel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.ui.CommonDecor;
import ru.fly.client.ui.Container;

/**
 * User: fil
 * Date: 03.09.13
 * Time: 23:10
 */
public class LayoutContainer extends Container implements Layout {

    protected boolean sizeChanged = true;

    public LayoutContainer(Element el) {
        super(el);
        getStyle().setPosition(Style.Position.RELATIVE);
        getStyle().setOverflow(Style.Overflow.HIDDEN);
    }

    public void addBackground(){
        CommonDecor decor = GWT.create(CommonDecor.class);
        getContainerElement().getStyle().setBackgroundColor(decor.css().pColorBackground());
    }

    public void layout(boolean force){
        if(!isAttached() || (!sizeChanged && !force))
            return;
        doLayout();
        for(Widget child : getWidgets()){
            if(child != null && child instanceof Layout){
                ((Layout) child).layout(force);
            }
        }
        sizeChanged = false;
    }

    protected void doLayout(){}

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if(visible) {
            layout(true);
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        layout(true);
    }

    @Override
    public void add(Widget w) {
        super.add(w);
        layout(true);
    }

    @Override
    public void insert(Widget w, int idx) {
        super.insert(w, idx);
        layout(true);
    }

    @Override
    public void setPixelSize(int width, int height) {
        if(this.width != width || this.height != height)
            sizeChanged = true;
        super.setPixelSize(width, height);
    }
}
