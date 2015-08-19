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
import com.google.gwt.user.client.Event;

/**
 * User: fil
 * Date: 05.08.13
 * Time: 23:52
 */
public class KeyEnterEvent extends GwtEvent<KeyEnterEvent.KeyEnterHandler> {

    private static Type<KeyEnterHandler> TYPE;

    private Event nativeEvent;

    public KeyEnterEvent(Event nativeEvent){
        this.nativeEvent = nativeEvent;
    }

    public static Type<KeyEnterHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<KeyEnterHandler>();
        }
        return TYPE;
    }

    @Override
    public Type<KeyEnterHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(KeyEnterHandler handler) {
        handler.onKeyEnter(nativeEvent);
    }

    public interface HasKeyEnterHandler{
        HandlerRegistration addKeyEnterHandler(KeyEnterHandler h);
    }

    public interface KeyEnterHandler extends EventHandler {
        void onKeyEnter(Event e);
    }
}
