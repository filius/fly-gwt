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
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.RootPanel;
import ru.fly.client.event.ValueChangeEvent;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.field.TriggerController;
import ru.fly.client.ui.field.datefield.decor.DateFieldDecor;

import java.util.Date;

/**
 * User: fil
 * Date: 06.08.13
 * Time: 23:05
 */
public class DatePickerField extends DateField {

    private DateFieldDecor decor;
    private TriggerController triggerController;
    private FDatePicker datePicker;
    private FElement triggerElement;

    public DatePickerField() {
        this(GWT.<DateFieldDecor>create(DateFieldDecor.class));
    }

    public DatePickerField(DateFieldDecor decor){
        this.decor = decor;
        addStyleName(decor.css().dateField());
        setWidth(100);

        triggerElement = buildTriggerElement();
        triggerController = new TriggerController(this, triggerElement) {
            @Override
            protected FElement getExpandedElement() {
                return DatePickerField.this.getDatePicker().getElement().cast();
            }

            @Override
            public void onExpand() {
                DatePickerField.this.onExpand();
            }

            @Override
            public void onCollapse() {
                DatePickerField.this.onCollapse();
            }

            @Override
            public boolean isEnabled() {
                return DatePickerField.this.isEnabled();
            }
        };
    }

    public void setEditable(boolean editable){
        ((InputElement)getInputElement().cast()).setReadOnly(!editable);
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        getInputElement().addClassName(decor.css().dateFieldView());
        getElement().appendChild(triggerElement);

        if(((InputElement)getInputElement().cast()).isReadOnly()) {
            DOM.setEventListener(getInputElement(), new EventListener() {
                @Override
                public void onBrowserEvent(Event event) {
                    triggerController.expandCollapse();
                }
            });
            DOM.sinkEvents(getInputElement(), Event.ONCLICK);
        }
    }

    protected FElement buildTriggerElement() {
        FElement ret = DOM.createDiv().cast();
        ret.addClassName(decor.css().dateFieldTrigger());
        FElement trIcon = DOM.createDiv().cast();
        ret.appendChild(trIcon);
        trIcon.setClassName(decor.css().dateFieldTriggerIcon());
        return ret;
    }

    protected void onExpand(){
        int left = getAbsoluteLeft()+getWidth()-130;
        int wndViewHeight = Window.getClientHeight()+Window.getScrollTop();
        getDatePicker().getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        getDatePicker().getElement().getStyle().setZIndex(10000);
        RootPanel.get().add(getDatePicker());
        if(getElement().getAbsoluteTop() + getHeight() + 170 > wndViewHeight){
            getDatePicker().getElement().setPosition(left, getAbsoluteTop() - 170);
        }else{
            getDatePicker().getElement().setPosition(left, getAbsoluteTop() + getHeight() + 2);
        }
        getDatePicker().setValue(getValue(), false);
    }

    private FDatePicker getDatePicker(){
        if(datePicker == null){
            datePicker = new FDatePicker(decor);
            datePicker.addValueChangeHandler(new ValueChangeEvent.ValueChangeHandler<Date>() {
                @Override
                public void onValueChange(Date object) {
                    DatePickerField.this.setValue(object);
                    triggerController.collapse();
                }
            });
        }
        return datePicker;
    }

    protected void onCollapse(){
        RootPanel.get().remove(getDatePicker());
    }

    @Override
    public void setPixelSize(int width, int height) {
        super.setPixelSize(width, height);
        if(decor != null) {
            getInputElement().setWidth(width - decor.css().pTriggerWidth());
        }
    }

}
