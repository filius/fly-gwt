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
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.ui.Container;

/**
 * User: fil
 * Date: 31.08.13
 * Time: 19:26
 */
public class ToolBar extends Container {

    private final ToolbarDecor res = GWT.create(ToolbarDecor.class);

    public ToolBar(){
        super(DOM.createDiv());
        setStyleName(res.css().toolbar());
    }

    public void addRight(Widget child){
        child.addStyleName(res.css().button());
        child.getElement().getStyle().setFloat(Style.Float.RIGHT);
        add(child);
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize(width, (height < 0)?height:height-2);
    }
}
