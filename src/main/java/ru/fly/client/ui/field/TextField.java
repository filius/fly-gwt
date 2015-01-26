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
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.ui.FElement;
import ru.fly.client.event.*;

/**
 * User: fil
 * Date: 05.08.13
 * Time: 21:06
 */
public class TextField extends InputElementField<String> {

    public TextField() {
        TextFieldDecor decor = GWT.create(TextFieldDecor.class);
        addStyleName(decor.css().textField());
        setHeight(24);
    }

    @Override
    protected FElement createInputElement() {
        return DOM.createInputText().cast();
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();

        getElement().appendChild(getInputElement());

//        DOM.setEventListener(getElement(), new EventListener() {
//            @Override
//            public void onBrowserEvent(Event event) {
//                switch(event.getTypeInt()){
//                    case Event.ONKEYPRESS:
//                        if(isEnabled())
//                            fireEvent(new KeyPressEvent(event));
//                        break;
//                    case Event.ONKEYUP:
//                        if(isEnabled())
//                            fireEvent(new KeyUpEvent(event));
//                        break;
//                }
//            }
//        });
//        DOM.sinkEvents(getElement(), Event.ONKEYPRESS | Event.ONKEYUP);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        setValue(super.getValue());
        sinkEvents(Event.ONKEYPRESS | Event.ONKEYUP);
    }

    @Override
    public void onBrowserEvent(Event event) {
        switch(event.getTypeInt()){
            case Event.ONKEYPRESS:
                if(isEnabled())
                    fireEvent(new KeyPressEvent(event));
                break;
            case Event.ONKEYUP:
                if(isEnabled())
                    fireEvent(new KeyUpEvent(event));
                break;
        }
        super.onBrowserEvent(event);
    }

    @Override
    public String getValue() {
        if(getInputElement() != null){
            String inputValue = ((InputElement)getInputElement().cast()).getValue();
            value = inputValue == null || inputValue.isEmpty() ? null : inputValue;
        }
        return super.getValue();
    }

    @Override
    public void setValue(String value) {
        super.setValue(value);
        if(getInputElement() != null) {
            ((InputElement) getInputElement().cast()).setValue((value == null) ? "" : value);
        }
    }

    public void addKeyPressHandler(KeyPressHandler lnr){
        addHandler(lnr, KeyPressEvent.getType());
    }

    public void addKeyUpHandler(KeyUpHandler lnr){
        addHandler(lnr, KeyUpEvent.getType());
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize((width < 0)?width:(width-2), (height < 0)?height:(height-2));
    }
}
