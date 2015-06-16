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
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.RootPanel;
import ru.fly.client.event.ChangeEvent;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.field.TriggerField;
import ru.fly.client.ui.field.datefield.decor.DateFieldDecor;

import java.util.Date;

/**
 * User: fil
 * Date: 06.08.13
 * Time: 23:05
 */
public class DatePickerField extends TriggerField<Date> {

    private final DateFieldDecor decor = GWT.create(DateFieldDecor.class);

    private FDatePicker datePicker;
    private DateTimeFormat format;

    public DatePickerField(){
        this(DateTimeFormat.getFormat("dd.MM.yyyy"));
    }

    public DatePickerField(DateTimeFormat format) {
        this.format = format;
        addStyleName(decor.css().dateField());
        setWidth(100);
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        view.addClassName(decor.css().dateFieldView());
        DOM.setEventListener(view, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                expander.expandCollapse();
            }
        });
        DOM.sinkEvents(view, Event.ONCLICK);
    }

    @Override
    protected FElement buildTriggerElement() {
        FElement ret = DOM.createDiv().cast();
        ret.addClassName(decor.css().dateFieldTrigger());
        FElement trIcon = DOM.createDiv().cast();
        ret.appendChild(trIcon);
        trIcon.setClassName(decor.css().dateFieldTriggerIcon());
        return ret;
    }

    @Override
    protected FElement getExpandedElement() {
        return getDatePicker().getElement().cast();
    }

    protected void onExpand(){
        int left = getAbsoluteLeft()+getWidth()-130;
        int wndViewHeight = Window.getClientHeight()+Window.getScrollTop();
        getDatePicker().getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
        getDatePicker().getElement().getStyle().setZIndex(10000);
        RootPanel.get().add(getDatePicker());
        if(getElement().getAbsoluteTop() + getHeight() + 170 > wndViewHeight){
            ((FElement)getDatePicker().getElement()).setPosition(left, getAbsoluteTop() - 170);
        }else{
            ((FElement)getDatePicker().getElement()).setPosition(left, getAbsoluteTop() + getHeight() + 2);
        }
        getDatePicker().setValue(getValue());
    }

    private FDatePicker getDatePicker(){
        if(datePicker == null){
            datePicker = new FDatePicker(){

            };
            datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
                @Override
                public void onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent<Date> event) {
                    DatePickerField.this.setValue(datePicker.getValue());
                    expander.collapse();
                }
            });
        }
        return datePicker;
    }

    protected void onCollapse(){
        RootPanel.get().remove(getDatePicker());
    }

    @Override
    public void setValue(Date value) {
        Date old = getValue();
        super.setValue(value);
        if(view != null){
            if(value == null)
                view.setInnerHTML("");
            else
                view.setInnerHTML(format.format(value));
        }
        if((old != null && !old.equals(value)) || (value != null && !value.equals(old))){
            fireEvent(new ChangeEvent<Date>(value));
        }
    }

}
