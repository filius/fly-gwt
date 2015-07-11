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

package ru.fly.client.event;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * User: fil
 * Date: 09.12.13
 * Time: 11:06
 */
public class ValueChangeEvent<T> extends GwtEvent<ValueChangeEvent.ValueChangeHandler<T>> {

    private static Type TYPE;

    private T object;

    public ValueChangeEvent(T object){
        this.object = object;
    }

    @SuppressWarnings("unchecked")
    public static <T> Type<ValueChangeHandler<T>> getType() {
        if (TYPE == null) {
            TYPE = new Type<>();
        }
        return TYPE;
    }

    @Override
    public Type<ValueChangeHandler<T>> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(ValueChangeHandler<T> handler) {
        handler.onValueChange(object);
    }

    public interface ValueChangeHandler<T> extends EventHandler {
        void onValueChange(T object);
    }

    public interface HasValueChangeHandler<T> {
        HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> h);
    }

}