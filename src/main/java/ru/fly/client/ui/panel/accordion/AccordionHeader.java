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
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import ru.fly.client.ui.Component;

/**
 * Created by fil on 05.01.14.
 */
public class AccordionHeader extends Component implements HasClickHandlers{

    public AccordionHeader(AccordionDecor decor, ImageResource image, String title){
        super(DOM.createDiv());
        if(image != null){
            getElement().setInnerHTML(new StringBuilder("<img src='")
                    .append(image.getSafeUri().asString()).append("' class='")
                    .append(decor.css().headerIcon()).append("'></img>")
                    .append(title).toString());
        }else
            getElement().setInnerHTML(title);
        setStyleName(decor.css().itemHeader());
    }

    @Override
    public HandlerRegistration addClickHandler(ClickHandler handler) {
        return addDomHandler(handler, ClickEvent.getType());
    }
}
