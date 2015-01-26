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

import com.google.gwt.event.shared.GwtEvent;

/**
 * User: fil
 * Date: 05.08.13
 * Time: 23:52
 */
public class OrderChangeEvent extends GwtEvent<OrderChangeHandler> {

    private static Type<OrderChangeHandler> TYPE;

    private String orderField;
    private boolean asc;

    public OrderChangeEvent(String orderField, boolean asc){
        this.orderField = orderField;
        this.asc = asc;
    }

    public static Type<OrderChangeHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<OrderChangeHandler>();
        }
        return TYPE;
    }

    @Override
    public Type<OrderChangeHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(OrderChangeHandler handler) {
        handler.onChange(orderField, asc);
    }
}
