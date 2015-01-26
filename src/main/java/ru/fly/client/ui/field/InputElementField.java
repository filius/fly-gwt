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

import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.event.FocusEvent;
import ru.fly.client.event.FocusHandler;
import ru.fly.client.ui.FElement;

/**
 * User: fil
 * Date: 10.09.13
 * Time: 0:29
 */
public abstract class InputElementField<T> extends Field<T>{

    private FElement inp;

    protected FElement getInputElement(){
        if(inp == null){
            inp = createInputElement();
            inp.setTabIndex(0);
            final EventListener oldLnr = DOM.getEventListener(inp);
            DOM.setEventListener(inp, new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    if(event.getTypeInt() == Event.ONFOCUS){
                        onFocus();
                        fireEvent(new FocusEvent());
                    }
                    if(event.getTypeInt() == Event.ONBLUR){
                        onBlur();
//                        fireEvent(new FocusEvent());
                    }
                    if(oldLnr != null){
                        oldLnr.onBrowserEvent(event);
                    }
                }
            });
            DOM.sinkEvents(inp, DOM.getEventsSunk(inp) | Event.ONFOCUS | Event.ONBLUR);
        }
        return inp;
    }

    protected abstract FElement createInputElement();

    public void selectAll(){
        ((InputElement)getInputElement().cast()).select();
    }

    public String getInputValue(){
        return ((InputElement)getInputElement().cast()).getValue();
    }

    public void setInputValue(String val){
        ((InputElement)getInputElement().cast()).setValue(val == null ? "" : val);
    }

    @Override
    public void setEnabled(boolean val) {
        super.setEnabled(val);
        if(val)
            getInputElement().removeAttribute("disabled");
        else
            getInputElement().setAttribute("disabled", "disabled");
    }

    public HandlerRegistration addFocusHandlser(FocusHandler h){
        return addHandler(h, FocusEvent.getType());
    }

    @Override
    public void focus() {
        getInputElement().focus();
    }
}
