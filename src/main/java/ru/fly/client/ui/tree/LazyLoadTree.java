package ru.fly.client.ui.tree;

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
import ru.fly.client.ui.tree.decor.TreeDecor;
import ru.fly.shared.PagingResult;

/**
 * @author fil.
 */
public class LazyLoadTree<T> extends Tree<T> {

    private final String uid = F.getUID();
    private long offset = 0;
    private long pageSize = 50;
    private long fullSize = -1;
    private PageLoader<T> loader;
    private boolean loading = false;
    private FElement loadingMasker;

    public LazyLoadTree(TreeGetter<T> getter) {
        this(GWT.<TreeDecor>create(TreeDecor.class), getter);
    }

    public LazyLoadTree(TreeDecor decor, TreeGetter<T> getter) {
        super(decor, getter);
    }

    @Override
    public void onAfterFirstAttach() {
        super.onAfterFirstAttach();
        addScrollListener();
    }

    public void setLoader(PageLoader<T> loader) {
        this.loader = loader;
    }

    public void reload(T select) {
        clear();
        loadNext(select);
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
        if (getView() != null) {
            getView().markDirty();
        }
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
                        offset = next;
                        fullSize = result.getFullSize();
                        for (T m : result.getList()) {
                            getStore().add(null, m, false, false);
                            getStore().addAll(m, getGetter().getChildren(m), false);
                        }
                        getStore().fireUpdateEvent();
                        if (select != null) {
                            select(select);
                        }
                        if (cback != null) {
                            cback.onSuccess(result);
                        }
                        hideLoadingMasker();
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
            loadingMasker.setClassName(getDecor().css().treeLazyLoadMask());
        }
        getElement().appendChild(loadingMasker);
    }

    protected void hideLoadingMasker() {
        loading = false;
        loadingMasker.removeFromParent();
    }

}
