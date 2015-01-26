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

package ru.fly.client.ui.toolbar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.ui.Component;
import ru.fly.client.event.ClickEvent;
import ru.fly.client.event.ClickHandler;

/**
 * User: fil
 * Date: 04.09.13
 * Time: 23:23
 */
public class MenuItem extends Component {

    private final ToolbarDecor decor = GWT.create(ToolbarDecor.class);

    public MenuItem(String text, ClickHandler lnr){
        super(DOM.createDiv());
        setStyleName(decor.css().menuItem());
        getElement().setInnerHTML(text);
        if(lnr != null)
            addHandler(lnr, ClickEvent.getType());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        DOM.setEventListener(getElement(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if(event.getTypeInt() == Event.ONCLICK){
                    getElement().removeClassName(decor.css().over());
                    fireEvent(new ClickEvent());
                }
            }
        });
        DOM.sinkEvents(getElement(), Event.ONCLICK);
        getElement().listenOver(decor.css().over());
    }
}
