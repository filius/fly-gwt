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

package ru.fly.client.ui.listview;

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.ui.FElement;
import ru.fly.shared.Getter;

/**
 * User: fil
 * Date: 20.04.14
 * Time: 20:28
 */
public class CheckListView<T> extends ListView<T>{

    public CheckListView(Getter<T> getter) {
        super(getter);
    }

    @Override
    protected void renderItem(final T model) {
        FElement el = DOM.createDiv().cast();
        el.setClassName(decor.css().listViewItem());
//        if(selected != null && selected.equals(model)){
//            el.addClassName(decor.css().selected());
//        }
        Object display = getGetter().get(model);
        el.setInnerHTML((display == null)?"":display.toString());
        getElement().appendChild(el);
        InputElement ch = DOM.createInputCheck().cast();
        el.insertFirst(ch);
        DOM.setEventListener(el, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if(event.getTypeInt() == Event.ONCLICK){
                    if(isEnabled()){
                        select(model,true);
                    }
                }
            }
        });
        DOM.sinkEvents(el, Event.ONCLICK);
        el.listenOver(decor.css().over());
    }
}
