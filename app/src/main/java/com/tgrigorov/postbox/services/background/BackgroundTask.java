package com.tgrigorov.postbox.services.background;

import android.os.AsyncTask;

public class BackgroundTask<T> extends AsyncTask<Void, Void, T> {
    public BackgroundTask(IBackgroundProcess<T> process, IBackgroundResult<T> result) {
        this.process = process;
        this.result = result;

        execute();
    }

    @Override
    protected T doInBackground(Void... params) {
        return process.run();
    }

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);
        result.success(t);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        result.cancel();
    }

    private IBackgroundProcess<T> process;
    private IBackgroundResult<T> result;
}
