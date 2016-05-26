package skedgo.loader;

import android.content.Context;
import android.support.annotation.UiThread;
import android.support.annotation.WorkerThread;
import android.support.v4.content.AsyncTaskLoader;

public abstract class AsyncLoader<D> extends AsyncTaskLoader<D> {
  private D data;

  public AsyncLoader(Context context) {
    super(context);
  }

  @Override @WorkerThread public D loadInBackground() {
    return loadData();
  }

  @Override @UiThread public void deliverResult(D data) {
    if (isReset()) {
      // Data is available but the loader is stopped.
      return;
    }

    this.data = data;
    if (isStarted()) {
      // This triggers onLoadFinished() if the callbacks exist.
      super.deliverResult(data);
    }
  }

  /**
   * Starts an asynchronous load of the data. When the result is ready, the callbacks
   * will be called on the UI thread. If a previous load has been completed and is still valid
   * the result may be passed to the callbacks immediately.
   */
  @Override @UiThread protected void onStartLoading() {
    if (data != null) {
      deliverResult(data);
    }

    if (takeContentChanged() || data == null) {
      // Data has changed or it isn't available yet.
      forceLoad();
    }
  }

  @WorkerThread protected abstract D loadData();
}