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

package ru.fly.client.ui.field.checkbox;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import ru.fly.client.event.ValueChangeEvent;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.field.InputElementField;

/**
 * User: fil
 * Date: 13.08.13
 * Time: 22:59
 */
public class CheckBoxField extends InputElementField<Boolean> {

    public static final int ICON_SIZE_16 = 16;
    public static final int ICON_SIZE_24 = 24;
    public static final int ICON_SIZE_36 = 36;

    private final CheckBoxDecor decor = GWT.create(CheckBoxDecor.class);
    private String text;
    private ImageResource imgRes;
    private int iconSize = ICON_SIZE_16;

    public CheckBoxField(){
        setStyleName(decor.css().checkbox());
        setWidth(20);
    }

    @Override
    protected FElement createInputElement() {
        return DOM.createInputCheck().cast();
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        buildContent();
        setValue(super.getValue(), false);
    }

    @Override
    public boolean setValue(Boolean value, boolean fire) {
        if(isAttached())
            ((InputElement)getInputElement().cast()).setChecked((value == null)?false:value);
        return super.setValue(value, fire);
    }

    @Override
    public Boolean getValue() {
        if(isAttached())
            return ((InputElement)getInputElement().cast()).isChecked();
        else
            return super.getValue();
    }

    public void setText(String text){
        this.text = text;
        buildContent();
    }

    public void setIcon(ImageResource imgRes){
        this.imgRes = imgRes;
        buildContent();
    }

    public void setIconSize(int size){
        iconSize = size;
        if(imgRes != null){
            buildContent();
        }
    }

    private void buildContent(){
        if(!isAttached())
            return;
        setHeight(iconSize + 4);
        getElement().removeAll();
        getElement().appendChild(getInputElement());
        getInputElement().getStyle().setMarginTop((iconSize - 12) / 2 + 2, Style.Unit.PX);
        if(imgRes != null){
            ImageElement img = DOM.createImg().cast();
            img.setSrc(imgRes.getSafeUri().asString());
            img.setWidth(iconSize);
            img.setHeight(iconSize);
            getElement().appendChild(img);
        }
        if(text != null){
            LabelElement label = DOM.createLabel().cast();
            label.setInnerText(text == null ? "" : text);
            label.getStyle().setLineHeight(iconSize + 4, Style.Unit.PX);
            getElement().appendChild(label);
        }
        DOM.setEventListener(getInputElement(), new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if(!isEnabled())
                    event.preventDefault();
                else if(event.getTypeInt() == Event.ONCLICK){
                    setValue(getValue());
                    fireEvent(new ValueChangeEvent<Boolean>(getValue()));
                }
            }
        });
        DOM.sinkEvents(getInputElement(), Event.ONCLICK | Event.ONMOUSEDOWN);
    }
}
