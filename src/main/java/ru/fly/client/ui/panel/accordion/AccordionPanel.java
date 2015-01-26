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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.ui.Container;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fil on 05.01.14.
 */
public class AccordionPanel extends Container{

    private final AccordionDecor decor;
    private final List<AccordionItem> items = new ArrayList<AccordionItem>();
    private AccordionItem expandedItem;

    public AccordionPanel(){
        this(GWT.<AccordionDecor>create(AccordionDecor.class));
    }

    public AccordionPanel(AccordionDecor decor){
        super(DOM.createDiv());
        this.decor = decor;
    }

    public void add(ImageResource ico, Widget w, String title){
        AccordionItem item = new AccordionItem(decor, ico, title, w){
            @Override
            protected void onHeaderClick() {
                if(expandedItem != this){
                    expandedItem.collapse();
                    expandedItem = this;
                    expandedItem.expand();
                }

            }
        };
        items.add(item);
        add(item);
        if(expandedItem == null){
            expandedItem = item;
            expandedItem.expand();
        }else{
            item.collapse();
        }
    }

}
