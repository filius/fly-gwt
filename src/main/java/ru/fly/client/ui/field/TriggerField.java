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

package ru.fly.client.ui.field;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.ui.CommonDecor;
import ru.fly.client.ui.FElement;

/**
 * User: fil
 * Date: 06.08.13
 * Time: 23:25
 */
public abstract class TriggerField<T> extends Field<T> {

    private final CommonDecor res = GWT.create(CommonDecor.class);

    protected Expander expander;
    protected FElement view;
    protected FElement tr;
    private boolean triggerVisible = true;

    protected abstract FElement getExpandedElement();

    public TriggerField() {
        setHeight(24);
        setWidth(160);
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        expander = new Expander(getElement(), getExpandedElement()) {
            @Override
            public void onExpand() {
                TriggerField.this.onExpand();
            }

            @Override
            public void onCollapse() {
                TriggerField.this.onCollapse();
            }
        };
        expander.setEnabled(isEnabled());
        view = buildViewElement();
        getElement().appendChild(view);
        if(triggerVisible){
            tr = buildTriggerElement();
            getElement().appendChild(tr);
            initListeners();
        }

        setValue(value);
    }

    protected FElement buildTriggerElement(){
        return DOM.createDiv().cast();
    }

    private void initListeners(){
        if(tr != null){
            DOM.setEventListener(tr, new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    switch(event.getTypeInt()){
                        case Event.ONCLICK:
                            expander.expandCollapse();
                            break;
                        case Event.ONMOUSEOVER:
                            if(isEnabled())
                                tr.addClassName(res.css().over());
                            break;
                        case Event.ONMOUSEOUT:
                            if(isEnabled())
                                tr.removeClassName(res.css().over());
                            break;
                    }
                }
            });
            DOM.sinkEvents(tr, Event.ONCLICK | Event.ONMOUSEOVER | Event.ONMOUSEOUT);
        }
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        initListeners();
    }

    public void setTriggerVisible(boolean triggerVisible){
        this.triggerVisible = triggerVisible;
    }

    protected FElement buildViewElement(){
        return DOM.createDiv().cast();
    }

    protected void onExpand(){}

    protected void onCollapse(){}

    @Override
    public void setEnabled(boolean val) {
        super.setEnabled(val);
        if(expander != null)
            expander.setEnabled(val);
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize((width < 0)?width:width-2, (height < 0)?height:height-2);
    }
}
