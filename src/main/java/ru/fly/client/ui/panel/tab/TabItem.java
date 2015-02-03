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
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.F;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.panel.Layout;

/**
 * User: fil
 * Date: 04.09.13
 * Time: 22:52
 */
public abstract class TabItem {

    private final TabRes res = GWT.create(TabRes.class);

    private TabPanel panel;
    private FElement btn;
    private Widget w;

    public TabItem(TabPanel panel, Widget w, String text){
        this.panel = panel;
        this.w = w;
        btn = DOM.createDiv().cast();
        btn.setInnerHTML("<span>"+text+"</span>");
        btn.setClassName(res.css().tabBtn());
    }

    public void renderTabButton(FElement parent){
        parent.appendChild(btn);
        DOM.setEventListener(btn, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                switch (event.getTypeInt()) {
                    case Event.ONCLICK:
                        onShow();
                        break;
                }
            }
        });
        DOM.sinkEvents(btn, Event.ONCLICK);
        btn.listenOver(res.css().over());
    }

    public void layout(){
        if(w instanceof Layout)
            ((Layout) w).layout(true);
    }

    public Widget getWidget(){
        return w;
    }

    public abstract void onShow();

    protected void show(){
        if(w.isAttached()){
            return;
        }
        F.render(panel, w);
        btn.addClassName(res.css().selected());
        layout();
    }

    protected void hide(){
        if(w.isAttached()){
            F.erase(panel, w);
            btn.removeClassName(res.css().selected());
        }
    }

}
