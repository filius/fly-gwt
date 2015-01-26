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

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;

/**
 * User: fil
 * Date: 03.09.13
 * Time: 23:52
 */
public class VLayout extends LayoutContainer {

    public VLayout() {
        super(DOM.createDiv());
    }

    @Override
    protected void doLayout() {
        int w = getWidth(true);
        int freeH = getHeight(true);
        for(Widget child : getWidgets()){
            Object ld = child.getLayoutData();
            if(ld == null || !(ld instanceof VHLayoutData)){
                freeH -= child.getOffsetHeight();
            }else{
                VHLayoutData vld = (VHLayoutData) ld;
                if(vld.getH() < 0 || vld.getH() > 1){
                    int cw = vld.getChildWidth(w);
                    int ch = (int) vld.getH();
                    child.setPixelSize(cw, ch);
                    freeH -=  (ch < 0) ? child.getOffsetHeight() : vld.getH();
                    if(vld.getMargin() != null)
                        freeH = freeH - vld.getMargin().getTop() - vld.getMargin().getBottom();
                }
            }
        }
        for(Widget child : getWidgets()){
            Object ld = child.getLayoutData();
            if(ld != null && ld instanceof VHLayoutData){
                VHLayoutData vld = (VHLayoutData) ld;
                if(vld.getH() >= 0 && vld.getH() <= 1){
                    int cw = vld.getChildWidth(w);
                    int ch = vld.getChildHeight(freeH);
                    child.setPixelSize(cw, ch);
                }
                if(vld.getMargin() != null)
                    vld.getMargin().fillMargins(child);
            }
        }
    }

    public void add(Widget w, VHLayoutData data){
        w.setLayoutData(data);
        super.add(w);
    }

    public void insert(Widget w, int idx, VHLayoutData data){
        w.setLayoutData(data);
        super.insert(w, idx);
    }

}
