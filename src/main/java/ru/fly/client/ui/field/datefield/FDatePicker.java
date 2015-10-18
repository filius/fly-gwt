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
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.datepicker.client.DatePicker;
import ru.fly.client.event.ClickEvent;
import ru.fly.client.event.ValueChangeEvent;
import ru.fly.client.ui.button.Button;
import ru.fly.client.ui.field.datefield.decor.DateFieldDecor;
import ru.fly.client.ui.panel.FlowLayout;

import java.util.Date;

/**
 * User: fil
 * Date: 25.01.14
 * Time: 0:30
 */
public class FDatePicker extends FlowLayout implements ValueChangeEvent.HasValueChangeHandler<Date> {

    private final DatePicker dp;

    public FDatePicker(){
        this(GWT.<DateFieldDecor>create(DateFieldDecor.class));
    }

    public FDatePicker(DateFieldDecor decor){
        setStyleName(decor.css().datePicker());
        dp = new DatePicker();
        dp.addValueChangeHandler(new ValueChangeHandler<Date>() {
            @Override
            public void onValueChange(com.google.gwt.event.logical.shared.ValueChangeEvent<Date> event) {
                FDatePicker.this.fireEvent(new ValueChangeEvent<Date>(event.getValue()));
            }
        });
        add(dp);
        add(new Button("сегодня", new ClickEvent.ClickHandler() {
            @Override
            public void onClick() {
                setValue(new Date(), true);
            }
        }));
        add(new Button("очистить", new ClickEvent.ClickHandler() {
            @Override
            public void onClick() {
                setValue(null, true);
            }
        }));
    }

    public void setValue(Date value){
        setValue(value, true);
    }

    public void setValue(Date value, boolean fire){
        dp.setCurrentMonth(value == null ? new Date() : value);
        dp.setValue(value, fire);
    }

    public Date getValue(){
        return dp.getValue();
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeEvent.ValueChangeHandler<Date> h) {
        return addHandler(h, ValueChangeEvent.<Date>getType());
    }
}
