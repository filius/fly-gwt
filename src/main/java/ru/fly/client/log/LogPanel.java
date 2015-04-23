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

package ru.fly.client.log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.RootPanel;
import ru.fly.client.event.ClickHandler;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.button.Button;
import ru.fly.client.ui.field.TextAreaField;
import ru.fly.shared.log.LogUtil;

import java.util.Date;
import java.util.logging.Level;

/**
 * User: fil
 * Date: 16.09.13
 * Time: 12:19
 */
public class LogPanel{

    private final LogDecor decor = GWT.create(LogDecor.class);

    private static LogPanel inst;

    public static void log(int lvl, String msg, Throwable e){
        if(inst == null) {
            inst = new LogPanel();
        }
        String prev = inst.area.getValue();
        StringBuilder sb = new StringBuilder((prev == null)?"":prev+"\n")
                .append(new Date().toString()).append("\n")
                .append(Log.getLevelName(lvl)).append(" ").append(msg)
                .append(e == null ? "" : ("\n"+LogUtil.printStackTrace(e)));

        inst.area.setValue(sb.toString());
        if(!inst.btn.isAttached()) {
            RootPanel.get().add(inst.btn);
        }
    }

    private TextAreaField area;
    private Button btn;

    private LogPanel(){
        area  = new TextAreaField(){
            @Override
            protected FElement getInputElement() {
                FElement el = super.getInputElement();
                el.getStyle().setColor("red");
                return el;
            }
        };
        area.setWidth("auto");
        area.setHeight("30%");
        area.setStyleName(decor.css().errorWnd());
        btn = new Button(decor.res.error(), new ClickHandler() {
            @Override
            public void onClick() {
                if(area.isAttached()){
                    area.removeFromParent();
                }else{
                    RootPanel.get().add(area);
                }
            }
        });
        btn.getStyle().setPosition(Style.Position.ABSOLUTE);
        btn.setRight(0);
        btn.setBottom(0);
        btn.getStyle().setZIndex(100001);
    }

}
