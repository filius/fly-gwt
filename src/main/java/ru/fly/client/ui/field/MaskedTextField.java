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
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.impl.TextBoxImpl;
import ru.fly.client.ui.validator.Validator;

import java.util.HashMap;
import java.util.Map;

/**
 * User: fil
 * Date: 15.01.14
 * Time: 12:22
 */
public class MaskedTextField extends TextField {

    private static TextBoxImpl impl = GWT.create(TextBoxImpl.class);
    private final Map<Character, RegExp> allows = new HashMap<Character, RegExp>(){{
        put('9',RegExp.compile("[0-9]"));
        put('a',RegExp.compile("[A-Za-zА-Яа-я]"));
        put('*',RegExp.compile("[A-Za-zА-Яа-я0-9]"));
    }};

    private String maskFormat = null;
    private String out;
    private boolean seekToEnd = false;
    private int selectionLength = 0;
    private boolean ctrlvFixed = false;

    public MaskedTextField(){
        addValidator(new Validator<String>() {
            @Override
            public String validate(String obj) {
                if(getInputValue() == null || isEmptyMask(getInputValue()) || !getInputValue().contains("_"))
                    return null;
                else
                    return "Поле заполнено неверно, необходимый формат "+maskFormat;
            }
        });
    }

    private String getEmptyMask(){
        return maskFormat.replace("9", "_").replace("a", "_").replace("*", "_");
    }

    public void setMaskFormat(String mask){
        clear();
        maskFormat = mask;
        out = getEmptyMask();
        setValue(getValue());
    }

    @Override
    public void setValue(String value) {
        if(value == null && maskFormat != null){
            out = getEmptyMask();
        }else{
            out = value;
        }
        super.setValue(value);
        ((InputElement)getInputElement().cast()).setValue(out);
    }

    @Override
    public String getValue() {
        String val = super.getValue();
        if(val != null && getEmptyMask().equals(val))
            return null;
        else
            return val;
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        sinkEvents(Event.ONKEYPRESS | Event.ONKEYDOWN | Event.ONMOUSEDOWN | Event.ONMOUSEUP);
        ((InputElement)getInputElement().cast()).setValue(out);
    }

    @Override
    public void onBrowserEvent(Event e) {
        switch(e.getTypeInt()){
            case Event.ONMOUSEDOWN:
                if(!isFocused())
                    seekToEnd = true;
                break;
            case Event.ONMOUSEUP:
                int pos = impl.getCursorPos(getInputElement());
                if(seekToEnd){
                    if(pos == out.length()){
                        pos = seekToEnd();
                        if(pos != -1)
                            impl.setSelectionRange(getInputElement(),pos,0);
                    }
                    seekToEnd = false;
                }
                break;
            case Event.ONKEYPRESS:
                if(e.getKeyCode() != KeyCodes.KEY_LEFT && e.getKeyCode() != KeyCodes.KEY_RIGHT) {
                    char code = (char) e.getCharCode();
                    onInput(code, impl.getCursorPos(getInputElement()));
                    e.stopPropagation();
                    e.preventDefault();
                }
                break;
            case Event.ONKEYDOWN:
                selectionLength = impl.getSelectionLength(getInputElement());
                if(e.getKeyCode() == KeyCodes.KEY_BACKSPACE){
                    if(selectionLength == out.length())
                        erasePrev(Integer.MAX_VALUE);
                    else
                        erasePrev(impl.getCursorPos(getInputElement())-1);
                    e.stopPropagation();
                    e.preventDefault();
                }else if(e.getKeyCode() == KeyCodes.KEY_DELETE){
                    onInput('_', impl.getCursorPos(getInputElement()));
                    e.stopPropagation();
                    e.preventDefault();
                }else if(e.getCtrlKey() && e.getKeyCode() == 86){//отменяем CTRL+v
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
                    ctrlv(impl.getCursorPos(getInputElement()));
                    ctrlvFixed = false;
                }
                break;
        }
        super.onBrowserEvent(e);
    }

    private boolean isEmptyMask(String val){
        for(int i=0; i<val.length(); i++){
            if(out.charAt(i) != '_' && maskFormat.charAt(i) == '9'){
                return false;
            }
        }
        return true;
    }

    private int onInput(char c, int pos){
        int newPos = -1;
        if(pos > -1){
            newPos = getValidPositionOrNext(c, pos);
            if(newPos != -1){
                out = out.substring(0,pos)+c+out.substring(pos+1);
                newPos = getValidPositionOrNext(pos + 1);
            }
        }
        ((InputElement)getInputElement().cast()).setValue(out);
        impl.setSelectionRange(getInputElement(),newPos > -1 ? newPos : pos,0);
        return pos+1;
    }

    private int getValidPositionOrNext(char c, int pos){
        while(pos != -1){
            if(pos < maskFormat.length()){
                char posChar = maskFormat.charAt(pos);
                if(posChar == '9' || posChar == 'a' || posChar == '*'){
                    if(c == '_' || allows.get(posChar).test(String.valueOf(c)))
                        return pos;
                    else
                        pos = -1;
                }else{
                    pos++;
                }
            }else
                pos = -1;
        }
        return pos;
    }

    private int getValidPositionOrNext(int pos){
        while(pos != -1){
            if(pos < maskFormat.length()){
                char posChar = maskFormat.charAt(pos);
                if(posChar == '9' || posChar == 'a' || posChar == '*'){
                    return pos;
                }else{
                    pos++;
                }
            }else
                pos = -1;
        }
        return pos;
    }

    private void erasePrev(int pos){
        if(pos == Integer.MAX_VALUE){
            out = getEmptyMask();
            pos = 0;
        }else if(pos != -1){
            pos = getValidPositionOrPrev(pos);
            if(pos != -1){
                out = out.substring(0,pos)+"_"+out.substring(pos+1);
            }
        }
        ((InputElement)getInputElement().cast()).setValue(out);
        if(pos == -1)
            pos = 0;
        impl.setSelectionRange(getInputElement(), pos, 0);
    }

    private int getValidPositionOrPrev(int pos){
        while(pos != -1){
            if(pos > -1){
                char posChar = maskFormat.charAt(pos);
                if(posChar == '9' || posChar == 'a' || posChar == '*'){
                    return pos;
                }else{
                    pos--;
                }
            }
        }
        return pos;
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
                pos = onInput(c,pos);
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
