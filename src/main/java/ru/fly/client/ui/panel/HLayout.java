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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.EndCallback;
import ru.fly.client.dnd.Dragger;
import ru.fly.client.dnd.Point;
import ru.fly.client.dnd.Rect;
import ru.fly.client.ui.FElement;
import ru.fly.shared.FlyException;

import java.util.ArrayList;
import java.util.List;

/**
 * User: fil
 * Date: 03.09.13
 * Time: 23:52
 */
public class HLayout extends LayoutContainer {

    private final LayoutDecor decor = GWT.create(LayoutDecor.class);
    private List<FElement> splits = new ArrayList<FElement>();

    public HLayout() {
        super(DOM.createDiv());
        getStyle().setPosition(Style.Position.RELATIVE);
    }

    @Override
    protected void doLayout() {
        int w = getWidth(true);
        int h = getHeight(true);
        int freeW = w;
        for(Widget child : getWidgets()){
            Object ld = child.getLayoutData();
            if(ld != null && ld instanceof VHLayoutData){
                VHLayoutData vld = (VHLayoutData) ld;
                if(vld.getW() < 0){
                    freeW -= child.getOffsetWidth();
                }else if(vld.getW() > 1){
                    freeW -= vld.getW();
                    Margin m = vld.getMargin();
                    if(m != null){
                        freeW = freeW - m.getLeft() - m.getRight();
                    }
                }
            }else{
                freeW -= child.getOffsetHeight();
            }
        }
        int left = 0;
        for(Widget child : getWidgets()){
            FElement el = child.getElement().cast();
            el.getStyle().setPosition(Style.Position.ABSOLUTE);
            el.getStyle().setMargin(0, Style.Unit.PX);
            Object ld = child.getLayoutData();
            if(ld != null && ld instanceof VHLayoutData){
                VHLayoutData vld = (VHLayoutData) ld;
                int cw = vld.getChildWidth(freeW);
                int ch = vld.getChildHeight(h);
                child.setPixelSize(cw, ch);
                el.setTop(vld.getMargin() == null ? 0 : vld.getMargin().getTop());
                left += vld.getMargin() == null ? 0 : vld.getMargin().getLeft();
                el.setLeft(left);
                left += vld.getMargin() == null ? 0 : vld.getMargin().getRight();
                if(cw < 0){
                    left += child.getOffsetWidth();
                }else{
                    left += cw;
                }
            }else{
                left += child.getOffsetWidth();
            }
        }
        rebuildSplits();
    }

    public void add(Widget w, VHLayoutData data){
        w.setLayoutData(data);
        super.add(w);
    }

    public void rebuildSplits(){
        for(FElement split : splits){
            split.removeFromParent();
        }
        splits.clear();
        for(final Widget child : getWidgets()){
            final Object ld = child.getLayoutData();
            if(ld != null && ld instanceof VHLayoutData && ((VHLayoutData) ld).isResizable()){
                final FElement splitEl = DOM.createDiv().cast();
                splitEl.setClassName(decor.css().split());
                splitEl.setLeft(child.getElement().getOffsetLeft()+child.getOffsetWidth() - 2);
                splits.add(splitEl);
                getElement().appendChild(splitEl);
                DOM.setEventListener(splitEl, new EventListener() {
                    @Override
                    public void onBrowserEvent(Event event) {
                        if(event.getTypeInt() == Event.ONMOUSEDOWN){
                            final FElement dragLine = DOM.createDiv().cast();
                            dragLine.setClassName(decor.css().dragLine());
                            getElement().appendChild(dragLine);
                            dragLine.setLeft(splitEl.getOffsetLeft());
                            new Dragger(dragLine, event, new EndCallback<Point>() {
                                @Override
                                public void onEnd(Point result) {
                                    dragLine.removeFromParent();
                                    doDnDResize(child, result.x);
                                }
                            }).setLockY(true).setBoundingRect(getBoundRect((VHLayoutData)ld, (FElement)child.getElement()));
                            event.stopPropagation();
                        }
                    }
                });
                DOM.sinkEvents(splitEl, Event.ONMOUSEDOWN);
            }
        }
    }

    public void doDnDResize(Widget child, int dx){
        //primary child
        Object ld = child.getLayoutData();
        double w = child.getElement().getOffsetWidth()+dx;
        if(ld != null){
            if(w < ((VHLayoutData)ld).getMinSize()){
                w = ((VHLayoutData)ld).getMinSize();
            }
            if(w > ((VHLayoutData)ld).getMaxSize()){
                w = ((VHLayoutData)ld).getMaxSize();
            }
        }
        ((VHLayoutData)child.getLayoutData()).setW(w);
        //next child
        int idx = getWidgets().indexOf(child);
        if(idx+1 < getWidgets().size()) {
            Widget nextChild = getWidgets().get(idx+1);
            ld = nextChild.getLayoutData();
            if(ld != null && ld instanceof VHLayoutData){
                ((VHLayoutData) ld).setW(nextChild.getOffsetWidth()-dx);
            }else{
                ((FElement)nextChild.getElement()).setWidth(nextChild.getOffsetWidth()-dx);
            }
        }
        layout(true);
    }

    private Rect getBoundRect(VHLayoutData ld, FElement el){
        if (ld.getMinSize() != -1 && ld.getMinSize() > el.getOffsetWidth()) {
            throw new FlyException("VHLayoutData.minSize must be less than width");
        }
        if (ld.getMaxSize() != -1 && ld.getMaxSize() < el.getOffsetWidth()) {
            throw new FlyException("VHLayoutData.maxSize must be greater than width");
        }
        if (ld.getMinSize() != -1 && ld.getMaxSize() != -1 && ld.getMaxSize() < ld.getMinSize()) {
            throw new FlyException("VHLayoutData.maxSize must be greater than VHLayoutData.minSize");
        }
        if(ld.getMinSize() < 0){
            ld.setMinMaxSize(el.getWidth(), ld.getMaxSize());
        }
        if(ld.getMaxSize() < 0){
            ld.setMinMaxSize(ld.getMinSize(),el.getWidth());
        }
        return new Rect((int)(el.getAbsoluteLeft()+ld.getMinSize()),getElement().getAbsoluteTop(),
                (int)(el.getAbsoluteLeft() + ld.getMaxSize()),getElement().getAbsoluteBottom());
    }

}
