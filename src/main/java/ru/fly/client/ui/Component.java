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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

import java.util.HashMap;
import java.util.Map;

/**
 * User: fil
 * Date: 05.08.13
 * Time: 23:05
 */
public class Component extends Widget{

    private final ComponentDecor decor = GWT.create(ComponentDecor.class);

    private Map<String, Object> data;
    private boolean firstAttached = false;
    private boolean disabled = false;
    protected int width;
    protected int height;
    private FElement maskEl;

    public Component(Element el){
        setElement(el);
    }

    protected void onAfterFirstAttach(){
        if(disabled)
            disable();
    }

    public FElement getElement() {
        return super.getElement().cast();
    }

    @Override
    protected void onAttach() {
        super.onAttach();
        if(!firstAttached){
            onAfterFirstAttach();
            firstAttached = true;
        }
    }

    public Style getStyle(){
        return getElement().getStyle();
    }

    public void setWidth(int width){
        setPixelSize(width, -1);
    }

    public int getWidth(){
        return getWidth(false);
    }

    public int getWidth(boolean client){
        int w = getElement().getWidth(client);
        return w > 0 ? w : this.width;
    }

    public void setHeight(int height){
        setPixelSize(-1, height);
    }

    public int getHeight(){
        return getHeight(false);
    }

    public int getHeight(boolean client){
        int h = getElement().getHeight(client);
        return h > 0 ? h : this.height;
    }

    public void setLeft(int left){
        getElement().setLeft(left);
    }

    public void setRight(int right){
        getElement().setRight(right);
    }

    public void setTop(int top){
        getElement().setTop(top);
    }

    public void setBottom(int bottom){
        getElement().setBottom(bottom);
    }

    public void setPosition(int left, int top){
        setLeft(left);
        setTop(top);
    }

    public void setVisibility(boolean value){
        if(value)
            getStyle().clearVisibility();
        else
            getStyle().setVisibility(Style.Visibility.HIDDEN);
    }

    public void setOpacity(double val){
        getStyle().setOpacity(val);
    }

    public Component withOpacity(double val){
        setOpacity(val);
        return this;
    }

    public void setMargin(int val){
        getElement().setMargin(val);
    }

    public Component withMargin(int val){
        setMargin(val);
        return this;
    }

    public void setMarginLeft(int val){
        getElement().setMarginLeft(val);
    }

    public Component withMarginLeft(int val){
        setMarginLeft(val);
        return this;
    }

    public void setMarginRight(int val){
        getElement().setMarginRight(val);
    }

    public Component withMarginRight(int val){
        setMarginRight(val);
        return this;
    }

    public void setMarginTop(int val){
        getElement().setMarginTop(val);
    }

    public Component withMarginTop(int val){
        setMarginTop(val);
        return this;
    }

    public void setMarginBottom(int val){
        getElement().setMarginBottom(val);
    }

    public Component withMarginBottom(int val){
        setMarginBottom(val);
        return this;
    }

    public void setFloatLeft(){
        getStyle().setFloat(Style.Float.LEFT);
    }

    public Component withFloatLeft(){
        setFloatLeft();
        return this;
    }

    public void setZIndex(int index){
        getStyle().setZIndex(index);
    }

    public void setEnabled(boolean val){
        disabled = !val;
        if(val){
            removeStyleName(decor.css().disabled());
        }else{
            addStyleName(decor.css().disabled());
        }
    }

    public void enable(){
        setEnabled(true);
    }

    public void disable(){
        setEnabled(false);
    }

    public boolean isEnabled(){
        return !disabled;
    }

    public void mask(){
        if(maskEl == null){
            maskEl = DOM.createDiv().cast();
            maskEl.setClassName(decor.css().mask());
            FElement bgEl = DOM.createDiv().cast();
            bgEl.setClassName(decor.css().maskBg());
            maskEl.appendChild(bgEl);
            FElement runnerEl = DOM.createDiv().cast();
            runnerEl.setClassName(decor.css().maskRunner());
            maskEl.appendChild(runnerEl);
        }
        String position = getStyle().getPosition();
        if(position == null || position.isEmpty() || Style.Position.STATIC.name().equalsIgnoreCase(position)){
            maskEl.setAttribute("f-old-position", position);
            getStyle().setPosition(Style.Position.RELATIVE);
        }
        getElement().appendChild(maskEl);
    }

    public void unmask(){
        if(maskEl == null)
            return;
        String oldPosition = maskEl.getAttribute("f-old-position");
        if(oldPosition != null && !oldPosition.isEmpty()){
            getStyle().setProperty("position", oldPosition);
        }
        maskEl.removeFromParent();
    }

    public void setData(String key, Object val){
        if(data == null)
            data = new HashMap<String, Object>();
        data.put(key, val);
    }

    public Object getData(String key){
        if(data == null)
            return null;
        return data.get(key);
    }

    @Override
    public void setPixelSize(int width, int height) {
        if(width >= 0)
            this.width = width;
        if(height >= 0)
            this.height = height;
        super.setPixelSize(width, height);
    }
}
