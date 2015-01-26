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

package ru.fly.client.ui.panel.accordion;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.ui.Container;

/**
 * Created by fil on 05.01.14.
 */
public abstract class AccordionItem extends Container {

    private final AccordionDecor decor;
    private Widget widget;
    private AccordionHeader header;

    protected abstract void onHeaderClick();

    public AccordionItem(AccordionDecor decor, ImageResource ico, String title, Widget w){
        super(DOM.createDiv());
        this.decor = decor;
        setStyleName(decor.css().item());
        widget = w;
        header = new AccordionHeader(decor,ico,title);
        header.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                onHeaderClick();
            }
        });
        add(header);
        add(w);
    }

    public void expand(){
        widget.setVisible(true);
        addStyleName(decor.res.css().expanded());
    }

    public void collapse(){
        widget.setVisible(false);
        removeStyleName(decor.res.css().expanded());
    }

}
