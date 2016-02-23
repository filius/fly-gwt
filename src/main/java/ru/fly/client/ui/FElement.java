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

package ru.fly.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;

/**
 * User: fil
 * Date: 12.08.13
 * Time: 17:53
 * TODO Element тут устарелый, но он необходим для совместимости с getElement из UIObject
 */
public class FElement extends com.google.gwt.user.client.Element {

    protected FElement() {
    }

    public final void removeAll(){
        while(hasChildNodes()){
            removeChild(getFirstChild());
        }
    }

    public final int getHeight(){
        return getHeight(false);
    }

    public final int getHeight(boolean client){
        return (client)?getClientHeight():getOffsetHeight();
    }

    public final void setPixelSize(int width, int height){
        setWidth(width);
        setHeight(height);
    }

    public final void setWidth(int width){
        getStyle().setWidth(width < 0 ? 0 : width, Style.Unit.PX);
    }

    public final int getWidth(){
        return getWidth(false);
    }

    public final int getWidth(boolean client){
        return (client)?getClientWidth():getOffsetWidth();
    }

    public final void setLineHeight(int height){
        getStyle().setLineHeight(height, Style.Unit.PX);
    }

    public final void setHeight(int height){
        getStyle().setHeight(height < 0 ? 0 : height, Style.Unit.PX);
    }

    public final void clearHeight() {
        getStyle().clearHeight();
    }

    public final void setMargin(int val){
        getStyle().setMargin(val, Style.Unit.PX);
    }

    public final void setMarginTop(int val){
        getStyle().setMarginTop(val, Style.Unit.PX);
    }

    public final void setMarginRight(int val){
        getStyle().setMarginRight(val, Style.Unit.PX);
    }

    public final void setMarginBottom(int val){
        getStyle().setMarginBottom(val, Style.Unit.PX);
    }

    public final void setMarginLeft(int val){
        getStyle().setMarginLeft(val, Style.Unit.PX);
    }

    public final void setTop(int top){
        getStyle().setTop(top, Style.Unit.PX);
    }

    public final void clearTop(){
        getStyle().clearTop();
    }

    public final void setBottom(int bottom){
        getStyle().setBottom(bottom, Style.Unit.PX);
    }

    public final void clearBottom(){
        getStyle().clearBottom();
    }

    public final void setLeft(int left){
        getStyle().setLeft(left, Style.Unit.PX);
    }

    public final void clearLeft(){
        getStyle().clearLeft();
    }

    public final void setRight(int right){
        getStyle().setRight(right, Style.Unit.PX);
    }

    public final void clearRight(){
        getStyle().clearRight();
    }

    public final void setPosition(int left, int top){
        setLeft(left);
        setTop(top);
    }

    public final void setPadding(int padding){
        getStyle().setPadding(padding, Style.Unit.PX);
    }

    public final void setPaddingTop(int padding){
        getStyle().setPaddingTop(padding, Style.Unit.PX);
    }

    public final void setPaddingRight(int padding){
        getStyle().setPaddingRight(padding, Style.Unit.PX);
    }

    public final void setPaddingBottom(int padding){
        getStyle().setPaddingBottom(padding, Style.Unit.PX);
    }

    public final void setPaddingLeft(int padding){
        getStyle().setPaddingLeft(padding, Style.Unit.PX);
    }

    public final int getRelativeLeft(Element relative){
        int left = getOffsetLeft();
        Element parent = getOffsetParent();
        while(parent != null && parent != relative){
            left += parent.getOffsetLeft();
            parent = parent.getOffsetParent();
        }
        return left;
    }

    public final int getRelativeTop(Element relative){
        int top = getOffsetTop();
        Element parent = getOffsetParent();
        while(parent != null && parent != relative){
            top += parent.getOffsetTop();
            parent = parent.getOffsetParent();
        }
        return top;
    }

    public final void listenOver(final String className){
        final EventListener oldLnr = DOM.getEventListener(this);
        DOM.setEventListener(this, new EventListener(){
            @Override
            public void onBrowserEvent(Event event) {
                if(oldLnr != null)
                    oldLnr.onBrowserEvent(event);
                switch(event.getTypeInt()){
                    case Event.ONMOUSEOVER:
                        addClassName(className);
                        break;
                    case Event.ONMOUSEOUT:
                        removeClassName(className);
                        break;
                }
            }
        });
        DOM.sinkEvents(this, DOM.getEventsSunk(this) | Event.ONMOUSEOVER | Event.ONMOUSEOUT);
    }

}
