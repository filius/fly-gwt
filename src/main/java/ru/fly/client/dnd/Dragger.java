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

package ru.fly.client.dnd;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.RootPanel;
import ru.fly.client.EndCallback;
import ru.fly.client.F;
import ru.fly.client.ui.FElement;

/**
 * User: fil
 * Date: 12.08.14
 */
public class Dragger {

    private HandlerRegistration handlerRegistration;
    private int ox;
    private int oy;
    private int ax;
    private int ay;
    private int ex;
    private int ey;

    private boolean lockX = false;
    private boolean lockY = false;
    private Rect boundingRect;

    public Dragger(final FElement dragEl, Event event, final EndCallback<Point> cback){
        ox = dragEl.getOffsetLeft();
        oy = dragEl.getOffsetTop();
        ax = dragEl.getAbsoluteLeft();
        ay = dragEl.getAbsoluteTop();
        ex = event.getClientX();
        ey = event.getClientY();

        F.setEnableTextSelection(RootPanel.getBodyElement(), false);

        handlerRegistration = Event.addNativePreviewHandler(new Event.NativePreviewHandler() {
            @Override
            public void onPreviewNativeEvent(Event.NativePreviewEvent event) {
                switch(event.getTypeInt()){
                    case Event.ONMOUSEMOVE:
                        if(!lockX) {
                            int dx = event.getNativeEvent().getClientX() - ex;
                            if(boundingRect != null){
                                int lx = (ax + dx) - boundingRect.p1.x;
                                if(lx < 0){
                                    dx -= lx;
                                }
                                int rx = boundingRect.p2.x - (ax + dx + dragEl.getWidth());
                                if(rx < 0){
                                    dx += rx;
                                }
                            }
                            dragEl.setLeft(ox + dx);
                        }
                        if(!lockY) {
                            int dy = event.getNativeEvent().getClientY() - ey;
                            dragEl.setTop(oy + dy);
                        }
                        break;
                    case Event.ONMOUSEUP:
                        handlerRegistration.removeHandler();
                        F.setEnableTextSelection(RootPanel.getBodyElement(), true);
                        if(cback != null){
                            cback.onEnd(new Point(dragEl.getOffsetLeft() - ox, dragEl.getOffsetTop() - oy));
                        }
                        break;
                }
            }
        });
    }

    public Dragger setLockX(boolean val){
        lockX = val;
        return this;
    }

    public Dragger setLockY(boolean val){
        lockY = val;
        return this;
    }

    public Dragger setBoundingRect(Rect rect){
        boundingRect = rect;
        return this;
    }

}
