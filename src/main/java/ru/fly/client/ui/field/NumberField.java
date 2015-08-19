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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import ru.fly.client.event.KeyUpEvent;
import ru.fly.client.ui.FElement;

import java.util.Arrays;
import java.util.List;

/**
 * User: fil
 * Date: 06.08.13
 * Time: 21:41
 */
public class NumberField<T extends Number> extends InputElementField<T> implements KeyUpEvent.HasKeyUpHandler {

    private final List<Character> allows = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    private final List<Character> decimals = Arrays.asList('.', ',');
    private final NumberFormat doubleFormat = NumberFormat.getFormat("#########################################.##");

    private Class<T> clazz;
    private boolean ctrlvFixed = false;

    public NumberField(Class<T> clazz) {
        this.clazz = clazz;
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
        setValue(super.getValue());
    }

    private boolean isDecimal(){
        return clazz == Double.class || clazz == Float.class;
    }

    @Override
    public void onBrowserEvent(Event e) {
        switch(e.getTypeInt()){
            case Event.ONKEYPRESS:
                if(e.getKeyCode() != KeyCodes.KEY_BACKSPACE &&
                        e.getKeyCode() != KeyCodes.KEY_DELETE &&
                        e.getKeyCode() != KeyCodes.KEY_LEFT &&
                        e.getKeyCode() != KeyCodes.KEY_RIGHT) {
                    char code = (char) e.getCharCode();
                    if (!allows.contains(code)) {
                        if (!isDecimal() || !decimals.contains(code)) {
                            e.stopPropagation();
                            e.preventDefault();
                            return;
                        }
                    }
                }
                break;
            case Event.ONKEYDOWN:
                if(e.getCtrlKey() && e.getKeyCode() == 86){//отменяем CTRL+v
                    //ловим только одно нажатие CTRL+v
                    if(ctrlvFixed){
                        e.stopPropagation();
                        e.preventDefault();
                    }else{
                        ctrlvFixed = true;
                    }
                }
                break;
            case Event.ONKEYUP:
                if(ctrlvFixed){//CTRL+v
                    checkInputValue();
                    ctrlvFixed = false;
                }
                if(isEnabled())
                    fireEvent(new KeyUpEvent(e));
                break;
        }
        super.onBrowserEvent(e);
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        sinkEvents(Event.ONKEYPRESS | Event.ONKEYDOWN | Event.ONKEYUP);
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getValue() {
        if(isAttached()){
            String strVal = getInputValue();
            if(strVal == null || strVal.isEmpty()){
                value = null;
            }else{
                strVal = strVal.replace(",",".");
                Double val = Double.valueOf(strVal);
                if(clazz == Double.class){
                    value = (T) val;
                }else if(clazz == Float.class){
                    if(val > Float.MAX_VALUE) {
                        value = (T) Float.valueOf(Float.MAX_VALUE);
                    } else {
                        value = (T) Float.valueOf(val.floatValue());
                    }
                }else if(clazz == Long.class){
                    value = (T) Long.valueOf(val.longValue());
                }else if(clazz == Integer.class){
                    if(val > Integer.MAX_VALUE){
                        value = (T) Integer.valueOf(Integer.MAX_VALUE);
                    } else {
                        value = (T) Integer.valueOf(strVal);
                    }
                }else{
                    value = null;
                }
            }
        }
        return super.getValue();
    }

    private void checkInputValue(){
        String strValue = getInputValue();
        String ret = "";
        for(Character c : strValue.toCharArray()){
            if(allows.contains(c) || (isDecimal() && decimals.contains(c))){
                ret += c;
            }
        }
        setInputValue(ret);
    }

    @Override
    public void setValue(T value) {
        if(isAttached()) {
            String strVal = "";
            if(value != null){
                if(clazz == Double.class || clazz == Float.class){
                    strVal = doubleFormat.format(value);
                }else{
                    strVal = String.valueOf(value);
                }
            }
            setInputValue(strVal);
        }
        super.setValue(value);
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize((width < 0)?width:width-2, (height < 0)?height:height-2);
    }

    @Override
    public HandlerRegistration addKeyUpHandler(KeyUpEvent.KeyUpHandler lnr){
        return addHandler(lnr, KeyUpEvent.getType());
    }

}
