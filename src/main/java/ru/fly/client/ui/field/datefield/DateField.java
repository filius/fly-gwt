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

package ru.fly.client.ui.field.datefield;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.impl.TextBoxImpl;
import ru.fly.client.event.KeyUpEvent;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.field.InputElementField;
import ru.fly.client.ui.field.TextFieldDecor;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * User: fil
 * Date: 08.01.14
 * Time: 15:43
 */
public class DateField extends InputElementField<Date> implements KeyUpEvent.HasKeyUpHandler {

    private static final String EMPTY_MASK = "__.__.____";
    private static final String DATE_FORMAT_ERROR_MSG = "Формат даты должен быть ДД.ММ.УУУУ";
    private static TextBoxImpl impl = GWT.create(TextBoxImpl.class);

    private final List<Character> allows = Arrays.asList('0', '1', '2', '3', '4', '5', '6', '7', '8', '9');
    private final DateTimeFormat fmt = DateTimeFormat.getFormat("dd.MM.yyy") ;
    private final String maskFormat = "99.99.9999";
    private String out;
    private boolean seekToEnd = false;
    private int selectionLength = 0;
    private boolean ctrlvFixed = false;

    public DateField(){
        TextFieldDecor decor = GWT.create(TextFieldDecor.class);
        addStyleName(decor.css().textField());
        setHeight(24);
    }

    @Override
    public boolean validate() {
        //TODO переделать это как MaskedTextBox
        boolean ret = true;
        String strVal = getInputValue();
        if(!isEmptyMask(strVal)){
            try{
                value = fmt.parse(strVal);
            }catch (Exception ignored){
                setError(DATE_FORMAT_ERROR_MSG);
                ret = false;
            }
        }
        return ret && super.validate();
    }

    @Override
    protected FElement createInputElement() {
        return DOM.createInputText().cast();
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getElement().appendChild(getInputElement());
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        sinkEvents(Event.ONKEYPRESS | Event.ONKEYDOWN | Event.ONKEYUP | Event.ONMOUSEDOWN | Event.ONMOUSEUP);
        setValue(super.getValue());
    }

    @Override
    public void onBrowserEvent(Event e) {
        if(e.getTypeInt() == Event.ONMOUSEDOWN){
            if(!isFocused())
                seekToEnd = true;
        }else if(e.getTypeInt() == Event.ONMOUSEUP){
            int pos = impl.getCursorPos(getInputElement());
            if(seekToEnd){
                if(pos == out.length()){
                    pos = seekToEnd();
                    if(pos != -1)
                        impl.setSelectionRange(getInputElement(),pos,0);
                }
                seekToEnd = false;
            }
        }else if(e.getTypeInt() == Event.ONKEYPRESS){
            char code = (char) e.getCharCode();
            if(allows.contains(code)){
                onInput(code, impl.getCursorPos(getInputElement()));
            }
            if(e.getKeyCode() != KeyCodes.KEY_LEFT && e.getKeyCode() != KeyCodes.KEY_RIGHT
                    && e.getKeyCode() != KeyCodes.KEY_TAB){
                e.stopPropagation();
                e.preventDefault();
            }
            return;
        }else if(e.getTypeInt() == Event.ONKEYDOWN){
            selectionLength = impl.getSelectionLength(getInputElement());
            if(e.getKeyCode() == KeyCodes.KEY_BACKSPACE){
                if(selectionLength == out.length())
                    erasePrev(Integer.MAX_VALUE);
                else
                    erasePrev(impl.getCursorPos(getInputElement())-1);
                e.stopPropagation();
                e.preventDefault();
                return;
            }else if(e.getKeyCode() == KeyCodes.KEY_DELETE){
                onInput('_', impl.getCursorPos(getInputElement()));
                e.stopPropagation();
                e.preventDefault();
                return;
            }else if(e.getCtrlKey() && e.getKeyCode() == 86){//отменяем CTRL+v
                //ловим только одно нажатие CTRL+v
                if(ctrlvFixed){
                    e.stopPropagation();
                    e.preventDefault();
                }else{
                    ctrlvFixed = true;
                }
            }
        }else if(e.getTypeInt() == Event.ONKEYUP){
            if(isEnabled() &&
                    e.getKeyCode() != KeyCodes.KEY_HOME &&
                    e.getKeyCode() != KeyCodes.KEY_LEFT &&
                    e.getKeyCode() != KeyCodes.KEY_RIGHT){
                fireEvent(new KeyUpEvent(e));
                if(ctrlvFixed){//CTRL+v
                    ctrlvFixed = false;
                    ctrlv(impl.getCursorPos(getInputElement()));
                }
            }
        }
        super.onBrowserEvent(e);
    }

