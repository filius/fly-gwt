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

package ru.fly.client.ui.grid;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.fly.client.PageLoader;
import ru.fly.client.event.ClickHandler;
import ru.fly.client.log.Log;
import ru.fly.client.ui.button.Button;
import ru.fly.client.ui.field.label.Label;
import ru.fly.client.ui.toolbar.ToolBar;
import ru.fly.shared.PagingResult;

/**
 * User: fil
 * Date: 12.03.14
 * Time: 14:38
 */
public class PagingPanel<T> extends ToolBar{

    private final GridDecor decor = GWT.create(GridDecor.class);

    private Grid<T> grid;
    private PageLoader<T> loader;
    private long pageSize = 100;
    private long fullSize = -1;
    private long currentPage = 1;
    private boolean loading = false;

    private Button firstBtn;
    private Button prevBtn;
    private Button nextBtn;
    private Button lastBtn;

    private Label pageInfo;

    public PagingPanel(Grid<T> grid){
        this.grid = grid;
        firstBtn = new Button(decor.res.navFirst(), new ClickHandler() {
            @Override
            public void onClick() {
                firstPage();
            }
        });
        firstBtn.setWidth(40);
        add(firstBtn);
        prevBtn = new Button(decor.res.navPrev(), new ClickHandler() {
            @Override
            public void onClick() {
                prevPage();
            }
        });
        prevBtn.setWidth(40);
        add(prevBtn);
        nextBtn = new Button(decor.res.navNext(), new ClickHandler() {
            @Override
            public void onClick() {
                nextPage();
            }
        });
        nextBtn.setWidth(40);
        add(nextBtn);
        lastBtn = new Button(decor.res.navLast(), new ClickHandler() {
            @Override
            public void onClick() {
                lastPage();
            }
        });
        lastBtn.setWidth(40);
        add(lastBtn);
        disableButtons();

        pageInfo = new Label("");
        add(pageInfo);
    }

    public void setLoader(PageLoader<T> loader){
        this.loader = loader;
    }

    public void load(){
        currentPage = 1;
        fullSize = -1;
        doLoad();
    }

    private long getMaxPage(){
        return (long)Math.ceil((double)fullSize / pageSize);
    }

    private void firstPage(){
        currentPage = 1;
        doLoad();
    }

    private void lastPage(){
        currentPage = getMaxPage();
        doLoad();
    }

    private void prevPage(){
        currentPage--;
        if(currentPage < 1){
            currentPage = 1;
        }
        doLoad();
    }

    private void nextPage(){
        currentPage++;
        if(currentPage > getMaxPage()){
            currentPage--;
        }
        doLoad();
    }

    private void doLoad(){
        if(loader == null || loading)
            return;
        long offset = (currentPage-1)*pageSize;
        if(fullSize == -1 || offset < fullSize){
            doMask();
            loader.load(offset, pageSize, new AsyncCallback<PagingResult<T>>() {
                @Override
                public void onSuccess(PagingResult<T> result) {
                    fullSize = result.getFullSize();
                    grid.getStore().fill(result.getList());
                    doUnmask();
                }

                @Override
                public void onFailure(Throwable caught) {
                    Log.error("Ошибка при получении записей", caught);
                    doUnmask();
                }
            });
        }
    }

    private void doMask(){
        loading = true;
        grid.mask();
        disableButtons();
    }

    private void doUnmask(){
        loading = false;
        grid.unmask();
        enableButtons();
        renderPageInfo();
    }

    private void disableButtons(){
        firstBtn.disable();
        prevBtn.disable();
        nextBtn.disable();
        lastBtn.disable();
    }

    private void enableButtons(){
        if(currentPage > 1){
            firstBtn.enable();
            prevBtn.enable();
        }
        if(currentPage < getMaxPage()){
            nextBtn.enable();
            lastBtn.enable();
        }
    }

    private void renderPageInfo(){
        long recordsFrom = (currentPage-1)*pageSize;
        long recordsTo = recordsFrom + grid.getStore().size();
        recordsFrom++;
        pageInfo.setHtml("страница: "+(currentPage)+" из "+(getMaxPage())+
                " ( записи "+recordsFrom+" - "+recordsTo+" )");
    }

}
