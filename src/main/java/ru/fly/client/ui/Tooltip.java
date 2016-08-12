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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author fil
 */
public class Tooltip extends PopupPanel {

    private final CommonDecor decor = GWT.create(CommonDecor.class);

    private String content;

    public Tooltip(String html){
        this();
        setContent(html);
    }

    public Tooltip(){
        setStyleName(decor.css().tooltip());
    }

    public void setContent(String html){
        clear();
        add(new HTML(html));
    }

    public void show(int x, int y){
        setPopupPosition(x, y);
        show();
    }

    /**
     * locate popup to the left side from target x coordinate, analogue of setRight
     *
     * @param x - absolute coordinate for right
     * @param y - top absolute coordinate
     */
    public void showToTheLeft(int x, int y) {
        setPopupPosition(x, y);
        show();
        // after width calculation
        setPopupPosition(x - getOffsetWidth(), y);
    }

}