    @Override
    public boolean setValue(Date value, boolean fire) {
        boolean ret = super.setValue(value, fire);
        if(isAttached()){
            out = (value == null) ? EMPTY_MASK : fmt.format(value);
            printMask(out);
        }
        return ret;
    }

    @Override
    public Date getValue() {
        if(isAttached()){
            clearError();
            String strVal = ((InputElement)getInputElement().cast()).getValue();
            if(isEmptyMask(strVal))
                value = null;
            else{
                try{
                    value = fmt.parse(strVal);
                    ((InputElement)getInputElement().cast()).setValue(fmt.format(value));
                }catch (Exception ignored){
                    value = null;
                    setError(DATE_FORMAT_ERROR_MSG);
                }
            }
        }
        return super.getValue();
    }
    
    private boolean isEmptyMask(String val){
        for(int i=0; i<val.length(); i++){
            if(out.charAt(i) != '_' && maskFormat.charAt(i) == '9'){
                return false;
            }
        }
        return true;
    }

    private String normalizeDate(String val){
        String day = val.substring(0,2);
        if(!day.contains("_")){
            int iDay = Integer.valueOf(day);
            if(iDay > 31) day = "31";
            if(iDay < 1) day = "01";
        }
        String month = val.substring(3,5);
        if(!month.contains("_")){
            int iMonth = Integer.valueOf(month);
            if(iMonth > 12) month = "12";
            if(iMonth < 1) month = "01";
        }
        return day + "." + month + val.substring(5, 10);
    }

    private void erasePrev(int pos){
        if(pos == Integer.MAX_VALUE){
            out = EMPTY_MASK;
            pos = 0;
        }else if(pos != -1){
            pos = getValidPositionOrPrev(pos);
            if(pos != -1){
                out = out.substring(0,pos)+"_"+out.substring(pos+1);
            }
        }
        printMask(out);
        if(pos == -1)
            pos = 0;
        impl.setSelectionRange(getInputElement(), pos, 0);
    }

    private int onInput(char c, int pos){
        if(pos != -1){
            pos = getValidPositionOrNext(pos);
            if(pos != -1){
                out = out.substring(0,pos)+c+out.substring(pos+1);
                pos = getValidPositionOrNext(pos + 1);
            }
        }
        printMask(out);
        if(pos != -1)
            impl.setSelectionRange(getInputElement(),pos,0);
        return pos;
    }

    private void printMask(String val){
        out = normalizeDate(val);
        ((InputElement)getInputElement().cast()).setValue(out);
    }

    private int getValidPositionOrNext(int pos){
        while(pos != -1){
            if(pos < maskFormat.length()){
                char posChar = maskFormat.charAt(pos);
                if(posChar == '9'){
                    return pos;
                }else{
                    pos++;
                }
            }else
                pos = -1;
        }
        return pos;
    }

    private int getValidPositionOrPrev(int pos){
        while(pos != -1){
            if(pos > -1){
                char posChar = maskFormat.charAt(pos);
                if(posChar == '9'){
                    return pos;
                }else{
                    pos--;
                }
            }
        }
        return pos;
    }

    public HandlerRegistration addKeyUpHandler(KeyUpEvent.KeyUpHandler h){
        return addHandler(h, KeyUpEvent.getType());
    }

    private int seekToEnd(){
        int pos = 0;
        while(pos < out.length()){
            char c = out.charAt(pos);
            if(c == '_'){
                return pos;
            }else{
                pos++;
            }
        }
        return -1;
    }

    private void ctrlv(int pos){
        if(selectionLength < 0)
            return;
        String val = getInputValue();
        int pastLength = val.length() + selectionLength - maskFormat.length();
        if(pastLength < 0){
            pastLength = 0;
        }
        pos -= pastLength;
        if(pos < 0){
            pos = 0;
        }
        String pastVal = val.substring(pos, pos+pastLength);
        if(pastLength > 0){
            for(char c : pastVal.toCharArray()){
                if(allows.contains(c)){
                    pos = onInput(c,pos);
                }
            }
        }else{
            ((InputElement)getInputElement().cast()).setValue(out);
        }
        if(pos == -1){
            pos = maskFormat.length();
        }
        impl.setSelectionRange(getInputElement(), pos, 0);
    }
}