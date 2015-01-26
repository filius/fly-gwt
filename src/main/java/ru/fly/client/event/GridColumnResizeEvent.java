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
import ru.fly.client.ui.grid.ColumnConfig;

/**
 * User: fil
 * Date: 13.08.14
 */
public class GridColumnResizeEvent extends GwtEvent<GridColumnResizeEvent.GridColumnResizeHandler> {

    private static Type<GridColumnResizeHandler> TYPE;

    private ColumnConfig config;

    public GridColumnResizeEvent(ColumnConfig config){
        this.config = config;
    }

    public static Type<GridColumnResizeHandler> getType() {
        if (TYPE == null) {
            TYPE = new Type<GridColumnResizeHandler>();
        }
        return TYPE;
    }

    @Override
    public Type<GridColumnResizeHandler> getAssociatedType() {
        return getType();
    }

    @Override
    protected void dispatch(GridColumnResizeHandler handler) {
        handler.onResize(config);
    }

    public static interface GridColumnResizeHandler extends EventHandler {
        void onResize(ColumnConfig config);
    }

    public static interface HasGridColumnResizeHandler{
        HandlerRegistration addGridColumnResizeHandler(GridColumnResizeHandler h);
    }

}
