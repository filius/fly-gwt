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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.fly.client.F;
import ru.fly.client.LastRespAsyncCallback;
import ru.fly.client.PageLoader;
import ru.fly.client.log.Log;
import ru.fly.client.ui.FElement;
import ru.fly.client.ui.grid.decor.GridDecor;
import ru.fly.shared.PagingResult;

import java.util.List;

/**
 * @author fil
 */
public class LazyLoadGrid<T> extends Grid<T> {

    private final String uid = F.getUID();
    private long offset = 0;
    private long pageSize = 100;
    private long fullSize = -1;
    private PageLoader<T> loader;
    private boolean loading = false;
    private FElement loadingMasker;

    public LazyLoadGrid(List<ColumnConfig<T>> cols) {
        this(GWT.<GridDecor>create(GridDecor.class), cols, new LazyGridView<T>());
    }

    public LazyLoadGrid(GridDecor decor, List<ColumnConfig<T>> cols, GridView<T> view) {
        super(decor, cols, view);
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        addScrollListener();
    }

    public void setLoader(PageLoader<T> loader) {
        this.loader = loader;
    }

    public void reload() {
        clear();
        loadNext();
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public void clear() {
        fullSize = -1;
        offset = 0;
        getStore().clear();
    }

    public void loadNext() {
        loadNext(null);
    }

    public void loadNext(T select) {
        loadNext(select, null);
    }

    public void loadNext(final T select, final AsyncCallback<PagingResult<T>> cback) {
        if (loader != null && !loading) {
            final long next = offset + pageSize;
            if (fullSize == -1 || offset < fullSize) {
                showLoadingMasker();
                loader.load(offset, pageSize, new LastRespAsyncCallback<PagingResult<T>>(uid) {

                    @Override
                    public void onSuccessLast(PagingResult<T> result) {
                        hideLoadingMasker();
                        offset = next;
                        fullSize = result.getFullSize();
                        getStore().addAll(result.getList());
                        if (select != null) {
                            select(select);
                        }
                        if (cback != null) {
                            cback.onSuccess(result);
                        }
                    }

                    @Override
                    public void onFailureLast(Throwable caught) {
                        Log.error("Ошибка при получении записей", caught);
                        hideLoadingMasker();
                        if (cback != null) {
                            cback.onFailure(caught);
                        }
                    }
                });
            }
        }
    }

    private void addScrollListener() {
        final FElement viewElement = getView().getElement();
        final EventListener oldLnr = DOM.getEventListener(viewElement);
        DOM.setEventListener(viewElement, new EventListener() {
            @Override
            public void onBrowserEvent(Event event) {
                if (oldLnr != null)
                    oldLnr.onBrowserEvent(event);
                if (!loading) {
                    FElement inner = viewElement.getFirstChild().cast();
                    int h = viewElement.getHeight(true);
                    int top = viewElement.getScrollTop();
                    if (top + h >= inner.getHeight() - (20 * pageSize / 2)) {
                        loadNext();
                    }
                }
            }
        });
        DOM.sinkEvents(viewElement, DOM.getEventsSunk(viewElement) | Event.ONSCROLL);
    }

    protected void showLoadingMasker() {
        loading = true;
        if (loadingMasker == null) {
            loadingMasker = DOM.createDiv().cast();
            loadingMasker.setInnerHTML("<div id='glbg'></div><div id='gltxt'>Загрузка...</div>");
            loadingMasker.setClassName(getDecor().css().gridLazyLoadMask());
        }
        getElement().appendChild(loadingMasker);
    }

    protected void hideLoadingMasker() {
        loading = false;
        loadingMasker.removeFromParent();
    }

}
