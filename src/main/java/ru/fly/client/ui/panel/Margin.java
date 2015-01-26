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

import com.google.gwt.user.client.ui.Widget;
import ru.fly.client.ui.FElement;

/**
 * User: fil
 * Date: 09.09.13
 * Time: 16:21
 */
public class Margin {

    private int top = 0;
    private int right = 0;
    private int bottom = 0;
    private int left = 0;

    public Margin(int margin){
        this(margin, margin, margin, margin);
    }

    public Margin(int top, int right, int bottom, int left){
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public int getTop() {
        return top;
    }

    public int getRight() {
        return right;
    }

    public int getBottom() {
        return bottom;
    }

    public int getLeft() {
        return left;
    }

    public void fillMargins(Widget w){
        if(top == right && top == bottom && top == left){
            ((FElement)w.getElement()).setMargin(top);
        }else{
            FElement el = (FElement)w.getElement();
            el.setMarginTop(top);
            el.setMarginRight(right);
            el.setMarginBottom(bottom);
            el.setMarginLeft(left);
        }
    }
}
