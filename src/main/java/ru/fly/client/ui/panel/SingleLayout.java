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

import com.google.gwt.user.client.DOM;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.*;

/**
 * User: fil
 * Date: 03.09.13
 * Time: 23:19
 */
public class SingleLayout extends LayoutContainer implements HasOneWidget {

    public SingleLayout() {
        this(DOM.createDiv());
    }

    public SingleLayout(Element el) {
        super(el);
    }

    public SingleLayout(Widget w){
        this();
        add(w);
    }

    @Override
    public void add(Widget w) {
        add(w, new Margin(0));
    }

    public void add(Widget w, Margin m){
        clear();
        w.setLayoutData(m);
        super.add(w);
    }

    @Override
    public Widget getWidget() {
        return (iterator().hasNext()) ? iterator().next() : null;
    }

    @Override
    public void setWidget(Widget w) {
        add(w);
    }

    @Override
    public void setWidget(IsWidget w) {
        setWidget(Widget.asWidgetOrNull(w));
    }

    @Override
    protected void doLayout() {
        Widget child = getWidget();
        if(child != null){
            int w = getWidth(true);
            int h = getHeight(true);
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
