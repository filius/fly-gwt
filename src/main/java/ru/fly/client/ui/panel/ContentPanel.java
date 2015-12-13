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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.F;
import ru.fly.client.ui.FElement;

/**
 * User: fil
 * Date: 03.02.15
 */
public class ContentPanel extends SingleLayout{

    private final LayoutDecor decor;
    private final int innerBorderSpace;

    private FElement headerEl;
    private FElement containerEl;

    public ContentPanel(){
        this(GWT.<LayoutDecor>create(LayoutDecor.class));
    }

    public ContentPanel(String headerText){
        this();
        setHeaderText(headerText);
    }

    public ContentPanel(LayoutDecor decor) {
        this.decor = decor;
        this.innerBorderSpace = (decor.css().pContentPanelInnerBorder() + decor.css().pContentPanelInnerMargin()) * 2;
        setStyleName(decor.css().contentPanel());
        headerEl = DOM.createDiv().cast();
        headerEl.setInnerHTML("&nbsp;");
        headerEl.setClassName(decor.css().header());
    }

    public void setHeaderText(String text){
        headerEl.setInnerHTML(text);
    }

    @Override
    public FElement getContainerElement() {
        if(containerEl == null){
            containerEl = DOM.createDiv().cast();
            containerEl.setClassName(decor.css().inner());
        }
        return containerEl;
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getElement().appendChild(headerEl);
        F.setEnableTextSelection(headerEl, false);
        getElement().appendChild(getContainerElement());
    }

    @Override
    protected void doLayout() {
        int w = getWidth(true)-innerBorderSpace;
        int h = getHeight(true)-headerEl.getOffsetHeight()-innerBorderSpace;
        containerEl.setWidth(w < 0 ? 0 : w);
        containerEl.setHeight(h < 0 ? h : h);
        Widget child = getWidget();
        if(child != null){
            if(child.getLayoutData() instanceof Margin){
                Margin m = (Margin) child.getLayoutData();
                m.fillMargins(child);
                w = w - m.getLeft() - m.getRight();
                h = h - m.getTop() - m.getBottom();
            }
            child.setPixelSize(w, h);
        }
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize(
                (width < 0)?width:(width-decor.css().pContentPanelBorder()*2),
                (height < 0)?height:(height-decor.css().pContentPanelBorder()*2));
    }
}
