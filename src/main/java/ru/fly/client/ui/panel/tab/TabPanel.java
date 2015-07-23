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

package ru.fly.client.ui.panel.tab;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.ui.FElement;
import ru.fly.client.event.SelectEvent;
import ru.fly.client.ui.panel.LayoutContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * User: fil
 * Date: 04.09.13
 * Time: 20:36
 */
public class TabPanel extends LayoutContainer {

    private final TabRes res = GWT.create(TabRes.class);
    private FElement tabButtons;
    private FElement container;
    private List<TabItem> items = new ArrayList<TabItem>();
    private TabItem current;

    public TabPanel() {
        super(DOM.createDiv());
        setStyleName(res.css().tabPanel());

        tabButtons = DOM.createDiv().cast();
        tabButtons.setClassName(res.css().tabHdr());
    }

    @Override
    public void layout(boolean force) {
        if(isAttached()){
            int cWidth = getWidth(true);
            int cHeight = getHeight(true)-res.css().pHdrHeight()-1;
            getContainerElement().setWidth(cWidth);
            getContainerElement().setHeight(cHeight);
            for(Widget child : getWidgets()){
                child.setPixelSize(cWidth, cHeight);
            }
            if(current != null)
                current.layout();
        }
    }

    @Override
    public FElement getContainerElement() {
        if(container == null){
            container = DOM.createDiv().cast();
            container.setClassName(res.css().tabContent());
        }
        return container;
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize((width < 0)?width:(width-2), (height < 0)?height:(height-2));
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getElement().appendChild(tabButtons);
        tabButtons.setHeight(res.css().pHdrHeight());
        getElement().appendChild(getContainerElement());
    }

    @Override
    public void add(Widget w) {
        this.add(w, "item" + getWidgets().size());
    }

    @Override
    protected void doAttachChild(Widget w) {
        super.doAttachChild(w);
    }

    @Override
    protected void doAttachChildren() {
        for(TabItem item : items){
            item.renderTabButton(tabButtons);
        }
        if(current == null){
            if(items.size() > 0)
                doShow(items.get(0));
        }else {
            doShow(current);
        }
    }

    public void add(Widget w, String text){
        getWidgets().add(w);
        TabItem item = new TabItem(this, w, text) {
            @Override
            public void onShow() {
                TabPanel.this.show(this);
            }
        };
        items.add(item);
        if(isAttached()){
            item.renderTabButton(tabButtons);
            if(getWidgets().size() == 1) {
                doShow(item);
            }
            layout(true);
        }
    }

    public void show(Widget w){
        show(getItem(w));
    }

    public void show(TabItem item){
        doShow(item);
    }

    public Widget getCurrent(){
        return current == null ? null : current.getWidget();
    }

    public void addSelectHandler(SelectEvent.SelectHandler<Widget> lnr){
        addHandler(lnr, SelectEvent.<Widget>getType());
    }

    protected TabItem getItem(Widget w){
        for(TabItem item : items) {
            if (item.getWidget() == w) {
                return item;
            }
        }
        return null;
    }

    // ------------- private --------------

    private void doShow(TabItem item){
        if(current != null){
            current.hide();
        }
        if(isAttached()) {
            item.show();
        }
        current = item;
        fireEvent(new SelectEvent<Widget>(current.getWidget()));
    }
}
