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

/**
 * User: fil
 * Date: 04.09.13
 * Time: 0:05
 */
public class VHLayoutData {

    private double w;
    private double h;
    private Margin margin;
    private boolean resizable = false;
    private double minSize = -1;
    private double maxSize = -1;

    public VHLayoutData(int w, int h){
        this(w, h, null);
    }

    public VHLayoutData(double w, double h){
        this(w,h,null);
    }

    public VHLayoutData(Margin margin){
        this(-1,-1,margin);
    }

    public VHLayoutData(double w, double h, Margin margin){
        this.w = w;
        this.h = h;
        this.margin = margin;
    }

    public int getChildWidth(int parentWidth){
        if(w < 0)
            return -1;
        if(w > 1 || w == 0){
            return (int)w;
        }
        int ret = (int)(parentWidth * w);
        if(margin != null)
            ret = ret - margin.getLeft() - margin.getRight();
        return ret;
    }

    public int getChildHeight(int parentHeight){
        if(h < 0)
            return -1;
        if(h == 0 || h > 1)
            return (int)h;
        int ret = (int)(parentHeight * h);
        if(margin != null)
            ret = ret - margin.getTop() - margin.getBottom();
        if(ret < 0)
            ret = 0;
        return ret;
    }

    public double getH(){
        return h;
    }

    public double getW(){
        return w;
    }

    public void setW(double w) {
        this.w = w;
    }

    public Margin getMargin(){
        return margin;
    }

    public boolean isResizable() {
        return resizable;
    }

    public VHLayoutData setResizable(boolean resizable) {
        this.resizable = resizable;
        return this;
    }

    public VHLayoutData setMinMaxSize(double minSize, double maxSize) {
        this.minSize = minSize;
        this.maxSize = maxSize;
        return this;
    }

    public double getMinSize() {
        return minSize;
    }

    public double getMaxSize() {
        return maxSize;
    }
}
