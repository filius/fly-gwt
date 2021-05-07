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
 * @author fil
 */
public class UpdateEvent extends GwtEvent<UpdateEvent.UpdateHandler> {

    private static Type<UpdateHandler> TYPE;

    public UpdateEvent(){
    }

    public static Type<UpdateHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<>();
        }
        return TYPE;
    }

    @Override
    public Type<UpdateHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(UpdateHandler handler) {
        handler.onUpdate();
    }

    public interface UpdateHandler extends EventHandler {
        void onUpdate();
    }

    public interface HasUpdateHandler {
        HandlerRegistration addUpdateHandler(UpdateHandler h);
    }
}
