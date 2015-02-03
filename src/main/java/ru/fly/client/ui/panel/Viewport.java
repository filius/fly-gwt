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

package ru.fly.client.ui.panel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.util.LastPassExecutor;

/**
 * User: fil
 * Date: 03.09.13
 * Time: 23:02
 */
public class Viewport extends SingleLayout {

    private int minWidth = -1;
    private LastPassExecutor resizeExec = new LastPassExecutor() {
        @Override
        protected void exec(Object param) {
            ResizeEvent ev = (ResizeEvent)param;
            setPixelSize(ev.getWidth(), ev.getHeight());
            layout(false);
        }
    };

    public Viewport(){
        getStyle().setOverflow(Style.Overflow.HIDDEN);
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                resizeExec.pass(event);
            }
        });
    }

    public void setMinWidth(int minWidth){
        this.minWidth = minWidth;
    }

    @Override
    protected void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        setPixelSize(Window.getClientWidth(), Window.getClientHeight());
        // wait complete loading all resources and recalculate layout
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                layout(true);
            }
        });
    }

    @Override
    public void layout(boolean force) {
        if(isAttached()){
            int w = getWidth(true);
            int h = getHeight(true);
            Widget child = getWidget();
            if(child != null){
                child.setPixelSize(w, h);
            }
        }
        super.layout(force);
    }

    @Override
    public void setPixelSize(int width, int height) {
        if(minWidth != -1 && width < minWidth){
            width = minWidth;
            getParent().getElement().getStyle().setOverflowX(Style.Overflow.SCROLL);
        }else{
            getParent().getElement().getStyle().setOverflow(Style.Overflow.HIDDEN);
        }
        getParent().setPixelSize(width, height);
        super.setPixelSize(width, height);
    }
}
