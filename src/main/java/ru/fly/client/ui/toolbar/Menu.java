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
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.ui.Container;
import ru.fly.client.event.ClickEvent;
import ru.fly.client.event.ClickHandler;

/**
 * User: fil
 * Date: 04.09.13
 * Time: 23:22
 */
public class Menu extends Container {

    private final ToolbarDecor res = GWT.create(ToolbarDecor.class);

    public Menu() {
        super(DOM.createDiv());
        setStyleName(res.css().menu());
    }

    @Override
    protected void doAttachChild(Widget w) {
        super.doAttachChild(w);
        w.addHandler(new ClickHandler() {
            @Override
            public void onClick() {
                Menu.this.fireEvent(new ClickEvent());
            }
        }, ClickEvent.getType());
    }
}
