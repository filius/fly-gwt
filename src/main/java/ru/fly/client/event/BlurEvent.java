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
 * Time: 11:04
 */
public class BlurEvent extends GwtEvent<BlurEvent.BlurHandler> {

    private static Type<BlurHandler> TYPE;

    public BlurEvent(){
    }

    public static Type<BlurHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<BlurHandler>();
        }
        return TYPE;
    }

    @Override
    public Type<BlurHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(BlurHandler handler) {
        handler.onBlur();
    }

    public interface BlurHandler extends EventHandler {
        void onBlur();
    }

    public interface HasBlurHandler extends EventHandler {
        HandlerRegistration addBlurHandler(BlurHandler h);
    }
}
